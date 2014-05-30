package com.example.musicmoves;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UI4 extends ActionBarActivity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	
	private String sessionName;
	private String filepath = Environment.getExternalStorageDirectory().getPath()+"/MusicMoves";
	private TextView tPlaybackPosition;
	private TextView tDuration;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setVolumeControlStream(AudioManager.STREAM_MUSIC); //aumenta volume musica anche se in pausa
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui4);
		
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
		Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
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
	                 try {runOnUiThread(new Runnable() {
	                	        public void run() {
	                	            try{
	                	           	 tPlaybackPosition = (TextView) findViewById(R.id.textViewPlaybackPosition);
	                	           	 tDuration = (TextView) findViewById(R.id.textViewDuration);
	                	           	 int time= PlayerService.getTime();
	                	           	 int current=PlayerService.audioX.getPlaybackHeadPosition()/PlayerService.sampleRate;
	                	             String curTime = intToTime(current);
	           	                	 String totTime = intToTime(time);
	                				 tPlaybackPosition.setText(curTime);
	           	                	 tDuration.setText(totTime);
	                	            }catch (Exception e) {Log.d("Thread","Exception");}
	                	        }
	                	    });
	                	 Thread.sleep(1000);
	                 } 
	                 catch (InterruptedException e) {
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
		Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();	
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
		Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
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
	
	
	/*-- MUSIC generation --*/
	public void proSoundGenerator(String filepath, String textFile) {//Legge file come stringa e modifica dato accel
												 //aggiungendo una certa frequenza 
        String line="";
        double[] x;
        double[] y;
        double[] z;
        int cnt = 0;
        try{
        	BufferedReader in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
        	while ((line = in.readLine()) != null)
        	{cnt++;}
        	x = new double[cnt];
        	y = new double[cnt];
        	z = new double[cnt];
        	in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
        	for(int i=0; i<cnt; i++)
        		{
        		line = in.readLine();
        		String[] coord = line.split(",");
        		x[i] = (Double.parseDouble(coord[0])*10) + 440.0 ; //aggiunge freq La4 ai dati dell'asse x
        		y[i] = (Double.parseDouble(coord[1])*10) + 698.0; //aggiunge freq Fa5 ai dati dell'asse y
        		z[i] = (Double.parseDouble(coord[2])*10) + 880.0; //aggiunge freq La5 ai dati dell'asse z
        	}
        	playSound(genTone(x,cnt)); //Genera suono per l'asse x
        	playSound(genTone(y,cnt)); //Genera suono per l'asse y
        	playSound(genTone(z,cnt)); //Genera suono per l'asse z
        	in.close();
        } catch (FileNotFoundException e) {
          Log.d("FileNotFoundException", "File:"+filepath+"/"+textFile);
        } catch (IOException e) {
        
        } 
    }
	
//	public int getDuration()	{return duration;}
//	public int getSampleRate()	{return sampleRate;}
//	public double getFreqOfTone()	{return freqOfTone;}
//	public void setDuration(int dur)	{if(dur>=1 && dur<=100) duration=dur; else duration=3;}
//	public void setSampleRate(int sampleR)	{if(sampleR>=4000 && sampleR<=10000) sampleRate=sampleR; else sampleRate=8000;}
//	public void setFreq(int freq)	{if(freq>=200 && freq<=3000) freqOfTone=freq; else freqOfTone=440;}
//    private int duration = 3; // seconds
//    private int numSamples = duration * sampleRate;
//    private double sample[] = new double[numSamples];
    private double freqOfTone; // hz //200-3000 range consigliato
    private int sampleRate = 8000;
    private int upsampling = 200;
    Handler handler = new Handler(); 
    private byte[] generatedArray;
    
    public byte[] genTone(double[] x, int cnt){
        // fill out the array
    	int numSamples = 10*cnt*upsampling;
        double sample[] = new double[numSamples];
    	for (int i = 0; i < (10*cnt*upsampling); ++i) { 
        	if ((i%(10*upsampling))==0) //inserisce dati accelerometro nell'array
        		{ freqOfTone = x[i/(10*upsampling)];}
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }
    	byte generatedSnd[] = new byte[2 * 10*cnt*upsampling];
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return generatedSnd;
    }
    
	synchronized void playSound(byte[] generatedSnd){
		generatedArray = generatedSnd;
		Thread thread = new Thread(new Runnable() {
	        public void run() {
	        
	        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	                sampleRate, AudioFormat.CHANNEL_OUT_MONO ,
	                AudioFormat.ENCODING_PCM_16BIT, generatedArray.length,
	                AudioTrack.MODE_STATIC);
	        audioTrack.write(generatedArray, 0, generatedArray.length);
		       
	        if(audioTrack.getState()==AudioTrack.STATE_INITIALIZED){
	        	audioTrack.play();	
	        }
	        
		        
		        else{
			        Log.d("AudioTrack", "Audiotrack not initialized");
		        }   
			}
		});
		thread.start();
	}
	

 public String intToTime (int time){ 	
	 int seconds = time%60;
	 int minutes = ((time-seconds)/60)%60;
	 String t = String.format("%02d%s%02d", minutes,":",seconds);
	 return t;
   }

	

}
