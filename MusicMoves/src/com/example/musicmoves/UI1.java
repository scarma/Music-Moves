package com.example.musicmoves;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;



public class UI1 extends ListActivity {
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
	//ActionMode mActionMode;
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ui1);
		Context ctx = getApplicationContext();
        Resources res = ctx.getResources();
        final String[] nomiPaesi = res.getStringArray(R.array.paesi);
        TypedArray immagini = res.obtainTypedArray(R.array.immagini);
        setListAdapter(new UI1Adapter(ctx, R.layout.riga_lista, nomiPaesi, immagini));
      //da qui inseriamo il codice utile a mostrare un messaggio al click
        ListView listaV = getListView();
        listaV.setTextFilterEnabled(true);
//        registerForContextMenu(listaV);
        
        listaV.setOnItemClickListener(new OnItemClickListener() {
            
        	@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // mostro il testo dell�oggetto cliccato, attenzione a recuperare il testo
                        // dall'oggetto giusto
        		Intent intent = new Intent(UI1.this, UI2.class);
        	    intent.putExtra(EXTRA_MESSAGE, nomiPaesi[position]);
        	    startActivity(intent);
            	
                //Toast.makeText(getApplicationContext(), nomiPaesi[position], Toast.LENGTH_SHORT).show();
            }
        });
        listaV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
        	@Override
        	// Called when the user long-clicks
    	    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//    	        if (mActionMode != null) {
//    	            return false;
//    	        }
//        		view.showContextMenu();
        		Toast.makeText(UI1.this,nomiPaesi[position], Toast.LENGTH_SHORT).show();
                
            
    	        // Start the CAB using the ActionMode.Callback defined above
    	       // mActionMode = startActionMode(mActionModeCallback);
    	        view.setSelected(true);
    	        return true;
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
	
	
//	@Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//      super.onCreateContextMenu(menu, v, menuInfo);
//      MenuInflater inflater = getMenuInflater();
//      inflater.inflate(R.menu.context_menu, menu);
//
////      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
////      long itemID = info.position;
////      menu.setHeaderTitle("lior" + itemID);
//    }
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
//	    switch (item.getItemId()) {
//	        case R.id.rename:
//	            renameRec(info.id);
//	            return true;
//	        case R.id.delete:
//	            deleteRec(info.id);
//	            return true;
//	        default:
//	            return super.onContextItemSelected(item);
//	    }
//	}

	private void deleteRec(long id) {
		// TODO Auto-generated method stub
		
	}

	private void renameRec(long id) {
		// TODO Auto-generated method stub
		
	}
//	
//	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
//
//	    // Called when the action mode is created; startActionMode() was called
//	    @Override
//	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//	        // Inflate a menu resource providing context menu items
//	        MenuInflater inflater = getMenuInflater();
//	        inflater.inflate(R.menu.context_menu, menu);
//	        return true;
//	    }
//
//	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
//	    // may be called multiple times if the mode is invalidated.
//	    @Override
//	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//	        return false; // Return false if nothing is done
//	    }
//
//	    // Called when the user selects a contextual menu item
//	    @Override
//	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//	        switch (item.getItemId()) {
//	            case R.id.play:
//	                playCurrentItem();
//	                mode.finish(); // Action picked, so close the CAB
//	                return true;
//	            default:
//	                return false;
//	        }
//	    }
//
//	   
//
//		// Called when the user exits the action mode
//	    @Override
//	    public void onDestroyActionMode(ActionMode mode) {
//	        mActionMode = null;
//	    }
//	};
	
	 private void playCurrentItem() {
			// TODO Auto-generated method stub
			
		}
	 
	
}
