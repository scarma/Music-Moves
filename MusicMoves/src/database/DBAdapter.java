package database;

import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import database.FeedReaderContract.FeedEntry;

/*
 * La classe DBAdapter ci permette di implementare tutte le query
 * che dobbiamo eseguire per interagire con il nostro database.
 */
public class DBAdapter {	        
	  private Context context;
	  private SQLiteDatabase database;
	  private DBMusicMoves dbHelper;

	  public DBAdapter(Context context) {
	    this.context = context;
	  }

	  public DBAdapter open() throws SQLException {
	    dbHelper = new DBMusicMoves(context);
	    database = dbHelper.getWritableDatabase();
	    return this;
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  private ContentValues createContentValues(String title, String location, String date_c, String date_m, String image, int sample_rate, int x, int y, int z){
	    ContentValues values = new ContentValues();
	    values.put( FeedEntry.COLUMN_NAME_TITLE, title);
	    values.put( FeedEntry.COLUMN_NAME_LOCATION, location);
	    values.put( FeedEntry.COLUMN_NAME_DATE_CREATION, date_c);
	    values.put( FeedEntry.COLUMN_NAME_LAST_MODIFY, date_m);
	    values.put( FeedEntry.COLUMN_NAME_IMAGE, image);
	    values.put( FeedEntry.COLUMN_NAME_UPSAMPLING, sample_rate);
	    values.put( FeedEntry.COLUMN_NAME_X, x);
	    values.put( FeedEntry.COLUMN_NAME_Y, y);
	    values.put( FeedEntry.COLUMN_NAME_Z, z);
	    
	   return values;
	  }
	        
	  //creo un'istanza nel database: cioe' mi permette di inserire una nuova riga che corrisponde ad una nuova registrazione
	  public long createSession(String title, String location, String date_c, String date_m, String image, int sample_rate, int x, int y, int z) {
	    ContentValues initialValues = createContentValues(title, location+"/", date_c, date_m, image, sample_rate, x, y, z);
	    return database.insertOrThrow(FeedEntry.TABLE_NAME, null, initialValues);
	  }

	  //mi permette di aggiornare un'istanza all'interno del db.
	  public boolean updateSession( long id, String title, String location, String date_c, String date_m, String image, int sample_rate, int x, int y, int z){
	    ContentValues updateValues = createContentValues(title, location, date_c, date_m, image, sample_rate, x, y, z);
	    return database.update(FeedEntry.TABLE_NAME, updateValues, FeedEntry._ID + "=" + id, null) > 0;
	  }
	                
	  //cancello un'istanza dal db     
	  public boolean deleteSession(long id) {
	    return database.delete(FeedEntry.TABLE_NAME, FeedEntry._ID + "=" + id, null) > 0;
	  }

	  //rilevo tutte le istanze presenti nel db.
	  public Cursor fetchAllSession() {
		return database.query(FeedEntry.TABLE_NAME, new String[] {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_LOCATION, 
	    		FeedEntry.COLUMN_NAME_DATE_CREATION, FeedEntry.COLUMN_NAME_LAST_MODIFY, FeedEntry.COLUMN_NAME_IMAGE, FeedEntry.COLUMN_NAME_UPSAMPLING, 
	    		FeedEntry.COLUMN_NAME_X, FeedEntry.COLUMN_NAME_Y, FeedEntry.COLUMN_NAME_Z}, null, null, null, null, FeedEntry.COLUMN_NAME_TITLE);
	  }
	  
	  //rilevo un'istanza in particolare all'interno del db.
	  public Cursor fetchSessionByFilter(String filter) {
	    Cursor mCursor = database.query(true, FeedEntry.TABLE_NAME, new String[] {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_LOCATION, 
	    		FeedEntry.COLUMN_NAME_DATE_CREATION, FeedEntry.COLUMN_NAME_LAST_MODIFY, FeedEntry.COLUMN_NAME_IMAGE, FeedEntry.COLUMN_NAME_UPSAMPLING, 
	    		FeedEntry.COLUMN_NAME_X, FeedEntry.COLUMN_NAME_Y, FeedEntry.COLUMN_NAME_Z},
	            FeedEntry.COLUMN_NAME_TITLE + " like '%"+ filter + "%'", null, null, null, null, null);
	        
	    return mCursor;
	  }
	  
	  //leggo id e location in un'istanza attraverso il nome del record.
	  public Cursor fetchIdSession(String name){
		  Cursor mCursor = database.query(FeedEntry.TABLE_NAME, new String[]{FeedEntry._ID, FeedEntry.COLUMN_NAME_LOCATION}, 
				  FeedEntry.COLUMN_NAME_TITLE + " like '%"+ name + "%'", null, null, null, null);
		  
		  return mCursor;
	  }
	  
	  //leggo tutto di un'istanza attraverso il nome del record.
	  public Cursor fetchASession(String name) {
	      return database.query(FeedEntry.TABLE_NAME, new String[] {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_LOCATION, 
		    		FeedEntry.COLUMN_NAME_DATE_CREATION, FeedEntry.COLUMN_NAME_LAST_MODIFY, FeedEntry.COLUMN_NAME_IMAGE, FeedEntry.COLUMN_NAME_UPSAMPLING, 
		    		FeedEntry.COLUMN_NAME_X, FeedEntry.COLUMN_NAME_Y, FeedEntry.COLUMN_NAME_Z}, FeedEntry.COLUMN_NAME_TITLE + " like '%"+ name + "%'", null, null, null, null);
	  }
	  
	  //verifico se il nome di una registrazione e' presente o meno all'interno del db.
	  public Cursor NameSessionAsExist(String name){
		  return database.rawQuery("select count(*) from " + FeedEntry.TABLE_NAME + " where " + FeedEntry.COLUMN_NAME_TITLE + "=?", new String[] {name});
	  }
	  
	  //aggiorno l'asse x di un'istanza.
	  public void updateX(String name, int x){
		  String sql=String.format(Locale.US,"update %s SET %s=%d  WHERE %s='%s'" ,  FeedEntry.TABLE_NAME,
				  FeedEntry.COLUMN_NAME_X, x, FeedEntry.COLUMN_NAME_TITLE, name
				  );
		  System.out.println(sql);
		  database.execSQL(sql);
		  return;
	  }
	  
	  //aggiorno l'asse y di un'istanza.
	  public void updateY(String name, int y){
		  String sql=String.format(Locale.US,"update %s SET %s=%d WHERE %s='%s'" ,  FeedEntry.TABLE_NAME,
				  FeedEntry.COLUMN_NAME_Y, y, FeedEntry.COLUMN_NAME_TITLE, name
				  );
		  System.out.println(sql);
		  database.execSQL(sql);
		  return;
	  }
	  
	  //aggiorno l'asse z di un'istanza.
	  public void updateZ(String name, int z){
		  String sql=String.format(Locale.US,"update %s SET %s=%d WHERE %s='%s'" ,  FeedEntry.TABLE_NAME,
				  FeedEntry.COLUMN_NAME_Z, z, FeedEntry.COLUMN_NAME_TITLE, name
				  );
		  System.out.println(sql);
		  database.execSQL(sql);
		  return;
	  }
	  
	  //aggiorno l'upsampling di un'istanza.
	  public void updateUpsampling(String name, int up){
		  String sql=String.format(Locale.US,"update %s SET %s=%d WHERE %s='%s'" ,  FeedEntry.TABLE_NAME,
				  FeedEntry.COLUMN_NAME_UPSAMPLING, up, FeedEntry.COLUMN_NAME_TITLE, name
				  );
		  System.out.println(sql);
		  database.execSQL(sql);
		  return;
	  }
	  
	  //aggiorno la data di ultima modifica di un'istanza.
	  public void updateDate(String name, String data){
		  String sql=String.format(Locale.US,"update %s SET %s='%s' WHERE %s='%s'" ,  FeedEntry.TABLE_NAME,
				  FeedEntry.COLUMN_NAME_LAST_MODIFY, data, FeedEntry.COLUMN_NAME_TITLE, name
				  );
		  System.out.println(sql);
		  database.execSQL(sql);
		  return;
	  }
}