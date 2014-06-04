package com.example.musicmoves;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerService extends Service {
	

	 public static String PLAY = "BGPlay";
	 public static String PAUSE = "BGPause";
	 public static String STOP = "BGStop"; // Not used 
	 private String sessionName ="";
	 private boolean initialized = false;
//	 private MediaPlayer myPlayer = null; 
	 private boolean isPlaying = false; 
//	 private FileInputStream fis;
	 @Override 
	 public IBinder onBind(Intent intent) 
	 { 
	 return null; // Clients can not bind to this service 
	 } 
	
	 @Override 
	 public int onStartCommand(Intent intent, int flags, int startId) 
	 { 
 	
	 if(intent.getBooleanExtra(PLAY, false)) 
	 	{	String message = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		 	if(!sessionName.equals(message))
	 			{ stop();}
		 	sessionName = message;
	 	  	play(); }
	 if(intent.getBooleanExtra(PAUSE, false)) pause();
	 return Service.START_STICKY;
	 
	 } 
	 
	
	 private void pause() {
		if (!isPlaying) return;
		isPlaying = false;
//		if (myPlayer != null)	
		if (audioX.getState()==AudioTrack.STATE_INITIALIZED &&
		   	audioY.getState()==AudioTrack.STATE_INITIALIZED &&
		   	audioZ.getState()==AudioTrack.STATE_INITIALIZED){
			        	audioX.pause();
			        	audioY.pause();
			        	audioZ.pause();	
	    }   
	    else {
		    Log.d("AudioTrack", "Audiotrack not initialized");
	    }
	}

	private void play() {
		
		//Fa riprodurre il file su sd chiamato "Tria.mp3"
		if(isPlaying) return; 
		isPlaying = true; 
//		 if (myPlayer != null)	myPlayer.start();
//		 else{	
//		 try {
		if(!initialized)	 {
			proSoundGenerator(Environment.getExternalStorageDirectory().getPath()+"/MusicMoves", sessionName);
		}
//					 FileDescriptor fd = null;
//					 File baseDir = Environment.getExternalStorageDirectory();
//				     String audioPath = baseDir.getAbsolutePath() + "/Tria" + ".mp3";
//				     
				     if(audioX.getState()==AudioTrack.STATE_INITIALIZED &&
				    	audioY.getState()==AudioTrack.STATE_INITIALIZED &&
				    	audioZ.getState()==AudioTrack.STATE_INITIALIZED){
				        audioX.play();
				        audioY.play();
				        audioZ.play();	
				        initialized = true;
				        
				     }
				     else {
						    Log.d("AudioTrack", "Audiotrack not initialized");
					    }
//					 fis = new FileInputStream(audioPath);	
//				     fd = fis.getFD();
//				     
				//	 myPlayer = MediaPlayer.create(this, R.raw.doowackadoo); 
//				     myPlayer = new MediaPlayer();
//					 myPlayer.setDataSource();
//					 myPlayer.prepare();
//					 myPlayer.setLooping(true); 
//					 myPlayer.start(); 
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IllegalArgumentException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IllegalStateException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//		   	   }
				 // Runs this service in the foreground, 
				 // supplying the ongoing notification to be shown to the user 
				 Intent intent = new Intent(this, UI4.class); 
				 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); 
				 PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0); 
				 Notification notification = new NotificationCompat.Builder(getApplicationContext()) 
				 .setContentTitle("MusicMoves") 
				 .setContentText(sessionName) 
				 .setSmallIcon(R.drawable.ic_launcher) 
				 .setContentIntent(pi) // Required on Gingerbread and below 
				 .build(); 
				 final int notificationID = 5786423; // An ID for this notification unique within the app 
				 startForeground(notificationID, notification); 
				 
	}
	
	 private void stop() { 
//		 if (isPlaying) { 
		 	isPlaying = false; 
		 	initialized = false;
			try{if (audioX.getState()==AudioTrack.STATE_INITIALIZED &&
		    	audioY.getState()==AudioTrack.STATE_INITIALIZED &&
		    	audioZ.getState()==AudioTrack.STATE_INITIALIZED){
	        	audioX.stop();
	        	audioY.stop();
	        	audioZ.stop();	
	        	audioX.release();
	        	audioY.release();
	        	audioZ.release();	
	        	audioX.flush();
	        	audioY.flush();
	        	audioZ.flush();	}  } 
	        catch (NullPointerException e){ 
			    Log.d("AudioTrack", "Audiotrack not initialized");
			    return;
		    }
			 
//			 if (myPlayer != null) { 
//					 myPlayer.release(); 
//				   	 myPlayer = null; 
//				   	 try {
//						fis.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			 } 
			 stopForeground(true); 
//		 } 
	 } 
	
	 @Override 
	 public void onDestroy() { stop(); } 
	 
	 
	 
	 /*-- MUSIC generation --*/
	 static AudioTrack audioX;
	 static AudioTrack audioY;
	 static AudioTrack audioZ;
	 
		public void proSoundGenerator(String filepath, String textFile) {//Legge file come stringa e modifica dato accel
			String line="";									 			//aggiungendo una certa frequenza
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
	        	audioX = playSound(genTone(x,cnt)); //Genera suono per l'asse x
	        	audioY = playSound(genTone(y,cnt)); //Genera suono per l'asse y
	        	audioZ = playSound(genTone(z,cnt)); //Genera suono per l'asse z
	        	in.close();
	        } catch (FileNotFoundException e) {
	        	Log.d("FileNotFoundException", "File:"+filepath+"/"+textFile);
	        } catch (IOException e) {
	        
	        } 
	      
	    }
		
