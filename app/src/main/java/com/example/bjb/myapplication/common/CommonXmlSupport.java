package com.example.bjb.myapplication.common;

import com.example.bjb.myapplication.utils.ClassLoaderUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;


/**
 * xml操作基类
 * @author Ethan.li
 *
 */
public abstract class CommonXmlSupport {
	
	protected static final String XML_ENCODING = "utf-8";
	
	public static final String DATETIME_FORMAT_STYLE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	protected Document xml;
	
	protected void setDocument(Document document) {
		this.xml = document;
	}
	
	protected Document getDocument() {
		return xml;
	}
	
	public static String getXML_ENCODING() {
		return XML_ENCODING;
	}

	public Document getXml() {
		return xml;
	}
	
	public Document parserByteArray(byte[] buf) {
		SAXReader reader = new SAXReader();
		reader.setEncoding(XML_ENCODING);
		
		try {
			Document xml = reader.read(new ByteArrayInputStream(buf), XML_ENCODING);
			xml.setXMLEncoding(XML_ENCODING);
			return xml;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
		
	protected String convertXmlHeader(Document document) {
		document.setXMLEncoding(XML_ENCODING);
		String str = document.asXML();
		return str;
	}
	
	protected Document loadDocumentFromXml(String xml) throws Exception {
		SAXReader reader = new SAXReader();
		InputStream is = null;
		try {
			is = ClassLoaderUtils.getResourceAsStream(xml, getClass());
			return reader.read(is);
		} finally {
			try {
				if (is != null) is.close();
			} catch(Exception ex) {}
			reader = null;
		}
	}
	

	///////////////////////////////////////////////////// utils methods ////////////
	protected Document parseXml(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml);
	}
	
	protected void setElementText(String xpath, String text) {
		Element e = getElement(xpath, true);
		e.setText(text==null? "" : text);
	}
	
	protected void addElementText(Node node, String name, String text) {
		Element e = addElement(node, name);
		e.setText(text==null? "" : text);
	}	
	
	protected void addElementText(Node node, String name, int text) {
		Element e = addElement(node, name);
		e.setText(String.valueOf(text));
	}
	
	protected void addElementText(Node node, String name, long text) {
		Element e = addElement(node, name);
		e.setText(String.valueOf(text));
	}
	
	protected void addElementText(Node node, String name, float text) {
		Element e = addElement(node, name);
		e.setText(String.valueOf(text));
	}
	
	protected void addElementText(Node node, String name, double text) {
		Element e = addElement(node, name);
		e.setText(String.valueOf(text));
	}
	
	protected void addElementText(Node node, String name, boolean b) {
		Element e = addElement(node, name);
		e.setText(b?"1":"0");
	}		
	
	protected void setElementText(Node node, String xpath, String text) {
		Element e = getElement(node, xpath, true);
		e.setText(text==null? "" : text);
	}
	
	protected void setElementText(Node node, String xpath, int text) {
		Element e = getElement(node, xpath, true);
		e.setText(String.valueOf(text));
	}
	
	protected void setElementText(Node node, String xpath, long text) {
		Element e = getElement(node, xpath, true);
		e.setText(String.valueOf(text));
	}
	
	protected void setElementText(Node node, String xpath, float text) {
		Element e = getElement(node, xpath, true);
		e.setText(String.valueOf(text));
	}
	
	protected void setElementText(Node node, String xpath, double text) {
		Element e = getElement(node, xpath, true);
		e.setText(String.valueOf(text));
	}
	
	protected void setElementText(Node node, String xpath, boolean b) {
		Element e = getElement(node, xpath, true);
		e.setText(b?"1":"0");
	}	
	
	protected String getElementText(String xpath){
		return getElementText(xpath,"");
	}
	protected String getElementText(String xpath, String defaultValue){
		Element e = getElement(xpath);
		return e == null? defaultValue: e.getText();
	}
	
	protected String getElementAttribute(String xpath, String attrName) {
		Element e = getElement(xpath);
		return e.attributeValue(attrName);
	}
	
	protected void setElementAttribute(String xpath, String attrName,
                                       String attrValue) {
		Element e = getElement(xpath, true);
		e.addAttribute(attrName, attrValue);
	}

	protected Element getElement(String xpath) {
		return getElement(xpath, false);
	}
	
	protected Element getElement(Node node, String xpath) {
		return getElement(node, xpath, false);
	}	
	
	protected Element getElement(String xpath, boolean autoCreate) {
		Element p = (Element) getDocument().selectSingleNode(xpath);
		if (p != null)
			return p;
		// 如果不存在，并要求自动创建这个Node
		if (autoCreate) {
			return DocumentHelper.makeElement(getDocument(),xpath);
		}
		return null;
	}
	
	protected Element getElement(Node node, String xpath, boolean autoCreate) {
		Element p = (Element)node.selectSingleNode(xpath);
		if (p != null)
			return p;
		// 如果不存在，并要求自动创建这个Node
		if (autoCreate) {
			return addElement(node,xpath);
		}
		return null;
	}
	
	protected Element addElement(Node node, String name) {
		return ((Element)node).addElement(name);
	}
	
	@SuppressWarnings("unchecked")
	protected List<Element> getElements(String xpath) {
		return getDocument().selectNodes(xpath);
	}
	
	@SuppressWarnings("unchecked")
	protected List<Element> getElements(Node node, String xpath) {
		return node.selectNodes(xpath);
	}
	
	protected String getValue(Node node, String xpath) {
		Node p = node.selectSingleNode(xpath);
		return p == null ? "" : p.getText();
	}
	
	protected short getShortValue(Node node, String xpath) {
		return (short)getIntValue(node, xpath);
	}
	
	protected int getIntValue(Node node, String xpath) {
		int iv = 0;
		try {
			String str = getValue(node, xpath);
			if (str == null || str.trim().length() == 0)
				return iv;
			else 
				iv = Integer.parseInt(str);
		} catch(Exception ex) {
			iv = 0;
		}
		return iv;
	}
	
	protected long getLongValue(Node node, String xpath) {
		long iv = 0L;
		try {
			String str = getValue(node, xpath);
			if (str == null || str.trim().length() == 0)
				return iv;
			else 
				iv = Long.parseLong(str);
		} catch(Exception ex) {
			iv = 0;
		}
		return iv;
	}

	protected float getFloatValue(Node node, String xpath) {
		float iv = 0.0f;
		try {
			String str = getValue(node, xpath);
			if (str == null || str.trim().length() == 0)
				return iv;
			else 
				iv = Float.parseFloat(str);
		} catch(Exception ex) {
			iv = 0;
		}
		return iv;
	}
	
	protected double getDoubleValue(Node node, String xpath) {
		double iv = 0.0d;
		try {
			String str = getValue(node, xpath);
			if (str == null || str.trim().length() == 0)
				return iv;
			else 
				iv = Double.parseDouble(str);
		} catch(Exception ex) {
			iv = 0;
		}
		return iv;
	}	
	
	protected boolean getBooleanValue(Node node, String xpath) {
		boolean b = false;
		try {
			String str = getValue(node, xpath);
			if (str == null || str.trim().length() == 0)
				return false;
			else if (str.equals("1"))
				return true;
			else 
				b = Boolean.parseBoolean(str);
		} catch(Exception ex) {
			b = false;
		}
		return b;
	}

	protected byte getByteValue(Node node, String xpath) {
		byte bt = 0;
		try {
			String str = getValue(node, xpath);
			if (str == null || str.trim().length() == 0)
				return bt;
			else 
				bt = Byte.parseByte(str);
		} catch(Exception ex) {
			bt = 0;
		}
		return bt;
	}	
}
