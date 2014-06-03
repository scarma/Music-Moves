package com.example.musicmoves;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class UI5 extends Activity implements OnSeekBarChangeListener{
	
	//	SEEKBAR
	private SeekBar seekbar/*, Duration, Speed*/;
//	private int progressUp = 10, progressD = 10, progressS = 10;
	private TextView textProgress, textAction;
	
//    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
	    setContentView(R.layout.activity_ui5);
        
	    
//        Upsampling = new SeekBar(getApplicationContext());
//        Duration = new SeekBar(getApplicationContext());
//        Speed = new SeekBar(getApplicationContext());
        
	    seekbar=(SeekBar) findViewById(R.id.seekbar1);
        //Duration.findViewById(R.id.DurationSeekBar);
        //Speed.findViewById(R.id.SpeedSeekBar);
        
//        Upsampling.setProgress(progressUp);
//        Duration.setProgress(progressD);
//        Speed.setProgress(progressS);
        
        seekbar.setOnSeekBarChangeListener(this);
        //Duration.setOnSeekBarChangeListener(this);
        //Speed.setOnSeekBarChangeListener(this);
        
        textProgress = (TextView)findViewById(R.id.textViewProgress);
        textAction = (TextView)findViewById(R.id.textViewAction);
        
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

//		 Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui5, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		 Handle action bar item clicks here. The action bar will
//		 automatically handle clicks on the Home/Up button, so long
//		 as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
			PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.commit();
			Intent intent = new Intent(getApplicationContext(), UI5.class);
		    startActivity(intent);
		    finish();
			}
		return true;
	}
	
	
	 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    // change progress text label with current seekbar value
	    textProgress.setText("The value is: "+ progress);
	 // change action text label to changing
    	textAction.setText("changing");
	 }

	 public void onStartTrackingTouch(SeekBar seekBar) {
	    // TODO Auto-generated method stub
		 textAction.setText("starting to track touch");	    
	    }

	public void onStopTrackingTouch(SeekBar seekBar) {
	    // TODO Auto-generated method stub
		seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of the previous value.
    	textAction.setText("ended tracking touch"); 
	}
	
	
	
		
		
//
//		
//		@Override
//		public void onCreate(Bundle savedInstanceState) {
//		    super.onCreate( savedInstanceState );
//		    sharedPreferences = getSharedPreferences( PREFERENCE_PROGRESS, Context.MODE_PRIVATE );
//		    currentProgress = sharedPreferences.getInt( Key_PROGRESS, 0 );
//		    currentProgress1 = sharedPreferences.getInt( Key_PROGRESS1, 0 );
//
//		    setContentView( R.layout.activity_apart );
//		    seekBar = (SeekBar) findViewById( R.id.seek );
//		    Seekbar = (SeekBar) findViewById( R.id.seekbar1 );
//		    int max = 5;
//		    seekBar.setMax( );
//		    Seekbar.setMax( max );
//		    seekBar.setProgress( currentProgress );
//		    Seekbar.setProgress( currentProgress1 );
//		    seekBar.setOnSeekBarChangeListener( this );
//		    Seekbar.setOnSeekBarChangeListener( this );
//
//		}
//
//		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
//		}
//
//		public void onStartTrackingTouch(SeekBar seekBar) {
//		}
//
//		public void onStopTrackingTouch(SeekBar seekBar) {
//		    if (seekBar.getId() == R.id.seekBarUpsampling) {
//		        newProgressValue = seekBar.getProgress();
//		        currentProgress = newProgressValue;
//
//		        SharedPreferences.Editor editor = sharedPreferences.edit();
//		        editor.putInt( Key_PROGRESS, newProgressValue );
//
//		        editor.commit();
//
//		    } else {
//		        newProgressValue1 = seekBar.getProgress();
//		        currentProgress1 = newProgressValue;
//		        SharedPreferences.Editor editor = sharedPreferences.edit();
//		        editor.putInt( Key_PROGRESS1, newProgressValue1 );
//
//		        editor.commit();
//
//		    }
//		}
	
}
