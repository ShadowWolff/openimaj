/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Methods for reading Readable objects and writing
 * Writeable objects.
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 * @author Sina Samangooei <ss@ecs.soton.ac.uk>
 */
public class IOUtils {
	
	private static<T extends Readable<?>> T newInstance(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	/**
	 * Read a new instance of type class from a file.
	 * 
	 * @param <T> instance type expected
	 * @param f the file
	 * @param cls the class
	 * @return new instance of class instantiated from the file
	 * @throws IOException problem reading file
	 */
	public static<T extends Readable<?>> T read(File f, Class<T> cls) throws IOException{
		return read(f,newInstance(cls));
	}
	
	/**
	 * Read a new instance of type class from an input stream.
	 * 
	 * @param <T> instance type expected
	 * @param ios the input stream
	 * @param cls the class
	 * @return new instance of class instantiated from the stream
	 * @throws IOException problem reading stream
	 */
	public static<T extends Readable<?>> T read(InputStream ios, Class<T> cls) throws IOException{
		return read(ios,newInstance(cls));
	}
	
	/**
	 * Open file input stream and call Readable#read(InputStream,T)
	 * 
	 * @param <T> instance type expected
	 * @param f the file
	 * @param obj the object of type T
	 * @return A new instance of type T
	 * @throws IOException an error reading the file
	 */
	public static<T extends Readable<?>> T read(File f, T obj) throws IOException{
		FileInputStream fos = new FileInputStream(f);
		try{
			return read(fos, obj);
		}
		finally{
			fos.close();
		}
	}
	
	/**
	 * Read an instance of an object from an input stream. The stream is tested to contain 
	 * the ASCII or binary header and the appropriate read instance is called.
	 * 
	 * @see Readable#binaryHeader
	 * @see Readable#readBinary
	 * @see Readable#readASCII
	 * @param <T> instance type expected
	 * @param fis the input stream
	 * @param obj the object to instantiate 
	 * @return the object
	 * 
	 * @throws IOException if there is a problem reading the stream from the file
	 */
	@SuppressWarnings("unchecked")
	public static<T extends Readable<?>> T read(InputStream fis, T obj) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(fis);
		if (!isBinary(bis, obj.binaryHeader())) {
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			char[] holder = new char[obj.asciiHeader().length()];
			br.read(holder);
			return (T) obj.readASCII(new Scanner(br));
		}
		else{
			byte[] header = new byte[obj.binaryHeader().length];
			bis.read(header, 0, header.length);
			return (T) obj.readBinary(new DataInputStream(bis));
		}
	}
	
