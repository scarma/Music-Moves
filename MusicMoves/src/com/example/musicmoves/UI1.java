package com.example.musicmoves;

import database.DBAdapter;
import database.FeedReaderContract.FeedEntry;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;

public class UI1 extends ListActivity {
	
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	private DBAdapter databaseHelper;
	private Cursor cursor;
	private String[] list_music;
	private UI1Adapter adapter;
	
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui1);
		Context ctx = getApplicationContext();
        Resources res = ctx.getResources();
//        final String[] list_music =new String[100];//res.getStringArray(R.array.paesi);
                
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
		
//		while(cursor.moveToNext())  {
//			list_music[i]=cursor.getString(cursor.getColumnIndex(FeedEntry._ID));
//			i++;
//		}	
//		
		databaseHelper.close();
		cursor.close();
        
        TypedArray immagini = res.obtainTypedArray(R.array.immagini);
        
        adapter = new UI1Adapter(ctx, R.layout.riga_lista, list_music, immagini);
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
	        case R.id.play:
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
		
	}

	private void deleteRec(int id) { //NON FUNZIONANTE
//		databaseHelper.open();
//		databaseHelper.deleteSession(id);
//		//come si fa a cancellare list_music[id]?
//		//e a cancellare il file.txt?
//		UI1.this.adapter.notifyDataSetChanged();
//		Log.d("Database", "Delete");
//		databaseHelper.close();
	}

	private void renameRec(int position) {
		// TODO Auto-generated method stub
		
	}
	
	private void playRec(int position) {
		Intent intent = new Intent(getApplicationContext(), UI4.class);
	    startActivity(intent);
	    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
	    finish();
	}
	
	private void detailsRec(int position) {
		Intent intent = new Intent(UI1.this, UI2.class);
	    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
	    startActivity(intent);	
	}
	
}
