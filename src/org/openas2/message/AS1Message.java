package org.openas2.message;

public class AS1Message extends BaseMessage {
    public static final String PROTOCOL_AS1 = "as1";

    public String getProtocol() {
        return PROTOCOL_AS1;
    }

    public boolean isRequestingMDN() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isRequestingAsynchMDN() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public String generateMessageID() {
        // TODO Auto-generated method stub
        return null;
    }
}
