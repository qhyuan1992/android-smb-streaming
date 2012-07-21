package com.test.smbstreamer.variant1;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import android.util.Log;

public class Streamer extends StreamServer {

	public static final int PORT = 8888;
	public static final String URL = "http://127.0.0.1:" + PORT;
	private StreamSource source;
	//private InputStream stream;
	//private long length;
	private static Streamer instance;
	//private CBItem source;
	//private String mime;

	protected Streamer(int port) throws IOException {
		super(port, new File("/"));
	}

	public static Streamer getInstance() {
		if (instance == null)
			try {
				instance = new Streamer(PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		return instance;
	}
	
	public static boolean isStreamMedia(String name){
		return name.matches("^.*\\.(?i)(mp3|mp4|avi|3gp|3gpp)$");
	}


	public void setStreamSrc(StreamSource src) {
		this.source = src;
	}

	
	@Override
	public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
		Response res = null;
		try
		{
			if(!uri.endsWith(source.getName()))
				res = new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, null);
			else{
				long startFrom = 0;
				long endAt = -1;
				String range = header.getProperty( "range" );
				if ( range != null )
				{
					if ( range.startsWith( "bytes=" ))
					{
						range = range.substring( "bytes=".length());
						int minus = range.indexOf( '-' );
						try {
							if ( minus > 0 )
							{
								startFrom = Long.parseLong( range.substring( 0, minus ));
								endAt = Long.parseLong( range.substring( minus+1 ));
							}
						}
						catch ( NumberFormatException nfe ) {}
					}
				}
				Log.d("Explorer", "Request: "+range+" from: "+startFrom+", to: "+endAt);

				// Change return code and add Content-Range header when skipping is requested
				source.open();
				long fileLen = source.length();
				if (range != null && startFrom >= 0)
				{
					if ( startFrom >= fileLen)
					{
						res = new Response( HTTP_RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, null );
						res.addHeader( "Content-Range", "bytes 0-0/" + fileLen);
					}
					else
					{
						if ( endAt < 0 )
							endAt = fileLen-1;
						long newLen = fileLen - startFrom;
						if ( newLen < 0 ) newLen = 0;
						Log.d("Explorer", "start="+startFrom+", endAt="+endAt+", newLen="+newLen);
						final long dataLen = newLen;
						source.moveTo( startFrom );
						Log.d("Explorer", "Skipped "+startFrom+" bytes");

						res = new Response( HTTP_PARTIALCONTENT, source.getMimeType(), source );
						res.addHeader( "Content-length", "" + dataLen);
					}
				}
				else
				{
					res = new Response( HTTP_OK, source.getMimeType(), source);
					res.addHeader( "Content-Length", "" + fileLen);
				}
			}
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
			res = new Response( HTTP_FORBIDDEN, MIME_PLAINTEXT, null );
		}

		res.addHeader( "Accept-Ranges", "bytes"); // Announce that the file server accepts partial content requestes
		return res;
	}

}
