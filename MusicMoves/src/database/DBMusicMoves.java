package database;

import database.FeedReaderContract.FeedEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBMusicMoves extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "DBMusicMoves.db";
	public static final int DB_VERSION = 1;
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
	    FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_DATE_CREATION + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_LAST_MODIFY + TEXT_TYPE + COMMA_SEP +
	    FeedEntry.COLUMN_NAME_SIGNATURE + TEXT_TYPE + COMMA_SEP + 
	    "UNIQUE (" + FeedEntry.COLUMN_NAME_TITLE + COMMA_SEP + 
	    FeedEntry.COLUMN_NAME_LOCATION + COMMA_SEP + 
	    FeedEntry.COLUMN_NAME_SIGNATURE + ")" +
	    " )";
	
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
	
	//Constructor
	public DBMusicMoves(Context context){
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
}