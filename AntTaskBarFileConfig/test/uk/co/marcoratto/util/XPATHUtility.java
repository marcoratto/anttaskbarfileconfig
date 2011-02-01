package uk.co.marcoratto.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xpath.NodeSet; 
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPATHUtility {
		
	private String override = null;
	private final static int BUFFER = 10 * 1024;
	private InputSource is = null;
	
	public XPATHUtility(File aFile) {
		override = null;
		this.is = new InputSource(aFile.getAbsolutePath().toString());
	}
	
	public XPATHUtility(String xml) {
		this.is = new InputSource(new StringReader(xml));	
	}

	public void parse() {
		try {			
			XPathFactory factory=XPathFactory.newInstance();
		    this.xPath = factory.newXPath();
		    this.override = this.extract("/Broker/CompiledMessageFlow/ConfigurableProperty/@override");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public String getOverride() {
		return this.override;
	}

	public void setOverride(String s) {
		this.override = s;
	}

	private String extract(String sXPath) throws XPathExpressionException, FileNotFoundException {
		String value = null;
		XPathExpression xPathExpression = xPath.compile(sXPath);
		
		FileInputStream fis = null;		
		
		try {
			NodeSet nodes = (NodeSet) xPathExpression.evaluate(this.is, XPathConstants.NODESET);
			NodeList nodeList= (NodeList) nodes;
			 
			for (int j=0; j<nodeList.getLength(); j++) {
			    Element element = (Element) nodeList.item(j);
			    System.out.println(element.getAttribute("uri"));
			}
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		return value;
	}

	public class HardcodedNamespaceResolver implements NamespaceContext {

	    /**
	     * This method returns the uri for all prefixes needed. Wherever possible
	     * it uses XMLConstants.
	     * 
	     * @param prefix
	     * @return uri
	     */
	    public String getNamespaceURI(String prefix) {
	        if (prefix == null) {
	            throw new IllegalArgumentException("No prefix provided!");
	        } else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
	            return "http://www.ibm.com/AC/commonbaseevent1_0_1";
	        } else if (prefix.equals("ns1")) {
	            return "http://www.ibm.com/AC/commonbaseevent1_0_1";
	        } else if (prefix.equals("xsi")) {
	            return "http://www.w3.org/2001/XMLSchema-instance";
	        } else if (prefix.equals("wmb")) {
	            return "http://www.ibm.com/xmlns/prod/websphere/messageBroker/6.1.0";	        
	        } else {
	            return XMLConstants.NULL_NS_URI;
	        }
	    }

	    public String getPrefix(String namespaceURI) {
	        // Not needed in this context.
	        return null;
	    }

	    public Iterator getPrefixes(String namespaceURI) {
	        // Not needed in this context.
	        return null;
	    }	    

	}
	
	  private void copyInputStream(InputStream in, OutputStream out) throws IOException {
		    byte[] buffer = new byte[BUFFER];
		    int len;

		    while((len = in.read(buffer)) >= 0) {
			      out.write(buffer, 0, len);	    	
		    }

		    in.close();
		    out.close();
		  }

	
	private XPath xPath;
}
