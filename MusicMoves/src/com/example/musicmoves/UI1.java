package com.example.musicmoves;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
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
import database.DBAdapter;

public class UI1 extends ListActivity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	private DBAdapter databaseHelper;
	private Cursor cursor;
	private String[] list_music;
	private UI1Adapter adapter;
	private int pos;
	private String loc;
	private String new_filename;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private int day = 0;
	private int month = 0;
	private int year = 0;
	private String date = null;
	
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui1);
		Context ctx = getApplicationContext();
        Resources res = ctx.getResources();
        
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
        
        TypedArray immagini = res.obtainTypedArray(R.array.immagini);
        
        adapter = new UI1Adapter(this, R.layout.riga_lista, list_music, immagini);
        setListAdapter(adapter);
        //da qui inseriamo il codice utile a mostrare un messaggio al click
        ListView listaV = getListView();
        listaV.setTextFilterEnabled(true);
        registerForContextMenu(listaV);
        
        listaV.setOnItemClickListener(new OnItemClickListener() {
            
        	@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		// vado a UI2 toccando elemento della lista
                Intent intent = new Intent(UI1.this, UI2.class);
        	    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
        	    startActivity(intent);	
            }
        });  
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
	    Intent intent = new Intent(getApplicationContext(), UI3.class);
	    startActivity(intent);
	    finish();
	}
	
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setTitle("Really Exit?")
	        .setMessage("Are you sure you want to exit?")
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
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
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.playB:
	        	playRec(info.position);
	            return true;
	        case R.id.delete:
	            deleteRec(info.position);
	            return true;
	        case R.id.clone:
	            cloneRec(info.position);
	        case R.id.rename:
	            renameRec(info.position);
	        case R.id.details:
	            detailsRec(info.position);
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	private void cloneRec(int position) {
		// TODO Auto-generated method stub
//		prelevo tutti i dati della sessione da clonare e ne creo una 
//		nuova chiedendo all'utente di inserire un nuovo nome		

//		databaseHelper.open();
//		cursor = databaseHelper.fetchASession(list_music[position]);
		//Pop up dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
				
		alert.setTitle("Raname new clone record");
		alert.setMessage("Insert name for new clone Session");
		
		// Set an EditText view to get user input 
//		final EditText input = new EditText(this);
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
		
//		input.setFilters(new InputFilter[]{filter}); 
//		alert.setView(input);

		//alert.create();
		
		alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Do something with value!   
//				String value="";
//				value = input.getText().toString().toLowerCase();
//				
//				try {value = value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();}
//				catch(java.lang.StringIndexOutOfBoundsException e){
//					}
//				
//				new_filename = value;
				
//				cursor.moveToFirst();
//				
////				int id_o = cursor.getInt(0);
////				String name_o = cursor.getString(1);
//				String loc_o = cursor.getString(2);
////				String date_co = cursor.getString(3);
////				String date_lmo = cursor.getString(4);
//				String image_o = cursor.getString(5);
//				int sample_o = cursor.getInt(6);
//				int x_o = cursor.getInt(7);
//				int y_o = cursor.getInt(8);
//				int z_o = cursor.getInt(9);
//				
//				//Ottengo la data e l'ora corrente
//				Calendar c = Calendar.getInstance();
//				hour = c.get(Calendar.HOUR_OF_DAY);
//			    minute = c.get(Calendar.MINUTE);
//			    second = c.get(Calendar.SECOND);
//			    day = c.get(Calendar.DATE);
//			    month = c.get(Calendar.MONTH);
//			    year = c.get(Calendar.YEAR);
//			    
//			    date = day +"/"+ month+"/"+ year +" - "+ hour+":"+ minute +":"+ second;
//				
//			    databaseHelper.createSession(new_filename, loc_o, date, date, image_o, sample_o, x_o, y_o, z_o);
//			    
//				databaseHelper.close();
//				cursor.close();
//				TextView textView = (TextView) findViewById(R.id.textViewCloneName);
//			    textView.setTextSize(25);
//			    textView.setText(value);
//			    textView.setTextColor(Color.rgb(255, 153, 0));
			}
		});
		
		alert.setNegativeButton(android.R.string.no, null/*new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				 onBackPressed();
	        }
	     }*/);
		
		alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog){onBackPressed();}
		});
		
		AlertDialog dialog = alert.create();
		//alert.create();
		dialog.show();

		//setContentView(R.layout.activity_ui1);
		
//		finish();
//		startActivity(getIntent());
	}

	private void deleteRec(int position) {
		databaseHelper.open();
		cursor = databaseHelper.fetchIdSession(list_music[position]);
		cursor.moveToFirst();
		pos = cursor.getInt(0);
		loc = cursor.getString(1);
		File file = new File(loc, list_music[position]+".txt");
		file.delete();
		databaseHelper.deleteSession(pos);
		databaseHelper.close();
		cursor.close();
		finish();
		startActivity(getIntent());
	}

	private void renameRec(int position) {
		// TODO Auto-generated method stub
//		faccio un semplice update
//		databaseHelper.open();
//		cursor = databaseHelper.fetchASession(list_music[position]);
		//Pop up dialog
		AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
		
		alert.setTitle("Raname record");
		alert.setMessage("Insert new name for this Session");
		
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

		//alert.create();
		
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
		        // Do something with value!   
				String value="";
				value = input.getText().toString().toLowerCase();
				
				try {value = value.substring(0,1).toUpperCase() + value.substring(1).toLowerCase();}
				catch(java.lang.StringIndexOutOfBoundsException e){
					}
				
				new_filename = value;
				
//				
//				cursor.moveToFirst();
//				
//				int id_o = cursor.getInt(0);
//				String name_o = cursor.getString(1);
//				String loc_o = cursor.getString(2);
//				String date_co = cursor.getString(3);
//				String date_lmo = cursor.getString(4);
//				String image_o = cursor.getString(5);
//				int sample_o = cursor.getInt(6);
//				int x_o = cursor.getInt(7);
//				int y_o = cursor.getInt(8);
//				int z_o = cursor.getInt(9);
//				
//			    databaseHelper.updateSession(id_o, new_filename, loc_o, date_co, date_lmo, image_o, sample_o, x_o, y_o, z_o);
//			    
//				databaseHelper.close();
//				cursor.close();
//				TextView textView = (TextView) findViewById(R.id.textViewReName);
//			    textView.setTextSize(25);
//			    textView.setText(value);
//			    textView.setTextColor(Color.rgb(255, 153, 0));
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
		
		//alert.create();
		alert.show();

		
//		finish();
//		startActivity(getIntent());
	}
	
	private void playRec(int position) {
		Intent intent = new Intent(getApplicationContext(), UI4.class);
	    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
	    startActivity(intent);
	  
	}
	
	private void detailsRec(int position) {
		Intent intent = new Intent(getApplicationContext(), UI2.class);
	    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
	    startActivity(intent);	
	}

//	private void LockScreenRotation() { // Sets screen rotation as fixed to current rotation setting
//		switch (this.getResources().getConfiguration().orientation)
//		{   case Configuration.ORIENTATION_PORTRAIT:     
//				this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//				break;   
//			case Configuration.ORIENTATION_LANDSCAPE:
//				this.setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//				break;
//		} 
//	}
//	
//	private void UnlockScreenRotation(){ // allow screen rotations
//		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//	}
	
}
