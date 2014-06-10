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



public class UI5 extends PreferenceActivity {

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
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);       
        addPreferencesFromResource(R.xml.preferences);
    }
//            @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//	        Preference p = findPreference(key);
//	        if (key.equals("x")) {
//	        Log.d("olè","sdflsdf");
//	        }
//	        else if (p instanceof ListPreference) {
//	        p.setSummary((String) ((ListPreference) p).getEntry());
//	        }
//	    }
//	        //First time initialization     
	        
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
//			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
			mPreferences= PreferenceManager.getDefaultSharedPreferences (this);
			PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.clear();
			editor.commit();
			Intent intent = new Intent(getApplicationContext(), UI5.class);
		    startActivity(intent);
		    finish();
			}
		return true;
	}
	
}