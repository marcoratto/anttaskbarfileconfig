package uk.co.marcoratto.util;

import javax.xml.xpath.*;

import java.io.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.apache.xpath.NodeSet;
import javax.xml.namespace.NamespaceContext;


public class XPathEvaluator{


 public void evaluateDocument(File xmlDocument){

  try{
    XPathFactory factory=XPathFactory.newInstance();
    XPath xPath=factory.newXPath();
    InputSource inputSource=new InputSource(new FileInputStream(xmlDocument));
    XPathExpression         

    xPathExpression=xPath.compile("/catalog/journal/article[@date='January-2004']/title");
    String title=xPathExpression.evaluate(inputSource);
    System.out.println("Title: "+ title);

    inputSource=new InputSource(new FileInputStream(xmlDocument));
    NodeSet nodes = (NodeSet) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);
	NodeList nodeList= (NodeList) nodes;
	 
	for (int j=0; j<nodeList.getLength(); j++) {
	    Element element = (Element) nodeList.item(j);
	    System.out.println(element.getAttribute("uri"));
	}   
   } catch(IOException  e){
	   
   } catch(XPathExpressionException e){
	   
   }

  }


 public static void main(String[] argv){


   XPathEvaluator evaluator=new XPathEvaluator();

   File xmlDocument = new File("G:\\workspaceECLIPSE\\AntTaskBarCustomize\\AntTaskBarFileConfig\\SDII0916_ImportDatiLancio_new\\META-INF\\broker.xml");
   evaluator.evaluateDocument(xmlDocument);

 }

}
