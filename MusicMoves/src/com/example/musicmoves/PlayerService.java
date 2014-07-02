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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.PresetReverb;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import database.DBAdapter;

public class PlayerService extends Service {
	
	 private String message;
	 public static String PLAY = "BGPlay";
	 public static String PAUSE = "BGPause";
	 public static String STOP = "BGStop"; // Not used 
	 //aggiunto per plus
	 public static String ECHO = "BGEcho";
	 public static String VOLUME = "BGVolume";
	 public static String SPEED = "BGSpeed";
	 public static String DELAY = "BGDelay";
	 //
	 private String sessionName ="";
	 private boolean initialized = false;
	 private boolean isPlaying = false; 
	 private DBAdapter databaseHelper;
	 private Cursor cursor;
	 EnvironmentalReverb nulleffect, delay, echo;
	 @Override 
	 public IBinder onBind(Intent intent) 
	 { 
	 return null; // Clients can not bind to this service 
	 } 
	
	 @Override 
	 public int onStartCommand(Intent intent, int flags, int startId) 
	 { 
 	
	 if(intent.getBooleanExtra(PLAY, false)) 
	 	{	message = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		 	if(!sessionName.equals(message))
	 			{ stop();}
		 	sessionName = message;
	 	  	play(); }
	 if(intent.getBooleanExtra(PAUSE, false)) pause();
	
	 if(intent.getBooleanExtra(ECHO, false)){ echo();}
	 if(intent.getBooleanExtra(VOLUME, false)) {
		 boolean up = intent.getBooleanExtra("up", false);
		 double volume = intent.getDoubleExtra("volume", 0.0);
		 volume(up, volume);
	 }
	 if(intent.getBooleanExtra(SPEED, false)){
		 boolean up = intent.getBooleanExtra("up", false);
		 int intensity = intent.getIntExtra("intensity", -1);
		 speed(up, intensity);
		 }
	 
	 if(intent.getBooleanExtra(DELAY, false)){ delay();}
	 
	 return Service.START_STICKY;
	 } 
	 
	
	 private synchronized void pause() {
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
		
		if(isPlaying) return; 
		isPlaying = true; 
		
		if(!initialized)	 {
			proSoundGenerator(Environment.getExternalStorageDirectory().getPath()+"/MusicMoves", sessionName);
//			proSoundGenerator(getFilesDir().getAbsolutePath(), sessionName);
		}				     
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
		 // Runs this service in the foreground, 
		 // supplying the ongoing notification to be shown to the user 
		 Intent intent = new Intent(this, UI4.class);
		 intent.putExtra(UI1.EXTRA_MESSAGE, message);
//		 intent.putExtra("my", "false");//
		 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); 
		 PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); 
		 Notification notification = new NotificationCompat.Builder(getApplicationContext()) 
		 .setContentTitle("MusicMoves") 
		 .setContentText(sessionName) 
		 .setSmallIcon(R.drawable.ic_launcher) 
		 .setContentIntent(pi) // Required on Gingerbread and below 
		 .build(); 
		 final int notificationID = 7071727; //An ID for this notification unique within the app 
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
	        	audioZ.flush();
	        	nulleffect.release();
				delay.release();
				echo.release();}  } 
	        catch (NullPointerException e){ 
			    Log.d("AudioTrack", "Audiotrack not initialized");
			    return;
		    }
			 stopForeground(true);
	 } 
	
	 @Override 
	 public void onDestroy() { stop(); } 
	 
	 
	 
	 /*-- MUSIC generation --*/
	 static AudioTrack audioX;
	 static AudioTrack audioY;
	 static AudioTrack audioZ;
	 
		public void proSoundGenerator(String filepath, String textFile) {//Legge file come stringa e modifica dato accel
			databaseHelper = new DBAdapter(this);
			databaseHelper.open();
			cursor = databaseHelper.fetchSessionByFilter(message);
			cursor.moveToFirst();
		    upsampling = cursor.getInt(6);
		    int UseX = cursor.getInt(7);
		    int UseY = cursor.getInt(8);
		    int UseZ = cursor.getInt(9);
		    databaseHelper.close();
			cursor.close();
			
			String line="";									 			//aggiungendo una certa frequenza
	        double[] x;
	        double[] y;
	        double[] z;
	        int cnt = 0;
	        try{
	        	BufferedReader in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
	        	while ((line = in.readLine()) != null)
	        	{cnt++;}
	        	if (cnt==0){cnt++;}//se file vuoto
	        	x = new double[cnt];
	        	y = new double[cnt];
	        	z = new double[cnt];
	        	in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
	        	for(int i=0; i<cnt; i++)
	        		{
	        		line = in.readLine();
	        		String[] coord = line.split(",");
	        		if (UseX == 1)
	        			x[i] = (Double.parseDouble(coord[0])*10) + 440.0 ; //aggiunge freq La4 ai dati dell'asse x
	        		else x[i] = 0.0;
	        		if (UseY == 1)
	        			y[i] = (Double.parseDouble(coord[1])*10) + 698.0; //aggiunge freq Fa5 ai dati dell'asse y
	        		else y[i] =	 0.0;
	        		if (UseZ == 1)	
	        			z[i] = (Double.parseDouble(coord[2])*10) + 880.0; //aggiunge freq La5 ai dati dell'asse z
	        		else z[i] =	 0.0;
	        	}
	        	
	        	audioX = playSound(genTone(x,cnt)); //Genera suono per l'asse x
	        	audioY = playSound(genTone(y,cnt)); //Genera suono per l'asse y
	        	audioZ = playSound(genTone(z,cnt)); //Genera suono per l'asse z
	        	in.close();
	        } catch (FileNotFoundException e) {
	        	Log.d("FileNotFoundException", "File:"+filepath+"/"+textFile);
	        } catch (IOException e) {
	        	Log.d("IOException", e.getMessage());
	        } catch (NullPointerException e) {
	        	Log.d("NullPointerException", "File empty");
	        } catch (NumberFormatException e) {
	        	Log.d("NumberFormatException", "File not valid");
	        } 
	    }
		
