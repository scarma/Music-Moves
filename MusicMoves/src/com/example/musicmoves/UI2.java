package com.example.musicmoves;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
			@SuppressWarnings("deprecation")
			int w = display.getWidth();
		    Bitmap bitmapScaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(cursor.getString(5)+message+".png"), 2*w/5, 2*w/5, false);
	        iv.setImageBitmap(bitmapScaled);
			//Modifica campo textView
			TextView textView = (TextView) findViewById(R.id.textViewFileName);
		    textView.setTextColor(Color.rgb(255, 153, 0));
		    textView.setText(message+ "\nDate Creation: "+cursor.getString(3)+ "\nLast Modified: "+cursor.getString(4));
		    this.setTitle(message);
		    //Setta checkboxes in base al database
		    CheckBox x = (CheckBox)findViewById(R.id.checkX);
		    if(cursor.getInt(7)==1)x.setChecked(true);
		    CheckBox y = (CheckBox)findViewById(R.id.checkY);
		    if(cursor.getInt(8)==1)y.setChecked(true);
		    CheckBox z = (CheckBox)findViewById(R.id.checkZ);
		    if(cursor.getInt(9)==1)z.setChecked(true);
		    //Setta barra upsampling
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
	
	/*
	 * Il metodo onCreateOptionsMenu() aggiunge il men�.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ui2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * L'evento click nell'action bar e' gestito qui. La Action Bar 
		 * gestira' automaticamente i clic sul pulsante Home/Up, cosi' 
		 * come si specifica un'attivit� padre in AndroidManifest.xml.
		 */
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent settings_intent = new Intent(getApplicationContext(), UI5.class);
			startActivity(settings_intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Il metodo aggiorna() aggiorna direttamente su db quali assi
	 * l'utente ha selezionato o deselezionato.
	 */
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
	
	/*
	 * Con il metodo rinomina() permetto all'utente di rinominare la
	 * registrazione
	 */
	public void rinomina (View view){
		
		EditText editText = (EditText) findViewById(R.id.editTextRename);
		//faccio un semplice update
		databaseHelper.open();
		cursor = databaseHelper.fetchASession(message);
	 
		String value="";
		value = editText.getText().toString().toLowerCase(Locale.getDefault());
		
		try {
			value = value.substring(0, 1).toUpperCase(Locale.getDefault())
					+ value.substring(1).toLowerCase(Locale.getDefault());
		} catch (java.lang.StringIndexOutOfBoundsException e)// caso stringa vuota
		{
			value = "Rec";
		}
		
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
		
		//Controllo che il nuovo nome non sia gi� presente nel db
		cursor = databaseHelper.NameSessionAsExist(value);
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		
		if(count != 0){
			//avviso l'utente che � gi� esistente una sessione con il nome che vuole inserire
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
	    finish();
	    startActivity(intent);
	}
	
	/*
	 *Con questo metodo rilevo la modifica dell'upsampling attraverso la seekbar
	 *e aggiorno direttamente su db.  
	 */
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
	
	/*
	 * toUI4() mi permette di andare alla UI4 dove posso
	 * ascoltare la registrazione con le eventuali modifiche
	 * alle sue impostazioni fatte.
	 */
	public void toUI4(View view) 
	{
		Intent intent = new Intent(getApplicationContext(), UI4.class);
	    intent.putExtra(UI1.EXTRA_MESSAGE, message);
	    intent.putExtra("my",true);
	    startActivity(intent);
	}
	
	
}