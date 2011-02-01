package uk.co.marcoratto.util;

import java.io.File;

public interface ZipListener {
	
   public void onZipFile(File aFile) throws ZipException;
   
   public void onZipDirectory(File aDirectory) throws ZipException;
   
}