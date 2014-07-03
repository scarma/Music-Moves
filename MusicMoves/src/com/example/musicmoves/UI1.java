package com.example.musicmoves;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import database.DBAdapter;


public class UI1 extends ListActivity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	private static boolean enoughSpace;
	private DBAdapter databaseHelper;
	private Cursor cursor;
	private String[] list_music;
	private UI1Adapter adapter;
	private int pos;
	private int p; //posizione nella lista, non sapevo se c'era già una variabile che potevo usare
	private String loc;
	private String new_filename;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private int day = 0;
	private int month = 0;
	private int year = 0;
	private String date = null;
	private FileWriter writer;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private boolean hasAccelerometer;
	
	
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui1);
		
		//Visualizzo spazio rimanente
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
//		StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
		@SuppressWarnings("deprecation")
		int   Free   = (int)(statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
		if (Free >= 5)
			enoughSpace = true;
		else {
			Toast.makeText(getApplicationContext(), "Warning, low disk space: "+ Free + " MB", Toast.LENGTH_LONG).show();
			enoughSpace = false;
		}
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager == null)
			hasAccelerometer = false;
		else {
			mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (mAccelerometer == null)
				hasAccelerometer = false;
			else
				hasAccelerometer = true;
		}

    }
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Visualizzo spazio rimanente
//		StatFs statFs = new StatFs(getFilesDir().getAbsolutePath());
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
		@SuppressWarnings("deprecation")
		int   Free   = (int)(statFs.getAvailableBlocks() * statFs.getBlockSize()) / 1048576;
		if (Free >= 5)
			enoughSpace = true;
		else {
			Toast.makeText(getApplicationContext(), "Warning, low disk space: "+ Free + " MB", Toast.LENGTH_LONG).show();
			enoughSpace = false;
		}
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

		//lettura dal database        
		databaseHelper = new DBAdapter(getApplicationContext());
		databaseHelper.open();
		cursor = databaseHelper.fetchAllSession();
		cursor.moveToFirst();
		list_music =new String[cursor.getCount()];
		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor.isAfterLast())
				break;
			list_music[i] = cursor.getString(1);
			cursor.moveToNext();
			
		}
		databaseHelper.close();
		cursor.close();
		
		adapter = new UI1Adapter(this, R.layout.riga_lista, list_music);
		setListAdapter(adapter);
		//da qui inseriamo il codice utile a mostrare un messaggio al click
		ListView listaV = getListView();
		listaV.setTextFilterEnabled(true);
		registerForContextMenu(listaV);
		
		listaV.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// vado a UI2 toccando elemento della lista
				runUI2(position);
			}
		});  
		
		
		
	}
	
	@Override
	protected void onPause() {
	//TODO: Salvare lo stato
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui1, menu);
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
		if (id == R.id.action_info) {
			Toast.makeText(getApplicationContext(), "App developed by The Ehi Team: Scarmagnan Andrea, Fabian Emanuele, Zampieri Giovanni", Toast.LENGTH_LONG).show();
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
			View rootView = inflater.inflate(R.layout.fragment_ui1, container,
					false);
			return rootView;
		}
	}
	public void toUI3(View view) 
	{
		if (!enoughSpace)
			Toast.makeText(getApplicationContext(), "Warning, not enough disk space! New recordings not allowed. Free some space first!", Toast.LENGTH_LONG).show();
		if (!hasAccelerometer)
			Toast.makeText(getApplicationContext(), "No accelerometer sensor found!", Toast.LENGTH_LONG).show();
		if (enoughSpace && hasAccelerometer) {
	    Intent intent = new Intent(getApplicationContext(), UI3.class);
	    startActivity(intent);
	    finish();
		}
	}
	
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setTitle("Really Exit?")
	        .setMessage("Are you sure you want to exit?")
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
	            	Intent i = new Intent(getApplicationContext(),PlayerService.class);
	            	UI4.isStopped=true;
	            	stopService(i);
	            	UI1.super.onBackPressed();
	            }
	        }).create().show();
	}
	
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.context_menu, menu);
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
      int position = info.position;
      menu.setHeaderTitle("Recording " + position);
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.playB:
			playRec(info.position);
			return true;
		case R.id.delete:
			deleteRec(info.position);
			return true;
		case R.id.clone:
			if (enoughSpace) {
				cloneRec(info.position);
			} else
				// TODO da sistemare
				Toast.makeText(getApplicationContext(), "Warning, not enough disk space! Cloning record not allowed. Free some space first!", Toast.LENGTH_LONG).show();
			return true;
		case R.id.rename:
			renameRec(info.position);
			return true;
		case R.id.details:
			detailsRec(info.position);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void cloneRec(int position) {
		p = position;
//		prelevo tutti i dati della sessione da clonare e ne creo una 
//		nuova chiedendo all'utente di inserire un nuovo nome		

		databaseHelper.open();
		cursor = databaseHelper.fetchASession(list_music[position]);
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage("Insert New Session Name")
		       .setTitle("Rename Cloned Session");
		
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
		//Input filter per accettare file al massimo di 10 caratteri
		InputFilter lenghtfilter = new InputFilter.LengthFilter(10);
		input.setFilters(new InputFilter[]{filter,lenghtfilter});
		builder.setView(input);
		
		builder.setPositiveButton(android.R.string.yes,new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String value="";
				value = input.getText().toString().toLowerCase(Locale.getDefault());
				
				try {
					value = value.substring(0, 1).toUpperCase(
							Locale.getDefault())
							+ value.substring(1).toLowerCase(
									Locale.getDefault());
				} catch (java.lang.StringIndexOutOfBoundsException e) {
					value = "Rec";
				}
				
				new_filename = value;
				
				cursor.moveToFirst();
				
//				int id_o = cursor.getInt(0);
				String name_o = cursor.getString(1);
				String loc_o = cursor.getString(2);
//				String date_co = cursor.getString(3);
//				String date_lmo = cursor.getString(4);
				String image_o = cursor.getString(5);
				int sample_o = cursor.getInt(6);
				int x_o = cursor.getInt(7);
				int y_o = cursor.getInt(8);
				int z_o = cursor.getInt(9);
				//Controllo che il nuovo nome non sia giï¿½ presente nel db
				cursor = databaseHelper.NameSessionAsExist(value);
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				
				if(count != 0){
					//avviso l'utente che ï¿½ giï¿½ esistente una sessione con il nome che vuole inserire
					Toast.makeText(getApplicationContext(), "Name not avaiable, please choose a different name", Toast.LENGTH_LONG).show();
					cloneRec(p);
					return;
				}
				//Ottengo la data e l'ora corrente
				Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
			    minute = c.get(Calendar.MINUTE);
			    second = c.get(Calendar.SECOND);
			    day = c.get(Calendar.DATE);
			    month = c.get(Calendar.MONTH)+1;
			    year = c.get(Calendar.YEAR);
			    
			    date = day +"/"+ month+"/"+ year +" - "+ hour+":"+ minute +":"+ second;
				
			    //creazione nuova immagine
			    creaThumbNail(day, month, year, hour, minute, second, loc_o, new_filename);
			    
			    databaseHelper.createSession(new_filename, loc_o, date, date, image_o, sample_o, x_o, y_o, z_o);
			    
				databaseHelper.close();
				cursor.close();
				
				try {
					writer = new FileWriter(new File(loc_o, new_filename+".txt"), true);
					cloneFileToFile(loc_o, name_o+".txt");
					writer.close();
				} catch (IOException e) {
					Log.d("FileWriter",e.getMessage());
					e.printStackTrace();
				}
								
				finish();
				startActivity(getIntent());					
			}
		});
		
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
//						onBackPressed();
					}
				});
		
		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
	
	public void cloneFileToFile(String filepath, String fileName) {//Legge file come stringa
        String line = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(filepath,fileName)));
            while ((line = in.readLine()) != null) 
            	writer.write(line+"\n");
            in.close();
        } catch (FileNotFoundException e) {
           Log.d("cloneFileToFile", "File Not Found!");
        } catch (IOException e) {
            Log.d("cloneFileToFile", e.getMessage());
        } 
    }

	private void deleteRec(int position) {
		databaseHelper.open();
		cursor = databaseHelper.fetchIdSession(list_music[position]);
		cursor.moveToFirst();
		pos = cursor.getInt(0);
		loc = cursor.getString(1);
		//cancello il file txt registrato
		File file = new File(loc, list_music[position]+".txt");
		file.delete();
		//cancello il file della sua immagine corrispondente
		File file_im = new File(loc, list_music[position]+".png");
		file_im.delete();
		databaseHelper.deleteSession(pos);
		databaseHelper.close();
		cursor.close();
		finish();
		startActivity(getIntent());
	}
	
	private void renameRec(int position) {//TODO: Sistemare, non funzionante
		p = position;
//		faccio un semplice update
		databaseHelper.open();
		cursor = databaseHelper.fetchASession(list_music[position]);
		
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage("Insert Session Name")
		       .setTitle("Rename Session");
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		//Input filter to accept only letter or digit
//		InputFilter filter = new InputFilter() { 
//			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
//				for (int i = start; i < end; i++) { 
//					if (!Character.isLetterOrDigit(source.charAt(i))) { 
//						return ""; 
//					} 
//				} 
//				return null; 
//			} 
//		};
//		//Input filter per accettare file al massimo di 10 caratteri
//		InputFilter lenghtfilter = new InputFilter.LengthFilter(10);
//		input.setFilters(new InputFilter[]{filter,lenghtfilter});
		input.setFilters(getFilter());
		builder.setView(input);
		
		builder.setPositiveButton(android.R.string.yes,new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
		        // Do something with value!   
				String value="";
				value = input.getText().toString().toLowerCase(Locale.getDefault());
				
				try {
					value = value.substring(0, 1).toUpperCase(
							Locale.getDefault())
							+ value.substring(1).toLowerCase(
									Locale.getDefault());
				} catch (java.lang.StringIndexOutOfBoundsException e)// caso stringa vuota
				{
					value = "Rec";
				}
				new_filename = value;
				
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
					//avviso l'utente che ï¿½ giï¿½ esistente una sessione con il nome che vuole inserire
					Toast.makeText(getApplicationContext(), "Name not avaiable, please choose a different name", Toast.LENGTH_LONG).show();
					renameRec(p);
					return;
				}
			    databaseHelper.updateSession(id_o, new_filename, loc_o, date_co, date_lmo, image_o, sample_o, x_o, y_o, z_o);
			    
				databaseHelper.close();
				cursor.close();
				
				File from = new File(loc_o,name_o+".txt");
				File to = new File(loc_o,new_filename+".txt");
				from.renameTo(to);

				File from_im = new File(loc_o,name_o+".png");
				File to_im = new File(loc_o,new_filename+".png");
				from_im.renameTo(to_im);
				
				finish();
				startActivity(getIntent());
			}
		});
		
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
//						onBackPressed();
					}
				});
		
		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		
		dialog.show();
	}
	//pisello
	
	public static InputFilter[] getFilter(){
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
		//Input filter per accettare file al massimo di 10 caratteri
		InputFilter lenghtfilter = new InputFilter.LengthFilter(10);
		return new InputFilter[]{filter,lenghtfilter};
	}
	
	private void playRec(int position) {
		runUI4(position);
	}
	
	private void detailsRec(int position) {
		runUI2(position);
	}
	
	private void runUI2(int position){
		Intent intent = new Intent(UI1.this, UI2.class);
		intent.putExtra(EXTRA_MESSAGE, list_music[position]);
		startActivity(intent);	
	}
	
	private void runUI4(int position){
		Intent intent = new Intent(getApplicationContext(), UI4.class);
	    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
	    intent.putExtra("my",true);
	    startActivity(intent);
	}

	 //crea la thumbnail
		public void creaThumbNail(int da, int m, int y, int h, int mi, int s, String filepath, String filename) {
			
			 Bitmap bitmap = null;
			 Bitmap temp1;
			// Variabili che variano da 0 a 255
			int a, b, c, d, e, f;
			
			e= da*255/31;
			d= m*255/12;
			a= y%255;
			c= h*255/60;
			b= mi*255/60;
			f= s*255/60;
		
			//	Log.d("Valori:",a+" "+b+" "+c+" "+d+" "+e+" "+f);
			
			// Inizializza colori
			int primo, secondo, terzo, quarto, quinto, sesto;
//			primo =   Color.rgb(a, b, c); //sfondo
//			secondo = Color.rgb(d, e, f); //2
//			terzo =   Color.rgb(a, c, e); //3
//			quarto =  Color.rgb(b, d, f); //1
//			quinto =  Color.rgb(f, e, c); //altoparlante interno
//			sesto =   Color.rgb(a, d, b); //altoparlante
			primo =   Color.rgb(a, b, f); //sfondo
			secondo = Color.rgb(d, e, f); //2
			terzo =   Color.rgb(c, f, e); //3
			quarto =  Color.rgb(f, d, b); //1
			quinto =  Color.rgb(f, e, c); //altoparlante interno
			sesto =   Color.rgb(a, d, b); //altoparlante
		
			//Prende la bitmap dalla cartella res/raw/
		    try {
		      bitmap = BitmapFactory.decodeResource(getResources(),R.raw.icon_trasp_play);
		    	} 
		    catch (Exception ex) {
		      ex.printStackTrace();
		    }	
		    
		   temp1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		   
		   //Scrive nuova bitmap in base ai colori
		   int pixels[]= new int[bitmap.getWidth()*bitmap.getHeight()];
		   bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		    for (int i=0; i<pixels.length; i++){
		    	if (pixels[i]==Color.WHITE){
		    		pixels[i]=primo;
		    	}
		    	else if (pixels[i]==Color.BLUE){
		    		pixels[i]=secondo;
		    	}
		    	else if (pixels[i]==Color.GREEN){
		    		pixels[i]=terzo;
		    	}
		    	else if (pixels[i]==Color.YELLOW){
		    		pixels[i]=quarto;
		    	}
		    	else if (pixels[i]==Color.BLACK){
		    		pixels[i]=quinto;
		    	}
		    	else if (pixels[i]==Color.RED){
		    		pixels[i]=sesto;
		    	}
		    	else if (pixels[i]==Color.TRANSPARENT){
		    		pixels[i]=Color.TRANSPARENT;
		    	}
		    	else pixels[i]=primo;
		    	
		    }
		    temp1.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		    
		    File pictureFile = new File(filepath, filename + ".png");
			
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				if (!temp1.compress(Bitmap.CompressFormat.PNG, 100, fos))
					Log.d("storeImage", "Error compressing file!");
				fos.close();
			} catch (IOException ex) {
				Log.d("storeImage", ex.getMessage());
			}// fine Try Catch
		}
		
}
