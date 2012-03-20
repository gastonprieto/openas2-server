package org.openas2.lib;

public class OpenAS2Exception extends Exception {
    public OpenAS2Exception() {
        super();
    }

    public OpenAS2Exception(String msg) {
        super(msg);
    }

    public OpenAS2Exception(String msg, Throwable cause) {
        super(msg, cause);
    }

    public OpenAS2Exception(Throwable cause) {
        super(cause);
    }
}
