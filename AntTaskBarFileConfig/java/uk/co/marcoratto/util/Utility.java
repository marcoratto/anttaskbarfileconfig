/*
 * Copyright (C) 2009 Marco Ratto
 *
 * This file is part of the project AntTaskMqSeries.
 *
 * AntTaskMqSeries is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * AntTaskMqSeries is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AntTaskMqSeries; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.co.marcoratto.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

public class Utility {
	
	public final static int BUFFER_SIZE = 10 * 1024;
	
	public final static String TEMP_DIR = System.getProperty("java.io.tmpdir");
	public final static String FILE_SEP = System.getProperty("file.separator");

	public static void deleteAll(File path) throws UtilityException {
	    try {
		    if (path.isDirectory()) {
		    	String[] list = path.list();
		    	for(int i=0; i<list.length; i++) {
		    		deleteAll(new File(path, list[i]));
		    	}
		    	path.delete();
		    	System.out.println("Delete directory " + path);
		    } else {
		    	System.out.println("Delete file " + path);
		    	path.delete();
		    }
	    } catch (Exception e) {
	      throw new UtilityException(e);
	    }
	  }

	public final static void stringToFile(File file, String encoding, String buffer) throws UtilityException {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(buffer));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				bw.write(space);
				bw.write(line);
				space = NEWLINE;
			}
		} catch (Throwable t) {
			throw new UtilityException(t);
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
	
	public final static String fileToString(File file, String encoding) throws UtilityException {
		BufferedReader br = null;
		StringBuffer out = new StringBuffer("");
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			String line = null;
			String space = "";
			while ((line = br.readLine()) != null) {
				out.append(space);
				out.append(line);
				space = NEWLINE;
			}
		} catch (Throwable t) {
			throw new UtilityException(t);
		} finally {
			if (br != null) {
				try {
						br.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
		return out.toString();
	}	
	
	public int stringToInt(String s, int defaultValue) {
		int out = defaultValue;
		try {
			out = Integer.parseInt(s, 10);
		} catch (NumberFormatException e) {
			out = defaultValue;
		}
		return out;
	}
	
	public static final void copyInputStream(InputStream in, OutputStream out) throws UtilityException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int len;
		try {
	    	while((len = in.read(buffer)) >= 0) {
		      out.write(buffer, 0, len);	    	
	    	}
		} catch (IOException e) {
			throw new UtilityException(e);
		} finally {
			if (in != null) {
				try  {
					in.close();	
				} catch (IOException e) {					
				}
			}
			if (out != null) {
			    try {
			    	out.close();							
			    } catch (IOException e) {					
			    }
			}
		}

	  }
		
	  public static File createTempDirectory(File fromPath, String prefix, String suffix) throws UtilityException {
		  File temp = null;
		  if (fromPath == null) {
			  try {
				temp = File.createTempFile(prefix, suffix);
			} catch (IOException e) {
				throw new UtilityException(e);
			}
		  } else {
			  temp = new File(fromPath, prefix + Long.toString(System.currentTimeMillis()) + suffix);  
		  }		  
		  temp.mkdirs();
		  System.out.println("Created directory " + temp.getAbsolutePath().toString());
		  return temp;
	  }
	  
	public final static String NEWLINE = System.getProperty("line.separator");

}
