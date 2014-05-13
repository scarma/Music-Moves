package com.example.musicmoves;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class UI5 extends PreferenceActivity {
	
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
	
	
}
