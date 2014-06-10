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
	
}
