// ST:BODY:start

package edu.vuum.mocca.provider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * The Content Provider for this application.
 * <p>
 * Content providers are one of the primary building blocks of Android
 * applications, providing content to applications. They encapsulate data and
 * provide it to applications through the single ContentResolver interface. A
 * content provider is only required if you need to share data between multiple
 * applications. For example, the contacts data is used by multiple applications
 * and must be stored in a content provider. If you don't need to share data
 * amongst multiple applications you can use a database directly via
 * SQLiteDatabase.
 * 
 * @author Michael A. Walker
 * 
 */
public class MoocProvider extends ContentProvider {

    private final static String LOG_TAG = MoocProvider.class.getCanonicalName();

    // Local backend DB
    MoocDataDBAdapter mDB;

    // shorten variable names for easier readability

    // ST:createShortContentURIforRelations:begin
    public final static Uri STORY_CONTENT_URI = MoocSchema.Story.CONTENT_URI;
    public final static Uri TAGS_CONTENT_URI = MoocSchema.Tags.CONTENT_URI;
    // ST:createShortContentURIforRelations:finish

    public static String AUTHORITY = MoocSchema.AUTHORITY;

    // ST:createShortURIMatchingTokens:begin
    public static final int STORY_ALL_ROWS = MoocSchema.Story.PATH_TOKEN;
    public static final int STORY_SINGLE_ROW = MoocSchema.Story.PATH_FOR_ID_TOKEN;
    public static final int TAGS_ALL_ROWS = MoocSchema.Tags.PATH_TOKEN;
    public static final int TAGS_SINGLE_ROW = MoocSchema.Tags.PATH_FOR_ID_TOKEN;
    // ST:createShortURIMatchingTokens:finish

    private static final UriMatcher uriMatcher = MoocSchema.URI_MATCHER;

