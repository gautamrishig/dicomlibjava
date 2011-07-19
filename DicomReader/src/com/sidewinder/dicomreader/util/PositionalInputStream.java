package com.sidewinder.dicomreader.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PositionalInputStream {
	
	private FileInputStream fis;
	private BufferedInputStream bis;
	private int position;
	
	public PositionalInputStream(File file)
			throws FileNotFoundException {
		
		fis = new FileInputStream(file);
		bis = new BufferedInputStream(fis);
		position = 0;
	}
	
	public int read() throws IOException {
		int value;
		
		value = bis.read();
		
		if (value != -1) {
			position++;
		}
		
		return value;
	}
	
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int bytesRead;
		
		bytesRead = bis.read(buffer, offset, length);
		
		if (bytesRead != -1) {
			position += bytesRead;
		}
		
		return bytesRead;
	}
	
	public int read(byte[] buffer) throws IOException {
		
		return read(buffer, 0, buffer.length);
	}
	
	public int read(byte[] buffer, int length) throws IOException {
		return read(buffer, 0, length);
	}
	
	public void skip(int toSkip) throws IOException {
		int skipped;
		
		while (toSkip > 0) {
			skipped = (int)bis.skip(toSkip);
			if (skipped < 0) {
				throw new IOException("Error skipping bytes.");
			}
			
			position += skipped;
			toSkip -= skipped;
		}
	}
	
	public int available() throws IOException {
		return bis.available();
	}
	
	public int getPosition() {
		return position;
	}
	
	public void position(int position) throws IOException {
		fis.getChannel().position(position);
		bis = new BufferedInputStream(fis);
		this.position = position;
	}
}
