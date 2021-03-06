package database;

import database.FeedReaderContract.FeedEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * La classe DBMusicMoves mi permette di creare e/o aggiornare
 * in una versione superiore il database.
 * Inoltre mi permette di tornare alle versioni precedenti
 * del database. 
 */
public class DBMusicMoves extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "DBMusicMoves.db";
	public static final int DB_VERSION = 1;
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	//questa stringa contiene la query create table dell'unica tabella del nostro database.
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
	    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_DATE_CREATION + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_LAST_MODIFY + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP + 
	    FeedEntry.COLUMN_NAME_UPSAMPLING + " INTEGER" + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_X + " INTEGER" + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_Y + " INTEGER" + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_Z + " INTEGER" + COMMA_SEP +
	    "UNIQUE (" + FeedEntry.COLUMN_NAME_TITLE + ")" +
	    " )";
	
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
	
	//Costruttore
	public DBMusicMoves(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }	
}