package org.arrobaLite.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.arrobaLite.util.TableBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d070169 on 17/11/2017.
 */

public class SqlLiteConnector extends SQLiteOpenHelper {

    protected static final String DB_ERROR = "[DB_ERROR]";
    protected static final String TAG = "[SIMPLE_DB]";
    private static final String PRAGMA_FOREIGN_KEYS_ON = " PRAGMA foreign_keys = ON ";
    private static final String PRAGMA_FOREIGN_KEYS_OFF = " PRAGMA foreign_keys = OFF ";
    private static final String QUERY_FIND_TABLES = "SELECT name FROM sqlite_master WHERE type='table' AND name != 'sqlite_sequence' AND name != 'android_metadata'";
    private static final String QUERY_FIND_INDEXES = "SELECT name FROM sqlite_master WHERE type='index' AND name NOT LIKE '%sqlite_auto%'";
    private static final String DROP_TABLE_FORMAT = "DROP TABLE IF EXISTS %s";
    private static final String DROP_INDEX_FORMAT = "DROP INDEX IF EXISTS %s";

    private Context dbContext;
    private Class dbClass;

    public SqlLiteConnector(Context context, String databaseName, int version, Class myClass) {
        super(context, databaseName, null, version);
        this.dbContext = context; this.dbClass = myClass;
    }

    public Class getDbClass() { return dbClass; }
    public Context getDbContext() { return dbContext; }

    protected SQLiteDatabase getConnectionReadOnly(){ return getReadableDatabase(); }
    protected SQLiteDatabase getConnection(){ return getWritableDatabase(); }
    protected void closeCursor(Cursor cursor){ if( cursor!= null ){ cursor.close(); }  }
    protected void getTransaction(SQLiteDatabase db){db.beginTransaction(); }
    protected void commit(SQLiteDatabase db){ if(db!=null && db.inTransaction()){ db.setTransactionSuccessful(); } }
    protected void closeTransaction(SQLiteDatabase db){ if(db!=null && db.inTransaction()){ db.endTransaction(); } this.closeConnnection(db); }

    public void closeConnnection(Cursor cursor,SQLiteDatabase db){
        if( cursor!= null ){ cursor.close(); } 
        //if( db!= null && db.isOpen() ){ db.close(); }
    }

    public void closeConnnection(SQLiteDatabase db){ 
        //if( db!= null && db.isOpen() ){ db.close(); } 
    }

    @Override
    public void onCreate(SQLiteDatabase db) { }

    protected void createTable(SQLiteDatabase db) {
        try {
            db.execSQL(PRAGMA_FOREIGN_KEYS_OFF);
            String table = new TableBuilder().createTable(dbClass);
            Log.i(TAG, table); db.execSQL(table);
            String index = new TableBuilder().createTableIndex(dbClass);
            Log.i(TAG, index); db.execSQL(index);
            db.execSQL(PRAGMA_FOREIGN_KEYS_ON);
        } catch (Exception e){ Log.e(TAG, e.getMessage()); }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            List<String> tables = new ArrayList<String>();
            List<String> indexes = new ArrayList<String>();
            //Find Tables.
            Cursor tableCursor = db.rawQuery(QUERY_FIND_TABLES, null);
            while (tableCursor.moveToNext()) { tables.add(tableCursor.getString(0)); }
            tableCursor.close();
            //Find Indexes.
            Cursor indexCursor = db.rawQuery(QUERY_FIND_INDEXES, null);
            while (indexCursor.moveToNext()) { indexes.add(indexCursor.getString(0)); }
            indexCursor.close();
            //Drop Tables.
            db.execSQL(PRAGMA_FOREIGN_KEYS_OFF);
            for (String table : tables) {
                try { db.execSQL( String.format(DROP_TABLE_FORMAT,table) ); }
                catch (Exception e){ Log.e(DB_ERROR,e.getMessage()); }
            }
            //Drop Indexes.
            for (String index : indexes) {
                try { db.execSQL( String.format(DROP_INDEX_FORMAT,index) ); }
                catch (Exception e){ Log.e(DB_ERROR,e.getMessage()); }
            }
            db.execSQL(PRAGMA_FOREIGN_KEYS_ON);
            this.onCreate(db);
        } catch (Exception e){ Log.e(DB_ERROR,e.getMessage()); }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close(); super.finalize();
    }
}
