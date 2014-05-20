package com.example.musicmoves;





import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UI3 extends ActionBarActivity implements SensorEventListener {
	String filename;
	String filepath="/sdcard/";
	
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
				LockScreenRotation();
				 // Do something with value!   
				String value="";
				
				value = input.getText().toString().toLowerCase();
				
				int recCounter = 0; //da rendere globale
				try {value = value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();}
				catch(java.lang.StringIndexOutOfBoundsException e){
					value = "Rec_"+recCounter;
					}
				filename = value+".txt";
				//Ottengo la data e l'ora corrente
				Calendar c = Calendar.getInstance();
				int hour = c.get(Calendar.HOUR_OF_DAY);
			    int minute = c.get(Calendar.MINUTE);
			    int second = c.get(Calendar.SECOND);
			    int date = c.get(Calendar.DATE);
			    int month = c.get(Calendar.MONTH);
			    int year = c.get(Calendar.YEAR);
			    
				value = value +"\n"+ date +"/"+ month +"/"+ year +" - "+ hour+":"+ minute +":"+ second +"\n" +"Rec "+ recCounter;
				
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
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;// = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	private FileWriter writer;
	
	
	public void Recording(View view) { //Cambia pulsanti visibili, crea il FileWriter, ascolta i dati accelerometro
		Toast.makeText(getApplicationContext(), "Recording", Toast.LENGTH_SHORT).show();
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
			writer = new FileWriter(new File(filepath, filename), true);
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
		mSensorManager.unregisterListener(this);
		if(writer != null) {
		       try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	}
		
		Toast.makeText(getApplicationContext(), "Maximum range:"+mAccelerometer.getMaximumRange()+
					   "\n"+readFileAsStringMod(filename), Toast.LENGTH_LONG).show();
		
		
		UnlockScreenRotation();
//		getApplicationContext().deleteFile(filepath);
	}
	
	public String accelToFreq(String accel){ //Aggiunge determinati valori a quelli dell'accelerometro
		String[] coord = accel.split(",");
		double[] freq = new double[3] ;
		freq [0] = (Double.parseDouble(coord[0])) + 440.0; //aggiunge freq La
		freq [1] = Double.parseDouble(coord[1]) + 329.0; //aggiunge freq Mi
		freq [2] = Double.parseDouble(coord[2]) + 392.0; //agguinge freq Sol
		
		String[] s = new String[freq.length];
		StringBuffer result = new StringBuffer();
			for (int i = 0; i < s.length; i++){
			    s[i] = String.valueOf(freq[i]);
				result.append( s[i] );
				result.append( " - " );
			}
		String doubleTripletString = result.toString();
		return doubleTripletString;
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
	}

//	public String readFileAsString(String fileName) {//Legge file come stringa
//        Context context = getApplicationContext();
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        BufferedReader in = null;
//
//        try {
//            in = new BufferedReader(new FileReader(new File(context.getFilesDir(), fileName)));
//            while ((line = in.readLine()) != null) stringBuilder.append(line+"\n");
//            in.close();
//        } catch (FileNotFoundException e) {
//           
//        } catch (IOException e) {
//            
//        } 
//        
//        return stringBuilder.toString();
//    }
	
	public String readFileAsStringMod(String fileName) {//Legge file come stringa e modifica dato accel
													 //aggiungendo una certa frequenza 
        Context context = getApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(new File(filepath, fileName)));
            while ((line = in.readLine()) != null) stringBuilder.append(accelToFreq(line)+"\n");
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