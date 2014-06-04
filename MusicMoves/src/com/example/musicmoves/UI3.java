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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import database.DBAdapter;

public class UI3 extends ActionBarActivity implements SensorEventListener {
	private String filename;
	String filepath= Environment.getExternalStorageDirectory().getPath()+"/MusicMoves";
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private FileWriter writer;
	private int hour;
	private int minute;
	private int second;
	private int day;
	private int month;
	private int year;
	private String date = null;
	private DBAdapter databaseHelper;
	private Cursor cursor;
	private int recCounter = 0; //salvare stato
	private int sampleCnt = 0;
	private VerticalProgressBar progressBarX;
    private VerticalProgressBar progressBarY;
    private VerticalProgressBar progressBarZ;
    Bitmap bitmap = null;
    Bitmap temp1;
    Calendar c = Calendar.getInstance();
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//Crea cartella in cui salvare i file
		File folder = new File(filepath);
	    if (!folder.exists()) {
	        Toast.makeText(UI3.this, "Directory Does Not Exist, I Create It", Toast.LENGTH_SHORT).show();
	        folder.mkdir();
	    }
	    
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
				LockScreenRotation();
				 // Do something with value!   
				String value="";
				
				value = input.getText().toString().toLowerCase(Locale.getDefault());
				try {value = value.substring(0,1).toUpperCase(Locale.getDefault()) + value.substring(1).toLowerCase(Locale.getDefault());}
				catch(java.lang.StringIndexOutOfBoundsException e){
					value = "Rec_"+recCounter;
					}
				
				//controllo che il nuovo nome non sia gi� presente nel db
				databaseHelper = new DBAdapter(getApplicationContext());
				databaseHelper.open();
				cursor = databaseHelper.NameSessionAsExist(value);
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				databaseHelper.close();
				cursor.close();
				
				if(count != 0){
					//avviso l'utente che � gi� esistente una sessione con il nome che vuole inserire
					Toast.makeText(getApplicationContext(), "Name not avaiable, please choose a different name", Toast.LENGTH_LONG).show();
					finish();
					startActivity(getIntent());
				}
				
				filename = value;
				//Ottengo la data e l'ora corrente
				Calendar c = Calendar.getInstance();
				hour = c.get(Calendar.HOUR_OF_DAY);
			    minute = c.get(Calendar.MINUTE);
			    second = c.get(Calendar.SECOND);
			    day = c.get(Calendar.DATE);
			    month = c.get(Calendar.MONTH);
			    year = c.get(Calendar.YEAR);
			    
			    date = day +"/"+ month+"/"+ year +" - "+ hour+":"+ minute +":"+ second;
				value = value +"\n"+ day +"/"+ month +"/"+ year +" - "+ hour+":"+ minute +":"+ second +"\n" +"Rec "+ recCounter;
				
				TextView textView = (TextView) findViewById(R.id.textViewNewRecName);
			    textView.setTextSize(25);
			    textView.setText(value);
			    textView.setTextColor(Color.rgb(255, 153, 0));
			    recCounter++;
			  }
			});

		alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				 onBackPressed();
				    
			  }
			});
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog){onBackPressed();}
		});
		
		alert.show();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui3);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        
//		if (savedInstanceState == null) {	//duplicava il layout con questo
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
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
			startActivity(settings_intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		Intent settings_intent = new Intent(getApplicationContext(), UI1.class);
		startActivity(settings_intent);
		finish();
	}

