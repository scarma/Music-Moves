package com.example.musicmoves;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;



public class UI5 extends PreferenceActivity  implements OnSharedPreferenceChangeListener {

/**
 * The Key to be used to access the preference. This should be defined already on your preferences.xml file
 */
public static final String KEY_CURRENT_THEME = "pref_current_theme";
/**
 * The instance of the Theme Preference. 
 */
Preference mThemePreference;
SharedPreferences mPreferences;
OnPreferenceChangeListener mChangeListener;
boolean x = true;
	//	SEEKBAR
	private SeekBar seekbar;
	private TextView textViewUpsampling;
	private TextView textViewSampleRate;
	private TextView textViewMaxDur;
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences setting = getSharedPreferences("preferences",MODE_PRIVATE);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);       
        addPreferencesFromResource(R.xml.preferences);
        
	    textViewUpsampling = (TextView)findViewById(R.id.textViewUpsampling);
	    textViewSampleRate = (TextView)findViewById(R.id.textViewSampleRate);
	    textViewMaxDur = (TextView)findViewById(R.id.textViewMaxDur);
//	    //Find the preference
//	    mThemePreference = findPreference("x"); //chiave "x" presa da preferences.xml
//	    //Set the listener
//	    mThemePreference.setOnPreferenceChangeListener(mOnPreferencedChanged);
//	    
//		//Call onPrerenceChanged to begin with in case the settings may have changed
//	    //The system will call this later each time the preference gets updated by the user via Settings
//	    mChangeListener.onPreferenceChange(
//	            mThemePreference,
//	            PreferenceManager.getDefaultSharedPreferences(
//	                    mThemePreference.getContext()).getString(mThemePreference.getKey(),
//	                            ""));   
    }
    
    private void updatePrefDependentValues() {
        //get the instance of the SharedPreference Object. 
    	mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Update the boolean value based on the current preferences
        //The second parameter to getBoolean is a default value, we want false as default value. 
        x = mPreferences.getBoolean("x", false); //chiave "x" presa da preferences.xml
        if (x) {
            //Cose da fare se la x è attiva
        }
        else if (!x) {
            //Cose da fare se la x non è attiva
        }
    }
    
    private OnPreferenceChangeListener mOnPreferencedChanged = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Resources mResources = preference.getContext().getResources();

            //You can check to see what change in two different ways.           
            //a) you can check the TypeOf the preference that changed. 
            if (preference.getKey().equals("x")) {
            	Log.d("asdasd","asd");
                //If the preference that changes is a CheckBoxPreference, then do such and such changes. 
            } 
            //Or, you can check if it's a specific preference that changed

            if (preference == mThemePreference) {
                //If the preference is actually the Theme Preference itself, then do such and such changes. 
            }

            //Always return true so that the preference value change can be persisted, false otherwise. 
            return true;
        }};
      //Listener to detect changes 
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        Preference p = findPreference(key);
	        if (key.equals("x")) {
	        Log.d("olè","sdflsdf");
	        }
	        else if (p instanceof ListPreference) {
	        p.setSummary((String) ((ListPreference) p).getEntry());
	        }
	    }
	        //First time initialization     
	    private void setSummary(SharedPreferences sharedPreferences, String key) {
		        Preference p = findPreference(key);
		        if (p instanceof EditTextPreference) {
		        p.setSummary(((EditTextPreference) p).getText());
		        }
		        else if (p instanceof ListPreference) {        
		        p.setSummary((String) ((ListPreference) p).getEntry());
		        }
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
	
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
//	 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//	    // change progress text label with current seekbar value
//	    textProgress.setText("The value is: "+ progress);
//	 // change action text label to changing
//    	textAction.setText("changing");
//	 }
////
	 public void onStartTrackingTouch(SeekBar seekBar) {
	    // TODO Auto-generated method stub
//		 textAction.setText("starting to track touch");	    
	    }
//
//	public void onStopTrackingTouch(SeekBar seekBar) {
//	    // TODO Auto-generated method stub
//		seekBar.setSecondaryProgress(seekBar.getProgress()); // set the shade of the previous value.
//    	textAction.setText("ended tracking touch"); 
//	}
	
	
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