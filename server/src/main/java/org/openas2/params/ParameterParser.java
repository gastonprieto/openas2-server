package org.openas2.params;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.openas2.OpenAS2Exception;


public abstract class ParameterParser {
    public abstract void setParameter(String key, String value)
        throws InvalidParameterException;

    public abstract String getParameter(String key) throws InvalidParameterException;

    /**
     * Set parameters from a string, like "msg.sender.as2_id=ME,msg.headers.content-type=application/X12"
     * @param encodedParams string to parse
     * @throws InvalidParameterException
     */
    public void setParameters(String encodedParams) throws InvalidParameterException {
        StringTokenizer params = new StringTokenizer(encodedParams, "=,", false);
        String key;
        String value;

        while (params.hasMoreTokens()) {
            key = params.nextToken().trim();

            if (!params.hasMoreTokens()) {
                throw new InvalidParameterException("Invalid value", this, key, null);
            }

            value = params.nextToken();
            setParameter(key, value);
        }
    }

    /**
     * Set parameters from a string seperated by delimiters.
     * @param format		Comma seperated list of parameters to set, like
     * 						<code>msg.sender.as2_id,msg.receiver.as2_id,msg.header.content-type</code>
     * @param delimiters	delimiters in string to parse, like "-."
     * @param value			string to parse, like <code>"NORINCO-WALMART.application/X12"</code>
     * @throws OpenAS2Exception
     */
    public void setParameters(String format, String delimiters, String value)
        throws OpenAS2Exception {
        List keys = parseKeys(format);

        StringTokenizer valueTokens = new StringTokenizer(value, delimiters, false);
        Iterator keyIt = keys.iterator();
        String key;

        while (keyIt.hasNext()) {
            if (!valueTokens.hasMoreTokens()) {
                throw new OpenAS2Exception("Invalid value: Format=" + format + ", value=" + value);
            }

            key = ((String) keyIt.next()).trim();

            if (!key.equals("")) {
                setParameter(key, valueTokens.nextToken());
            }
        }
    }

    /**
     * Static way (why?) of getting at format method.
     * @param format	the format to fill in
     * @param parser	the place to get the parsed info
     * @return			the filled in format
     * @throws InvalidParameterException
     */
    public static String parse(String format, ParameterParser parser)
    	throws InvalidParameterException
    {
    	return parser.format(format);
    }

    /**
     * Fill in a format string with information from a ParameterParser
     * @param format	the format string to fill in
     * @return			the filled in format string.
     * @throws InvalidParameterException
     */
    public String format(String format) throws InvalidParameterException {
        StringBuffer result = new StringBuffer();
        
        for (int next = 0; next < format.length (); ++next) {
        	int prev = next;
        	
        	// Find start of $xxx$ sequence.
        	next = format.indexOf('$', prev);
        	if (next == -1) {
        		result.append(format.substring (prev, format.length ()));
        		break;
        	}
        	
        	// Save text before $xxx$ sequence, if there is any
        	if (next > prev)
        		result.append(format.substring(prev, next));
        		
        	// Find end of $xxx$ sequence
        	prev = next + 1;
        	next = format.indexOf('$', prev);
        	if (next == -1)
            	throw new InvalidParameterException("Invalid key (missing closing $)");
        	
        	// If we have just $$ then output $, else we have $xxx$, lookup xxx
        	if (next == prev) {
        		result.append("$");
        	}
        	else {
        		result.append(getParameter(format.substring(prev, next)));
        	}
        }
        
        return result.toString();
    }

    protected List parseKeys(String format) {
        StringTokenizer tokens = new StringTokenizer(format, ",", false);
        List keys = new ArrayList();

        while (tokens.hasMoreTokens()) {
            keys.add(tokens.nextToken());
        }

        return keys;
    }
}
