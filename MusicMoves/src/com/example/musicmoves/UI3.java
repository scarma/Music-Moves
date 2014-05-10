package com.example.musicmoves;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class UI3 extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Pop up dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("New Recording Session");
		alert.setMessage("Insert Session Name");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		//Input filter to accept only letter or digit
		InputFilter filter = new InputFilter() { 
	        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
	        		
	                for (int i = start; i < end; i++) { 
	                        if (!Character.isLetterOrDigit(source.charAt(i))) { 
	                                return ""; 
	                        } 
	                } 
	                return null; 
	        } 
		}; 
		input.setFilters(new InputFilter[]{filter}); 
		alert.setView(input);

		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				 // Do something with value!   
				String value="";
				
				value = input.getText().toString().toLowerCase();
				
				
				try {value = value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();}
				catch(java.lang.StringIndexOutOfBoundsException e){ value = "Rec01";}
			    TextView textView = new TextView(getApplicationContext());
			    textView.setTextSize(40);
			    textView.setText(value);
			    textView.setTextColor(Color.RED);
	
			    // Set the text view as the activity layout
			    setContentView(textView);
			  }
			});

		alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				  Intent intent = new Intent(getApplicationContext(), UI1.class);
				    startActivity(intent);  
			  }
			});

		alert.show();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui3);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui3, menu);
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
			//settings_intent.putExtras(bundle); //per salvare
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
			View rootView = inflater.inflate(R.layout.fragment_ui3, container,
					false);
			return rootView;
		}
	}
	
	
}
