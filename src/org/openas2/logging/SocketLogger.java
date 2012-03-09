package org.openas2.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.message.Message;

/**
 * class to write log meesage to a socket
 * 
 * @since october 2007.
 * @author joseph mcverry
 * 
 */
public class SocketLogger extends BaseLogger {

	public final static String PARAM_PORTID = "portid";
	public final static String PARAM_IPADDDR = "ipaddr";
	public final static int iQUEUESIZE = 100;

	private int port = 0;
	private String ipAddr = null;

	public void init(Session session, Map parameters) throws OpenAS2Exception {
		super.init(session, parameters);
		String portID = getParameter(PARAM_PORTID, true);
		ipAddr = getParameter(PARAM_IPADDDR, true);
		try {
			port = Integer.parseInt(portID);
		} catch (NumberFormatException nfe) {
			throw new OpenAS2Exception(PARAM_PORTID
					+ " is not a valid integer value, see \"" + portID+ "\"", nfe);
		}

	}

	public void doLog(Level level, String msgText, Message message) {
		sendToSocket(getFormatter().format(level, msgText));
	}

	public void sendToSocket(String msgText) {
		
	

		try {
			Socket sckt = new Socket(ipAddr, port);
			OutputStream os = sckt.getOutputStream();
			os.write(msgText.getBytes());
			os.flush();
			os.close();
			sckt.close();

		} catch (UnknownHostException e) {
	          
			// don't do anything.
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected String getShowDefaults() {
		return VALUE_SHOW_ALL;
	}

	protected void doLog(OpenAS2Exception exception, boolean terminated) {
		sendToSocket(getFormatter().format(exception, terminated));
	}

}