package org.openas2.partner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openas2.BaseComponent;
import org.openas2.OpenAS2Exception;
import org.openas2.message.Message;
import org.openas2.message.MessageMDN;
import org.openas2.params.MessageParameters;
import org.openas2.params.ParameterParser;

public abstract class BasePartnershipFactory extends BaseComponent implements PartnershipFactory {
    private List partnerships;

    public Partnership getPartnership(Partnership p) throws OpenAS2Exception {
        Partnership ps = (p.getName() == null) ? null : getPartnership(p.getName());

        if (ps == null) {
            ps = getPartnership(p.getSenderIDs(), p.getReceiverIDs());
        }

        if (ps == null) {
            throw new PartnershipNotFoundException(p);
        }

        return ps;
    }

    public void setPartnerships(List list) {
        partnerships = list;
    }

    public List getPartnerships() {
        if (partnerships == null) {
            partnerships = new ArrayList();
        }

        return partnerships;
    }

    public void updatePartnership(Message msg, boolean overwrite) throws OpenAS2Exception {
        // Fill in any available partnership information
        Partnership partnership = getPartnership(msg.getPartnership());
        msg.getPartnership().copy(partnership);

        // Set attributes
        if (overwrite) {
            String subject = partnership.getAttribute(Partnership.PA_SUBJECT);
            if (subject != null) {
                msg.setSubject(ParameterParser.parse(subject, new MessageParameters(msg)));
            }
        }
    }

    public void updatePartnership(MessageMDN mdn, boolean overwrite) throws OpenAS2Exception {
        // Fill in any available partnership information
        Partnership partnership = getPartnership(mdn.getPartnership());
        mdn.getPartnership().copy(partnership);
    }

    protected Partnership getPartnership(Map senderIDs, Map receiverIDs) {
        Iterator psIt = getPartnerships().iterator();
        Partnership currentPs;
        Map currentSids;
        Map currentRids;

        while (psIt.hasNext()) {
            currentPs = (Partnership) psIt.next();
            currentSids = currentPs.getSenderIDs();
            currentRids = currentPs.getReceiverIDs();

            if (compareMap(senderIDs, currentSids) && compareMap(receiverIDs, currentRids)) {
                return currentPs;
            }
        }

        return null;
    }

    protected Partnership getPartnership(List partnerships, String name) {
        Iterator psIt = partnerships.iterator();
        Partnership currentPs;
        String currentName;

        while (psIt.hasNext()) {
            currentPs = (Partnership) psIt.next();
            currentName = currentPs.getName();

            if ((currentName != null) && currentName.equals(name)) {
                return currentPs;
            }
        }

        return null;
    }

    protected Partnership getPartnership(String name) throws OpenAS2Exception {
        return getPartnership(getPartnerships(), name);
    }

    // returns true if all values in searchIds match values in partnerIds
    protected boolean compareMap(Map searchIds, Map partnerIds) {
        Iterator searchIt = searchIds.entrySet().iterator();

        if (!searchIt.hasNext()) {
            return false;
        }

        Map.Entry searchEntry;
        String searchKey;
        Object searchValue;
        Object partnerValue;

        while (searchIt.hasNext()) {
            searchEntry = (Map.Entry) searchIt.next();
            searchKey = (String) searchEntry.getKey();
            searchValue = searchEntry.getValue();
            partnerValue = partnerIds.get(searchKey);

            if ((searchValue == null) && (partnerValue != null)) {
                return false;
            } else if ((searchValue != null) && (partnerValue == null)) {
                return false;
            } else if (!searchValue.equals(partnerValue)) {
                return false;
            }
        }

        return true;
    }
}