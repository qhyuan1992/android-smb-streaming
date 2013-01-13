package com.test.smbstreamer;

import java.io.File;
import java.net.MalformedURLException;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.test.smbstreamer.variant1.Streamer;

public class MainActivity extends Activity {
	
	
	Streamer s;
	Config c = new ConfigKrzysiek();
	NtlmPasswordAuthentication auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = new NtlmPasswordAuthentication("", c.username, c.password);
        findViewById(R.id.launch).setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				launch();
			}
        });
    }
    
    private void launch(){
		s = Streamer.getInstance();
		new Thread(){
			public void run(){
				try{
					SmbFile file = new SmbFile(c.path, auth);
					s.setStreamSrc(file, null);//the second argument can be a list of subtitle files
					runOnUiThread(new Runnable(){
						public void run(){
							try{
								Uri uri = Uri.parse(Streamer.URL + Uri.fromFile(new File(Uri.parse(c.path).getPath())).getEncodedPath());
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setDataAndType(uri, c.mime);
								startActivity(i);
							}catch (ActivityNotFoundException e){
								e.printStackTrace();
							}
						}
					});
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
