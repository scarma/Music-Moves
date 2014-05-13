package com.example.musicmoves;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class UI1Adapter extends ArrayAdapter<String> {
    private LayoutInflater mInflater;
 
    private String[] nomiPaesi;
    private TypedArray immagini;
 
    private int mIdRisorsaVista;

	private Context ctx;
 
    public UI1Adapter(Context ctx, int IdRisorsaVista, String[] strings, TypedArray icons) {
        super(ctx, IdRisorsaVista, strings);
        //per instanziare un file xml layout nel suo oggetto vista corrispondente
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
        this.ctx=ctx;
        
        nomiPaesi = strings;
        immagini = icons;
 
        mIdRisorsaVista = IdRisorsaVista;
    }
 
    @Override
    public int getCount() {
        return nomiPaesi.length;
    }
 
    @Override
    public String getItem(int position) {
        return nomiPaesi[position];
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mIdRisorsaVista, null);
 
        ImageView iv = (ImageView)convertView.findViewById(R.id.immagine_vista);
        iv.setImageDrawable(immagini.getDrawable(position));
 
        TextView tv = (TextView)convertView.findViewById(R.id.testo_vista);
        tv.setText(nomiPaesi[position]);
        
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
				Toast.makeText(ctx, "immagine " + position, Toast.LENGTH_SHORT).show();
			}
		});
        return convertView;
    }
}