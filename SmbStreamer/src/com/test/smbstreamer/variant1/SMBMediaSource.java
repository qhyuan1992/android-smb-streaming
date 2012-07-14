package com.test.smbstreamer.variant1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class SMBMediaSource extends StreamSource{

	private SmbFile file;
	private SmbFileInputStream in;
	public SMBMediaSource(SmbFile file, String mime) throws StreamSourceException {
		this.file = file;
		this.mime = mime;
		
		try {
			len = file.length();
		} catch (SmbException e) {
			throw new StreamSourceException(e);
		}
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		int read =  in.read(bytes);
		fp += read;
		return read;
	}

	@Override
	public long moveTo(long position) throws IOException {
		return in.skip(position);
	}

	@Override
	public void close() {
		try {
			//fp = 0;
			in.close();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void open() throws IOException {
		try {
			if(in!=null){
				in.close();
			}
			fp = 0;
			in = new SmbFileInputStream(file);
		} catch (SmbException e) {
			throw new IOException(e);
		} catch (MalformedURLException e) {
			throw new IOException(e);
		} catch (UnknownHostException e) {
			throw new IOException(e);
		}
	}


}
