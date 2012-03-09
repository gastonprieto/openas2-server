package org.openas2.cert;

import java.security.cert.X509Certificate;

import org.openas2.OpenAS2Exception;


public class CertificateNotFoundException extends OpenAS2Exception {
    private String alias;
    private String partnershipType;

    public CertificateNotFoundException(String partnershipType, String alias) {
        super("Type: " + partnershipType + ", Alias: " + alias);
        this.partnershipType = partnershipType;
        this.alias = alias;
    }

    public CertificateNotFoundException(X509Certificate cert) {
        super("Certificate not in store: " + cert.toString());
    }

    public void setAlias(String string) {
        alias = string;
    }

    public String getAlias() {
        return alias;
    }

    public void setPartnershipType(String string) {
        partnershipType = string;
    }

    public String getPartnershipType() {
        return partnershipType;
    }
}
