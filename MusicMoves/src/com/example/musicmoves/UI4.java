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
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UI4 extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	
	private String sessionName;
	private TextView tPlaybackPosition;
	private TextView tDuration;
	private ProgressBar tBar;
    private Thread thread;
    public static boolean isStopped;
	public boolean isPlaying;
	
	//plus
	public float x1, x2 , y1 , y2;
	private GestureDetector mDetector;
	private View.OnTouchListener gestureListener;
	
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
		
		  
	    //plus 
	    mDetector = new GestureDetector(this, new MyGestureListener());
	    ImageView background = (ImageView)findViewById(R.id.imageView1);
	   // trova un modo perch� funga background.setOnTouchListener(this);
	    gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        };
	    
	    
/*	    background.setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(final View view, final MotionEvent event) {
	           return mDetector.onTouchEvent(event);
	        }
	     });*/
	    background.setOnTouchListener(gestureListener);

		
		
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
		//TODO: CHE ROBA �! SISTEMARE ON BACK PRESSED Grazie
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
	
	
	
	
	//parte per il plus
	
	
	/*
	 * 
	 * questo metodo gestirebbe tutti gli eventi possibili che non sono gia' gestiti da altri metodi
	 * percio' restituisce tantissimi toast quando clicckiamo fuori dall'imageview
	 @Override 
	    public boolean onTouchEvent(MotionEvent event){ 
//	        this.mDetector.onTouchEvent(event);
//	        return super.onTouchEvent(event);
     	Toast.makeText(getApplicationContext(), "Evento non gestito ", Toast.LENGTH_SHORT).show();

		 return true;
	    }
	   */ 
	
	
	//int dimensioni = background
	    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
	      
	    	
	        
	        public boolean onDoubleTap(MotionEvent event){
	        	delay();
	        	return true;
	        }
	        
	        @Override
	        public boolean onDown(MotionEvent event) { 
	        	//fa qualcosa MENTRE il dito � premuto sullo schermo
	        	//diverso da longpress
	            return true;
	        }
	        
	        public boolean onSingleTapConfirmed(MotionEvent event){
	        	
	        	echo();
	        	return true;
	        }
	        //occhio, si potrebbe usare anche onScroll, far� una prova
	        @Override
	        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
	        	  x1=event1.getX();
	        	  x2=event2.getX();
	        	  y1=event1.getY();
	        	  y2=event2.getY();
	        	  int quantity = (int)Math.abs(x2-x1)+1000;
	        	  if (quantity > 2000) quantity =2000;
	        	  
	        	  double volume = Math.abs(y2-y1);
	
	        	  //fixati i parametri perch� faceva un pelino fatica a riconoscere i movimenti giusti
	        	  
	        	        // right to left
	        	        if(x1 - x2 > 20 && Math.abs(y1-y2) < 100 ) {
	        	        	speed(false, quantity);
	        	            return true;
	        	        }
	        	        // left to right
	        	        else if (x2-x1 > 20 && Math.abs(y1-y2) < 100) {
	        	        	speed(true, quantity);
	        	            return true; 
	        	        }
	        	        
	        	        
	        	        else if (y1-y2 > 20 && Math.abs(x1-x2) < 100){
	        	        	//Toast.makeText(getApplicationContext(), "drag su " + (velocityY), Toast.LENGTH_SHORT).show();
	        	        	volume(true, volume);
	        	        	return true;
	        	        }
	        	        
	        	        else if (y2-y1 > 20 && Math.abs(x1-x2) < 100){
	        	        	//Toast.makeText(getApplicationContext(), "drag giu " + (velocityY), Toast.LENGTH_SHORT).show();
	        	        	volume(false, volume);
	        	        	return true;
	        	        }
	        	       return true;
	        }
	        	
	    }//fine classe
	    


	public void echo(){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.ECHO, true);
		i.putExtra(EXTRA_MESSAGE, sessionName);
		startService(i); 
		
	}
	
	public void delay(){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.DELAY, true); 
		i.putExtra(EXTRA_MESSAGE, sessionName);
		startService(i); 
		
	}
	
	public void volume(boolean up, double intensity){
		
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.VOLUME, true); 
		i.putExtra("up", up);
		i.putExtra("intensity", intensity);
		startService(i); 
	}

	public void speed(boolean up, int intensity){
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.SPEED, true);
		i.putExtra("up", up); 
		i.putExtra("intensity", intensity);
		startService(i); 
	}
	
	

	
}
