package com.example.musicmoves;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UI4 extends ActionBarActivity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	private String sessionName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui4);
	}
	@Override
	protected void onStart() {
		super.onStart();
		// Get the message from the intent
		Intent intent = getIntent();
	    sessionName = intent.getStringExtra(UI1.EXTRA_MESSAGE);
		//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewSessionName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(sessionName);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui4, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_details) {
			Intent intent = new Intent(getApplicationContext(), UI2.class);
			intent.putExtra(EXTRA_MESSAGE, sessionName);
			startActivity(intent);
			return true;
		}
		if (id == R.id.action_list) {
			Intent intent = new Intent(getApplicationContext(), UI1.class);
			startActivity(intent);
			finish();
			return true;
		}
		if (id == R.id.action_settings) {
			Intent intent = new Intent(getApplicationContext(), UI5.class);
			startActivity(intent);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void PlayMusic(View view) { //// Play button: starts the playback music service 
		Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
		ImageButton play = (ImageButton) findViewById(R.id.playB);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedB);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedB);
		
		play.setVisibility(View.INVISIBLE);
		pause.setVisibility(View.VISIBLE);
		stopUns.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.VISIBLE);
		
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		i.putExtra(PlayerService.PLAY_START, true); 
		startService(i); 
//		
//		Context context = getApplicationContext();
//		super.onResume();   
		
	}
	
	public void PauseMusic(View view) { //Pause button
		Toast.makeText(getApplicationContext(), "Paused", Toast.LENGTH_SHORT).show();	
		ImageButton play = (ImageButton) findViewById(R.id.playB);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		pause.setVisibility(View.INVISIBLE);
		play.setVisibility(View.VISIBLE);
		super.onPause();
	}
	
	public void StopMusic(View view) { // Stop button: stops the music by stopping the service 
		Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
		ImageButton play = (ImageButton) findViewById(R.id.playB);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseB);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedB);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedB);
		
		play.setVisibility(View.VISIBLE);
		pause.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.INVISIBLE);
		stopUns.setVisibility(View.VISIBLE);
		
		Intent i = new Intent(getApplicationContext(),PlayerService.class); 
		stopService(i); 
	}
	
	

}
