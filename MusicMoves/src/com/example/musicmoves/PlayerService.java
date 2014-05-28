package com.example.musicmoves;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class PlayerService extends Service {
	

	 public static String PLAY_START = "BGPlayStart"; 
	 public static String PLAY_STOP = "BGPlayStop"; // Not used 
	
	 private MediaPlayer myPlayer = null; 
	 private boolean isPlaying = false; 
	 private FileInputStream fis;
	 @Override 
	 public IBinder onBind(Intent intent) 
	 { 
	 return null; // Clients can not bind to this service 
	 } 
	
	 @Override 
	 public int onStartCommand(Intent intent, int flags, int startId) 
	 { 
	 if(intent.getBooleanExtra(PLAY_START, false)) play(); 
	 return Service.START_STICKY; 
	 } 
	
	 private void play() { //Fa riprodurre il file su sd chiamato "Tria.mp3"
		 if(isPlaying) return; 
		 
		 isPlaying = true; 
		 
		 try {
				
			 FileDescriptor fd = null;
			 File baseDir = Environment.getExternalStorageDirectory();
		     String audioPath = baseDir.getAbsolutePath() + "/Tria" + ".mp3";
		     
		    	
			 fis = new FileInputStream(audioPath);	
		     fd = fis.getFD();
		     
		//	 myPlayer = MediaPlayer.create(this, R.raw.doowackadoo); 
		     myPlayer = new MediaPlayer();
			 myPlayer.setDataSource(fd);
			 myPlayer.prepare();
			 myPlayer.setLooping(true); 
			 myPlayer.start(); 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
   		 }
		 // Runs this service in the foreground, 
		 // supplying the ongoing notification to be shown to the user 
		 Intent intent = new Intent(this, UI4.class); 
		 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); 
		 PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0); 
		 Notification notification = new NotificationCompat.Builder(getApplicationContext()) 
		 .setContentTitle("Stai cane!") 
		 .setContentText("Dio calmo") 
		 .setSmallIcon(R.drawable.ic_launcher) 
		 .setContentIntent(pi) // Required on Gingerbread and below 
		 .build(); 
		 final int notificationID = 5786423; // An ID for this notification unique within the app 
		 startForeground(notificationID, notification); 
	 } 
	
	 private void stop() { 
		 if (isPlaying) { 
			 isPlaying = false; 
			 if (myPlayer != null) { 
					 myPlayer.release(); 
				   	 myPlayer = null; 
				   	 try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			 } 
			 stopForeground(true); 
		 } 
	 } 
	
	 @Override 
	 public void onDestroy() { stop(); } 
}