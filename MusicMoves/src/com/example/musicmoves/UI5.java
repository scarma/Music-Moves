package com.example.musicmoves;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class UI5 extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      Carica le preferences dal file preferences.xml
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);       
        addPreferencesFromResource(R.xml.preferences);
    } 
      
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		Aggiunge menù
		getMenuInflater().inflate(R.menu.ui5, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		Permette di riportare le impostazioni ai parametri di default
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			SharedPreferences mPreferences= PreferenceManager.getDefaultSharedPreferences (this);
			
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