package com.example.musicmoves;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import database.DBAdapter;

public class UI2 extends ActionBarActivity {
	private String message;
	private DBAdapter databaseHelper;
	private Cursor cursor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui2);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Get the message from the intent
		Intent intent = getIntent();
	    message = intent.getStringExtra(UI1.EXTRA_MESSAGE);
	    databaseHelper = new DBAdapter(this);
		databaseHelper.open();
		cursor = databaseHelper.fetchSessionByFilter(message);
		cursor.moveToFirst();
		ImageView iv = (ImageView) findViewById(R.id.imageThumb);
		Bitmap bitmapScaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(cursor.getString(5)+message+".png"), 200, 200, false);
        iv.setImageBitmap(bitmapScaled);
		//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewFileName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(message+ "\nDate Creation: "+cursor.getString(3)+ "\nLast Modified: "+cursor.getString(4));
	    this.setTitle(message);
	}
	
	@Override
	protected void onPause() {
	//TODO: Salvare lo stato
		super.onPause();
	}

	@Override
	protected void onResume() {
	//TODO: Ripristinare lo stato
		super.onResume();
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
	
	public void toUI4(View view) 
	{
	    Intent intent = new Intent(getApplicationContext(), UI4.class);
	    intent.putExtra(UI1.EXTRA_MESSAGE, message);
	    startActivity(intent);
	    
	}
}
