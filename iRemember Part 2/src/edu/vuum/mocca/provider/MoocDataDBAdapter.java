// ST:BODY:startTransaction

package edu.vuum.mocca.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is the class that actually interacts with the SQLite3 database and does
 * the operations to manipulate the data within the database.
 * 
 * @author Michael A. Walker
 * 
 */
public class MoocDataDBAdapter {

    private static final String LOG_TAG = MoocDataDBAdapter.class
            .getCanonicalName();

    private static final String DATABASE_NAME = "myDatabase.db";

    // ST:databaseTableVariableDeclaration:start
    static final String DATABASE_TABLE_STORY = MoocSchema.Story.TABLE_NAME;
    static final String DATABASE_TABLE_TAGS = MoocSchema.Tags.TABLE_NAME;
    // ST:databaseTableVariableDeclaration:finish

    static final int DATABASE_VERSION = 2;

    // The SHORT name of each column in your table
    // ST:createShortVariables:start
    private static final String Story_KEY_ID = MoocSchema.Story.Cols.ID;
    private static final String Story_LoginId = MoocSchema.Story.Cols.LOGIN_ID;
    private static final String Story_StoryId = MoocSchema.Story.Cols.STORY_ID;
    private static final String Story_Title = MoocSchema.Story.Cols.TITLE;
    private static final String Story_Body = MoocSchema.Story.Cols.BODY;
    private static final String Story_AudioLink = MoocSchema.Story.Cols.AUDIO_LINK;
    private static final String Story_VideoLink = MoocSchema.Story.Cols.VIDEO_LINK;
    private static final String Story_ImageName = MoocSchema.Story.Cols.IMAGE_NAME;
    private static final String Story_ImageMetaData = MoocSchema.Story.Cols.IMAGE_LINK;
    private static final String Story_Tags = MoocSchema.Story.Cols.TAGS;
    private static final String Story_CreationTime = MoocSchema.Story.Cols.CREATION_TIME;
    private static final String Story_StoryTime = MoocSchema.Story.Cols.STORY_TIME;
    private static final String Story_Latitude = MoocSchema.Story.Cols.LATITUDE;
    private static final String Story_Longitude = MoocSchema.Story.Cols.LONGITUDE;
    private static final String Tags_KEY_ID = MoocSchema.Tags.Cols.ID;
    private static final String Tags_LoginId = MoocSchema.Tags.Cols.LOGIN_ID;
    private static final String Tags_StoryId = MoocSchema.Tags.Cols.STORY_ID;
    private static final String Tags_Tag = MoocSchema.Tags.Cols.TAG;
    // ST:createShortVariables:finish

    // ST:databaseTableCreationStrings:start
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_STORY = "create table "
            + DATABASE_TABLE_STORY + " (" // start table
            + Story_KEY_ID + " integer primary key autoincrement, " // setup
                                                                    // auto-inc.
            // ST:tableCreateVariables:start
            + Story_LoginId + " INTEGER ," //
            + Story_StoryId + " INTEGER ," //
            + Story_Title + " TEXT ," //
            + Story_Body + " TEXT ," //
            + Story_AudioLink + " TEXT ," //
            + Story_VideoLink + " TEXT ," //
            + Story_ImageName + " TEXT ," //
            + Story_ImageMetaData + " TEXT ," //
            + Story_Tags + " TEXT ," //
            + Story_CreationTime + " INTEGER ," //
            + Story_StoryTime + " INTEGER ," //
            + Story_Latitude + " REAL ," //
            + Story_Longitude + " REAL  " //
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // SQL Statement to create a new database table.
    private static final String DATABASE_CREATE_TAGS = "create table "
            + DATABASE_TABLE_TAGS + " (" // start table
            + Tags_KEY_ID + " integer primary key autoincrement, " // setup
                                                                   // auto-inc.
            // ST:tableCreateVariables:start
            + Tags_LoginId + " INTEGER ," //
            + Tags_StoryId + " INTEGER ," //
            + Tags_Tag + " TEXT  " //
            // ST:tableCreateVariables:finish
            + " );"; // end table
    // ST:databaseTableCreationStrings:finish

    // Variable to hold the database instance.
    private SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private myDbHelper dbHelper;
    // if the DB is in memory or to file.
    private boolean MEMORY_ONLY_DB = false;

    /**
     * constructor that accepts the context to be associated with
     * 
     * @param _context
     */
    public MoocDataDBAdapter(Context _context) {
        Log.d(LOG_TAG, "MyDBAdapter constructor");

        context = _context;
        dbHelper = new myDbHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
    }