//	/**
//	 * A placeholder fragment containing a simple view.
//	 */
//	public static class PlaceholderFragment extends Fragment {
//
//		public PlaceholderFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_ui3, container,
//					false);
//			return rootView;
//		}
//	}
	



	public void Recording(View view) { //Cambia pulsanti visibili, crea il FileWriter, ascolta i dati accelerometro
		Toast.makeText(getApplicationContext(), "Recording", Toast.LENGTH_SHORT).show();

	    progressBarX = (VerticalProgressBar) findViewById(R.id.progressX);
		progressBarY = (VerticalProgressBar) findViewById(R.id.progressY);
		progressBarZ = (VerticalProgressBar) findViewById(R.id.progressZ);
		
	    progressBarX.setVisibility(ProgressBar.VISIBLE);
	    progressBarY.setVisibility(ProgressBar.VISIBLE);
	    progressBarZ.setVisibility(ProgressBar.VISIBLE);
		
	    ImageButton rec = (ImageButton) findViewById(R.id.recButton);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseButton);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedButton);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedButton);
		   
		rec.setVisibility(View.INVISIBLE);
		pause.setVisibility(View.VISIBLE);
		stopUns.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.VISIBLE);
		
		super.onResume();
		
	    try {

			writer = new FileWriter(new File(filepath, filename+".txt"), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
		
	}
	
	public void Paused(View view) { //Cambia pulsanti visibili, chiude il FileWriter se aperto
		Toast.makeText(getApplicationContext(), "Recording paused", Toast.LENGTH_SHORT).show();	
		ImageButton rec = (ImageButton) findViewById(R.id.recButton);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseButton);
		pause.setVisibility(View.INVISIBLE);
		rec.setVisibility(View.VISIBLE);
		
		super.onPause();
		
	    if(writer != null) {
	       try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	}
	    mSensorManager.unregisterListener(this);
	}
	
	public void Stopped(View view) { //Cambia pulsanti visibili, chiude il FileWriter se aperto,
									 //mostra dati registrati e cancella il file
		
		ImageButton rec = (ImageButton) findViewById(R.id.recButton);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseButton);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedButton);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedButton);
		
		rec.setVisibility(View.INVISIBLE);
		pause.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.INVISIBLE);
		stopUns.setVisibility(View.VISIBLE);
		
		if(writer != null) {
		       try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	}
		mSensorManager.unregisterListener(this);

		//Ottengo la data e l'ora corrente
		
		 hour = c.get(Calendar.HOUR_OF_DAY);
	     minute = c.get(Calendar.MINUTE);
	     second = c.get(Calendar.SECOND);
	     day = c.get(Calendar.DATE);
	     month = c.get(Calendar.MONTH);
	     year = c.get(Calendar.YEAR);
	    String date_m = day +"/"+ month +"/"+ year +" - "+ hour+":"+ minute +":"+ second;
	    
		Toast.makeText(getApplicationContext(), "Recorded\n"+readFileAsString(filename)+"\n"+date_m + filepath, Toast.LENGTH_LONG).show();
		
		//Inserimento dati nel database
		databaseHelper = new DBAdapter(getApplicationContext());
		databaseHelper.open();
		databaseHelper.createSession(filename, filepath+"/", date, date_m, "ls", 100, 1, 1, 1);
		databaseHelper.close();
