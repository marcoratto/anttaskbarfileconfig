package uk.co.marcoratto.ant.ibm.broker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import uk.co.marcoratto.util.Unzip;
import uk.co.marcoratto.util.UnzipException;
import uk.co.marcoratto.util.UnzipListener;
import uk.co.marcoratto.util.Utility;
import uk.co.marcoratto.util.UtilityException;
import uk.co.marcoratto.util.XMLException;
import uk.co.marcoratto.util.XMLUtility;
import uk.co.marcoratto.util.Zip;
import uk.co.marcoratto.util.ZipException;

public class BarFileConfig extends Task {

	private String action = null;
	
	private File propertiesFile = null;
	
	private File sourceBarFile = null;
	private File targetBarFile = null;
	private File tempDir = null;
	
	private File tempDirForBarFile = null;
	
	private boolean override = false;
	
	private String prefixProperty = "";

	public BarFileConfig() {
		super();
		log("BarFileConfig", Project.MSG_DEBUG);
	}
	
	// The method executing the task
	public void execute() throws BuildException {
		log("CustomizeSapAdapter.execute()", Project.MSG_DEBUG);
        if (getProject() == null) {
            throw new IllegalStateException("project has not been set");
        }		
        
		if (this.action == null) {
			throw new BuildException("action has not been set");
		}
		log("The action is " + this.action, Project.MSG_DEBUG);
		
		if (this.sourceBarFile == null) {
			throw new BuildException("sourceBarFile has not been set");
		}
		log("The sourceBarFile is " + this.sourceBarFile, Project.MSG_DEBUG);	
				
		if ((this.tempDir != null) && 
			(!this.tempDir.exists())) {
				this.tempDir.mkdirs();
				log("Temp directory '" + this.tempDir.getAbsolutePath().toString() + "' created.", Project.MSG_DEBUG);
		}		
		log("The tempDir is " + this.tempDir, Project.MSG_DEBUG);

		try {
			tempDirForBarFile = Utility.createTempDirectory(this.tempDir, "bar_", ".tmp");
			log("The tempDirForBarFile is " + this.tempDirForBarFile.getAbsolutePath().toString(), Project.MSG_DEBUG);

			if (this.action.trim().equalsIgnoreCase("read")) {
				this.actionRead();			
			} else if (this.action.trim().equalsIgnoreCase("write")) {
				if (this.propertiesFile == null) {
					throw new BuildException("propertiesFile has not been set!");
				}
				
				if ((this.override == false) &&
						(this.targetBarFile.exists())) {
					throw new BuildException("Target BAR file " + this.targetBarFile + " already exists.");
				}		
				if (this.override) {
					this.targetBarFile.delete();
					log(this.targetBarFile + " deleted.", Project.MSG_DEBUG);
				}
				this.actionWrite();
			} else if (this.action.trim().equalsIgnoreCase("export")) {
				if (this.propertiesFile == null) {
					throw new BuildException("propertiesFile has not been set!");
				}
				
				if ((this.override == false) &&
						(this.propertiesFile.exists())) {
					throw new BuildException("Properties file " + this.propertiesFile + " already exists.");
				}		
				if (this.override) {
					this.propertiesFile.delete();
					log(this.propertiesFile + " deleted.", Project.MSG_DEBUG);
				}
				this.actionExport();
			} else {
				throw new BuildException("Action '" + this.action + "' unknown!");			
			}
		} catch (BuildException e) {
			throw e;
		} catch (UtilityException e) {
			throw new BuildException(e);
		} finally {
			try {
				Utility.deleteAll(tempDirForBarFile);
			} catch (UtilityException e) {
				log("WARNING!I cannot delete temp directory " + tempDir + ".", Project.MSG_WARN);
			}				
		}
	}

	public File getSourceBarFile() {
		return sourceBarFile;
	}

	public void setSourceBarFile(File sourceBarFile) {
		this.sourceBarFile = sourceBarFile;
	}

