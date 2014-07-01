package com.example.musicmoves;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import database.DBAdapter;

public class UI2 extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {
	private String message;
	private DBAdapter databaseHelper;
	private Cursor cursor;
	private Intent intent;
	private int hour;
	private int minute;
	private int second;
	private int day;
	private int month;
	private int year;
	private String date = null;
	Calendar c;
	private boolean modify=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui2);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	
	@Override
	protected void onPause() {
		if(modify){
			//Ottengo la data e l'ora corrente
			Calendar c = Calendar.getInstance();
			hour = c.get(Calendar.HOUR_OF_DAY);
			minute = c.get(Calendar.MINUTE);
			second = c.get(Calendar.SECOND);
			day = c.get(Calendar.DATE);
			month = c.get(Calendar.MONTH)+1;
			year = c.get(Calendar.YEAR);
						    
			date = day +"/"+ month+"/"+ year +" - "+ hour+":"+ minute +":"+ second;
			databaseHelper = new DBAdapter(getApplicationContext());
			databaseHelper.open();
			databaseHelper.updateDate(message, date);
			databaseHelper.close();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
			intent = getIntent();
			message=intent.getStringExtra(UI1.EXTRA_MESSAGE);
	    
		 	//Cambia focus
		  	RelativeLayout focuslayout = (RelativeLayout) findViewById(R.id.RelativeLayout);
		    focuslayout.requestFocus();
		    
		    databaseHelper = new DBAdapter(getApplicationContext());
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
		    TextView text = (TextView)findViewById(R.id.textViewUpsampling);
		   	text.setText("Upsampling: "+tempor);
		   	bar.setOnSeekBarChangeListener(this);
		   	
		   	databaseHelper.close();
			cursor.close();

			EditText editText = (EditText) findViewById(R.id.editTextRename);
			editText.setFilters(UI1.getFilter());
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
		databaseHelper = new DBAdapter(getApplicationContext());
		databaseHelper.open();
		CheckBox checkBox = (CheckBox)view;

		if(checkBox.getId()==R.id.checkX){
			if(checkBox.isChecked()){
				databaseHelper.updateX(message, 1);
				modify=true;
			}
			else{ 	
				databaseHelper.updateX(message, 0);
				modify=true;
			}
		}//fine asse x
		
		else if(checkBox.getId()==R.id.checkY){
			if(checkBox.isChecked()){
				databaseHelper.updateY(message, 1);
				modify=true;
			}
			else{
				databaseHelper.updateY(message, 0);
				modify=true;
			}
		}//fine asse y
		
		else if(checkBox.getId()==R.id.checkZ){
			if(checkBox.isChecked()){
				databaseHelper.updateZ(message, 1);
				modify=true;
			}
			else{
				databaseHelper.updateZ(message, 0);
				modify=true;
			}
				
		}//fine asse z
		
		databaseHelper.close();
}//fine aggiona
	
	
	public void rinomina (View view){
		
		EditText editText = (EditText) findViewById(R.id.editTextRename);
		//faccio un semplice update
		databaseHelper.open();
		cursor = databaseHelper.fetchASession(message);
	 
		String value="";
		value = editText.getText().toString().toLowerCase(Locale.getDefault());
		
		try { value = value.substring(0,1).toUpperCase(Locale.getDefault()) + value.substring(1).toLowerCase(Locale.getDefault());}
		catch(java.lang.StringIndexOutOfBoundsException e)//caso stringa vuota
			{ value = "Rec";	}
		
		cursor.moveToFirst();
		
		int id_o = cursor.getInt(0);
		String name_o = cursor.getString(1);
		String loc_o = cursor.getString(2);
		String date_co = cursor.getString(3);
		String date_lmo = cursor.getString(4);
		String image_o = cursor.getString(5);
		int sample_o = cursor.getInt(6);
		int x_o = cursor.getInt(7);
		int y_o = cursor.getInt(8);
		int z_o = cursor.getInt(9);
		
		//Controllo che il nuovo nome non sia già presente nel db
		cursor = databaseHelper.NameSessionAsExist(value);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		
		if(count != 0){
			//avviso l'utente che è già esistente una sessione con il nome che vuole inserire
			Toast.makeText(getApplicationContext(), "Name not avaiable, please choose a different name", Toast.LENGTH_LONG).show();
			//renameRec(p);
			return;
		}
	    databaseHelper.updateSession(id_o, value, loc_o, date_co, date_lmo, image_o, sample_o, x_o, y_o, z_o);
	    
		databaseHelper.close();
		cursor.close();
		
		File from = new File(loc_o,name_o+".txt");
		File to = new File(loc_o,value+".txt");
		from.renameTo(to);

		File from_im = new File(loc_o,name_o+".png");
		File to_im = new File(loc_o,value+".png");
		from_im.renameTo(to_im);
		
		message=value;
		
		Intent intent = new Intent(getApplicationContext(), UI2.class);
	    intent.putExtra(UI1.EXTRA_MESSAGE, message);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    //android salva in uno stack le activity nell'ordine in cui le apriamo. quando noi gli diciamo di riavviare l'activity nello stack compare una nuova UI2
	    //se noi quindi andiamo a premere il tasto back lui tenta di riaprire la precedente ui2 pero' nel frattempo abbiamo rinominato quindi crasha nel caricare la bitmap
	    //set flag mi assicura che nello stack venga inserita solo una istanza (l'ultima) per ogni activity. insomma cancella dallo stack la vecchia UI2 quindi se premo back
	    //ritorna correttamente alla UI1
	    finish();
	    startActivity(intent);
	}
	
	
	//metodi che devono essere implementati
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		progress+=100;
		TextView text = (TextView)findViewById(R.id.textViewUpsampling);
	   	text.setText("Upsampling: "+progress);
	   	databaseHelper = new DBAdapter(getApplicationContext());
		databaseHelper.open();
		databaseHelper.updateUpsampling(message, progress);
		databaseHelper.close();
		modify=true;
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
	
	public void toUI4(View view) 
	{
		Intent intent = new Intent(getApplicationContext(), UI4.class);
	    intent.putExtra(UI1.EXTRA_MESSAGE, message);
	    intent.putExtra("my",true);
	    startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
