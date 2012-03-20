package org.openas2;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openas2.params.InvalidParameterException;


public class BaseComponent implements Component {
    private Map parameters;
    private Session session;

    public String getName() {
        String clippedName = this.getClass().getName();

        // this clips off the package information
        StringTokenizer classParts = new StringTokenizer(clippedName, ".", false);

        while (classParts.hasMoreTokens()) {
            clippedName = classParts.nextToken();
        }

        return clippedName;
    }

    public void setParameter(String key, String value) {
        getParameters().put(key, value);
    }

    public void setParameter(String key, int value) {
        setParameter(key, Integer.toString(value));
    }

    public String getParameter(String key, boolean required)
        throws InvalidParameterException {
        String parameter = (String) getParameters().get(key);

        if (required && (parameter == null)) {
            throw new InvalidParameterException(this, key, null);
        }

        return parameter;
    }

    public String getParameter(String key, String defaultValue)
        throws InvalidParameterException {
        String value = getParameter(key, false);

        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    public int getParameterInt(String key, boolean required)
        throws InvalidParameterException {
        String value = getParameter(key, required);

        if (value != null) {
            return Integer.parseInt(value);
        }

        return 0;
    }

    public Map getParameters() {
        if (parameters == null) {
            parameters = new HashMap();
        }

        return parameters;
    }

    public Session getSession() {
        return session;
    }

    public void init(Session session, Map parameters) throws OpenAS2Exception {
        this.session = session;
        this.parameters = parameters;
    }
	
}
