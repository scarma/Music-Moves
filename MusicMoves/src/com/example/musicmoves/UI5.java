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
	
	/*
	 * Carica le preferences dal file preferences.xml
	 * Le preferenze cosi fatte vengono direttamente salvate dal
	 * sistema operativo. Le preference da noi utilizzate sono
	 * gli assi, il sample rate, l'upsampling, la massima durata di registrazione,
	 * la durata massima di riproduzione. Per il plus: il ritardo 
	 * con cui vengono applicati in real time il delay e l'echo 
	 * e le loro rispettive intensita'.
	 */
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);       
        addPreferencesFromResource(R.xml.preferences);
    } 
      
	/*
	 * Il metodo onCreateOptionsMenu() aggiunge il menù.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ui5, menu);
		return true;
	}
	/*
	 *onOptionsItemSelected() permette di riportare le impostazioni
	 *delle preference ai parametri di default.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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