	public File getTargetBarFile() {
		return targetBarFile;
	}

	public void setTargetBarFile(File targetBarFile) {
		this.targetBarFile = targetBarFile;
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}

	public boolean getOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	public File getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	private void actionWrite() throws BuildException {
		try {
			Unzip unzip = new Unzip(new ActionWrite());
			unzip.unzip(this.sourceBarFile, tempDirForBarFile);
			Zip zip = new Zip(null);
			zip.zip(tempDirForBarFile, this.targetBarFile);
		} catch (UnzipException e) {
			throw new BuildException(e);
		} catch (ZipException e) {
			throw new BuildException(e);
		}
	}
	
	private void actionRead() throws BuildException {
		try {
			Unzip unzip = new Unzip(new ActionRead());
			unzip.unzip(this.sourceBarFile, tempDirForBarFile);
		} catch (UnzipException e) {
			throw new BuildException(e);
		}
	}

	private void actionExport() throws BuildException {
		try {
			Unzip unzip = new Unzip(new ActionExport());
			unzip.unzip(this.sourceBarFile, tempDirForBarFile);
		} catch (UnzipException e) {
			throw new BuildException(e);
		} 		
	}
	
	class ActionWrite implements UnzipListener {
		
		private void checkSapAdapterField(XMLUtility xml, Properties properties, String prefix, String tagName) throws XMLException {
			String applicationServerHost = xml.getTextContent(tagName)[0];
			log("The " + tagName + " is " + applicationServerHost, Project.MSG_DEBUG);					
			String key = prefix + "#" + tagName;			
			if (properties.containsKey(key)) {
				String value = properties.getProperty(key);
				log("Change the key " + key + " to " + value, Project.MSG_DEBUG);
				xml.setTextContent(tagName, value);
			}
		}
		
		public void onUnzipDirectory(File aDirectory) throws UnzipException {
			log("ActionWrite: Dir=" + aDirectory, Project.MSG_DEBUG);
		}

