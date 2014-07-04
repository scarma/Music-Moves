package com.example.musicmoves;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/*
 * ProgressBar verticale mostrata nella UI3 in fase di registrazione
 * Nell'UI3 mostro in fase di registrazione 3 progressBar che indicano
 * i valori dei tre assi x, y e z letti dall'accelerometro: che variano
 * a seconda della posizione del dispositivo.
 */
public class VerticalProgressBar extends ProgressBar{
    private int x, y, z, w;

    public VerticalProgressBar(Context context) {
        super(context);
    }

    public VerticalProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);        
        this.x = w;
        this.y = h;
        this.z = oldw;
        this.w = oldh;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
            int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
//    	Ruota la progress bar in modo che sia verticale.
//    	Altrimenti di default sarebbe orizzontale.
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        super.onDraw(c);
    }

    /*
     * Con setProgress() setto il valore delle progressBar.
     */
    @Override
    public synchronized void setProgress(int progress) {
        if (progress >= 0)
            super.setProgress(progress);
        else
            super.setProgress(0);
        onSizeChanged(x, y, z, w);
    }
}