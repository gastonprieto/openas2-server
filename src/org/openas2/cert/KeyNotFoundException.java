package org.openas2.cert;

import java.security.cert.X509Certificate;

import org.openas2.OpenAS2Exception;

public class KeyNotFoundException extends OpenAS2Exception {
	
	public KeyNotFoundException(X509Certificate cert, String alias) {
		super("Certificate: " + cert + ", Alias: " + alias);
	}
	
	public KeyNotFoundException(X509Certificate cert, String alias, Throwable cause) {
		super("Certificate: " + cert + ", Alias: " + alias);
		initCause(cause);
	}
}