	/**
	 * Opens an input stream and calls input stream version
	 * 
	 * @see IOUtils#isBinary(BufferedInputStream,byte[])
	 * 
	 * @param f file containing data
	 * @param header expected header in binary format
	 * @return is the file in the binary format
	 * @throws IOException if there was a problem reading the input stream
	 */
	public static boolean isBinary(File f, byte [] header) throws IOException{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try{
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);

			return isBinary(bis, header);
		}
		finally{
			if (fis != null) try { fis.close(); } catch (IOException e) {}
			if (bis != null) try { bis.close(); } catch (IOException e) {}
		}
	}
		
	/**
	 * Extracts the binary header from object and calls the byte[] version
	 * 
	 * @see IOUtils#isBinary(BufferedInputStream,byte[])
	 * 
	 * @param <T> expected data type
	 * @param bis stream containing data
	 * @param obj instance of expected data type
	 * @return does the stream contain binary information
	 * @throws IOException problem reading input stream
	 */
	public static<T extends Readable<T>> boolean isBinary(BufferedInputStream bis, T obj) throws IOException {
		return isBinary(bis, obj.binaryHeader());
	}
	
	/**
	 * Checks whether a given input stream contains readable binary information by checking for 
	 * the first header.length bytes == header. The stream is reset to the beginning of the header once
	 * checked.
	 * 
	 * @param bis stream containing data
	 * @param header expected binary header
	 * @return does the stream contain binary information
	 * @throws IOException problem reading or reseting the stream
	 */
	public static boolean isBinary(BufferedInputStream bis, byte [] header) throws IOException {
		bis.mark(header.length + 10);
		byte [] aheader = new byte[header.length];
		bis.read(aheader, 0, aheader.length);
		bis.reset();
				
		return Arrays.equals(aheader, header) ;
	}
	
	/**
	 * Write a Writeable T object instance. Opens a file stream and calls output stream version
	 * 
	 * @see IOUtils#writeBinary(OutputStream,Writeable)
	 * 
	 * @param <T> data type to be written
	 * @param f file to write instance to
	 * @param obj instance to be written
	 * @throws IOException error reading file
	 */
	public static<T extends Writeable<?>> void writeBinary(File f, T obj) throws IOException{
		FileOutputStream fos = new FileOutputStream(f);
		try{
			writeBinary(fos, obj);
			fos.flush();
		}
		finally{
			fos.close();
		}
	}
	
	/**
	 * Write a Writeable T object instance to the provided output stream, calls BufferedOutputStream version
	 * 
	 * @see IOUtils#writeBinary(BufferedOutputStream,Writeable)
	 * 
	 * @param <T> Expected data type
	 * @param fos output stream
	 * @param obj the object to write
	 * @throws IOException error writing to stream
	 */
	public static<T extends Writeable<?>> void writeBinary(OutputStream fos, T obj) throws IOException {
		BufferedOutputStream bos = null;
		try{
			bos = new BufferedOutputStream(fos);
			writeBinary(bos,obj);
		}
		finally{
			if(bos!=null){
				bos.flush();
				bos.close();
			}
		}
		
	}
	
	/**
	 * Writeable object is written to the output stream in binary format. Firstly the binaryHeader is written then the object
	 * is handed the output stream to write it's content.
	 * 
	 * @see Writeable#writeBinary(java.io.DataOutput)
	 * @see Writeable#binaryHeader()
	 * 
	 * @param <T> instance type expected
	 * @param bos the output stream
	 * @param obj the object to write
	 * @throws IOException error writing to stream
	 */
	public static<T extends Writeable<?>> void writeBinary(BufferedOutputStream bos, T obj) throws IOException {
		DataOutputStream dos = new DataOutputStream(bos);
		dos.write(obj.binaryHeader());
		obj.writeBinary(dos);		
	}
	
	
	/**
	 * Writeable object is written to the a file in ASCII format. File stream is opened and stream version is called
	 * 
	 * @see IOUtils#writeASCII(OutputStream, Writeable)
	 * 
	 * @param <T> instance type expected
	 * @param f the file to write to
	 * @param obj the object to write
	 * @throws IOException error writing to file
	 */
	public static<T extends Writeable<?>> void writeASCII(File f, T obj) throws IOException{
		FileOutputStream fos = new FileOutputStream(f);
		try{
			writeASCII(fos, obj);
			fos.flush();
		}
		finally{
			fos.close();
		}
	}
	
	/**
	 * Write the object in ASCII format to the output stream. Construct a PrintWriter using the outputstream,
	 * write the object's ASCII header then write the object in ASCII format.
	 * 
	 * @see PrintWriter
	 * @see Writeable#asciiHeader()
	 * @see Writeable#writeASCII(PrintWriter)
	 * 
	 * @param <T> instance type expected
	 * @param fos the output stream
	 * @param obj the object
	 * @throws IOException error writing to stream
	 */
	public static<T extends Writeable<?>> void writeASCII(OutputStream fos, T obj) throws IOException {
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(fos);
			pw.print(obj.asciiHeader());
			obj.writeASCII(pw);
		} finally {
			if(pw!=null) {
				pw.flush();
				pw.close();
			}
		}
	}
	
	/**
	 * Check whether a given file is readable by a given Writeable class. Instantiates the class to get
	 * it's binary and ascii header which is then passed to the byte[] version of this method
	 * 
	 * @see Readable#asciiHeader()
	 * 
	 * @param <T> instance type expected
	 * @param f the file to check
	 * @param cls the class to instantiate the Readable object
	 * @return is file readable by a given class
	 * @throws IOException error reading file
	 */
	public static<T extends Readable<?>> boolean readable(File f, Class<T> cls) throws IOException {
		return readable(f, newInstance(cls).binaryHeader()) || readable(f, newInstance(cls).asciiHeader());
	}
	
	/**
	 * Check whether a file is readable by checking it's first bytes contains the header. Converts the header
	 * to a byte[] and calls the byte[] version of this method
	 * 
	 * @see IOUtils#readable(File, byte[])
	 * 
	 * @param f the file to check
	 * @param header the header to check with
	 * @return does this file contain this header
	 * @throws IOException error reading file
	 */
	public static boolean readable(File f, String header) throws IOException {
		return readable(f, header.getBytes());
	}
	
	/**
	 * Check readability by checking whether a file starts with a given header. Instantiate an input stream and check whether 
	 * the stream isBinary (i.e. starts with the header)
	 * 
	 * @see IOUtils#isBinary(BufferedInputStream, byte[])
	 * 
	 * @param f file to check
	 * @param header the expected header 
	 * @return does the file start with the header
	 * @throws IOException error reading file
	 */
	public static boolean readable(File f, byte [] header) throws IOException {
		FileInputStream fos = null;
		try{
			fos = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fos);
			return isBinary(bis, header);
		}
		finally{
			try {fos.close();} catch (IOException e) {}
		}
	}
	
	/**
	 * Check whether an InputStream can be read by an instantiated class based on it's binary and ascii headers. Buffered so 
	 * the stream can be reset after the check.
	 * 
	 * @param <T> instance type expected
	 * @param bis the stream
	 * @param cls the class to instantiate and check
	 * @return can an object be read from this stream of the class type
	 * @throws IOException error reading stream
	 */ 
	public static<T extends Readable<?>> boolean readable(BufferedInputStream bis, Class<T> cls) throws IOException {
		return readable(bis, newInstance(cls).binaryHeader()) || readable(bis, newInstance(cls).asciiHeader());
	}
	
	/**
	 * Check whether an input stream starts with a header string. Calls the byte[] version of this function
	 * 
	 * @see IOUtils#isBinary(BufferedInputStream, byte[])
	 * 
	 * @param bis the input stream
	 * @param header the header
	 * @return whether the stream starts with the string
	 * @throws IOException error reading stream
	 */
	public static boolean readable(BufferedInputStream bis, String header) throws IOException {
		return readable(bis, header.getBytes());
	}
	
	/**
	 * Check whether a stream starts with a header. Uses isBinary (therefore resetting the stream after the check)
	 * 
	 * @param bis the input stream
	 * @param header the byte[] header
	 * @return whether the stream starts with the header
	 * @throws IOException error reading stream
	 */
	public static boolean readable(BufferedInputStream bis, byte[] header) throws IOException {
		return isBinary(bis, header);
	}
}
