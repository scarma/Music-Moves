package com.example.musicmoves;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/*
 * La classe SeekBarPreference permette di gestire le preferenze utilizzando le SeekBar.
 * Implementando i vari metodi gestiamo il layout della preference
 */
public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
	//Preference per le Seekbar, utilizzata nella UI5
	private final String TAG = getClass().getName();
	
	private static final String ANDROID="http://schemas.android.com/apk/res/android";
	private static final String APPLICATIONS="musicmoves";
	private static final int DEFAULT_VALUE = 50;
	
	private int mMaxValue = 100;
	private int mMinValue = 0;
	private int mInterval = 1;
	private int mCurrentValue;
	private String mUnitsLeft  = "";
	private String mUnitsRight = "";
	private SeekBar mSeekBar;
	
	private TextView mStatusText;

	//Costruttore
	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPreference(context, attrs);
	}

	//Costruttore
	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPreference(context, attrs);
	}

	//Inizializza e imposta il layout della preference
	private void initPreference(Context context, AttributeSet attrs) {
		setValuesFromXml(attrs);
		mSeekBar = new SeekBar(context, attrs);
		mSeekBar.setMax(mMaxValue - mMinValue);
		mSeekBar.setOnSeekBarChangeListener(this);
		
		setWidgetLayoutResource(R.layout.seek_bar_preference); //Imposta layout come da file xml
	}
	
	//Imposta valori definiti nell'xml
	private void setValuesFromXml(AttributeSet attrs) {
		mMaxValue = attrs.getAttributeIntValue(ANDROID, "max", 100);
		mMinValue = attrs.getAttributeIntValue(APPLICATIONS, "min", 0);
		
		mUnitsLeft = getAttributeStringValue(attrs, APPLICATIONS, "unitsLeft", "");
		String units = getAttributeStringValue(attrs, APPLICATIONS, "units", "");
		mUnitsRight = getAttributeStringValue(attrs, APPLICATIONS, "unitsRight", units);
		
		try {
			String newInterval = attrs.getAttributeValue(APPLICATIONS, "interval");
			if(newInterval != null)
				mInterval = Integer.parseInt(newInterval);
		}
		catch(Exception e) {
			Log.e(TAG, "Invalid interval value", e);
		}
		
	}
	
	//Ritorna il valore della stringa
	private String getAttributeStringValue(AttributeSet attrs, String namespace, String name, String defaultValue) {
		String value = attrs.getAttributeValue(namespace, name);
		if(value == null)
			value = defaultValue;
		return value;
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		View view = super.onCreateView(parent);
		//Faccio un linear layout e lo oriento verticalmente
		LinearLayout layout = (LinearLayout) view;
		layout.setOrientation(LinearLayout.VERTICAL);
		
		return view;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		try {
			// mette la seekbar nella nuova view che gli assegniamo
			ViewParent oldContainer = mSeekBar.getParent();
			ViewGroup newContainer = (ViewGroup) view.findViewById(R.id.seekBarPrefBarContainer);
			
			if (oldContainer != newContainer) {
				// rimuove la seekbar dalla vecchia view
				if (oldContainer != null) {
					((ViewGroup) oldContainer).removeView(mSeekBar);
				}
				// rimuove la seekbar esistente (può non essercene una) e aggiunge la nostra
				newContainer.removeAllViews();
				newContainer.addView(mSeekBar, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		}
		catch(Exception ex) {
			Log.e(TAG, "Error binding view: " + ex.toString());
		}
		//se la dipendenza è falsa dall'inizio, disabilita la seekbar
		if (view != null && !view.isEnabled())
		{
			mSeekBar.setEnabled(false);
		}
		updateView(view);
	}
    
    /*
	 * Aggiorna la SeekBarPreference col nostro stato corrente
	 */
	protected void updateView(View view) {
		try {
			mStatusText = (TextView) view.findViewById(R.id.seekBarPrefValue);

			mStatusText.setText(String.valueOf(mCurrentValue));
			mStatusText.setMinimumWidth(30);
			
			mSeekBar.setProgress(mCurrentValue - mMinValue);

			TextView unitsRight = (TextView)view.findViewById(R.id.seekBarPrefUnitsRight);
			unitsRight.setText(mUnitsRight);
			
			TextView unitsLeft = (TextView)view.findViewById(R.id.seekBarPrefUnitsLeft);
			unitsLeft.setText(mUnitsLeft);
		}
		catch(Exception e) {
			Log.e(TAG, "Error updating seek bar preference", e);
		}
	}
	
	/* Chiamato ogni qualvolta viene cambiato il valore alla seekbar
	 * Imposta il valore corretto con setProgress
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int newValue = progress + mMinValue;
		if(newValue > mMaxValue)
			newValue = mMaxValue;
		else if(newValue < mMinValue)
			newValue = mMinValue;
		else if(mInterval != 1 && newValue % mInterval != 0)
			newValue = Math.round(((float)newValue)/mInterval)*mInterval;  
		
		// torno al valore precedente senza salvare il nuovo
		if(!callChangeListener(newValue)){
			seekBar.setProgress(mCurrentValue - mMinValue); 
			return; 
		}

		// cambio accettato, salvo il valore
		mCurrentValue = newValue;
		mStatusText.setText(String.valueOf(newValue));
		persistInt(newValue);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		notifyChanged();
	}

	// Imposta valore di default come da xml
	@Override 
	protected Object onGetDefaultValue(TypedArray ta, int index){
		int defaultValue = ta.getInt(index, DEFAULT_VALUE);
		return defaultValue;		
	}

	// Ripristina il valore salvato o (se nessun valore valido è salvato)
	// imposta quello di default (se valido)
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		if(restoreValue) {
			mCurrentValue = getPersistedInt(mCurrentValue);
		}
		else {
			int temp = 0;
			try {
				temp = (Integer)defaultValue;
			}
			catch(Exception ex) {
				Log.e(TAG, "Invalid default value: " + defaultValue.toString());
			}
			persistInt(temp);
			mCurrentValue = temp;
		}
	}
	
	/*
	 * assicura che la seekbar sia disabilitata se lo è la preference
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mSeekBar.setEnabled(enabled);
	}
	
	@Override
	public void onDependencyChanged(Preference dependency, boolean disableDependent) {
		super.onDependencyChanged(dependency, disableDependent);
		//Disabilita movimenti della seek bar se la dipendenza è false
		if (mSeekBar != null)
		{
			mSeekBar.setEnabled(!disableDependent);
		}
	}
}