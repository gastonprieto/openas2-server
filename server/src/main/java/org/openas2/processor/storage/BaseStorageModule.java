package org.openas2.processor.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.message.Message;
import org.openas2.params.InvalidParameterException;
import org.openas2.processor.BaseProcessorModule;
import org.openas2.util.IOUtilOld;

public abstract class BaseStorageModule extends BaseProcessorModule implements StorageModule {
    public static final String PARAM_FILENAME = "filename";
    public static final String PARAM_PROTOCOL = "protocol";
    public static final String PARAM_TEMPDIR = "tempdir";

    public boolean canHandle(String action, Message msg, Map options) {
        try {
            if (!action.equals(getModuleAction())) {
                return false;
            }

            String modProtocol = getParameter(PARAM_PROTOCOL, false);
            String msgProtocol = msg.getProtocol();

            if (modProtocol != null) {
                if ((msgProtocol != null) && msgProtocol.equals(modProtocol)) {
                    return true;
                }

                return false;
            }

            return true;
        } catch (OpenAS2Exception oae) {
            return false;
        }
    }

    public void init(Session session, Map options) throws OpenAS2Exception {
        super.init(session, options);
        getParameter(PARAM_FILENAME, true);
    }

    protected abstract String getModuleAction();

    
/**
 *   Add one more method "getFile" to make no impact to all modules who call this method with
 *   only two parameter "Message msg" & "String fileParam"
 */ 
  
    
    protected File getFile(Message msg, String fileParam) throws IOException,
			OpenAS2Exception {
		return getFile(msg, fileParam, "");
	} 
 
    /**
     * @since 2007-06-01
     * @param msg
     * @param fileParam
     * @param action
     * @return
     * @throws IOException
     * @throws OpenAS2Exception
     */
    protected File getFile(Message msg, String fileParam, String action) throws IOException, OpenAS2Exception {
    	String filename = getFilename(msg, fileParam); 
    	 
// make sure the parent directories exist
    	File file = new File(filename); 
    	File parentDir = file.getParentFile(); 
    	parentDir.mkdirs(); 
// don't overwrite existing files
    	return IOUtilOld.getUnique(parentDir, IOUtilOld.cleanFilename(file.getName())); 
    
    }


    protected abstract String getFilename(Message msg, String fileParam) throws InvalidParameterException;
    protected abstract String getFilename(Message msg, String fileParam, String action) throws InvalidParameterException;

    protected void store(File msgFile, InputStream in) throws IOException, OpenAS2Exception {
        String tempDirname = getParameter(PARAM_TEMPDIR, false);        
        if (tempDirname != null) {
            // write the data to a temporary directory first
            File tempDir = IOUtilOld.getDirectoryFile(tempDirname);
            String tempFilename = msgFile.getName();            
            File tempFile = IOUtilOld.getUnique(tempDir, tempFilename);
            writeStream(in, tempFile);
            
            // copy the temp file over to the destination
            tempFile.renameTo(msgFile);            
        } else {
            writeStream(in, msgFile);            
        }
    }
    
    protected void writeStream(InputStream in, File destination) throws IOException {
        FileOutputStream out = new FileOutputStream(destination);
        try {
            IOUtilOld.copy(in, out);
        } finally {
            out.close();
            in.close();
        }
    }
    
    
}