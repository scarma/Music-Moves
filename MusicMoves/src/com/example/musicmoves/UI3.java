package com.example.musicmoves;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
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
	String filepath="/sdcard/MusicMoves";
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private FileWriter writer;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private int day = 0;
	private int month = 0;
	private int year = 0;
	private String date = null;
	private DBAdapter databaseHelper;
	private Cursor cursor;
	private int recCounter = 0; //salvare stato
	private int sampleCnt = 0;
	private VerticalProgressBar progressBarX;
    private VerticalProgressBar progressBarY;
    private VerticalProgressBar progressBarZ;
    
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		//Crea cartella in cui salvare i file
		File folder = new File(filepath);
	    boolean success = true;
	    if (!folder.exists()) {
	        Toast.makeText(UI3.this, "Directory Does Not Exist, I Create It", Toast.LENGTH_SHORT).show();
	        success = folder.mkdir();
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
				
				value = input.getText().toString().toLowerCase();
				
				
				
				try {value = value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();}
				catch(java.lang.StringIndexOutOfBoundsException e){
					value = "Rec_"+recCounter;
					}
				
				//controllo che il nuovo nome non sia già presente nel db
				databaseHelper = new DBAdapter(getApplicationContext());
				databaseHelper.open();
				cursor = databaseHelper.NameSessionAsExist(value);
				cursor.moveToFirst();
				int count = cursor.getInt(0);
				databaseHelper.close();
				cursor.close();
				
				if(count != 0){
					//avviso l'utente che è già esistente una sessione con il nome che vuole inserire
					Toast.makeText(getApplicationContext(), "Il nome inserito è già presente ! Riprova !", Toast.LENGTH_LONG).show();
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
		
//	    progressBarX.setProgress(20);
//	    progressBarY.setProgress(20);
//	    progressBarZ.setProgress(20);
	    
	    ImageButton rec = (ImageButton) findViewById(R.id.recButton);
		ImageButton pause = (ImageButton) findViewById(R.id.pauseButton);
		ImageButton stopUns = (ImageButton) findViewById(R.id.stop_unselectedButton);
		ImageButton stopSel = (ImageButton) findViewById(R.id.stop_selectedButton);
		   
		rec.setVisibility(View.INVISIBLE);
		pause.setVisibility(View.VISIBLE);
		stopUns.setVisibility(View.INVISIBLE);
		stopSel.setVisibility(View.VISIBLE);
		
		Context context = getApplicationContext();
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
		Calendar c = Calendar.getInstance();
		int hour_m = c.get(Calendar.HOUR_OF_DAY);
	    int minute_m = c.get(Calendar.MINUTE);
	    int second_m = c.get(Calendar.SECOND);
	    int day_m = c.get(Calendar.DATE);
	    int month_m = c.get(Calendar.MONTH);
	    int year_m = c.get(Calendar.YEAR);
	    String date_m = day_m +"/"+ month_m +"/"+ year_m +" - "+ hour_m+":"+ minute_m +":"+ second_m;
	    Context context = getApplicationContext();
	   
		Toast.makeText(getApplicationContext(), "Recorded\n"+readFileAsString(filename+".txt")+"\n"+date_m + filepath, Toast.LENGTH_LONG).show();
		
		//Inserimento dati nel database
		databaseHelper = new DBAdapter(getApplicationContext());
		databaseHelper.open();
		databaseHelper.createSession(filename, filepath+"/", date, date_m, "ls", 100, 1, 1, 1);
		databaseHelper.close();
		proSoundGenerator(filename+".txt");
		
		UnlockScreenRotation(); //Permetto rotazione
	}

	public void proSoundGenerator(String textFile) {//Legge file come stringa e modifica dato accel
													 //aggiungendo una certa frequenza 
        
        StringBuilder stringBuilder = new StringBuilder();
        String line="";
        double[] x;
        double[] y;
        double[] z;
        int cnt = 0;
        try{
        	BufferedReader in = new BufferedReader(new FileReader(new File(filepath, textFile)));
        	while ((line = in.readLine()) != null)
        	{cnt++;}
        	x = new double[cnt];
        	y = new double[cnt];
        	z = new double[cnt];
        	in = new BufferedReader(new FileReader(new File(filepath, textFile)));
        	for(int i=0; i<cnt; i++)
        		{
        		line = in.readLine();
        		String[] coord = line.split(",");
        		x[i] = (Double.parseDouble(coord[0])*10) + 440.0 ; //aggiunge freq La4 ai dati dell'asse x
        		y[i] = (Double.parseDouble(coord[1])*10) + 698.0; //aggiunge freq Fa5 ai dati dell'asse y
        		z[i] = (Double.parseDouble(coord[2])*10) + 880.0; //aggiunge freq La5 ai dati dell'asse z
        	}
        	playSound(genTone(x,cnt)); //Genera suono per l'asse x
        	playSound(genTone(y,cnt)); //Genera suono per l'asse y
        	playSound(genTone(z,cnt)); //Genera suono per l'asse z
        	in.close();
        } catch (FileNotFoundException e) {
        
        } catch (IOException e) {
        
        } 
    }
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {//Quando l'accel. ascolta scrive su file
		
	    float x = event.values[0];
	    float y = event.values[1];
	    float z = event.values[2];
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
//	    int xx = 20+(int)x; 	//Aggiornamento progress bar (non va su android 2.3.6!)
//	    int yy = 20+(int)y;
//	    int zz = 20+(int)z;
//	    progressBarX = (VerticalProgressBar) findViewById(R.id.progressX);
//		progressBarY = (VerticalProgressBar) findViewById(R.id.progressY);
//		progressBarZ = (VerticalProgressBar) findViewById(R.id.progressZ);
//		if (sampleCnt%10==0){prog((int)x,(int)y,(int)z);}
//    	progressBarX.setProgress(0);
//	    progressBarX.setMax(40);
	    progressBarX.setProgress(20+(int)x);
	   
//	    progressBarY.setProgress(0);
//	    progressBarY.setMax(40);
	    progressBarY.setProgress(20+(int)y);
	    
//	    progressBarZ.setProgress(0);
//	    progressBarZ.setMax(40);
	    progressBarZ.setProgress(20+(int)z);
	    
//	    progressBarX.updateThumb();
//	    progressBarY.updateThumb();
//	    progressBarZ.updateThumb();
//		  
	}
//	public synchronized void prog(int x, int y, int z) { 
//		progressBarX.setProgress(20+x);
//		progressBarY.setProgress(20+y);
//		progressBarZ.setProgress(20+z);
//	}

	public String readFileAsString(String fileName) {//Legge file come stringa
        StringBuilder stringBuilder = new StringBuilder();
        String line="";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(filepath, fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(line+"\n");
            in.close();
        } catch (FileNotFoundException e) {
           
        } catch (IOException e) {
            
        } 
        
        return stringBuilder.toString();
    }
	
//	public String readFileAsStringMod(String fileName) {//Legge file come stringa e modifica dato accel
//													 //aggiungendo una certa frequenza 
//        Context context = getApplicationContext();
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        BufferedReader in = null;
//
//        try { 
//        	File text;
//        	boolean exists = (new File(filepath, filename)).exists();  
//        	if (!exists){ 
//        		text = new File(filepath, filename);
//        		text.mkdirs();
//        	} 
//        	else { 
//        		Log.d("Creation text problem","Filename already in use");
//        		text = new File(filepath, "Gianni.txt");
//        	}
//        	in = new BufferedReader(new FileReader(text));
//        	while ((line = in.readLine()) != null) {
//        		stringBuilder.append(accelToFreq(line)+"\n");
//        	}
//        	in.close();
//        } 
//        
//        catch (FileNotFoundException e) {
//        	Log.d("Creation text problem","File not found");
//        } 
//        
//        catch (IOException e) {
//        	Log.d("Creation text problem","IOException");
//        } 
//        
//        return stringBuilder.toString();
//    }
	
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
	
	/*-- MUSIC generation --*/
//	public int getDuration()	{return duration;}
//	public int getSampleRate()	{return sampleRate;}
//	public double getFreqOfTone()	{return freqOfTone;}
//	public void setDuration(int dur)	{if(dur>=1 && dur<=100) duration=dur; else duration=3;}
//	public void setSampleRate(int sampleR)	{if(sampleR>=4000 && sampleR<=10000) sampleRate=sampleR; else sampleRate=8000;}
//	public void setFreq(int freq)	{if(freq>=200 && freq<=3000) freqOfTone=freq; else freqOfTone=440;}
//    private int duration = 3; // seconds
//    private int numSamples = duration * sampleRate;
//    private double sample[] = new double[numSamples];
    private double freqOfTone; // hz //200-3000 range consigliato
    private int sampleRate = 8000;
    private int upsampling = 200;
    Handler handler = new Handler(); 
    private byte[] generatedArray;
    
    public byte[] genTone(double[] x, int cnt){
        // fill out the array
    	int numSamples = 10*cnt*upsampling;
        double sample[] = new double[numSamples];
    	for (int i = 0; i < (10*cnt*upsampling); ++i) { 
        	if ((i%(10*upsampling))==0) //inserisce dati accelerometro nell'array
        		{ freqOfTone = x[i/(10*upsampling)];}
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }
    	byte generatedSnd[] = new byte[2 * 10*cnt*upsampling];
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return generatedSnd;
    }
    
	void playSound(byte[] generatedSnd){
		generatedArray = generatedSnd;
		Thread thread = new Thread(new Runnable() {
	        public void run() {
	        
	        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	                sampleRate, AudioFormat.CHANNEL_OUT_MONO ,
	                AudioFormat.ENCODING_PCM_16BIT, generatedArray.length,
	                AudioTrack.MODE_STATIC);
	        audioTrack.write(generatedArray, 0, generatedArray.length);
		       
	        if(audioTrack.getState()==AudioTrack.STATE_INITIALIZED){
	        	audioTrack.play();	
	        }
	        
		        
		        else{
			        Log.d("AudioTrack", "Audiotrack not initialized");
		        }   
			}
		});
		thread.start();
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
