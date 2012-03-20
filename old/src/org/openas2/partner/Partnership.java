package org.openas2.partner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class Partnership implements Serializable {
    public static final String PTYPE_SENDER = "sender"; // Sender partner type
    public static final String PTYPE_RECEIVER = "receiver"; // Receiver partner type
    public static final String PID_EMAIL = "email"; // Email address
    public static final String PA_PROTOCOL = "protocol"; // AS1 or AS2
    public static final String PA_SUBJECT = "subject"; // Subject sent in messages    
    public static final String PA_CONTENT_TRANSFER_ENCODING = "content_transfer_encoding"; // optional content transer enc value
   
 
    private Map attributes;
    private Map receiverIDs;
    private Map senderIDs;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAttribute(String id, String value) {
        getAttributes().put(id, value);
    }

    public String getAttribute(String id) {
        return (String) getAttributes().get(id);
    }

    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

    public Map getAttributes() {
        if (attributes == null) {
            attributes = new HashMap();
        }

        return attributes;
    }

    public void setReceiverID(String id, String value) {
        getReceiverIDs().put(id, value);
    }

    public String getReceiverID(String id) {
        return (String) getReceiverIDs().get(id);
    }

    public void setReceiverIDs(Map receiverIDs) {
        this.receiverIDs = receiverIDs;
    }

    public Map getReceiverIDs() {
        if (receiverIDs == null) {
            receiverIDs = new HashMap();
        }

        return receiverIDs;
    }

    public void setSenderID(String id, String value) {
        getSenderIDs().put(id, value);
    }

    public String getSenderID(String id) {
        return (String) getSenderIDs().get(id);
    }

    public void setSenderIDs(Map senderIDs) {
        this.senderIDs = senderIDs;
    }

    public Map getSenderIDs() {
        if (senderIDs == null) {
            senderIDs = new HashMap();
        }

        return senderIDs;
    }

    public boolean matches(Partnership partnership) {
        Map senderIDs = partnership.getSenderIDs();
        Map receiverIDs = partnership.getReceiverIDs();

        if (compareIDs(senderIDs, getSenderIDs())) {
            return true;
        } else if (compareIDs(receiverIDs, getReceiverIDs())) {
            return true;
        }

        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Partnership " + getName());
        buf.append(" Sender IDs = ").append(getSenderIDs());
        buf.append(" Receiver IDs = ").append(getReceiverIDs());
        buf.append(" Attributes = ").append(getAttributes());

        return buf.toString();
    }

    protected boolean compareIDs(Map ids, Map compareTo) {
        Set idSet = ids.entrySet();
        Iterator it = idSet.iterator();

        if (!it.hasNext()) {
            return false;
        }

        Map.Entry currentId;
        Object currentValue;
        Object compareValue;

        while (it.hasNext()) {
            currentId = (Entry) it.next();
            currentValue = currentId.getValue();
            compareValue = compareTo.get(currentId.getKey());

            if ((currentValue != null) && (compareValue == null)) {
                return false;
            } else if ((currentValue == null) && (compareValue != null)) {
                return false;
            } else if (!currentValue.equals(compareValue)) {
                return false;
            }
        }

        return true;
    }

    public void copy(Partnership partnership) {
        if (partnership.getName() != null) {
            setName(partnership.getName());
        }        
        getSenderIDs().putAll(partnership.getSenderIDs());
        getReceiverIDs().putAll(partnership.getReceiverIDs());
        getAttributes().putAll(partnership.getAttributes());
    }
}
