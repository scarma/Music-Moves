package com.example.musicmoves;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UI2 extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui2);
		 // Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		// Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(30);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText("Il file si chiama "+message+""+"\nChe bel nome!"+ "\n<3 "+message+ " <3"
	    		+ "\n<3 "+message+ " <3"+ "\n<3 "+message+ " <3"+ "\n<3 "+message+ " <3");

	    // Set the text view as the activity layout
	    setContentView(textView);
//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent settings_intent = new Intent(getApplicationContext(), UI5.class);
			startActivity(settings_intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_ui2, container,
					false);
			return rootView;
		}
	}

}
