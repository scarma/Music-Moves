package com.example.musicmoves;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Environment;
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
	String filepath= Environment.getExternalStorageDirectory().getPath()+"/MusicMoves";
	
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
		Calendar c = Calendar.getInstance();
		int hour_m = c.get(Calendar.HOUR_OF_DAY);
	    int minute_m = c.get(Calendar.MINUTE);
	    int second_m = c.get(Calendar.SECOND);
	    int day_m = c.get(Calendar.DATE);
	    int month_m = c.get(Calendar.MONTH);
	    int year_m = c.get(Calendar.YEAR);
	    String date_m = day_m +"/"+ month_m +"/"+ year_m +" - "+ hour_m+":"+ minute_m +":"+ second_m;
	    
		Toast.makeText(getApplicationContext(), "Recorded\n"+readFileAsString(filename)+"\n"+date_m + filepath, Toast.LENGTH_LONG).show();
		
		//Inserimento dati nel database
		databaseHelper = new DBAdapter(getApplicationContext());
		databaseHelper.open();
		databaseHelper.createSession(filename, filepath+"/", date, date_m, "ls", 100, 1, 1, 1);
		databaseHelper.close();
//		proSoundGenerator(filename);
		
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
	
//	public void proSoundGenerator(String textFile) {//Legge file come stringa e modifica dato accel
//													 //aggiungendo una certa frequenza 
//        
//        String line="";
//        double[] x;
//        double[] y;
//        double[] z;
//        int cnt = 0;
//        try{
//        	BufferedReader in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
//        	while ((line = in.readLine()) != null)
//        	{cnt++;}
//        	x = new double[cnt];
//        	y = new double[cnt];
//        	z = new double[cnt];
//        	in = new BufferedReader(new FileReader(new File(filepath, textFile+".txt")));
//        	for(int i=0; i<cnt; i++)
//        		{
//        		line = in.readLine();
//        		String[] coord = line.split(",");
//        		x[i] = (Double.parseDouble(coord[0])*10) + 440.0 ; //aggiunge freq La4 ai dati dell'asse x
//        		y[i] = (Double.parseDouble(coord[1])*10) + 698.0; //aggiunge freq Fa5 ai dati dell'asse y
//        		z[i] = (Double.parseDouble(coord[2])*10) + 880.0; //aggiunge freq La5 ai dati dell'asse z
//        	}
//        	playSound(genTone(x,cnt)); //Genera suono per l'asse x
//        	playSound(genTone(y,cnt)); //Genera suono per l'asse y
//        	playSound(genTone(z,cnt)); //Genera suono per l'asse z
//        	in.close();
//        } catch (FileNotFoundException e) {
//        
//        } catch (IOException e) {
//        
//        } 
//    }
//	
//	/*-- MUSIC generation --*/
////	public int getDuration()	{return duration;}
////	public int getSampleRate()	{return sampleRate;}
////	public double getFreqOfTone()	{return freqOfTone;}
////	public void setDuration(int dur)	{if(dur>=1 && dur<=100) duration=dur; else duration=3;}
////	public void setSampleRate(int sampleR)	{if(sampleR>=4000 && sampleR<=10000) sampleRate=sampleR; else sampleRate=8000;}
////	public void setFreq(int freq)	{if(freq>=200 && freq<=3000) freqOfTone=freq; else freqOfTone=440;}
////    private int duration = 3; // seconds
////    private int numSamples = duration * sampleRate;
////    private double sample[] = new double[numSamples];
//    private double freqOfTone; // hz //200-3000 range consigliato
//    private int sampleRate = 8000;
//    private int upsampling = 200;
//    Handler handler = new Handler(); 
//    private byte[] generatedArray;
//    
//    public byte[] genTone(double[] x, int cnt){
//        // fill out the array
//    	int numSamples = 10*cnt*upsampling;
//        double sample[] = new double[numSamples];
//    	for (int i = 0; i < (10*cnt*upsampling); ++i) { 
//        	if ((i%(10*upsampling))==0) //inserisce dati accelerometro nell'array
//        		{ freqOfTone = x[i/(10*upsampling)];}
//            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
//        }
//    	byte generatedSnd[] = new byte[2 * 10*cnt*upsampling];
//        // convert to 16 bit pcm sound array
//        // assumes the sample buffer is normalised.
//        int idx = 0;
//        for (final double dVal : sample) {
//            // scale to maximum amplitude
//            final short val = (short) ((dVal * 32767));
//            // in 16 bit wav PCM, first byte is the low order byte
//            generatedSnd[idx++] = (byte) (val & 0x00ff);
//            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
//        }
//        return generatedSnd;
//    }
//    
//    public synchronized void playSound(byte[] generatedSnd){
//		generatedArray = generatedSnd;
//		Thread thread = new Thread(new Runnable() {
//	        public void run() {
//	        
//	        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//	                sampleRate, AudioFormat.CHANNEL_OUT_MONO ,
//	                AudioFormat.ENCODING_PCM_16BIT, generatedArray.length,
//	                AudioTrack.MODE_STATIC);
//	        audioTrack.write(generatedArray, 0, generatedArray.length);
//		       
//	        if(audioTrack.getState()==AudioTrack.STATE_INITIALIZED){
//	        	audioTrack.play();	
//	        }  
//		    else{
//			        Log.d("AudioTrack", "Audiotrack not initialized");
//		        }
//	        float time = (float)generatedArray.length/audioTrack.getSampleRate()/2;
//	        Log.d("AudioTrack", "Time: "+time);
//	        }
//		});
//		thread.start();
//	}
//	

    

    
	
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
	
	//--CODICE PER ZAMPA--
