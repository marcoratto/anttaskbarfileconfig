package uk.co.marcoratto.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

public class TestXMLUtility {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		TestXMLUtility test = new TestXMLUtility();
		test.runme(args);
	}
	
	public void runme(String[] args) throws Exception {
		XMLUtility xml = new XMLUtility();
		xml.open(new File("G:\\workspaceECLIPSE\\AntTaskBarCustomize\\AntTaskBarFileConfig\\SDII0916_ImportDatiLancio_new\\META-INF\\broker.xml"));
		
		Hashtable<String,String> ht = xml.getConfigurableProperty();
		Enumeration<String> keys = ht.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();			
			String value = ht.get(key);
			System.out.println(key + "=" + value);
		}
	}

}
