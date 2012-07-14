package com.test.smbstreamer.variant1;

import java.io.IOException;

public abstract class StreamSource {

	protected String mime;
	protected long fp;
	protected long len;
	
	public StreamSource() throws StreamSourceException{
		fp = 0;
	}

	public abstract void open() throws IOException;
	public abstract int read(byte[] bytes) throws IOException;
	public abstract long moveTo(long position) throws IOException;

	public String getMimeType(){
		return mime;
	}
	public long length(){
		return len;
	}
	public abstract void close();
	public long available(){
		return len - fp;
	}
	
}