//	package com.example.pixels;
//
//
//	import java.util.ArrayList;
//	import android.annotation.SuppressLint;
//	import android.app.Activity;
//	import android.content.Context;
//	import android.graphics.Bitmap;
//	import android.graphics.BitmapFactory;
//	import android.graphics.Canvas;
//	import android.graphics.Color;
//	import android.os.Bundle;
//	import android.os.Debug;
//	import android.view.MotionEvent;
//	import android.view.SurfaceHolder;
//	import android.view.SurfaceView;
//	import android.view.Window;
//
//	public class MainActivity extends Activity {
//	    @Override
//	    public void onCreate(Bundle savedInstanceState) {
//	        super.onCreate(savedInstanceState);
//	        requestWindowFeature(Window.FEATURE_NO_TITLE);
//	        setContentView(new Panel(this));
//	        Debug.startMethodTracing("pixeldraw");
//	    }
//
//	    @Override
//	    protected void onDestroy() {
//	        super.onDestroy();
//	        Debug.stopMethodTracing();
//	    }
//
//	    class Panel extends SurfaceView implements SurfaceHolder.Callback {
//	        private TutorialThread _thread;
//	        private Bitmap _buffer;
//	        private ArrayList<GraphicObject> _graphics = new ArrayList<GraphicObject>();
//	        private int width = 40;
//	        private int height = 40;
//	        public Panel(Context context) {
//	            super(context);
//	            getHolder().addCallback(this);
//	            _thread = new TutorialThread(getHolder(), this);
//	            setFocusable(true);
//	        }
//
//	        @Override
//	        public boolean onTouchEvent(MotionEvent event) {
//	            synchronized (_thread.getSurfaceHolder()) {
//	                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//	                    GraphicObject graphic = new GraphicObject(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//	                    graphic.getCoordinates().setX((int) event.getX() - graphic.getGraphic().getWidth() / 2);
//	                    graphic.getCoordinates().setY((int) event.getY() - graphic.getGraphic().getHeight() / 2);
//	                    _graphics.add(graphic);
//	                }
//	                return true;
//	            }
//	        }
//	        long lastTime = System.currentTimeMillis();
//	        int CLOCKS_PER_SEC = 500;
//	        float timeCnt = 0;
//	        @Override
//	        public void onDraw(Canvas canvas) {
////	            canvas.drawColor(Color.BLACK);
//	            width = getWidth()/2;
//	            height = getHeight()/2;
//
//	            long frameTime = System.currentTimeMillis();
//
//	            // The elapsed seconds per frame will almost always be less than 1.0.
//	            float elapsedSeconds = (float)(frameTime - lastTime) / CLOCKS_PER_SEC;
//
//	            int colors[] = new int[width*height];
//	            for (int x = 0; x<width ; x++) {
//	                for (int y = 0; y<height ; y++){
//	                    int r = (int)(timeCnt/5*255);
//	                    int b = 0;
//	                    int g = 0;
//	                    int a = 255;
//	                    colors[x + y * width] = (a << 24) | (r << 16) | (g << 8) | b;
//	                }
//	            }
//	            for (int x = 0; x<10 ; x++) {
//	                for (int y = 0; y<height; y++){
//	                    int r = 0;
//	                    int b = 255;
//	                    int g = 0;
//	                    int a = 255;
//	                    colors[(int)(timeCnt*32)+x + y * width] = (a << 24) | (r << 16) | (g << 8) | b;
//	                }
//	            }
//	            _buffer = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
//	            canvas.drawBitmap(_buffer, 0, 0, null);
//
//	            timeCnt += elapsedSeconds;
//	            if (timeCnt > 5) timeCnt = 0;
//	            // Update the last time counter so that we can use it next frame.
//	            lastTime = frameTime;
//
//	        }
//
//	        @Override
//	        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//	            // TODO Auto-generated method stub
//	        }
//
//	        @Override
//	        public void surfaceCreated(SurfaceHolder holder) {
//	            _thread.setRunning(true);
//	            _thread.start();
//	        }
//
//	        @Override
//	        public void surfaceDestroyed(SurfaceHolder holder) {
//	            // simply copied from sample application LunarLander:
//	            // we have to tell thread to shut down & wait for it to finish, or else
//	            // it might touch the Surface after we return and explode
//	            boolean retry = true;
//	            _thread.setRunning(false);
//	            while (retry) {
//	                try {
//	                    _thread.join();
//	                    retry = false;
//	                } catch (InterruptedException e) {
//	                    // we will try it again and again...
//	                }
//	            }
//	        }
//	    }
//
//	    class TutorialThread extends Thread {
//	        private SurfaceHolder _surfaceHolder;
//	        private Panel _panel;
//	        private boolean _run = false;
//
//	        public TutorialThread(SurfaceHolder surfaceHolder, Panel panel) {
//	            _surfaceHolder = surfaceHolder;
//	            _panel = panel;
//	        }
//
//	        public void setRunning(boolean run) {
//	            _run = run;
//	        }
//
//	        public SurfaceHolder getSurfaceHolder() {
//	            return _surfaceHolder;
//	        }
//
//	        @SuppressLint("WrongCall")
//			@Override
//	        public void run() {
//	            Canvas c;
//	            while (_run) {
//	                c = null;
//	                try {
//	                    c = _surfaceHolder.lockCanvas(null);
//	                    synchronized (_surfaceHolder) {
//	                        _panel.onDraw(c);
//	                    }
//	                } finally {
//	                    // do this in a finally so that if an exception is thrown
//	                    // during the above, we don't leave the Surface in an
//	                    // inconsistent state
//	                    if (c != null) {
//	                        _surfaceHolder.unlockCanvasAndPost(c);
//	                    }
//	                }
//	            }
//	        }
//	    }
//
//	    class GraphicObject {
//	        /**
//	         * Contains the coordinates of the graphic.
//	         */
//	        public class Coordinates {
//	            private int _x = 100;
//	            private int _y = 0;
//
//	            public int getX() {
//	                return _x + _bitmap.getWidth() / 2;
//	            }
//
//	            public void setX(int value) {
//	                _x = value - _bitmap.getWidth() / 2;
//	            }
//
//	            public int getY() {
//	                return _y + _bitmap.getHeight() / 2;
//	            }
//
//	            public void setY(int value) {
//	                _y = value - _bitmap.getHeight() / 2;
//	            }
//
//	            public String toString() {
//	                return "Coordinates: (" + _x + "/" + _y + ")";
//	            }
//	        }
//
//	        private Bitmap _bitmap;
//	        private Coordinates _coordinates;
//
//	        public GraphicObject(Bitmap bitmap) {
//	            _bitmap = bitmap;
//	            _coordinates = new Coordinates();
//	        }
//
//	        public Bitmap getGraphic() {
//	            return _bitmap;
//	        }
//
//	        public Coordinates getCoordinates() {
//	            return _coordinates;
//	        }
//	    }
//	    
//	}

}