    /**
     * constructor that accepts the context to be associated with, and if this
     * DB should be created in memory only(non-persistent).
     * 
     * @param _context
     */
    public MoocDataDBAdapter(Context _context, boolean memory_only_db) {
        Log.d(LOG_TAG, "MyDBAdapter constructor w/ mem only =" + memory_only_db);

        context = _context;
        MEMORY_ONLY_DB = memory_only_db;
        if (MEMORY_ONLY_DB == true) {
            dbHelper = new myDbHelper(context, null, null, DATABASE_VERSION);
        } else {
            dbHelper = new myDbHelper(context, DATABASE_NAME, null,
                    DATABASE_VERSION);
        }
    }

    /**
     * open the DB Get Memory or File version of DB, and write/read access or
     * just read access if that is all that is possible.
     * 
     * @return this MoocDataDBAdaptor
     * @throws SQLException
     */
    public MoocDataDBAdapter open() throws SQLException {
        Log.d(LOG_TAG, "open()");
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException ex) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    /**
     * Remove a row of the DB where the rowIndex matches.
     * 
     * @param rowIndex
     *            row to remove from DB
     * @return if the row was removed
     */
    public int delete(final String table, long _id) {
        Log.d(LOG_TAG, "delete(" + _id + ") ");
        return db.delete(table, android.provider.BaseColumns._ID + " = " + _id,
                null);
    }

    /**
     * Delete row(s) that match the whereClause and whereArgs(if used).
     * <p>
     * the whereArgs is an String[] of values to substitute for the '?'s in the
     * whereClause
     * 
     * @param whereClause
     * @param whereArgs
     * @return
     */
    public int delete(final String table, final String whereClause,
            final String[] whereArgs) {
        Log.d(LOG_TAG, "delete(" + whereClause + ") ");
        return db.delete(table, whereClause, whereArgs);
    }

    /**
     * Query the Database with the provided specifics.
     * 
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return Cursor of results
     */
    public Cursor query(final String table, final String[] projection,
            final String selection, final String[] selectionArgs,
            final String sortOrder) {

        // Perform a query on the database with the given parameters
    	return db.query(table, projection, selection, selectionArgs, "", "", sortOrder);    
    	
    }

    /**
     * close the DB.
     */
    public void close() {
        Log.d(LOG_TAG, "close()");
        db.close();
    }

    /**
     * Start a transaction.
     */
    public void startTransaction() {
        Log.d(LOG_TAG, "startTransaction()");
        db.beginTransaction();
    }

    /**
     * End a transaction.
     */
    public void endTransaction() {
        Log.d(LOG_TAG, "endTransaction()");
        db.endTransaction();
    }

    /**
     * Get the underlying Database.
     * 
     * @return
     */
    SQLiteDatabase getDB() {
        return db;
    }

    /**
     * Insert a ContentValues into the DB.
     * 
     * @param location
     * @return row's '_id' of the newly inserted ContentValues
     */
    public long insert(final String table, final ContentValues cv) {
        Log.d(LOG_TAG, "insert(CV)");
        return db.insert(table, null, cv);
    }

    /**
     * Update Value(s) in the DB.
     * 
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return number of rows changed.
     */
    public int update(final String table, final ContentValues values,
            final String whereClause, final String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    @Override
    /**
     * finalize operations to this DB, and close it.
     */
    protected void finalize() throws Throwable {
        try {
            db.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, "exception on finalize():" + e.getMessage());
        }
        super.finalize();
    }

    /**
     * This class can support running the database in a non-persistent mode,
     * this tells you if that is happening.
     * 
     * @return boolean true/false of if this DBAdaptor is persistent or in
     *         memory only.
     */
    public boolean isMemoryOnlyDB() {
        return MEMORY_ONLY_DB;
    }

    /**
     * DB Helper Class.
     * 
     * @author mwalker
     * 
     */
    private static class myDbHelper extends SQLiteOpenHelper {

        public myDbHelper(Context context, String name, CursorFactory factory,
                int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "DATABASE_CREATE: version: " + DATABASE_VERSION);
            // ST:createTable:start
            db.execSQL(DATABASE_CREATE_STORY);
            db.execSQL(DATABASE_CREATE_TAGS);
            // ST:createTable:finish

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Log version upgrade.
            Log.w(LOG_TAG + "DBHelper", "Upgrading from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");

            // **** Upgrade DB ****
            // drop old DB

            // ST:dropTableIfExists:start
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_STORY);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TAGS);
            // ST:dropTableIfExists:finish

            // Create a new one.
            onCreate(db);

        }

    }

}
