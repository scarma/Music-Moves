package com.example.musicmoves;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import database.DBAdapter;
 
public class UI1Adapter extends ArrayAdapter<String> {
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
    private LayoutInflater mInflater;
 
    private String[] list_music;
    
    private int mIdRisorsaVista;

    private Context ctx;
	
	private DBAdapter databaseHelper;
	private Cursor cursor;
 
    public UI1Adapter(Context ctx, int IdRisorsaVista, String[] strings) {
        super(ctx, IdRisorsaVista, strings);
        //per instanziare un file xml layout nel suo oggetto vista corrispondente
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        this.ctx=ctx;
        
        list_music = strings;
        
        mIdRisorsaVista = IdRisorsaVista;
    }
 
    @Override
    public int getCount() {
        return list_music.length;
    }
 
    @Override
    public String getItem(int position) {
        return list_music[position];
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mIdRisorsaVista, null);
 
        databaseHelper = new DBAdapter(ctx);
		databaseHelper.open();
		cursor = databaseHelper.fetchSessionByFilter(list_music[position]);
		cursor.moveToFirst();
        
        ImageView iv = (ImageView)convertView.findViewById(R.id.immagine_vista);
        //Scala dimensioni Bitmap e la mostra
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(cursor.getString(5)+list_music[position]+".png"), 50, 50, false);
        iv.setImageBitmap(bitmapScaled);

		TextView name = (TextView)convertView.findViewById(R.id.textName);
        name.setText(list_music[position]);
        
        TextView dateCreation = (TextView)convertView.findViewById(R.id.textDateCreation);
        dateCreation.setText(cursor.getString(3)+" ");
        
        TextView dateLastModified = (TextView)convertView.findViewById(R.id.textDateLastModified);
        dateLastModified.setText(cursor.getString(4)+" ");
        
        databaseHelper.close();
		cursor.close();
        
//        tv.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(ctx, "asasd" + position, Toast.LENGTH_SHORT).show();
//			}
//		});
 
        iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, UI4.class);
			    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
			    ctx.startActivity(intent);
			}
		});
        return convertView;
    }
}
