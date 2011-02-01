package uk.co.marcoratto.util;

import java.io.File;

public interface UnzipListener {
	
	   public void onUnzipFile(File aFile) throws UnzipException;
	   
	   public void onUnzipDirectory(File aDirectory) throws UnzipException;
   
}