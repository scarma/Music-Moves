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
import android.os.Environment;
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
	 public static String ECHO = "BGEcho";
	 public static String VOLUME = "BGVolume";
	 public static String SPEED = "BGSpeed";
	 public static String DELAY = "BGDelay";
	 
	 private String sessionName = "";
	 private boolean initialized = false;
	 private boolean isPlaying = false;
	 private boolean isDelaying = false;
	 private DBAdapter databaseHelper;
	 private Cursor cursor;
	 EnvironmentalReverb delay, echo; //Effetti audio che vengono applicati alle audiotrack
	 private byte[] genArrayX, genArrayY, genArrayZ; //Array "sonori" dei 3 assi x, y, z
	 static AudioTrack audioXe, audioYe, audioZe, audioXd, audioYd, audioZd; //Tracce audio per gli effetti echo e delay
	 Thread timer, timer1, timer2; //Timer per gli effetti echo e delay
	 private int maxduration = 100; // Massima durata del file audio consentita in secondi
     private double freqOfTone; // Frequenza in Hz
     static int sampleRate = 8000; //Samplerate dell'audiotrack
     public int upsampling = 200; //Numero di volte che viene riutilizzato un dato dell'accelerometro
     private byte[] generatedArray; //Array "sonoro" generato dai dati dell'accelerometro
     private static int time; //Durata totale della traccia audio
     static int numSamples; //Numero di campioni totali per 8 bit dell'array
     private double amplitude = 0.5;	 //Ampiezza del file audio. Se >1 distorge (clipping).
    								 //Abbiamo volontariamente consentito la distorsione
     int maxintensity = 600; //Valore massimo di intensità di un tocco nell'area dell'immagine
	 int posX, posY, posZ; //Posizione della riproduzione

	 Toast toast;
	 @Override 
	 public IBinder onBind(Intent intent) { 
		 return null; // I Client non possono fare il bind a questo service 
	 } 
	
	 @Override 
	 public int onStartCommand(Intent intent, int flags, int startId) { 
		 //In base al tipo di intent che arriva faccio azioni diverse
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
		
	    }   else {
		    Log.d("AudioTrack", "Audiotrack not initialized");
	    } 
		try{
			if (audioXe.getState()==AudioTrack.STATE_INITIALIZED &&
		    	audioYe.getState()==AudioTrack.STATE_INITIALIZED &&
		    	audioZe.getState()==AudioTrack.STATE_INITIALIZED){
			audioXe.pause();
			audioYe.pause();
			audioZe.pause();
//			timer1.interrupt();
			
		}
		if (audioXd.getState()==AudioTrack.STATE_INITIALIZED &&
			   	audioYd.getState()==AudioTrack.STATE_INITIALIZED &&
			   	audioZd.getState()==AudioTrack.STATE_INITIALIZED){
			audioXd.pause();
			audioYd.pause();
			audioZd.pause();
	//        	nulleffect.release();
				
//				timer.interrupt();
			
		    } 
		}
		catch(NullPointerException e){
			//Gli effetti non sono attivi, non serve metterli in pausa

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
				}
				if (audioXe.getState()==AudioTrack.STATE_INITIALIZED &&
			    	audioYe.getState()==AudioTrack.STATE_INITIALIZED &&
			    	audioZe.getState()==AudioTrack.STATE_INITIALIZED){
		        	audioXe.stop();
		        	audioYe.stop();
		        	audioZe.stop();
		        	audioXe.release();
		        	audioYe.release();
		        	audioZe.release();	
		        	audioXe.flush();
		        	audioYe.flush();
		        	audioZe.flush();
					echo.release();
					}  
				if (audioXd.getState()==AudioTrack.STATE_INITIALIZED &&
				    	audioYd.getState()==AudioTrack.STATE_INITIALIZED &&
				    	audioZd.getState()==AudioTrack.STATE_INITIALIZED){
		        	audioXd.stop();
		        	audioYd.stop();
		        	audioZd.stop();
		        	audioXd.release();
		        	audioYd.release();
		        	audioZd.release();	
		        	audioXd.flush();
		        	audioYd.flush();
		        	audioZd.flush();
					delay.release();
				} 
			}//Fine try
			
	        catch (NullPointerException e){ 
			    Log.d("AudioTrack", "Audiotrack not initialized");
			    return;
		    }
			 stopForeground(true);
	 } 
	
	 @Override 
	 public void onDestroy() { stop(); } 
	 
	 static AudioTrack audioX;
	 static AudioTrack audioY;
	 static AudioTrack audioZ;
	 
	 public void proSoundGenerator(String filepath, String textFile) {
		//A partire dal file di testo arrivo a creare le 3 audiotrack per i 3 assi x,y,z
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
		
		String line="";		
        double[] x;
        double[] y;
        double[] z;
        int cnt = 0;
        
        try{
        	BufferedReader in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
        	while ((line = in.readLine()) != null) //Leggo tutto il file di testo
        	{cnt++;}
        	if (cnt==0){cnt++;}//Se il file è vuoto
        	x = new double[cnt]; //Inizializzo gli array in base al numero di righe del file di testo
        	y = new double[cnt];
        	z = new double[cnt];
        	in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
        	for(int i=0; i<cnt; i++) //Rileggo il file
        		{	
        		line = in.readLine();
        		String[] coord = line.split(","); //Uso la virgola per separare i 3 valori di x,y,z in una riga
        		if (UseX == 1)
        			x[i] = (Double.parseDouble(coord[0])*10) + 440.0 ; //Aggiunge freq La4 ai dati dell'asse x e lo mette nell'array
        		else x[i] = 0.0;
        		if (UseY == 1)
        			y[i] = (Double.parseDouble(coord[1])*10) + 698.0; //Aggiunge freq Fa5 ai dati dell'asse y e lo mette nell'array
        		else y[i] =	 0.0;
        		if (UseZ == 1)	
        			z[i] = (Double.parseDouble(coord[2])*10) + 880.0; //Aggiunge freq La5 ai dati dell'asse z e lo mette nell'array
        		else z[i] =	 0.0;
        	}
        	genArrayX=genTone(x,cnt);
        	genArrayY=genTone(y,cnt);
        	genArrayZ=genTone(z,cnt);
        	audioX = playSound(genArrayX); //Genera suono per l'asse x
        	audioY = playSound(genArrayY); //Genera suono per l'asse y
        	audioZ = playSound(genArrayZ); //Genera suono per l'asse z
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
        // Converto ad un array "sonoro" a 16 bit
        int index = 0;
        for (final double dVal : sample) {
            // Scalo alla massima ampiezza
            final short val = (short) ((dVal * 32767));
            // Scrivo i 16 bit (2 byte) dell'array "sonoro"
            generatedSnd[index++] = (byte) (val & 0x00ff);
            generatedSnd[index++] = (byte) ((val & 0xff00) >>> 8);
        }
        return generatedSnd;
     }
    
	 synchronized AudioTrack playSound(byte[] generatedSnd){
		generatedArray = generatedSnd;
		AudioTrack audioTrack;
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	                sampleRate, AudioFormat.CHANNEL_OUT_MONO ,
	                AudioFormat.ENCODING_PCM_16BIT, generatedArray.length,
	                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedArray, 0, generatedArray.length);
	    audioTrack.setLoopPoints(0, numSamples, -1);
	    setTime((numSamples)/sampleRate); //Durata totale della traccia audio in secondi 
        return audioTrack;
	 }

	 public static int getTime() {
		return time;
	 }

	 public void setTime(int time) {
		PlayerService.time = time;
	 }
	
	 synchronized void echo(){
		if (isPlaying == true){
			showToast("Echo on");
		    if(echo!=null)
			{echo.release();}

			//Creo effetto audio per le tracce ritardate che vado a sovrapporre
			echo = new EnvironmentalReverb(1,0);
			  	echo.setDecayHFRatio((short) 1000);
	            echo.setDecayTime(2000);
	            echo.setDensity((short) 500);
	            echo.setDiffusion((short) 700);
	            echo.setReflectionsLevel((short) 1000);
	            echo.setReverbLevel((short) 2000);
	            echo.setReflectionsDelay(100);
	            echo.setRoomLevel((short) -10);
	            echo.setEnabled(true);
	            
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        	final int echotime = 1000+preferences.getInt("recho", 10);
        	final float iecho  = preferences.getInt("iecho", 10)/10;
        	
			timer1 = new Thread(){
		        @Override
		        public void run() {
		        	audioXe= playSound(genArrayX);
	            	audioYe= playSound(genArrayY);
	            	audioZe= playSound(genArrayZ);
		            try {
		            	audioXe.setPlaybackHeadPosition(audioX.getPlaybackHeadPosition());
		            	audioYe.setPlaybackHeadPosition(audioY.getPlaybackHeadPosition());
		            	audioZe.setPlaybackHeadPosition(audioZ.getPlaybackHeadPosition());
		            	audioXe.attachAuxEffect(echo.getId());
						audioXe.setAuxEffectSendLevel(iecho);
						audioYe.attachAuxEffect(echo.getId());
						audioYe.setAuxEffectSendLevel(iecho);
						audioZe.attachAuxEffect(echo.getId());
						audioZe.setAuxEffectSendLevel(iecho);
						audioXe.play();
		            	audioYe.play();
		            	audioZe.play();
		            	audioXe.pause();
		            	audioYe.pause();
		            	audioZe.pause();
						sleep(500);//Tempo in millisecondi di ritardo delle tracce
								   //per creare l'effetto echo
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            } finally{
		            	audioXe.play();	//Trascorso il tempo mando le tracce in riproduzi
		            	audioYe.play(); //(sovrapposte alle tracce audio "principali")
		            	audioZe.play();
		            }
		            super.run();
		        }
		    };
		    timer1.start();
		    timer2 = new Thread(){
		        @Override
		        public void run() {

		            try {
		                sleep(echotime);//Tempo in millisecondi a cui fermare l'echo (settabile tramite seekbar)
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            } finally{
		            	if (audioXe.getState()==AudioTrack.STATE_INITIALIZED &&
		    			    	audioYe.getState()==AudioTrack.STATE_INITIALIZED &&
		    			    	audioZe.getState()==AudioTrack.STATE_INITIALIZED){
		            	
		            	audioXe.stop();
			        	audioYe.stop();
			        	audioZe.stop();
			        	
						audioXe.release();
						audioYe.release();
						audioZe.release();
						echo.release();
						}
		            }
		            super.run();
		        }
		    };
		    timer2.start();
			
		}
	 }
	 
	 synchronized void volume(boolean up, double intensity){


		if(isPlaying == true){
			
//			Modifichiamo l'ampezza delle tracce audio(amplitude), 
//			otteniamo la posizione di riproduzione, rilasciamo le audiotrack
//			e le ricreiamo con l'ampiezza aggiornata (proSoundGenerator)
//			e impostiamo la riproduzione a dov'era arrivato
		
		//Variamo amplitude in base all'intensità del gesto e alla direzione 
		if (up==true){
			amplitude = amplitude + intensity/maxintensity;
			if(amplitude >10)amplitude=10; //valore max 1
		}
		else {
			amplitude = amplitude - intensity/maxintensity;
			if(amplitude<0) amplitude=0; //valore min 0
		}
		
		String vol;
		if(up) vol = "Up touch strenght: ";
		else vol = "Down touch strenght: ";
		showToast( vol + (int)(100*intensity)/maxintensity+"% , Amplitude: "+amplitude);
		
		//Salvo la posizione delle audiotrack
		posX = audioX.getPlaybackHeadPosition();
		posY = audioY.getPlaybackHeadPosition();
		posZ = audioZ.getPlaybackHeadPosition();
		
		//Rilascio le audiotrack per farne di nuove
		audioX.release();
		audioY.release();
		audioZ.release();
		
		//Ricreo le audiotrack
		proSoundGenerator(Environment.getExternalStorageDirectory().getPath()+"/MusicMoves", sessionName);
		
		//Facciamo ripartire le audiotrack da dove le abbiamo lasciate
		audioX.setPlaybackHeadPosition(posX);
		audioY.setPlaybackHeadPosition(posY);
		audioZ.setPlaybackHeadPosition(posZ);
		audioX.play();
		audioY.play();
		audioZ.play();
		
		}//fine isplaying==true
	 }//fine volume
	
	 synchronized void speed(boolean direction, int quantity){
//			Stessa cosa che per il metodo volume. Solo che al posto di amplitude modifico il sampleRate
		if(isPlaying == true){

		int maxrate, minrate, actualrate;
		maxrate=12000;
		minrate=4000;
		actualrate = audioX.getPlaybackRate();
		
		if (actualrate <= maxrate && direction == true){
			if(actualrate + quantity > maxrate){ // Se andiamo oltre un sampleRate di 12000, ci fremiamo a 12000
				actualrate=0;
				quantity=maxrate;
			}
			sampleRate=(actualrate + quantity);
		}
		
		if (actualrate >=minrate && direction == false){ 
			if(actualrate-quantity < minrate){	// Se andiamo sotto un sampleRate di 4000, ci fermiamo a 4000
				actualrate=minrate;
				quantity=0;
			}
			sampleRate=(actualrate - quantity);
		}
		
		showToast( "New sample rate: " + sampleRate);
		
		//Salvo la posizione delle audiotrack
		posX = audioX.getPlaybackHeadPosition();
		posY = audioY.getPlaybackHeadPosition();
		posZ = audioZ.getPlaybackHeadPosition();
		
		//Rilascio le audiotrack per farne di nuove
		audioX.release();
		audioY.release();
		audioZ.release();
		
		//Ricreo le audiotrack
		proSoundGenerator(Environment.getExternalStorageDirectory().getPath()+"/MusicMoves", sessionName);
		
		//Facciamo ripartire le audiotrack da dove le abbiamo lasciate
		audioX.setPlaybackHeadPosition(posX);
		audioY.setPlaybackHeadPosition(posY);
		audioZ.setPlaybackHeadPosition(posZ);
		audioX.play();
		audioY.play();
		audioZ.play();
		}
	 }
	
	 synchronized void delay(){

		if (isPlaying == true){
			
				if (isDelaying == false){
					isDelaying = true;
					showToast( "Delay on");
					
					//Creo effetto audio per le tracce ritardate che vado a sovrapporre
					delay = new EnvironmentalReverb(1, 0);
					  	delay.setReverbLevel((short) 1000);
					  	delay.setRoomLevel ((short)-10);
					  	delay.setRoomHFLevel((short) -10);
					  	delay.setReverbDelay (100);
					  	delay.setEnabled(true);
				 
	            	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
	            	final int delaytime = preferences.getInt("rdelay", 10);
	            	final float idelay  = preferences.getInt("idelay", 10)/10;
	            	
					timer = new Thread(){
				        @Override
				        public void run() {
				        	audioXd= playSound(genArrayX);	//Genero le audiotrack basate sullo stesso
			            	audioYd= playSound(genArrayY);	//array "sonoro" delle audiotrack "principali"
			            	audioZd= playSound(genArrayZ);

				            try {
				            	audioXd.setPlaybackHeadPosition(audioX.getPlaybackHeadPosition());
				            	audioYd.setPlaybackHeadPosition(audioY.getPlaybackHeadPosition());
				            	audioZd.setPlaybackHeadPosition(audioZ.getPlaybackHeadPosition());
				            	audioXd.attachAuxEffect(delay.getId());
								audioXd.setAuxEffectSendLevel(idelay);
								audioYd.attachAuxEffect(delay.getId());
								audioYd.setAuxEffectSendLevel(idelay);
								audioZd.attachAuxEffect(delay.getId());
								audioZd.setAuxEffectSendLevel(idelay);	//Aggiungo effetto alle audiotrack
								audioXd.play();
				            	audioYd.play();
				            	audioZd.play();
				            	audioXd.pause();
				            	audioYd.pause();
				            	audioZd.pause();	//Necessario per inizializzare le audiotrack
				                sleep(delaytime);//Tempo in millisecondi di ritardo tra la traccia principale
				                				 //e quella ritardata (settabile tramite seekbar)
				            } catch (InterruptedException e) {
				                e.printStackTrace();
				            } finally{
				            	if(isPlaying)
				            	{audioXd.play();
				            	audioYd.play();
				            	audioZd.play();
				            	}
				            }
				            
				            super.run();
				        }
				    };
				    timer.start();
				}//Fine isdelaying == false
				
				else {
					isDelaying = false;
					showToast( "Delay off");
					audioXd.release();
					audioYd.release();
					audioZd.release();
					delay.release();
				}
				
		}//Fine isplaying
	}//Fine delay
	
	 void showToast(String s){
		if(toast==null) {
			toast=Toast.makeText(this, s, Toast.LENGTH_SHORT);
		}
		toast.setText(s);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	 }	
	
}//Fine class
