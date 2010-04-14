package uk.co.marcoratto.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
	   
	private final static int BUFFER = 10 * 1024;
	
	private ZipListener target = null;
	
	public Zip(ZipListener aZipListener) {
		this.target = aZipListener;
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
	   
	public void zip(File sourcePath, File targetZip) throws ZipException {
		System.out.println("Create file " + targetZip.getAbsolutePath().toString());
		ZipOutputStream out = null;	
		try {
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetZip)));
			String[] list = sourcePath.list();
			for(int i=0; i<list.length; i++) {
	    		zipRecursive(new File(sourcePath, list[i]), "", out);
	    	}
		} catch (FileNotFoundException e) {
			throw new ZipException(e);
		} catch (IOException e) {
			throw new ZipException(e);
		} finally {			
	    	if (out != null) {
			    try {
					out.close();
				} catch (IOException e) {
					// Ignore
				}	    		
	    	}
		}
    }
	
	private void zipRecursive(File sourceFile, String startDir, ZipOutputStream out) throws ZipException, IOException {		
	    if (sourceFile.isDirectory()) {
	    	if (this.target != null) {
	    		this.target.onZipDirectory(sourceFile);
	    	}
	    	startDir = sourceFile.getName() + "/";
	    	ZipEntry entry = new ZipEntry(startDir);
            out.putNextEntry(entry);
	    	System.out.println("adding directory: " + startDir);
	    	String[] list = sourceFile.list();
	    	for(int i=0; i<list.length; i++) {
	    		zipRecursive(new File(sourceFile, list[i]), startDir, out);
	    	}
	    } else {
	    	if (this.target != null) {
	    		this.target.onZipFile(sourceFile);
	    	}
			BufferedInputStream origin = null;
	        byte data[] = new byte[BUFFER];	    
		    	 
	         FileInputStream fi = new FileInputStream(sourceFile);
	            origin = new BufferedInputStream(fi, BUFFER);
	            ZipEntry entry = new ZipEntry(startDir + sourceFile.getName());
	            System.out.println("adding file: " + entry.getName());
	            out.putNextEntry(entry);
	            int count;
	            while((count = origin.read(data, 0, 
	              BUFFER)) != -1) {
	               out.write(data, 0, count);
	            }
	    }

	   }
	}