//		public int getDuration()	{return duration;}
//		public int getSampleRate()	{return sampleRate;}
//		public double getFreqOfTone()	{return freqOfTone;}
//		public void setDuration(int dur)	{if(dur>=1 && dur<=100) duration=dur; else duration=3;}
//		public void setSampleRate(int sampleR)	{if(sampleR>=4000 && sampleR<=10000) sampleRate=sampleR; else sampleRate=8000;}
//		public void setFreq(int freq)	{if(freq>=200 && freq<=3000) freqOfTone=freq; else freqOfTone=440;}
	    private int maxduration = 100; // secondi
//	   
//	    private double sample[] = new double[numSamples];
	    private double freqOfTone; // hz //200-3000 range consigliato
	    static int sampleRate = 8000;
	    public int upsampling = 200;
	    Handler handler = new Handler(); 
	    private byte[] generatedArray;
	    private static int time;
	    static int numSamples;
	   // private int amplitude=1; //ampiezza del file audio. Se >1 distorge (clipping). Dev'essere compreso tra 0 e 1
	    private double amplitude=0.5;
	    public byte[] genTone(double[] x, int cnt){
	        // fill out the array
	    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    	maxduration = preferences.getInt("maxPlayTime", 10);
	    	numSamples = 10*cnt*upsampling;
	        if(numSamples > maxduration*sampleRate)
	        	{numSamples = maxduration*sampleRate;}
	    	double sample[] = new double[numSamples];
	    	for (int i = 0; i < (numSamples); ++i) { 
	        	if ((i%(10*upsampling))==0) //inserisce dati accelerometro nell'array
	        		{ freqOfTone = x[i/(10*upsampling)];}
	            sample[i] = amplitude*Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
	        }
	    	byte generatedSnd[] = new byte[2 * numSamples];
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
		
		
		synchronized void echo(){
			Toast.makeText(getApplicationContext(), "Echo on single click", Toast.LENGTH_SHORT).show();
			if (isPlaying == true){
				
				
				nulleffect= new EnvironmentalReverb(0, 0); //effetto che non contiene nessuna modifica
				nulleffect.setEnabled(true);
				audioX.attachAuxEffect(nulleffect.getId()); //toglie eventuali effetti aggiunti in precedenza
				audioY.attachAuxEffect(nulleffect.getId());	//aggiungendo ad ogni audiotrack un effetto nullo
				audioZ.attachAuxEffect(nulleffect.getId());	//come suggerito dalla documentazione
				
//				PresetReverb mReverb = new PresetReverb(1,audioX.getAudioSessionId());//<<<<<<<<<<<<<
//			    mReverb.setPreset(PresetReverb.PRESET_LARGEROOM);
//			    mReverb.setEnabled(true);
//			    audioX.attachAuxEffect(mReverb.getId());
//			    audioX.setAuxEffectSendLevel(1.0f);
			    
				echo = new EnvironmentalReverb(1, audioX.getAudioSessionId());
				
				  	echo.setDecayHFRatio((short) 1000);
		            echo.setDecayTime(2000);
		            echo.setDensity((short) 500);
		            echo.setDiffusion((short) 700);
		            echo.setReflectionsLevel((short) 1000);
		            echo.setReverbLevel((short) 2000);
		            echo.setReflectionsDelay(100);
		            echo.setRoomLevel((short) -10);
		            echo.setEnabled(true);
		            
		           // Toast.makeText(getApplicationContext(), ""+echo.setEnabled(true), Toast.LENGTH_SHORT).show();
		            //in realta' il toast restituisce 0 a dimostrazione che lo attacca effettivamente. pero non sisentono differenze 
		            
				if (audioX.STATE_INITIALIZED==1) {
					audioX.attachAuxEffect(echo.getId());
					audioX.setAuxEffectSendLevel(1.0f);
					Toast.makeText(getApplicationContext(), "inizializzata " + audioX.setAuxEffectSendLevel(1.0f), Toast.LENGTH_SHORT).show();
					
				}
//				if (audioY.STATE_INITIALIZED==1){
//					audioY.attachAuxEffect(echo.getId());
//					audioY.setAuxEffectSendLevel(1.0f);
//					
//				}
//				if (audioZ.STATE_INITIALIZED==1){
//					audioZ.attachAuxEffect(echo.getId());
//					audioZ.setAuxEffectSendLevel(1.0f);
//					
//				}
				/* questi metodi fermano la riproduzione, quello che io definisco drop dell'audiotrack
				echo.release();
				audioX.release();
				audioY.release();
				audioZ.release();
				*/
				Thread timer = new Thread(){
			        @Override
			        public void run() {

			            try {
			                sleep(5000);//tempo in millisecondi
			            } catch (InterruptedException e) {
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            } finally{
			                echo.release();
			            }
			            super.run();
			        }
			    };
			    timer.start();
			    
				
			}
	}
		
		int maxintensity = 600;
		int posX, posY, posZ;
		synchronized void volume(boolean up, double intensity){


			if(isPlaying == true){
				
//			Bisogna: modificare la variabile amplitude, 
//			ottenere posizione riproduzione, stoppare audiotrack
//			ricreare le audiotrack(basta chiamare proSoundGenerator) 
//			e impostare la riproduzione a dov'era arrivato(probabilmente sar� necessario modificare
//			leggermente proSoundGenerator per far questo),
			
//			amplitude modificata da int 1 a double 0.5
			
			String vol;
			if(up) vol = "Up touch strenght: ";
			else vol = "Down touch strenght: ";
			//variamo amplitude di un valore 
			
			if (up==true){
				amplitude = amplitude + intensity/maxintensity;
				if(amplitude >10)amplitude=10; //valore max 1
			}
			else {
				amplitude = amplitude - intensity/maxintensity;
				if(amplitude<0) amplitude=0; //valore min 0
			}
			Toast.makeText(getApplicationContext(), vol + (int)(100*intensity)/maxintensity+"% , Amplitude: "+amplitude, Toast.LENGTH_SHORT).show();
			
			//salvo la posizione degli audiotrack
			posX = audioX.getPlaybackHeadPosition();
			posY = audioY.getPlaybackHeadPosition();
			posZ = audioZ.getPlaybackHeadPosition();
			//rilascio le audiotrack per farne di nuove
			audioX.flush();
			audioY.flush();
			audioZ.flush();
			
			proSoundGenerator(Environment.getExternalStorageDirectory().getPath()+"/MusicMoves", sessionName);
			//ripartiamio da dove li abbiamo lasciati
			audioX.setPlaybackHeadPosition(posX);
			audioY.setPlaybackHeadPosition(posY);
			audioZ.setPlaybackHeadPosition(posZ);
			 audioX.play();
			 audioY.play();
			 audioZ.play();
			
			}//fine isplaying==true
		}//fine volume
		
		
		
		synchronized void speed(boolean direction, int quantity){
			

			if(isPlaying == true){
//			Stessa cosa che per il metodo volume. Solo che al posto di amplitude modificare sampleRate
//			Oppure provate a usare setPlaybackRate(int sampleRateInHz) di AudioTrack
			int maxrate, minrate, actualrate;
			maxrate=12000;
			minrate=4000;
			actualrate = audioX.getPlaybackRate();
			
			if (actualrate <= maxrate && direction == true){
				if(actualrate + quantity > maxrate){ //se andiamo oltre i 12000, ci fremiamo a 12000
					actualrate=0;
					quantity=maxrate;
				}
				sampleRate=(actualrate + quantity);
			}
			
			if (actualrate >=minrate && direction == false){ // se andiamo sotto i 2000, ci fermiamo a 2000
				if(actualrate-quantity < minrate){
					actualrate=minrate;
					quantity=0;
				}
				sampleRate=(actualrate - quantity);
			}
			
			Toast.makeText(getApplicationContext(), "New sample rate: " + sampleRate, Toast.LENGTH_SHORT).show();
			}
			//salvo la posizione degli audiotrack
			posX = audioX.getPlaybackHeadPosition();
			posY = audioY.getPlaybackHeadPosition();
			posZ = audioZ.getPlaybackHeadPosition();
			//rilascio le audiotrack per farne di nuove
			audioX.flush();
			audioY.flush();
			audioZ.flush();
			
			proSoundGenerator(Environment.getExternalStorageDirectory().getPath()+"/MusicMoves", sessionName);
			//ripartiamio da dove li abbiamo lasciati
			audioX.setPlaybackHeadPosition(posX);
			audioY.setPlaybackHeadPosition(posY);
			audioZ.setPlaybackHeadPosition(posZ);
			 audioX.play();
			 audioY.play();
			 audioZ.play();
		}
		
		public boolean isDelaying = false;
		
synchronized void delay(){
			
			if (isPlaying == true){
					
				
				
					if (isDelaying == false){
				Toast.makeText(getApplicationContext(), "Delay on", Toast.LENGTH_SHORT).show();
				
					isDelaying = true;
					
					
		    
					//correggere i parametri in modo che risulti il delay che vogliamo nelle specifiche
					delay = new EnvironmentalReverb(1, audioX.getAudioSessionId());
//					 	delay.setDecayHFRatio((short) 1000);
//					  	delay.setDensity((short) 500);
//					  	delay.setDiffusion((short) 700);
					  	delay.setReverbLevel((short) 1000);
					  	delay.setRoomLevel ((short)-10);
					  	delay.setRoomHFLevel((short) -10);
					  	delay.setReverbDelay (100);
					  	delay.setEnabled(true);
					 // Toast.makeText(getApplicationContext(), ""+echo.setEnabled(true), Toast.LENGTH_SHORT).show();
			            //in realta' il toast restituisce 0 a dimostrazione che lo attacca effettivamente. pero non sisentono differenze 
			            
					if (audioX.STATE_INITIALIZED==1) {
						audioX.attachAuxEffect(delay.getId());
						audioX.setAuxEffectSendLevel(1.0f);
						Toast.makeText(getApplicationContext(), "inizializzata " + audioX.setAuxEffectSendLevel(1.0f), Toast.LENGTH_SHORT).show();
						
					}
//					if (audioY.STATE_INITIALIZED==1){
//						audioY.attachAuxEffect(delay.getId());
//						audioY.setAuxEffectSendLevel(1.0f);
//						
//					}
//					if (audioZ.STATE_INITIALIZED==1){
//						audioZ.attachAuxEffect(delay.getId());
//						audioZ.setAuxEffectSendLevel(1.0f);
//						
//					}
					/* questi metodi fermano la riproduzione, quello che io definisco drop dell'audiotrack
					echo.release();
					audioX.release();
					audioY.release();
					audioZ.release();
					*/
					
				}//fine isdelaying == false
					
					
				else {
					Toast.makeText(getApplicationContext(), "Delay off", Toast.LENGTH_SHORT).show();
//					nulleffect= new EnvironmentalReverb(0, 0); //effetto che non contiene nessuna modifica
//					nulleffect.setEnabled(true);
//					audioX.attachAuxEffect(nulleffect.getId()); //toglie eventuali effetti aggiunti in precedenza
//					audioY.attachAuxEffect(nulleffect.getId());	//aggiungendo ad ogni audiotrack un effetto nullo
//					audioZ.attachAuxEffect(nulleffect.getId());	//come suggerito dalla documentazione
					delay.release();
					isDelaying = false;
					
				}//fine else
					
			}//fine isplaying
		}//fine delay
		



		
		
}//fine class




/*
 * gestire l'esaurimento dello spazio di memoria
 * bisogna catturare l'eccezione lanciata da file stream
 * l'idea e':catturare, salvare tutto, lanciare un messaggio all'utente e forzare larresto dell'input
 */
