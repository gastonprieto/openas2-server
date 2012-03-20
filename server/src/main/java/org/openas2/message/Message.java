package org.openas2.message;

import java.io.Serializable;
import java.util.Map;

import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

import org.openas2.OpenAS2Exception;
import org.openas2.partner.Partnership;


public interface Message extends Serializable {
    public void setAttribute(String key, String value);

    public String getAttribute(String key);

    public void setAttributes(Map attributes);

    public Map getAttributes();

    public void setContentType(String contentType);

    public String getContentType();

    public void setContentDisposition(String contentDisposition);

    public String getContentDisposition();

    public void setData(MimeBodyPart data, DataHistoryItem historyItem) throws OpenAS2Exception;
    
	public DataHistoryItem setData(MimeBodyPart data) throws OpenAS2Exception;

    public MimeBodyPart getData();

    public void setHeader(String key, String value);

    public String getHeader(String key);

    public String getHeader(String key, String delimiter);

    public void setHeaders(InternetHeaders headers);

    public InternetHeaders getHeaders();

    public void setHistory(DataHistory history);

    public DataHistory getHistory();

    public void setMDN(MessageMDN mdn);

    public MessageMDN getMDN();

    public void setMessageID(String messageID);

    public String getMessageID();

    public void setPartnership(Partnership partnership);

    public Partnership getPartnership();

    public String getProtocol();

    public boolean isRequestingMDN();

    public boolean isRequestingAsynchMDN();

    public void setSubject(String subject);

    public String getSubject();

    public void addHeader(String key, String value);

    public String generateMessageID();

    public void updateMessageID();
    
    public String getLoggingText();
}
