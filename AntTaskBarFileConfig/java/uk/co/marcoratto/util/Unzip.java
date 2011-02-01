package uk.co.marcoratto.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Unzip {

	private final static int BUFFER = 10 * 1024;
	
	private UnzipListener target = null;
	
	public Unzip(UnzipListener aUnzipListener) {
		this.target = aUnzipListener;
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

	  public void unzip(File zip, File path) throws UnzipException {
	    ZipFile zipFile = null;

	    try {
		    zipFile = new ZipFile(zip);

		    Enumeration entries = zipFile.entries();

	      while(entries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry)entries.nextElement();
	        if(entry.isDirectory()) {
	        	File dir = new File(path, entry.getName());
	        	dir.mkdir();
	        	if (this.target != null) {
	        		this.target.onUnzipDirectory(dir);
	        	}
	        } else {
		        if (entry.getName().indexOf("/") >= 0) {
		        	File dir = new File(new File(path, entry.getName()).getParent());
		        	dir.mkdir();
		        }
		        File f = new File(path, entry.getName());
		        copyInputStream(zipFile.getInputStream(entry),
		           new BufferedOutputStream(new FileOutputStream(f)));	        	
	        	if (this.target != null) {
	        		this.target.onUnzipFile(f);
	        	}
	        }

	      }

	    } catch (Exception e) {
	      throw new UnzipException(e); 
	    } finally {
	    	if (zipFile != null) {
	  		  try {
				zipFile.close();
			} catch (IOException e) {
				// Ignore
			}	    		    		
	    	}
	    }
	  }

	}
