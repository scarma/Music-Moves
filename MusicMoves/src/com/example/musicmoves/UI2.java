package com.example.musicmoves;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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
	
	@SuppressWarnings("deprecation")
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
		Display display = getWindowManager().getDefaultDisplay();
		int w = display.getWidth();
	    Bitmap bitmapScaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(cursor.getString(5)+message+".png"), 2*w/5, 2*w/5, false);
        iv.setImageBitmap(bitmapScaled);
		//Modifica campo textView
		TextView textView = (TextView) findViewById(R.id.textViewFileName);
	    textView.setTextColor(Color.rgb(255, 153, 0));
	    textView.setText(message+ "\nDate Creation: "+cursor.getString(3)+ "\nLast Modified: "+cursor.getString(4));
	    this.setTitle(message);
	    //setta checkboxes in base al database
	    CheckBox x = (CheckBox)findViewById(R.id.checkX);
	    if(cursor.getInt(7)==1)x.setChecked(true);
	    CheckBox y = (CheckBox)findViewById(R.id.checkY);
	    if(cursor.getInt(8)==1)y.setChecked(true);
	    CheckBox z = (CheckBox)findViewById(R.id.checkZ);
	    if(cursor.getInt(9)==1)z.setChecked(true);
	    //setta barra upsampling
	    SeekBar bar = (SeekBar)findViewById(R.id.seekBarUpsampling);
	    bar.setProgress(cursor.getInt(6)-100);
	    int tempor = cursor.getInt(6);
	   Toast.makeText(getApplicationContext(), ""+tempor, Toast.LENGTH_LONG).show();
	   	TextView text = (TextView)findViewById(R.id.textViewUpsampling);
	   	text.setText("Upsampling: "+tempor);
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
	
	//Metodo unico per i checkbox, per il momento li riconosce e se li clicchiamo manda un toast
	public void aggiorna (View view){
		
		CheckBox checkBox = (CheckBox)view;
		
		if(checkBox.getId()==R.id.checkX){
			if(checkBox.isChecked()){
				Toast.makeText(getApplicationContext(), "X_Attivo", Toast.LENGTH_LONG).show();
			}
			else Toast.makeText(getApplicationContext(), "X_Inattivo", Toast.LENGTH_LONG).show();
		}//fine asse x
		
		else if(checkBox.getId()==R.id.checkY){
			if(checkBox.isChecked()){
				Toast.makeText(getApplicationContext(), "Y_Attivo", Toast.LENGTH_LONG).show();
			}
			else Toast.makeText(getApplicationContext(), "Y_Inattivo", Toast.LENGTH_LONG).show();
		}//fine asse y
		
		else if(checkBox.getId()==R.id.checkZ){
			if(checkBox.isChecked()){
				Toast.makeText(getApplicationContext(), "Z_Attivo", Toast.LENGTH_LONG).show();
			}
			else Toast.makeText(getApplicationContext(), "Z_Inattivo", Toast.LENGTH_LONG).show();
		}//fine asse z
	
	}//fine aggiona
	
	public void toUI4(View view) 
	{
	    Intent intent = new Intent(getApplicationContext(), UI4.class);
	    intent.putExtra(UI1.EXTRA_MESSAGE, message);
	    startActivity(intent);
	    
	}
}
