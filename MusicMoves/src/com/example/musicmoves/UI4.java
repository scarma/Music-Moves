package com.example.musicmoves;

import java.util.Locale;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UI4 extends ActionBarActivity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	
	private String sessionName;
//	private String filepath = Environment.getExternalStorageDirectory().getPath()+"/MusicMoves";
	private TextView tPlaybackPosition;
	private TextView tDuration;
	private ProgressBar tBar;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setVolumeControlStream(AudioManager.STREAM_MUSIC); //aumenta volume musica anche se in pausa
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui4);	
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
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Get the message from the intent
		Intent intent = getIntent();
	    sessionName = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewSessionName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(sessionName);
//	    proSoundGenerator(filepath, sessionName);
	    PlayMusic(null);
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
	    Thread thread = new Thread(new Runnable() {
	        public void run() {
	        	 while(!Thread.currentThread().isInterrupted()){
	                 try {runOnUiThread(new Runnable()  {
	                	        public void run() {
	                	            try{
	                	           	 tPlaybackPosition = (TextView) findViewById(R.id.textViewPlaybackPosition);
	                	           	 tDuration = (TextView) findViewById(R.id.textViewDuration);
	                	           	 tBar = (ProgressBar) findViewById(R.id.progressBarMusic);
	                	           	 int time= PlayerService.getTime();
	                	           	 int current=PlayerService.audioX.getPlaybackHeadPosition()/PlayerService.sampleRate;
	                	             String curTime = intToTime(current);
	           	                	 String totTime = intToTime(time);
	                				 tPlaybackPosition.setText(curTime);
	           	                	 tDuration.setText(totTime);
	           	                	 tBar.setMax(time);
	           	                	 tBar.setProgress(current);
	           	                	 tBar.getProgressDrawable().setColorFilter(Color.rgb(255, 209, 179), Mode.MULTIPLY);
	                	            }catch (IllegalStateException e) {Thread.currentThread().interrupt();}
	                	        }
	                	    });
	                	 Thread.sleep(1000);
	                 } 
	                 catch (Exception e) {
	                        Thread.currentThread().interrupt();
	                        Log.d("Thread", "Time thread interrupted");
	                 }
	        	 }
	        }
		});
		thread.start();
        
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
		
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		stopService(i); 
	}
	
	public String intToTime (int time){ 	
		 int seconds = time%60;
		 int minutes = ((time-seconds)/60)%60;
		 String t = String.format(Locale.getDefault(),"%02d%s%02d", minutes,":",seconds);
		 return t;
	   }

}
