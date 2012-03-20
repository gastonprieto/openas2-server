package org.openas2.processor.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openas2.DispositionException;
import org.openas2.OpenAS2Exception;
import org.openas2.WrappedException;
import org.openas2.message.Message;
import org.openas2.params.CompositeParameters;
import org.openas2.params.DateParameters;
import org.openas2.params.InvalidParameterException;
import org.openas2.params.MessageParameters;
import org.openas2.params.ParameterParser;
import org.openas2.processor.receiver.AS2ReceiverModule;
import org.openas2.util.DispositionType;

public class MessageFileModule extends BaseStorageModule {
    public static final String PARAM_HEADER = "header";
    
	private Log logger = LogFactory.getLog(MessageFileModule.class.getSimpleName());


    public void handle(String action, Message msg, Map options) throws OpenAS2Exception {
        // store message content
        try {
            File msgFile = getFile(msg, getParameter(PARAM_FILENAME, true), action);
            InputStream in = msg.getData().getInputStream();
            store(msgFile, in);
            logger.info("stored message to " + msgFile.getAbsolutePath()+msg.getLoggingText());
        } catch (Exception e) {
            throw new DispositionException(new DispositionType("automatic-action", "MDN-sent-automatically",
                    "processed", "Error", "Error storing transaction"), AS2ReceiverModule.DISP_STORAGE_FAILED, e);
        }

        String headerFilename = getParameter(PARAM_HEADER, false);

        if (headerFilename != null) {
            try {
                File headerFile = getFile(msg, headerFilename, action);
                InputStream in = getHeaderStream(msg);
                store(headerFile, in);
                logger.info("stored headers to " + headerFile.getAbsolutePath()+msg.getLoggingText());
            } catch (IOException ioe) {
                throw new WrappedException(ioe);
            }
        }
    }

    protected String getModuleAction() {
        return DO_STORE;
    }

/**
 * @deprecated 2007-06-01
 */ 
    
    protected String getFilename(Message msg, String fileParam) throws InvalidParameterException {

        return getFilename(msg, fileParam, "");
    }

    /**
     * @since 2007-06-01
     */
    protected String getFilename(Message msg, String fileParam, String action) throws InvalidParameterException {
        CompositeParameters compParams = new CompositeParameters(false) .
        	add ("date", new DateParameters()) .
        	add ("msg", new MessageParameters(msg));

        return ParameterParser.parse(fileParam, compParams);
    }

    protected InputStream getHeaderStream(Message msg) throws IOException {
        StringBuffer headerBuf = new StringBuffer();

        // write headers to the string buffer
        headerBuf.append("Headers:\r\n");

        Enumeration headers = msg.getHeaders().getAllHeaderLines();
        String header;

        while (headers.hasMoreElements()) {
            header = (String) headers.nextElement();
            headerBuf.append(header).append("\r\n");
        }

        headerBuf.append("\r\n");

        // write attributes to the string buffer
        headerBuf.append("Attributes:\r\n");

        Iterator attrIt = msg.getAttributes().entrySet().iterator();
        Map.Entry attrEntry;

        while (attrIt.hasNext()) {
            attrEntry = (Map.Entry) attrIt.next();
            headerBuf.append(attrEntry.getKey()).append(": ");
            headerBuf.append(attrEntry.getValue()).append("\r\n");
        }

        return new ByteArrayInputStream(headerBuf.toString().getBytes());
    }
}