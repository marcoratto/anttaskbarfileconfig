package uk.co.marcoratto.util;

import java.io.File;

public class TestXPATHUtility {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XPATHUtility xpath = new XPATHUtility(new File("G:\\workspaceECLIPSE\\AntTaskBarCustomize\\AntTaskBarFileConfig\\SDII0916_ImportDatiLancio_new\\META-INF\\broker.xml"));
		xpath.parse();
		System.out.println(xpath.getOverride());
	}

}
