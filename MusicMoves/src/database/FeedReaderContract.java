package database;

import android.provider.BaseColumns;

public class FeedReaderContract {

	/* Inner class that defines the table contents */
	public static abstract class FeedEntry implements BaseColumns {
    	public static final String TABLE_NAME = "musics";
    	public static final String COLUMN_NAME_TITLE = "title";
    	public static final String COLUMN_NAME_LOCATION = "location";
    	public static final String COLUMN_NAME_DATE_CREATION = "date_creation";
    	public static final String COLUMN_NAME_LAST_MODIFY = "last_modify";
    	public static final String COLUMN_NAME_SIGNATURE = "signature";
	}
}