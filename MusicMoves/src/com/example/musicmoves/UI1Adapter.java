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
	//Adapter per la riga con immagine, titolo e date nella UI1
	public final static String EXTRA_MESSAGE = "com.example.MusicMoves.MESSAGE";
    private LayoutInflater mInflater; 
    private String[] list_music;
    private int mIdLayout;
    private Context ctx;
	private DBAdapter databaseHelper;
	private Cursor cursor;
 
    public UI1Adapter(Context ctx, int IdLayout, String[] strings) {
        super(ctx, IdLayout, strings);
        //Istanzia il layout definito dall'xml nella sua View corrispondente
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ctx=ctx;
        list_music = strings;
        mIdLayout = IdLayout;
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
        convertView = mInflater.inflate(mIdLayout, null);
 
        databaseHelper = new DBAdapter(ctx);
		databaseHelper.open();
		cursor = databaseHelper.fetchSessionByFilter(list_music[position]);
		cursor.moveToFirst();
        
        ImageView iv = (ImageView)convertView.findViewById(R.id.immagine_vista);
        
        //Scala dimensioni Bitmap e la mostra
        int w = parent.getWidth();
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(cursor.getString(5)+list_music[position]+".png"), w/5, w/5, false);
        iv.setImageBitmap(bitmapScaled);

		TextView name = (TextView)convertView.findViewById(R.id.textName);
        name.setText(list_music[position]);
        
        TextView dateCreation = (TextView)convertView.findViewById(R.id.textDateCreation);
        dateCreation.setText(cursor.getString(3)+" ");
        
        TextView dateLastModified = (TextView)convertView.findViewById(R.id.textDateLastModified);
        dateLastModified.setText(cursor.getString(4)+" ");
        
        databaseHelper.close();
		cursor.close();
 
        iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, UI4.class);
			    intent.putExtra(EXTRA_MESSAGE, list_music[position]);
			    intent.putExtra("my",true);
			    ctx.startActivity(intent);
			}
		});
        return convertView;
    }
}