//		public int getDuration()	{return duration;}
//		public int getSampleRate()	{return sampleRate;}
//		public double getFreqOfTone()	{return freqOfTone;}
//		public void setDuration(int dur)	{if(dur>=1 && dur<=100) duration=dur; else duration=3;}
//		public void setSampleRate(int sampleR)	{if(sampleR>=4000 && sampleR<=10000) sampleRate=sampleR; else sampleRate=8000;}
//		public void setFreq(int freq)	{if(freq>=200 && freq<=3000) freqOfTone=freq; else freqOfTone=440;}
//	    private int duration = 3; // seconds
//	   
//	    private double sample[] = new double[numSamples];
	    private double freqOfTone; // hz //200-3000 range consigliato
	    static int sampleRate = 8000;
	    private int upsampling = 200;
	    Handler handler = new Handler(); 
	    private byte[] generatedArray;
	    private static int time;
	    static int numSamples;
	    
	    public byte[] genTone(double[] x, int cnt){
	        // fill out the array
	    	numSamples = 10*cnt*upsampling;
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
	    
		synchronized AudioTrack playSound(byte[] generatedSnd){
			generatedArray = generatedSnd;
			AudioTrack audioTrack;
//			Thread thread = new Thread(new Runnable() {
//		        public void run() {
			        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
			                sampleRate, AudioFormat.CHANNEL_OUT_MONO ,
			                AudioFormat.ENCODING_PCM_16BIT, generatedArray.length,
			                AudioTrack.MODE_STATIC);
			        audioTrack.write(generatedArray, 0, generatedArray.length);
				    audioTrack.setLoopPoints(0, numSamples, -1);
				    setTime((numSamples)/sampleRate);
//				    Toast.makeText(getApplicationContext(), "Maxvol:"+AudioTrack.getMaxVolume(), Toast.LENGTH_SHORT).show();
//			        if(audioTrack.getState()==AudioTrack.STATE_INITIALIZED){
//			        	audioTrack.play();	
//			        }   
//			        else {
//					    Log.d("AudioTrack", "Audiotrack not initialized");
//				    }
//				}
//			});
//			thread.start();
	        return audioTrack;
		}

		public static int getTime() {
			
			return time;
		}

		public void setTime(int time) {
			PlayerService.time = time;
		}
}