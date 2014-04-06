// ST:BODY:start

package edu.vuum.mocca.provider;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 
 * <p>
 * based on the work by Vladimir Vivien (http://vladimirvivien.com/), which
 * provides a very logical organization of the meta-data of the Database and
 * Content Provider
 * <p>
 * This note might be moved to a 'Special Thanks' section once one is created
 * and moved out of future test code.
 * 
 * @author Michael A. Walker
 */
public class MoocSchema {

    /**
     * Project Related Constants
     */

    public static final String ORGANIZATIONAL_NAME = "edu.vanderbilt";
    public static final String PROJECT_NAME = "mooc";
    public static final String DATABASE_NAME = "mooc.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * ConentProvider Related Constants
     */
    public static final String AUTHORITY = ORGANIZATIONAL_NAME + "."
            + PROJECT_NAME + ".moocprovider";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();

    // register identifying URIs for Restaurant entity
    // the TOKEN value is associated with each URI registered
    private static UriMatcher buildUriMatcher() {

        // add default 'no match' result to matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // ST:addMatcherURIs:inline
        // Story URIs
        matcher.addURI(AUTHORITY, Story.PATH, Story.PATH_TOKEN);
        matcher.addURI(AUTHORITY, Story.PATH_FOR_ID, Story.PATH_FOR_ID_TOKEN);
        // Tags URIs
        matcher.addURI(AUTHORITY, Tags.PATH, Tags.PATH_TOKEN);
        matcher.addURI(AUTHORITY, Tags.PATH_FOR_ID, Tags.PATH_FOR_ID_TOKEN);
        // ST:addMatcherURIs:complete
        return matcher;

    }

    // ST:createRelationMetaData:inline
    // Define a static class that represents description of stored content
    // entity.
    public static class Story {
        // an identifying name for entity

        public static final String TABLE_NAME = "story_table";

        // define a URI paths to access entity
        // BASE_URI/story - for list of story(s)
        // BASE_URI/story/* - retrieve specific story by id
        // the token value are used to register path in matcher (see above)
        public static final String PATH = "story";
        public static final int PATH_TOKEN = 110;

        public static final String PATH_FOR_ID = "story/*";
        public static final int PATH_FOR_ID_TOKEN = 120;

        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH).build();

        public static final String CONTENT_TOPIC = "topic/edu.vanderbilt.story";

        private final static String MIME_TYPE_END = "story";

        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME
                + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME
                + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;

        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = {
                Cols.ID,
                // ST:getColumnNames:inline
                Cols.LOGIN_ID, Cols.STORY_ID, Cols.TITLE, Cols.BODY,
                Cols.AUDIO_LINK, Cols.VIDEO_LINK, Cols.IMAGE_NAME,
                Cols.IMAGE_LINK, Cols.TAGS, Cols.CREATION_TIME,
                Cols.STORY_TIME, Cols.LATITUDE, Cols.LONGITUDE
        // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.LOGIN_ID)) {
                setValues.put(Cols.LOGIN_ID, 0);
            }
            if (!setValues.containsKey(Cols.STORY_ID)) {
                setValues.put(Cols.STORY_ID, 0);
            }
            if (!setValues.containsKey(Cols.TITLE)) {
                setValues.put(Cols.TITLE, "");
            }
            if (!setValues.containsKey(Cols.BODY)) {
                setValues.put(Cols.BODY, "");
            }
            if (!setValues.containsKey(Cols.AUDIO_LINK)) {
                setValues.put(Cols.AUDIO_LINK, "");
            }
            if (!setValues.containsKey(Cols.VIDEO_LINK)) {
                setValues.put(Cols.VIDEO_LINK, "");
            }
            if (!setValues.containsKey(Cols.IMAGE_NAME)) {
                setValues.put(Cols.IMAGE_NAME, "");
            }
            if (!setValues.containsKey(Cols.IMAGE_LINK)) {
                setValues.put(Cols.IMAGE_LINK, "");
            }
            if (!setValues.containsKey(Cols.TAGS)) {
                setValues.put(Cols.TAGS, "");
            }
            if (!setValues.containsKey(Cols.CREATION_TIME)) {
                setValues.put(Cols.CREATION_TIME, 0);
            }
            if (!setValues.containsKey(Cols.STORY_TIME)) {
                setValues.put(Cols.STORY_TIME, 0);
            }
            if (!setValues.containsKey(Cols.LATITUDE)) {
                setValues.put(Cols.LATITUDE, 0);
            }
            if (!setValues.containsKey(Cols.LONGITUDE)) {
                setValues.put(Cols.LONGITUDE, 0);
            }
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String LOGIN_ID = "LOGIN_ID";
            public static final String STORY_ID = "STORY_ID";
            public static final String TITLE = "TITLE";
            public static final String BODY = "BODY";
            public static final String AUDIO_LINK = "AUDIO_LINK";
            public static final String VIDEO_LINK = "VIDEO_LINK";
            public static final String IMAGE_NAME = "IMAGE_NAME";
            public static final String IMAGE_LINK = "IMAGE_LINK";
            public static final String TAGS = "TAGS";
            public static final String CREATION_TIME = "CREATION_TIME";
            public static final String STORY_TIME = "STORY_TIME";
            public static final String LATITUDE = "LATITUDE";
            public static final String LONGITUDE = "LONGITUDE";
            // ST:getColumnDeclaration:complete
        }
    }

    // Define a static class that represents description of stored content
    // entity.
    public static class Tags {
        // an identifying name for entity

        public static final String TABLE_NAME = "tags_table";

        // define a URI paths to access entity
        // BASE_URI/tag - for list of tag(s)
        // BASE_URI/tag/* - retrieve specific tag by id
        // the token value are used to register path in matcher (see above)
        public static final String PATH = "tag";
        public static final int PATH_TOKEN = 210;

        public static final String PATH_FOR_ID = "tag/*";
        public static final int PATH_FOR_ID_TOKEN = 220;

        // URI for all content stored as Restaurant entity
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH).build();

        public static final String CONTENT_TOPIC = "topic/edu.vanderbilt.tags";

        // ST:relationKeyClause:inline
        public static final String ALL_KEY_CLAUSE;
        static {
            ALL_KEY_CLAUSE = new StringBuilder().append('"').append(Cols.TAG)
                    .append('"').append("=?").append(" AND ").append('"')
                    .append(Cols.STORY_ID).append('"').append("=?")
                    .append(" AND ").append('"').append(Cols.LOGIN_ID)
                    .append('"').append("=?").toString();
        };

        public static final String[] ALL_KEY_COLUMNS = new String[] { Cols.TAG,
                Cols.STORY_ID, Cols.LOGIN_ID };

        // ST:relationKeyClause:complete

        private final static String MIME_TYPE_END = "tags";

        // define the MIME type of data in the content provider
        public static final String CONTENT_TYPE_DIR = ORGANIZATIONAL_NAME
                + ".cursor.dir/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = ORGANIZATIONAL_NAME
                + ".cursor.item/" + ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;

        // the names and order of ALL columns, including internal use ones
        public static final String[] ALL_COLUMN_NAMES = { Cols.ID,
                // ST:getColumnNames:inline
                Cols.LOGIN_ID, Cols.STORY_ID, Cols.TAG
        // ST:getColumnNames:complete
        };

        public static ContentValues initializeWithDefault(
                final ContentValues assignedValues) {
            // final Long now = Long.valueOf(System.currentTimeMillis());
            final ContentValues setValues = (assignedValues == null) ? new ContentValues()
                    : assignedValues;
            if (!setValues.containsKey(Cols.LOGIN_ID)) {
                setValues.put(Cols.LOGIN_ID, 0);
            }
            if (!setValues.containsKey(Cols.STORY_ID)) {
                setValues.put(Cols.STORY_ID, 0);
            }
            if (!setValues.containsKey(Cols.TAG)) {
                setValues.put(Cols.TAG, "");
            }
            return setValues;
        }

        // a static class to store columns in entity
        public static class Cols {
            public static final String ID = BaseColumns._ID; // convention
            // The name and column index of each column in your database
            // ST:getColumnDeclaration:inline
            public static final String LOGIN_ID = "LOGIN_ID";
            public static final String STORY_ID = "STORY_ID";
            public static final String TAG = "TAG";
            // ST:getColumnDeclaration:complete
        }
    }
    // ST:createRelationMetaData:complete

}
// ST:BODY:end