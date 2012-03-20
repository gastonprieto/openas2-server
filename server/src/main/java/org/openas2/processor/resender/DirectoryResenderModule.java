package org.openas2.processor.resender;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.WrappedException;
import org.openas2.message.Message;
import org.openas2.params.InvalidParameterException;
import org.openas2.processor.sender.SenderModule;
import org.openas2.util.DateUtil;
import org.openas2.util.IOUtilOld;


public class DirectoryResenderModule extends BaseResenderModule {
	public static final String PARAM_RESEND_DIRECTORY = "resenddir";
	public static final String PARAM_ERROR_DIRECTORY = "errordir";
    public static final String PARAM_RESEND_DELAY = "resenddelay"; // in seconds

	// TODO Resend set to 15 minutes. Implement a scaling resend time with eventual permanent failure of transmission    
	public static final long DEFAULT_RESEND_DELAY = 15 * 60 * 1000; // 15 minutes

	private Log logger = LogFactory.getLog(DirectoryResenderModule.class.getSimpleName());

	
	public boolean canHandle(String action, Message msg, Map options) {
		return action.equals(ResenderModule.DO_RESEND);
	}
        
	public void handle(String action, Message msg, Map options)
		throws OpenAS2Exception {
		try {                        
			File resendDir = IOUtilOld.getDirectoryFile(getParameter(PARAM_RESEND_DIRECTORY, true));
			File resendFile = IOUtilOld.getUnique(resendDir, getFilename());
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(resendFile));
			String method = (String) options.get (ResenderModule.OPTION_RESEND_METHOD);
			if (method == null) method = SenderModule.DO_SEND;
			String retries = (String) options.get(ResenderModule.OPTION_RETRIES);
			if (retries == null) retries = "-1";
			oos.writeObject(method);
			oos.writeObject(retries);
			oos.writeObject(msg);
			oos.close();
            
            logger.info("message put in resend queue"+msg.getLoggingText());
		} catch (IOException ioe) {
			throw new WrappedException(ioe);
		}
	}
    
	public void init(Session session, Map options) throws OpenAS2Exception {
		super.init(session, options);
		getParameter(PARAM_RESEND_DIRECTORY, true);
		getParameter(PARAM_ERROR_DIRECTORY, true);        
	}

	public void resend() {
		try {
			try {
				// get a list of files that need to be sent now
				List sendFiles = scanDirectory();

				// iterator through and send each file
				Iterator fileIt = sendFiles.iterator();
				File currentFile;

				while (fileIt.hasNext()) {
					currentFile = (File) fileIt.next();
					processFile(currentFile);
				}
			} catch (IOException ioe) {
				throw new WrappedException(ioe);
			}
		} catch (OpenAS2Exception oae) {
			oae.terminate();
			forceStop(oae);
		}
	}

	protected String getFilename() throws InvalidParameterException {		
        long resendDelay;
        if (getParameter(PARAM_RESEND_DELAY, false) == null) {
            resendDelay = DEFAULT_RESEND_DELAY;
        } else {
            resendDelay = getParameterInt(PARAM_RESEND_DELAY, false) * 1000;
        }
		long resendTime = new Date().getTime() + resendDelay;

		return DateUtil.formatDate("MM-dd-yy-HH-mm-ss", new Date(resendTime));
	}

	protected boolean isTimeToSend(File currentFile) {
		try {			
			StringTokenizer fileTokens = new StringTokenizer(currentFile.getName(), ".", false);

			Date timestamp = DateUtil.parseDate("MM-dd-yy-HH-mm-ss", fileTokens.nextToken());			

			return timestamp.before(new Date());
		} catch (Exception e) {
			return true;
		}
	}

	protected void processFile(File file) throws OpenAS2Exception {
		logger.debug("processing " + file.getAbsolutePath());

		Message msg = null;

		try {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
				String method = (String) ois.readObject();
				String retries = (String) ois.readObject();
				msg = (Message) ois.readObject();
                ois.close();

				// Transmit the message
                logger.info("loaded message for resend."+msg.getLoggingText());
                
                Map options = new HashMap();
                options.put(SenderModule.SOPT_RETRIES, retries);
				getSession().getProcessor().handle(method, msg, options);
                
				if (!file.delete()) { // Delete the file, sender will re-queue if the transmission fails again
					throw new OpenAS2Exception("File was successfully sent but not deleted: " +
						file.getAbsolutePath());
				}

				logger.info("deleted " + file.getAbsolutePath()+msg.getLoggingText());
			} catch (IOException ioe) {
				throw new WrappedException(ioe);
			} catch (ClassNotFoundException cnfe) {
				throw new WrappedException(cnfe);
			}
		} catch (OpenAS2Exception oae) {
			oae.addSource(OpenAS2Exception.SOURCE_MESSAGE, msg);
			oae.addSource(OpenAS2Exception.SOURCE_FILE, file);
			oae.terminate();
			IOUtilOld.handleError(file, getParameter(PARAM_ERROR_DIRECTORY, true));
		}
	}

	protected List scanDirectory() throws OpenAS2Exception, IOException {
		File resendDir = IOUtilOld.getDirectoryFile(getParameter(PARAM_RESEND_DIRECTORY, true));
		List sendFiles = new ArrayList();

		File[] files = resendDir.listFiles();

		if (files == null) {
			throw new InvalidParameterException("Error getting list of files in directory", this,
				PARAM_RESEND_DIRECTORY, resendDir.getAbsolutePath());
		}

		if (files.length > 0) {
			File currentFile;

			for (int i = 0; i < files.length; i++) {
				currentFile = files[i];

				if (currentFile.exists() && currentFile.isFile() && currentFile.canWrite()) {
					if (isTimeToSend(currentFile)) {
						sendFiles.add(currentFile);
					}
				}
			}
		}

		return sendFiles;
	}
}
