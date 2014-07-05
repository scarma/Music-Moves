package com.example.musicmoves;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UI4 extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	
	private String sessionName;
	private TextView tPlaybackPosition;
	private TextView tDuration;
	private ProgressBar tBar;
    private Thread thread;
    public static boolean isStopped;
	private boolean isPlaying;
	
	//Parte del progetto plus
	public float x1, x2 , y1 , y2;
	private GestureDetector mDetector;
	private View.OnTouchListener gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setVolumeControlStream(AudioManager.STREAM_MUSIC); //Aumenta volume musica anche se in pausa
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui4);	
		Intent intent = getIntent();
	    sessionName = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewSessionName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(sessionName);
	    PlayMusic(null);
	    mDetector = new GestureDetector(this, new MyGestureListener());
	    ImageView background = (ImageView)findViewById(R.id.imageView1);
	    gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        };
	    background.setOnTouchListener(gestureListener);
   }
	
	/*
	 * Il metodo onCreateOptionsMenu() aggiunge il menù 
	 * che consente di tornare a UI1, UI2 o UI5.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ui4, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_details) {
			Intent intent = new Intent(getApplicationContext(), UI2.class);
			intent.putExtra(EXTRA_MESSAGE, sessionName);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_list) {
			Intent intent = new Intent(getApplicationContext(), UI1.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
	
	/*
	 * Il metodo PlayMusic() permette di far partire il PlayerService
	 * e gestisce i bottoni presenti nell'UI.
	 * Inoltre fa partire il thread che gestisce l'aggiornamento della
	 * ProgressBar e delle TextView a seconda del tempo di riproduzione
	 * e della durata delle tracce audio
	 */
	public void PlayMusic(View view) { 
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
	}
	
	/*
	 * PauseMusic() gestisce i bottoni quando metto in pausa la riproduzione
	 * e manda l'intent al service che mette in pausa le AudioTrack. 
	 */
	public void PauseMusic(View view) { 
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
	
	/*
	 * StopMusic() blocca la riproduzione e il service e rimanda 
	 * l'utente alla UI di partenza (UI1 o UI2).
	 */
	public void StopMusic(View view) { 
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
	
	/*
	 * intToTime() e' un metodo per trasformare i tempi
	 * in formato stringa in modo da poterli mostrare correttamente all'utente.
	 */
	public String intToTime(int time){ 	
	    int seconds = time%60;
	    int minutes = ((time-seconds)/60)%60;
	    String t = String.format(Locale.getDefault(),"%02d%s%02d", minutes,":",seconds);
	    return t;
	}

	/*
	 * onConfigurationChanged() viene chiamato quando si passa da modalità
	 * portrait a landscape e viceversa. 
	 * In questo metodo ricreiamo lo stato dell'activity.
	 */
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
	    mDetector = new GestureDetector(this, new MyGestureListener());
	    ImageView background = (ImageView)findViewById(R.id.imageView1);
	    gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        };
	    background.setOnTouchListener(gestureListener);
	}

	//plus
	/*
	 * La classe MyGestureListener serve per riconoscere i gesti dell'utente
	 * sull'immagine nell'UI4 e applicare al gesto l'effetto corrispondente.
	 */
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
	        
		public boolean onDoubleTap(MotionEvent event){
        	delay();
        	return true;
        }
	    
		/*
		 * Fa qualcosa mentre il dito e' premuto sullo schermo 
		 * e non rilevo un longpress. 
		 */
        @Override
        public boolean onDown(MotionEvent event) { 
        	return true;
        }
	        
        /*
         * onSingleTapConfirmed() con questo metodo riconosco il singolo tocco
         * e faccio partire l'effetto echo.
         */
        public boolean onSingleTapConfirmed(MotionEvent event){
        	echo();
        	return true;
        }
	        
        /*
         * onFling() mi permette di riconoscere i movimenti che corrispondono agli
         * altri effetti e li fa partire quando rileva il gesto corrispondente.
         */
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        	  x1=event1.getRawX();
        	  x2=event2.getRawX();
        	  y1=event1.getRawY();
        	  y2=event2.getRawY();
        	  int quantity = (int)Math.abs(x2-x1)+1000;
        	  if (quantity > 2000) quantity =2000;
        	  
        	  double volume = Math.abs(y2-y1); 
    	      // Verso sinistra
	          if(x1 - x2 > 20 && Math.abs(y1-y2) < 100 ) {
	          	  speed(false, quantity);
    	          return true;
    	      }
    	      // Verso destra
	          else if (x2-x1 > 20 && Math.abs(y1-y2) < 100) {
    	          speed(true, quantity);
    	          return true; 
	          }
	          // Verso l'alto
	          else if (y1-y2 > 20 && Math.abs(x1-x2) < 100){
	        	  volume(true, volume);
	        	  return true;
	          }
	          // Verso il basso
	          else if (y2-y1 > 20 && Math.abs(x1-x2) < 100){
	        	   volume(false, volume);
	        	   return true;
	          }
	          return true;
	     }  	
	}//fine classe MyGestureListener

	/*
	 * Con il metodo echo() faccio partire l'effetto attraverso il service
	 * che viene oppurtunamente chiamato.
	 */
	public void echo(){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.ECHO, true);
		i.putExtra(EXTRA_MESSAGE, sessionName);
		startService(i);		
	}
	
	/*
	 * Con il metodo delay() faccio partire l'effetto attraverso il service
	 * che viene oppurtunamente chiamato.
	 */
	public void delay(){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.DELAY, true); 
		i.putExtra(EXTRA_MESSAGE, sessionName);
		startService(i);
	}
	
	/*
	 * Con il metodo volume() faccio partire l'effetto attraverso il service
	 * che viene oppurtunamente chiamato.
	 */
	public void volume(boolean up, double volume){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.VOLUME, true); 
		i.putExtra("up", up);
		i.putExtra("volume", volume);
		startService(i); 
	}

	/*
	 * Con il metodo speed() faccio partire l'effetto attraverso il service
	 * che viene oppurtunamente chiamato.
	 */
	public void speed(boolean up, int intensity){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.SPEED, true);
		i.putExtra("up", up); 
		i.putExtra("intensity", intensity);
		startService(i); 
	}	
}