//		proSoundGenerator(filename);
		
		//crea la thumbnail univoca
		creaThumbNail();
		
		UnlockScreenRotation(); //Permetto rotazione
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {//Quando l'accel. ascolta scrive su file
		
	    final float x = event.values[0];
	    final float y = event.values[1];
	    final float z = event.values[2];
	    try {
			writer.write(x+", "+y+", "+z+"\n");
		} 
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    sampleCnt++;
	    TextView sampleCounter = (TextView)findViewById(R.id.textViewSampleCounter); //Contatore Samples
	    sampleCounter.setText("Samples recorded: "+sampleCnt);

	    progressBarX.setProgress(20+(int)x);
	    progressBarY.setProgress(20+(int)y);
	    progressBarZ.setProgress(20+(int)z);
	    
	    progressBarX.postInvalidate();
	    progressBarY.postInvalidate();
	    progressBarZ.postInvalidate(); 
	}

	public String readFileAsString(String fileName) {//Legge file come stringa
        StringBuilder stringBuilder = new StringBuilder();
        String line="";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(filepath, fileName+".txt")));
            while ((line = in.readLine()) != null) stringBuilder.append(line+"\n");
            in.close();
        } catch (FileNotFoundException e) {
           
        } catch (IOException e) {
            
        } 
        
        return stringBuilder.toString();
    }
	
	
	private void LockScreenRotation() { // Sets screen rotation as fixed to current rotation setting
		switch (this.getResources().getConfiguration().orientation)
		{   case Configuration.ORIENTATION_PORTRAIT:     
				this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;   
			case Configuration.ORIENTATION_LANDSCAPE:
				this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
		} 
	}
	
	private void UnlockScreenRotation(){ // allow screen rotations
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	

    //crea la thumbnail
	//metodo che salva un'immagine nella cartella MusicMoves
	private void storeImage(Bitmap image) {
		File pictureFile = new File(filepath, filename + ".png");
		
		try {
		FileOutputStream fos = new FileOutputStream(pictureFile);
		if (!image.compress(Bitmap.CompressFormat.PNG, 100, fos))
			Log.d("storeImage", "Error compressing file");
		fos.close();
		}
		catch (IOException e) {
		}//fine Try Catch
	}//fine storeImage
	
	
	protected void creaThumbNail() {
		int primo, secondo, terzo, quarto, quinto, sesto;
		int a, b, c, d, e, f;
		//I valori assumevano dati che non spaziavano da 1 a 255 (i mesi sono massimo 12)
		a= (year)%255;
		b= (int)(minute*255/60);
		c= (int)(hour*255/60);
		d= (int)(month*255/12);
		e= (int)(day*255/31);
		f= (int)(second*255/60);
		//		Log.d("Valori:",a+" "+b+" "+c+" "+d+" "+e+" "+f);
		
		// inizializza colori, da modificare
		primo =   Color.rgb(a, b, c);
		secondo = Color.rgb(d, e, f);
		terzo =   Color.rgb(a, c, e);
		quarto =  Color.rgb(b, d, f);
		quinto =  Color.rgb(f, e, c);
		sesto =   Color.rgb(a, d, b);
	

    	//per gli dei non cambiare ne rimuovere assolutamente
		
		//Prende la bitmap dalla cartela res/raw/
	    try {
	      bitmap = BitmapFactory.decodeResource(getResources(),R.raw.icon_trasp);
	    	} 
	    catch (Exception ex) {
	      ex.printStackTrace();
	    }	
	    
	   temp1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
	   


	    
	// primo metodo inefficiente, codice che cambia i colori della bitmap in modo univoco
//	for (int y = 0; y < bitmap.getWidth(); y++) {
//		for (int x = 0; x < bitmap.getHeight(); x++) {
//			
//			
//			
//				if(temp1.getPixel(x, y)==Color.RED){
//						temp1.setPixel(x, y, primo);
//					}
//				else if(temp1.getPixel(x, y)==Color.BLUE){
//						temp1.setPixel(x, y, secondo);
//					}
//				else if(temp1.getPixel(x, y)==Color.GREEN){
//						temp1.setPixel(x, y, terzo);
//					}
//				else if(temp1.getPixel(x, y)==Color.YELLOW){
//						temp1.setPixel(x, y, quarto);
//					}
//				else if(temp1.getPixel(x, y)==Color.BLACK){
//						temp1.setPixel(x, y, quinto);
//				}
//				else temp1.setPixel(x, y, sesto);
//		}//fine for
//	}//fine for
	
	 //
	//Nuovo metodo piu efficiente, sistemato a dovere
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
	   
	    
	storeImage(temp1);
	}
	
	
  
	
	
    
	
//	private void ThumbnailFromDateCreator(int date, int month, int year, int hour, int minute, int second){
//		Bitmap _buffer;
//        ArrayList<GraphicObject> _graphics = new ArrayList<GraphicObject>();
//        int width = 40;
//        int height = 40; 
//		int colors[] = new int[width*height];
//         for (int x = 0; x<width ; x++) {
//             for (int y = 0; y<height ; y++){
//                 int r = ((date/31)-(hour/24))*255;
//                 int b = ((month/12-(minute/60))*255;
//                 int g = ((year/1900-(second/60))*255;
//                 int a = 255;
//                 colors[x + y * width] = (a << 24) | (r << 16) | (g << 8) | b;
//             }
//         }
//         Canvas canvas=_surfaceHolder.lockCanvas(null);
//         _buffer = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
//         canvas.drawBitmap(_buffer, 0, 0, null);
//	}
	
}
