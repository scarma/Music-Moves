package database;

import android.provider.BaseColumns;

/*
 * La classe FeedReaderContract contiene i nomi delle colonne del database.
 * Abbiamo fatto questa scelta di progettazione (in una classe separata) in modo
 * che in futuro se si vuole cambiare nome ad una variabile e/o toglierne
 * e/o aggiungerne lo si fa in un unico luogo, cioe' in questa classe.
 * Quindi senza dover modificare altre parti del codice dell'applicazione.
 */
public class FeedReaderContract {
	/* Inner class that defines the table contents */
	public static abstract class FeedEntry implements BaseColumns {
    	public static final String TABLE_NAME = "music";
    	public static final String COLUMN_NAME_TITLE = "title";
    	public static final String COLUMN_NAME_LOCATION = "location";
    	public static final String COLUMN_NAME_DATE_CREATION = "date_creation";
    	public static final String COLUMN_NAME_LAST_MODIFY = "last_modify";
    	public static final String COLUMN_NAME_IMAGE = "image";
    	public static final String COLUMN_NAME_UPSAMPLING = "upsampling";
    	public static final String COLUMN_NAME_X = "x";
    	public static final String COLUMN_NAME_Y = "y";
    	public static final String COLUMN_NAME_Z = "z";    	
	}
}