    @Override
    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the application
     * main thread at application launch time. It must not perform lengthy operations,
     * or application startup will be delayed.
     */
    synchronized public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate()");
        mDB = new MoocDataDBAdapter(getContext());
        mDB.open();
        return true;
    }

    @Override
    /**
     * Implement this to handle requests for the MIME type of the data at the given URI. 
     * The returned MIME type should start with vnd.android.cursor.item for a single record,
     * or vnd.android.cursor.dir/ for multiple items. This method can be called from multiple 
     * threads, as described in Processes and Threads.
     */
    synchronized public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType()");
        switch (uriMatcher.match(uri)) {

        // ST:createContentTypeReturnsforRelations:begin
        case STORY_ALL_ROWS:
            return MoocSchema.Story.CONTENT_TYPE_DIR;
        case STORY_SINGLE_ROW:
            return MoocSchema.Story.CONTENT_ITEM_TYPE;
        case TAGS_ALL_ROWS:
            return MoocSchema.Tags.CONTENT_TYPE_DIR;
        case TAGS_SINGLE_ROW:
            return MoocSchema.Tags.CONTENT_ITEM_TYPE;
            // ST:createContentTypeReturnsforRelations:finish

        default:
            throw new UnsupportedOperationException("URI " + uri
                    + " is not supported.");
        }
    }

    @Override
    /**
     * Retrieve data from your provider. Use the arguments to select the table to query,
     * the rows and columns to return, and the sort order of the result. Return the data as a Cursor object.
     */
    synchronized public Cursor query(final Uri uri, final String[] projection,
            final String selection, final String[] selectionArgs,
            final String sortOrder) {
        Log.d(LOG_TAG, "query()");
        String modifiedSelection = selection;
        switch (uriMatcher.match(uri)) {

        // ST:createPublicQueryforRelations:begin
        case STORY_SINGLE_ROW: {
            modifiedSelection = modifiedSelection + MoocSchema.Story.Cols.ID
                    + " = " + uri.getLastPathSegment();
        }
        case STORY_ALL_ROWS: {
            return query(uri, MoocSchema.Story.TABLE_NAME, projection,
                    modifiedSelection, selectionArgs, sortOrder);
        }
        case TAGS_SINGLE_ROW: {
            modifiedSelection = modifiedSelection + MoocSchema.Tags.Cols.ID
                    + " = " + uri.getLastPathSegment();
        }
        case TAGS_ALL_ROWS: {
            return query(uri, MoocSchema.Tags.TABLE_NAME, projection,
                    modifiedSelection, selectionArgs, sortOrder);
        }
        // ST:createPublicQueryforRelations:finish

        default:
            return null;
        }

    }

    /*
     * Private query that does the actual query based on the table
     */

    synchronized private Cursor query(final Uri uri, final String tableName,
            final String[] projection, final String selection,
            final String[] selectionArgs, final String sortOrder) {

        // Perform a query on the database with the given parameters
    	return mDB.query(tableName, projection, selection, selectionArgs, sortOrder);
    
    }

    @Override
    /**
     * Implement this to handle requests to insert a new row. As a courtesy,
     * call notifyChange() after inserting. This method can be called from multiple threads, 
     * as described in Processes and Threads.
     * <p>
     * (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    synchronized public Uri insert(Uri uri, ContentValues assignedValues) {

        Log.d(LOG_TAG, "query()");
        final int match = uriMatcher.match(uri);
        switch (match) {

        // ST:createUpsertForRelations:begin
        case STORY_ALL_ROWS: {
            final ContentValues values = MoocSchema.Story
                    .initializeWithDefault(assignedValues);
            values.remove(MoocSchema.Story.Cols.ID);

            final long rowID = mDB.insert(MoocSchema.Story.TABLE_NAME, values);
            if (rowID < 0) {
                Log.d(LOG_TAG, "query()");
                return null;
            }
            final Uri insertedID = ContentUris.withAppendedId(
                    STORY_CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(insertedID, null);
            return ContentUris.withAppendedId(STORY_CONTENT_URI, rowID);
        }
        case TAGS_ALL_ROWS: {
            final ContentValues values = MoocSchema.Tags
                    .initializeWithDefault(assignedValues);
            values.remove(MoocSchema.Tags.Cols.ID);

            final long rowID = mDB.insert(MoocSchema.Tags.TABLE_NAME, values);
            if (rowID < 0) {
                Log.d(LOG_TAG, "query()");
                return null;
            }
            final Uri insertedID = ContentUris.withAppendedId(TAGS_CONTENT_URI,
                    rowID);
            getContext().getContentResolver().notifyChange(insertedID, null);
            return ContentUris.withAppendedId(TAGS_CONTENT_URI, rowID);
        }
        // ST:createUpsertForRelations:finish

        // breaks intentionally omitted
        case STORY_SINGLE_ROW:
        case TAGS_SINGLE_ROW: {
            throw new IllegalArgumentException(
                    "Unsupported URI, unable to insert into specific row: "
                            + uri);
        }
        default: {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        }
    }

    @Override
    /**
     * Override this to handle requests to open a file blob.
     */
    public ParcelFileDescriptor openFile(Uri uri, String mode) {
        int imode = 0;
        try {
            if (mode.contains("w")) {
                imode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
            }
            if (mode.contains("r")) {
                imode |= ParcelFileDescriptor.MODE_READ_ONLY;
            }
            if (mode.contains("+")) {
                imode |= ParcelFileDescriptor.MODE_APPEND;
            }
        } finally {
        }

        int token = MoocSchema.URI_MATCHER.match(uri);
        File imageDirectory = getContext().getCacheDir();
        switch (token) {
        case MoocSchema.Story.PATH_FOR_ID_TOKEN: {
            final List<String> segments = uri.getPathSegments();
            final File storyFile = new File(imageDirectory, "story"
                    + segments.get(1));
            try {
                if (!storyFile.exists()) {
                    storyFile.createNewFile();
                }
                return ParcelFileDescriptor.open(storyFile, imode);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
        }
        case MoocSchema.Tags.PATH_FOR_ID_TOKEN: {
            final List<String> segments = uri.getPathSegments();
            final File tagsFile = new File(imageDirectory, "tags"
                    + segments.get(1));
            try {
                if (!tagsFile.exists()) {
                    tagsFile.createNewFile();
                }
                return ParcelFileDescriptor.open(tagsFile, imode);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
        }
            break;
        default: {
            throw new UnsupportedOperationException("URI: " + uri
                    + " not supported.");
        }
        }
        return null;
    }

    @Override
    /**
     * Implement this to handle requests to delete one or more rows.
     */
    synchronized public int delete(Uri uri, String whereClause,
            String[] whereArgs) {

        switch (uriMatcher.match(uri)) {
        // ST:createDeleteforRelations:begin
        case STORY_SINGLE_ROW:
            whereClause = whereClause + MoocSchema.Story.Cols.ID + " = "
                    + uri.getLastPathSegment();
            // no break here on purpose
        case STORY_ALL_ROWS: {
            return deleteAndNotify(uri, MoocSchema.Story.TABLE_NAME,
                    whereClause, whereArgs);
        }
        case TAGS_SINGLE_ROW:
            whereClause = whereClause + MoocSchema.Tags.Cols.ID + " = "
                    + uri.getLastPathSegment();
            // no break here on purpose
        case TAGS_ALL_ROWS: {
            return deleteAndNotify(uri, MoocSchema.Tags.TABLE_NAME,
                    whereClause, whereArgs);
        }
        // ST:createDeleteforRelations:finish

        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }

    /*
     * Private method to both attempt the delete command, and then to notify of
     * the changes
     */
    private int deleteAndNotify(final Uri uri, final String tableName,
            final String whereClause, final String[] whereArgs) {
        int count = mDB.delete(tableName, whereClause, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    /**
     * Implement this to handle requests to update one or more rows.
     */
    synchronized public int update(Uri uri, ContentValues values,
            String whereClause, String[] whereArgs) {
        Log.d(LOG_TAG, "query()");

        switch (uriMatcher.match(uri)) {

        // ST:createUpdateForRelations:begin
        case STORY_SINGLE_ROW:
            whereClause = whereClause + MoocSchema.Story.Cols.ID + " = "
                    + uri.getLastPathSegment();
        case STORY_ALL_ROWS: {
            return updateAndNotify(uri, MoocSchema.Story.TABLE_NAME, values,
                    whereClause, whereArgs);

        }
        case TAGS_SINGLE_ROW:
            whereClause = whereClause + MoocSchema.Tags.Cols.ID + " = "
                    + uri.getLastPathSegment();
        case TAGS_ALL_ROWS: {
            return updateAndNotify(uri, MoocSchema.Tags.TABLE_NAME, values,
                    whereClause, whereArgs);

        }
        // ST:createUpdateForRelations:finish

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /*
     * private update function that updates based on parameters, then notifies
     * change
     */
    private int updateAndNotify(final Uri uri, final String tableName,
            final ContentValues values, final String whereClause,
            final String[] whereArgs) {
        int count = mDB.update(tableName, values, whereClause, whereArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}