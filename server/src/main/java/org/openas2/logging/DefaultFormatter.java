package org.openas2.logging;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.openas2.OpenAS2Exception;
import org.openas2.util.DateUtil;


public class DefaultFormatter extends BaseFormatter {
    public String getTerminatedMessage(OpenAS2Exception exception) {
        StringBuffer buf = new StringBuffer("Termination of exception:\r\n");

        // Write exception trace
        StringWriter strWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(strWriter);
        exception.printStackTrace(printWriter);
        buf.append(strWriter.toString());

        // Write sources
        Map sources = exception.getSources();
        Iterator sourceIt = sources.entrySet().iterator();
        Map.Entry source;

        while (sourceIt.hasNext()) {
            source = (Map.Entry) sourceIt.next();
            buf.append(source.getKey().toString()).append("\r\n");
            buf.append(source.getValue().toString()).append("\r\n\r\n");
        }

        return buf.toString();
    }

    public void format(Level level, String msg, OutputStream out) {
		PrintWriter writer = new PrintWriter(out);
        
        // Write timestamp		
	    writer.print(DateUtil.formatDate("MM/dd/yy HH:mm:ss"));		
		
		// Write log level
		//writer.print(" ");
	    //writer.print(level.getName().toUpperCase());
	    
	    // Write message
	    writer.print(" ");
        writer.println(msg);        
		writer.flush();
    }

    public void format(OpenAS2Exception exception, boolean terminated, OutputStream out) {
        PrintWriter writer = new PrintWriter(out);

        if (terminated) {
            writer.println("Termination of exception:");
        }

        // Write exception trace
        exception.printStackTrace(writer);

        // Write sources if terminated
        if (terminated) {
            Map sources = exception.getSources();
            Iterator sourceIt = sources.entrySet().iterator();
            Map.Entry source;

            while (sourceIt.hasNext()) {
                source = (Map.Entry) sourceIt.next();
                if (source.getKey() != null) {                
                	writer.println(source.getKey().toString());
                } else {
                	writer.println("null key");
                }
                if (source.getValue() != null) {                
                writer.println(source.getValue().toString());
                } else {
                	writer.println("null value");
                }
            }
        }
		writer.println();
		writer.flush();
    }
}
