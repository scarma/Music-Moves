package com.example.musicmoves;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UI4 extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	
	private String sessionName;
//	private String filepath = Environment.getExternalStorageDirectory().getPath()+"/MusicMoves";
	private TextView tPlaybackPosition;
	private TextView tDuration;
	private ProgressBar tBar;
    private Thread thread;
    public static boolean isStopped;
	public boolean isPlaying;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setVolumeControlStream(AudioManager.STREAM_MUSIC); //aumenta volume musica anche se in pausa
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui4);	
		// Get the message from the intent
		Intent intent = getIntent();
	    sessionName = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewSessionName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(sessionName);
	    PlayMusic(null);
	}
	
	@Override
	protected void onPause() {
	//TODO: Salvare lo stato
		super.onPause();
	}

	@Override
	protected void onResume() {
	//TODO: Ripristinare lo stato
		super.onResume();
//		Intent intent = getIntent();
//		boolean startedbyme=intent.getBooleanExtra("my", false);
//		if (startedbyme)
//			Toast.makeText(this, "Lanciato da me", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui4, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_details) {
			Intent intent = new Intent(getApplicationContext(), UI2.class);
			intent.putExtra(EXTRA_MESSAGE, sessionName);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_list) {
			Intent intent = new Intent(getApplicationContext(), UI1.class);
			startActivity(intent);
			finish();
			return true;
		}
		if (id == R.id.action_settings) {
			Intent intent = new Intent(getApplicationContext(), UI5.class);
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void PlayMusic(View view) { //// Play button: starts the playback music service 
		ImageButton play = (ImageButton) findViewById(R.id.playB);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedB);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedB);
		
		play.setVisibility(View.INVISIBLE);
		pause.setVisibility(View.VISIBLE);
		stopUns.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.VISIBLE);
		
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.PLAY, true); 
		i.putExtra(EXTRA_MESSAGE, sessionName);
		startService(i); 
		isStopped=false;
	    thread = new Thread(new Runnable() {
			public void run() {
				while (!isStopped) {
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								tPlaybackPosition = (TextView) findViewById(R.id.textViewPlaybackPosition);
								tDuration = (TextView) findViewById(R.id.textViewDuration);
								tBar = (ProgressBar) findViewById(R.id.progressBarMusic);
								int time = PlayerService.getTime();
								int current = 1000
										* PlayerService.audioX
												.getPlaybackHeadPosition()
										/ PlayerService.sampleRate;
								String curTime = intToTime(current / 1000);
								String totTime = intToTime(time);
								tPlaybackPosition.setText(curTime);
								tDuration.setText(totTime);
								tBar.setMax(time * 1000);
								tBar.setProgress(current);
								tBar.getProgressDrawable()
										.setColorFilter(
												Color.rgb(255, 209, 179),
												Mode.MULTIPLY);
							} catch (IllegalStateException e) {
								isStopped=true;
							}
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						isStopped = true;
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tPlaybackPosition.setText("00:00");
						tBar.setProgress(0);
					}
				});
			}
		});
		thread.start();
        isPlaying=true;
//		Context context = getApplicationContext();
//		super.onResume();   
		
	}
	
	
	public void PauseMusic(View view) { //Pause button
		ImageButton play = (ImageButton) findViewById(R.id.playB);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		pause.setVisibility(View.INVISIBLE);
		play.setVisibility(View.VISIBLE);
		super.onPause();
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.PAUSE, true); 
		startService(i); 
		isPlaying=false;
		
	}
	
	public void StopMusic(View view) { // Stop button: stops the music by stopping the service 
		ImageButton play = (ImageButton) findViewById(R.id.playB);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedB);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedB);
		
		play.setVisibility(View.VISIBLE);
		pause.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.INVISIBLE);
		stopUns.setVisibility(View.VISIBLE);
		
		isStopped=true;
		
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		stopService(i); 
		onBackPressed();
	}
	
	public String intToTime (int time){ 	
		 int seconds = time%60;
		 int minutes = ((time-seconds)/60)%60;
		 String t = String.format(Locale.getDefault(),"%02d%s%02d", minutes,":",seconds);
		 return t;
	   }

	public void onBackPressed() {
		//TODO: CHE ROBA è! SISTEMARE ON BACK PRESSED Grazie
//		String s = "my";
//		Intent intent = getIntent();
//		boolean back = intent.getBooleanExtra(s, false);
//		String back = getIntent().getExtras().getString("my");
		
//		Bundle extras=getIntent().getExtras();
//		boolean back = extras.getBoolean("my",false);
//		
//		if(back)
			super.onBackPressed();
//		else
//			startActivity(new Intent(UI4.this, UI1.class));
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.activity_ui4);
	  if(!isPlaying) {
		  ImageButton play = (ImageButton) findViewById(R.id.playB);
		  ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		  play.setVisibility(View.VISIBLE);
		  pause.setVisibility(View.INVISIBLE);
	  }
	//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewSessionName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(sessionName);
	  
	}
}
