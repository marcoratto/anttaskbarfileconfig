package uk.co.marcoratto.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtility {

	private File fileXML = null;
	private Document doc = null;
	private DocumentBuilder docBuilder = null;
	private DocumentBuilderFactory docBuilderFactory = null;

	public XMLUtility() throws XMLException {
		this.docBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			this.docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLException(e);
		}
	}

	public void open(File aFile) throws XMLException {
		this.fileXML = aFile;
		try {
			this.doc = docBuilder.parse(fileXML);
		} catch (SAXException e) {
			throw new XMLException(e);
		} catch (IOException e) {
			throw new XMLException(e);
		}
	}

	public void save(File newFile) throws XMLException {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(newFile);
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fos);
			transformer.transform(source, result);			
		} catch (TransformerConfigurationException e) {
			throw new XMLException(e);
		} catch (TransformerException e) {
			throw new XMLException(e);
		} catch (FileNotFoundException e) {
			throw new XMLException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					System.err.println("Warning:" + e.getMessage());
				}
			}
		}

	}

	public void setTextContent(String tagName, String textContent) throws XMLException {
		try {
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName(tagName);
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				element.setTextContent(textContent);
			}

		} catch (Throwable t) {
			throw new XMLException(t);
		}
	}
	
	public String[] getTextContent(String tagName) throws XMLException {
		String[] textContent = null;
		try {
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName(tagName);
			textContent = new String[list.getLength()];
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				textContent[i] = element.getTextContent();
			}

		} catch (Exception e) {
			textContent = null;
			throw new XMLException(e);
		}
		return textContent;
	}

	public void setConfigurableProperty(String key, String value) throws XMLException {
		try {
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("ConfigurableProperty");			
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				String uri = element.getAttribute("uri");
				if (uri.equalsIgnoreCase(key)) {
					element.setAttribute("override", value);
				}
			}
		} catch (Exception e) {
			throw new XMLException(e);
		}		
	}
	
	public Hashtable<String, String> getConfigurableProperty() throws XMLException {
		Hashtable<String, String> out = new Hashtable<String, String>();
		try {
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("ConfigurableProperty");			
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				String uri = element.getAttribute("uri");
				String override = element.getAttribute("override");
				out.put(uri, override);
			}

		} catch (Exception e) {
			out = null;
			throw new XMLException(e);
		}
		return out;
	}
}
