package org.openas2.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.openas2.Component;
import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.WrappedException;
import org.openas2.XMLSession;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLUtil {
    public static Component getComponent(Node node, Session session)
        throws OpenAS2Exception {
        Node classNameNode = node.getAttributes().getNamedItem("classname");

        if (classNameNode == null) {
            throw new OpenAS2Exception("Missing classname");
        }

        String className = classNameNode.getNodeValue();
       
        try {
            Class objClass = Class.forName(className);

            if (!Component.class.isAssignableFrom(objClass)) {
                throw new OpenAS2Exception("Class " + className + " must implement " +
                    Component.class.getName());
            }
            
            Component obj = (Component) objClass.newInstance();

            Map parameters = XMLUtil.mapAttributes(node);

            if (session instanceof XMLSession) {
                updateDirectories(((XMLSession) session).getBaseDirectory(), parameters);
            }

            obj.init(session, parameters);

            return obj;
        } catch (Exception e) {
            throw new WrappedException("Error creating component: " + className, e);                                
        } 
    }

    public static Node findChildNode(Node parent, String childName) {
        NodeList childNodes = parent.getChildNodes();
        int childCount = childNodes.getLength();
        Node child;

        for (int i = 0; i < childCount; i++) {
            child = childNodes.item(i);

            if (child.getNodeName().equals(childName)) {
                return child;
            }
        }

        return null;
    }

    public static Map mapAttributeNodes(NodeList nodes, String nodeName, String nodeKeyName,
        String nodeValueName) throws OpenAS2Exception {
        Map attributes = new HashMap();
        int nodeCount = nodes.getLength();
        Node attrNode;
        NamedNodeMap nodeAttributes;
        Node tmpNode;
        String attrName;
        String attrValue;

        for (int i = 0; i < nodeCount; i++) {
            attrNode = nodes.item(i);

            if (attrNode.getNodeName().equals(nodeName)) {
                nodeAttributes = attrNode.getAttributes();
                tmpNode = nodeAttributes.getNamedItem(nodeKeyName);

                if (tmpNode == null) {
                    throw new OpenAS2Exception(attrNode.toString() +
                        " does not have key attribute: " + nodeKeyName);
                }

                attrName = tmpNode.getNodeValue();
                tmpNode = nodeAttributes.getNamedItem(nodeValueName);

                if (tmpNode == null) {
                    throw new OpenAS2Exception(attrNode.toString() +
                        " does not have value attribute: " + nodeValueName);
                }

                attrValue = tmpNode.getNodeValue();
                attributes.put(attrName, attrValue);
            }
        }

        return attributes;
    }

    public static Map mapAttributes(Node node) {
        Map attrMap = new HashMap();
        NamedNodeMap attrNodes = node.getAttributes();
        int attrCount = attrNodes.getLength();
        Node attribute;

        for (int i = 0; i < attrCount; i++) {
            attribute = attrNodes.item(i);
            attrMap.put(attribute.getNodeName().toLowerCase(), attribute.getNodeValue());
        }

        return attrMap;
    }

    public static Map mapAttributes(Node node, String[] requiredAttributes)
        throws OpenAS2Exception {
        Map attributes = mapAttributes(node);
        String attrName;

        for (int i = 0; i < requiredAttributes.length; i++) {
            attrName = requiredAttributes[i];

            if (attributes.get(attrName) == null) {
                throw new OpenAS2Exception(node.toString() + " is missing required attribute: " +
                    attrName);
            }
        }

        return attributes;
    }

    public static void updateDirectories(String baseDirectory, Map attributes)
        throws OpenAS2Exception {
        Iterator attrIt = attributes.entrySet().iterator();
        Map.Entry attrEntry;
        String value;

        while (attrIt.hasNext()) {
            attrEntry = (Entry) attrIt.next();
            value = (String) attrEntry.getValue();

            if (value.startsWith("%home%")) {
                if (baseDirectory != null) {
                    value = baseDirectory + value.substring(6);
                    attributes.put(attrEntry.getKey(), value);
                } else {
                    throw new OpenAS2Exception("Base directory isn't set");
                }
            }
        }
    }
}
