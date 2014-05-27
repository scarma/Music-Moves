package com.example.musicmoves;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class UI5 extends PreferenceActivity /*implements OnSeekBarChangeListener*/ {
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui5, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
			};
			return true;}
	
//	SEEKBAR
//		SeekBar seekBar, Seekbar;
//
//		private int newProgressValue, currentProgress;
//		private int newProgressValue1, currentProgress1;
//		private SharedPreferences sharedPreferences, sharedPreferences1;
//		private String Key_PROGRESS = "key_progress";
//		private String Key = "key_value";
//		private String PREFERENCE_PROGRESS = "preference_progress";
//		private String Key_PROGRESS1 = "key_progress1";
//		private String Key1 = "key_value1";
//		private String PREFERENCE_PROGRESS1 = "preference_progress1";

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