		public void onUnzipFile(File aFile) throws UnzipException {
			log("ActionWrite: File=" + aFile, Project.MSG_DEBUG);
			
			if (aFile.getName().endsWith("broker.xml")) {
				log("ActionWrite: Customizing " + aFile, Project.MSG_DEBUG);
				XMLUtility xml;
				FileInputStream fis = null;
				
				try {
					Properties properties = new Properties();
					if (propertiesFile.exists()) {
						fis = new FileInputStream(propertiesFile);						
						properties.load(fis);
						log("ActionWrite: Found " + properties.size() + " values to customize.", Project.MSG_DEBUG);
					} else {
						throw new UnzipException("Properties file " + propertiesFile.getAbsolutePath().toString() + " not found!");
					}
					xml = new XMLUtility();
					xml.open(aFile);
					
					Hashtable<String,String> ht = xml.getConfigurableProperty();
					Enumeration<String> keys = ht.keys();
					
					log("ActionWrite: Found " + ht.size() + " keys.", Project.MSG_DEBUG);
					while (keys.hasMoreElements()) {
						String key = keys.nextElement();			
						String value = ht.get(key);
						// log("The " + key + " is " + value, Project.MSG_DEBUG);
						if (properties.containsKey(key)) {
							value = properties.getProperty(key);
							xml.setConfigurableProperty(key, value);
							log("Change the " + key + " to " + value, Project.MSG_DEBUG);
						}						
					}
					xml.save(aFile);
				} catch (XMLException e) {
					throw new UnzipException(e);
				} catch (FileNotFoundException e) {
					throw new UnzipException(e);
				} catch (IOException e) {
					throw new UnzipException(e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			} else if (aFile.getName().endsWith(".import") || 
					(aFile.getName().endsWith(".export"))) {
				log("ActionWrite: Customizing " + aFile, Project.MSG_DEBUG);
				XMLUtility xml;
				FileInputStream fis = null;
				
				try {
					Properties properties = new Properties();
					if (propertiesFile.exists()) {
						fis = new FileInputStream(propertiesFile);						
						properties.load(fis);
					}
					xml = new XMLUtility();
					xml.open(aFile);
					
					checkSapAdapterField(xml, properties, prefixProperty, "systemNumber");
					checkSapAdapterField(xml, properties, prefixProperty, "language");
					checkSapAdapterField(xml, properties, prefixProperty, "applicationServerHost");
					checkSapAdapterField(xml, properties, prefixProperty, "RFCTraceLevel");
					checkSapAdapterField(xml, properties, prefixProperty, "sncQop");
					checkSapAdapterField(xml, properties, prefixProperty, "userName");
					checkSapAdapterField(xml, properties, prefixProperty, "password");
					checkSapAdapterField(xml, properties, prefixProperty, "client");										
					checkSapAdapterField(xml, properties, prefixProperty, "sncLib");
					checkSapAdapterField(xml, properties, prefixProperty, "RFCTracePath");
					checkSapAdapterField(xml, properties, prefixProperty, "BONamespace");
					checkSapAdapterField(xml, properties, prefixProperty, "rfcProgramID");
					checkSapAdapterField(xml, properties, prefixProperty, "numberOfListeners");
					checkSapAdapterField(xml, properties, prefixProperty, "gatewayHost");
					checkSapAdapterField(xml, properties, prefixProperty, "gatewayService");			        			       
			        																		
					xml.save(aFile);
										
				} catch (XMLException e) {
					throw new UnzipException(e);
				} catch (FileNotFoundException e) {
					throw new UnzipException(e);
				} catch (IOException e) {
					throw new UnzipException(e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			} else if (aFile.getName().endsWith(".outadapter")
					|| aFile.getName().endsWith(".inadapter")) {				
				File tempDirForOutAdapterFile = null;
				
				try {					
					prefixProperty = aFile.getName();
					log("The prefixProperty is " + prefixProperty, Project.MSG_DEBUG);
					tempDirForOutAdapterFile = Utility.createTempDirectory(tempDir, "sapadapter_", ".tmp");				
					File fileOutAdapter = new File(tempDirForOutAdapterFile, aFile.getName());
					log("The tempDirForOutAdapterFile is " + tempDirForOutAdapterFile, Project.MSG_DEBUG);
										
					Unzip unzip = new Unzip(this);
					unzip.unzip(aFile, tempDirForOutAdapterFile);
										
					Zip zip = new Zip(null);
					zip.zip(tempDirForOutAdapterFile, aFile);							
				} catch (UtilityException e) {
					throw new UnzipException(e);
				} catch (ZipException e) {
					throw new UnzipException(e);
				} finally {
					if (tempDirForOutAdapterFile != null) {
						try  {
							Utility.deleteAll(tempDirForOutAdapterFile);		
						} catch (UtilityException e) {
							// Ignore
						}
					}
				}			
			}	
		}		
	}
	
	class ActionExport implements UnzipListener {
		public void onUnzipDirectory(File aDirectory) throws UnzipException {
			log("ActionExport: Dir=" + aDirectory, Project.MSG_DEBUG);
		}

		public void onUnzipFile(File aFile) throws UnzipException {
			log("ActionExport: File=" + aFile, Project.MSG_DEBUG);
			
			if (aFile.getName().endsWith("broker.xml")) {
				XMLUtility xml;
				FileInputStream fis = null;
				FileOutputStream fos = null;
				
				try {
					Properties properties = new Properties();
					if (propertiesFile.exists()) {
						fis = new FileInputStream(propertiesFile);						
						properties.load(fis);
					}
					fos = new FileOutputStream(propertiesFile);
					xml = new XMLUtility();
					xml.open(aFile);
					
					Hashtable<String,String> ht = xml.getConfigurableProperty();
					Enumeration<String> keys = ht.keys();
					while (keys.hasMoreElements()) {
						String key = keys.nextElement();			
						String value = ht.get(key);
						log("The " + key + " is " + value, Project.MSG_DEBUG);
						properties.put(key, value);
					}

					properties.store(fos, "");
				} catch (XMLException e) {
					throw new UnzipException(e);
				} catch (FileNotFoundException e) {
					throw new UnzipException(e);
				} catch (IOException e) {
					throw new UnzipException(e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// Ignore
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			} else if (aFile.getName().endsWith(".import") || 
					(aFile.getName().endsWith(".export"))) {
				XMLUtility xml;
				FileInputStream fis = null;
				FileOutputStream fos = null;
				
				try {
					Properties properties = new Properties();
					if (propertiesFile.exists()) {
						fis = new FileInputStream(propertiesFile);						
						properties.load(fis);
					}
					fos = new FileOutputStream(propertiesFile);
					xml = new XMLUtility();
					xml.open(aFile);
					
					String systemNumber = xml.getTextContent("systemNumber")[0];
					log("The systemNumber is " + systemNumber, Project.MSG_DEBUG);
					properties.put(prefixProperty + "#systemNumber", systemNumber);
					properties.put(prefixProperty + "#language", xml.getTextContent("language")[0]);
					properties.put(prefixProperty + "#applicationServerHost", xml.getTextContent("applicationServerHost")[0]);
					properties.put(prefixProperty + "#RFCTraceLevel", xml.getTextContent("RFCTraceLevel")[0]);
					properties.put(prefixProperty + "#sncQop", xml.getTextContent("sncQop")[0]);
					properties.put(prefixProperty + "#userName", xml.getTextContent("userName")[0]);
					properties.put(prefixProperty + "#password", xml.getTextContent("password")[0]);					
					properties.put(prefixProperty + "#client", xml.getTextContent("client")[0]);
					properties.put(prefixProperty + "#sncLib", xml.getTextContent("sncLib")[0]);
					properties.put(prefixProperty + "#RFCTracePath", xml.getTextContent("RFCTracePath")[0]);
					properties.put(prefixProperty + "#rfcProgramID", xml.getTextContent("rfcProgramID")[0]);
					properties.put(prefixProperty + "#numberOfListeners", xml.getTextContent("numberOfListeners")[0]);
					properties.put(prefixProperty + "#gatewayHost", xml.getTextContent("gatewayHost")[0]);
					properties.put(prefixProperty + "#gatewayService", xml.getTextContent("gatewayService")[0]);					
					
					properties.store(fos, "");
				} catch (XMLException e) {
					throw new UnzipException(e);
				} catch (FileNotFoundException e) {
					throw new UnzipException(e);
				} catch (IOException e) {
					throw new UnzipException(e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							// Ignore
						}
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							// Ignore
						}
					}
				}
			} else if (aFile.getName().endsWith(".outadapter")
					|| aFile.getName().endsWith(".inadapter")) {				
				File tempDirForOutAdapterFile = null;
				
				try {
					prefixProperty = aFile.getName();
					log("The prefixProperty is " + prefixProperty, Project.MSG_DEBUG);
					tempDirForOutAdapterFile = Utility.createTempDirectory(tempDir, "outadapter_", ".tmp");				
					File fileOutAdapter = new File(tempDirForOutAdapterFile, aFile.getName());
					log("The tempDirForOutAdapterFile is " + tempDirForOutAdapterFile, Project.MSG_DEBUG);
										
					Unzip unzip = new Unzip(this);
					unzip.unzip(aFile, tempDirForOutAdapterFile);
										
				} catch (UtilityException e) {
					throw new UnzipException(e);
				} finally {
					if (tempDirForOutAdapterFile != null) {
						try  {
							Utility.deleteAll(tempDirForOutAdapterFile);		
						} catch (UtilityException e) {
							// Ignore
						}
					}
				}			
			}	
		}		
	}
	
	class ActionRead implements UnzipListener {

		public void onUnzipDirectory(File aDirectory) throws UnzipException {
			log("ActionRead: Dir=" + aDirectory, Project.MSG_DEBUG);
		}

		public void onUnzipFile(File aFile) throws UnzipException {
			log("ActionRead: File=" + aFile, Project.MSG_DEBUG);
			
			if (aFile.getName().endsWith("broker.xml")) {
				XMLUtility xml;
				
				try {
					xml = new XMLUtility();
					xml.open(aFile);
					
					Hashtable<String,String> ht = xml.getConfigurableProperty();
					Enumeration<String> keys = ht.keys();
					while (keys.hasMoreElements()) {
						String key = keys.nextElement();			
						String value = ht.get(key);
						log("The " + key + " is " + value, Project.MSG_DEBUG);
					}
				} catch (XMLException e) {
					throw new UnzipException(e);
				}
			} else if (aFile.getName().endsWith(".import")) {
				XMLUtility xml;
				try {
					xml = new XMLUtility();
					xml.open(aFile);
					log("The systemNumber is " + xml.getTextContent("systemNumber")[0], Project.MSG_DEBUG);					
					log("The language is " + xml.getTextContent("language")[0], Project.MSG_DEBUG);
					log("The applicationServerHost is " + xml.getTextContent("applicationServerHost")[0], Project.MSG_DEBUG);
					log("The RFCTraceLevel is " + xml.getTextContent("RFCTraceLevel")[0], Project.MSG_DEBUG);
					log("The sncQop is " + xml.getTextContent("sncQop")[0], Project.MSG_DEBUG);
					log("The userName is " + xml.getTextContent("userName")[0], Project.MSG_DEBUG);
					log("The password is " + xml.getTextContent("password")[0], Project.MSG_DEBUG);
					log("The client is " + xml.getTextContent("client")[0], Project.MSG_DEBUG);
					log("The sncLib is " + xml.getTextContent("sncLib")[0], Project.MSG_DEBUG);
					log("The RFCTracePath is " + xml.getTextContent("RFCTracePath")[0], Project.MSG_DEBUG);
					log("The BONamespace is " + xml.getTextContent("BONamespace")[0], Project.MSG_DEBUG);
					log("The rfcProgramID is " + xml.getTextContent("rfcProgramID")[0], Project.MSG_DEBUG);
					log("The numberOfListeners is " + xml.getTextContent("numberOfListeners")[0], Project.MSG_DEBUG);
					log("The gatewayHost is " + xml.getTextContent("gatewayHost")[0], Project.MSG_DEBUG);
					log("The gatewayService is " + xml.getTextContent("gatewayService")[0], Project.MSG_DEBUG);										
				} catch (XMLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			} else if (aFile.getName().endsWith(".outadapter")) {				
				File tempDirForOutAdapterFile = null;
				
				try {
					tempDirForOutAdapterFile = Utility.createTempDirectory(tempDir, "outadapter_", ".tmp");				
					File fileOutAdapter = new File(tempDirForOutAdapterFile, aFile.getName());
					log("The tempDirForOutAdapterFile is " + tempDirForOutAdapterFile, Project.MSG_DEBUG);
										
					Unzip unzip = new Unzip(this);
					unzip.unzip(aFile, tempDirForOutAdapterFile);
					
					
					// Zip zip = new Zip(null);
					// zip.zip(tempDirForOutAdapterFile, fileOutAdapter);							
				} catch (UtilityException e) {
					throw new UnzipException(e);
				} finally {
					if (tempDirForOutAdapterFile != null) {
						try  {
							Utility.deleteAll(tempDirForOutAdapterFile);		
						} catch (UtilityException e) {
							// Ignore
						}
					}
				}			
			}	
		}
	}
}
