/*
The iRemember source code (henceforth referred to as "iRemember") is
copyrighted by Mike Walker, Adam Porter, Doug Schmidt, and Jules White
at Vanderbilt University and the University of Maryland, Copyright (c)
2014, all rights reserved.  Since iRemember is open-source, freely
available software, you are free to use, modify, copy, and
distribute--perpetually and irrevocably--the source code and object code
produced from the source, as well as copy and distribute modified
versions of this software. You must, however, include this copyright
statement along with any code built using iRemember that you release. No
copyright statement needs to be provided if you just ship binary
executables of your software products.

You can use iRemember software in commercial and/or binary software
releases and are under no obligation to redistribute any of your source
code that is built using the software. Note, however, that you may not
misappropriate the iRemember code, such as copyrighting it yourself or
claiming authorship of the iRemember software code, in a way that will
prevent the software from being distributed freely using an open-source
development model. You needn't inform anyone that you're using iRemember
software in your software, though we encourage you to let us know so we
can promote your project in our success stories.

iRemember is provided as is with no warranties of any kind, including
the warranties of design, merchantability, and fitness for a particular
purpose, noninfringement, or arising from a course of dealing, usage or
trade practice.  Vanderbilt University and University of Maryland, their
employees, and students shall have no liability with respect to the
infringement of copyrights, trade secrets or any patents by DOC software
or any part thereof.  Moreover, in no event will Vanderbilt University,
University of Maryland, their employees, or students be liable for any
lost revenue or profits or other special, indirect and consequential
damages.

iRemember is provided with no support and without any obligation on the
part of Vanderbilt University and University of Maryland, their
employees, or students to assist in its use, correction, modification,
or enhancement.

The names Vanderbilt University and University of Maryland may not be
used to endorse or promote products or services derived from this source
without express written permission from Vanderbilt University or
University of Maryland. This license grants no permission to call
products or services derived from the iRemember source, nor does it
grant permission for the name Vanderbilt University or
University of Maryland to appear in their names.
 */

package edu.vuum.mocca.orm;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import edu.vuum.mocca.provider.MoocSchema;

/**
 * encapsulation of the ContentResolver for a single URI
 * <p>
 * Uses ContentResolver instead of ContentProviderClient or other mechanism to
 * simplify code and to make this object thread safe. Future revisions to the
 * code could have ContentProviderClient (which takes 50% of the time during
 * access, once setup, than ContentResolver( did independent testing to find
 * this out.))
 * 
 * @author Michael A. Walker
 * 
 */
public class MoocResolver {

	private ContentResolver cr;

	private Uri storyURI = MoocSchema.Story.CONTENT_URI;
	private Uri tagsURI = MoocSchema.Tags.CONTENT_URI;

	/**
	 * Constructor
	 * 
	 * @param activity
	 *            The Activity to get the ContentResolver from.
	 */
	public MoocResolver(Activity activity) {
		cr = activity.getContentResolver();
	}

	/**
	 * ApplyBatch, simple pass-through to the ContentResolver implementation.
	 * 
	 * @param operations
	 * @return array of ContentProviderResult
	 * @throws RemoteException
	 * @throws OperationApplicationException
	 */
	public ContentProviderResult[] applyBatch(
			final ArrayList<ContentProviderOperation> operations)
			throws RemoteException, OperationApplicationException {
		return cr.applyBatch(MoocSchema.AUTHORITY, operations);
	}

	/*
	 * Bulk Insert for each ORM Data Type
	 */

	/**
	 * Insert a group of StoryData all at once. Mainly useful for use on
	 * installation/first boot of an application. Allowing setup of the Database
	 * into a 'start state'
	 * 
	 * @param data
	 *            what is to be inserted into the ContentProvider
	 * @return number of rows inserted
	 * @throws RemoteException
	 */
	public int bulkInsertStory(final ArrayList<StoryData> data)
			throws RemoteException {
		ContentValues[] values = new ContentValues[data.size()];
		int index = 0;
		for (StoryData story : data) {
			values[index] = story.getCV();
			++index;
		}
		return cr.bulkInsert(storyURI, values);
	}

	/**
	 * Insert a group of TagsData all at once. Mainly useful for use on
	 * installation/first boot of an application. Allowing setup of the Database
	 * into a 'start state'
	 * 
	 * @param data
	 * @return
	 * @throws RemoteException
	 */
	public int bulkInsertTags(final ArrayList<TagsData> data)
			throws RemoteException {
		ContentValues[] values = new ContentValues[data.size()];
		int index = 0;
		for (TagsData tags : data) {
			values[index] = tags.getCV();
			++index;
		}
		return cr.bulkInsert(tagsURI, values);
	}

	/*
	 * Delete for each ORM Data Type
	 */
	/**
	 * Delete all StoryData(s) from the ContentProvider, that match the
	 * selectionArgs
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return number of StoryData rows deleted
	 * @throws RemoteException
	 */
	public int deleteStoryData(final String selection,
			final String[] selectionArgs) throws RemoteException {
		return cr.delete(storyURI, selection, selectionArgs);
	}

	/**
	 * Delete all TagsData(s) from the ContentProvider, that match the
	 * selectionArgs
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return number of TagsData rows deleted.
	 * @throws RemoteException
	 */
	public int deleteTagsData(final String selection,
			final String[] selectionArgs) throws RemoteException {
		return cr.delete(tagsURI, selection, selectionArgs);
	}

	/**
	 * Get (MIME) Type for a URI
	 * 
	 * @param uri
	 * @return MIME TYPE as a String
	 * @throws RemoteException
	 */
	public String getType(Uri uri) throws RemoteException {
		return cr.getType(uri);
	}

	/*
	 * Insert for each ORM Data Type
	 */

	/**
	 * Insert a new StoryData object into the ContentProvider
	 * 
	 * @param storyObject
	 *            object to be inserted
	 * @return URI of inserted StoryData in the ContentProvider
	 * @throws RemoteException
	 */
	public Uri insert(final StoryData storyObject) throws RemoteException {
		ContentValues tempCV = storyObject.getCV();
		tempCV.remove(MoocSchema.Story.Cols.ID);
		return cr.insert(storyURI, tempCV);
	}

	public Uri insert(final TagsData tagsObject) throws RemoteException {
		ContentValues tempCV = tagsObject.getCV();
		tempCV.remove(MoocSchema.Tags.Cols.ID);
		return cr.insert(tagsURI, tempCV);
	}

	/**
	 * Access files from the Application's Assets, getting a AssetFileDescriptor
	 * 
	 * @param uri
	 * @param mode
	 * @return AssetFileDescriptor
	 * @throws RemoteException
	 * @throws FileNotFoundException
	 */
	// public AssetFileDescriptor openAssetFileDescriptor(final Uri uri,
	// final String mode) throws RemoteException, FileNotFoundException {
	// return cr.openAssetFileDescriptor(uri, mode);
	// }

	/**
	 * Access files from the content provider, getting a AssetFileDescriptor
	 * 
	 * @param uri
	 * @param mode
	 * @return
	 * @throws RemoteException
	 * @throws FileNotFoundException
	 */
	public ParcelFileDescriptor openFileDescriptor(final Uri uri,
			final String mode) throws RemoteException, FileNotFoundException {
		return cr.openFileDescriptor(uri, mode);
	}

	/*
	 * Query for each ORM Data Type
	 */

	/**
	 * Query for each StoryData, Similar to standard Content Provider query,
	 * just different return type
	 * 
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return an ArrayList of StoryData objects
	 * @throws RemoteException
	 */
	public ArrayList<StoryData> queryStoryData(final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(storyURI, projection, selection,
				selectionArgs, sortOrder);
		// make return object
		ArrayList<StoryData> rValue = new ArrayList<StoryData>();
		// convert cursor to reutrn object
		rValue.addAll(StoryCreator.getStoryDataArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}

	/**
	 * Query for each ORM TagsData, Similar to standard Content Provider query,
	 * just different return type
	 * 
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return an ArrayList of TagsData objects
	 * @throws RemoteException
	 */
	public ArrayList<TagsData> queryTagsData(final String[] projection,
			final String selection, final String[] selectionArgs,
			final String sortOrder) throws RemoteException {
		// query the C.P.
		Cursor result = cr.query(tagsURI, projection, selection, selectionArgs,
				sortOrder);
		// make return object
		ArrayList<TagsData> rValue = new ArrayList<TagsData>();
		// convert cursor to reutrn object
		rValue.addAll(TagsCreator.getTagsDataArrayListFromCursor(result));
		result.close();
		// return 'return object'
		return rValue;
	}

	/*
	 * Update for each ORM Data Type
	 */

	/**
	 * do an Update for a StoryData, same input as standard Content Provider
	 * update
	 * 
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return number of rows changed
	 * @throws RemoteException
	 */
	public int updateStoryData(final StoryData values, final String selection,
			final String[] selectionArgs) throws RemoteException {
		return cr.update(storyURI, values.getCV(), selection, selectionArgs);
	}

	/**
	 * do an Update for a TagsData, same input as standard Content Provider
	 * update
	 * 
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return number of rows changed
	 * @throws RemoteException
	 */
	public int updateTagsData(final TagsData values, final String selection,
			final String[] selectionArgs) throws RemoteException {
		return cr.update(tagsURI, values.getCV(), selection, selectionArgs);
	}

	/*
	 * Sample extensions of above for customized additional methods for classes
	 * that extend this one
	 */

	/**
	 * Get all the StoryData objects current stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the StoryData objects
	 * @throws RemoteException
	 */
	public ArrayList<StoryData> getAllStoryData() throws RemoteException {
		return queryStoryData(null, null, null, null);
	}

	/**
	 * Get all the TagsData objects currently stored in the Content Provider
	 * 
	 * @return an ArrayList containing all the TagsData objects
	 * @throws RemoteException
	 */
	public ArrayList<TagsData> getAllTagsData() throws RemoteException {
		return queryTagsData(null, null, null, null);
	}

	/**
	 * Get a StoryData from the daga stored at the given rowID
	 * 
	 * @param rowID
	 * @return StoryData at the given rowID
	 * @throws RemoteException
	 */
	public StoryData getStoryDataViaRowID(final long rowID)
			throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<StoryData> results = queryStoryData(null,
				MoocSchema.Story.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Get a TagsData from the daga stored at the given rowID
	 * 
	 * @param rowID
	 * @return TagsData at the given rowID
	 * @throws RemoteException
	 */
	public TagsData getTagsDataViaRowID(final long rowID)
			throws RemoteException {
		String[] selectionArgs = { String.valueOf(rowID) };
		ArrayList<TagsData> results = queryTagsData(null,
				MoocSchema.Tags.Cols.ID + "= ?", selectionArgs, null);
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Delete All rows, from AllStory table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllStoryWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deleteStoryData(MoocSchema.Story.Cols.ID + " = ? ", args);
	}

	/**
	 * Delete All rows, from AllTags table, that have the given rowID. (Should
	 * only be 1 row, but Content Providers/SQLite3 deletes all rows with
	 * provided rowID)
	 * 
	 * @param rowID
	 * @return number of rows deleted
	 * @throws RemoteException
	 */
	public int deleteAllTagsWithRowID(long rowID) throws RemoteException {
		String[] args = { String.valueOf(rowID) };
		return deleteTagsData(MoocSchema.Tags.Cols.ID + " = ? ", args);
	}

	/**
	 * Updates all StoryData stored with the provided StoryData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updateStoryWithID(StoryData data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.KEY_ID) };
		return updateStoryData(data, selection, selectionArgs);
	}

	/**
	 * Updates all TagsData stored with the provided TagsData's 'KEY_ID'
	 * (should only be 1 row of data in the content provider, but content
	 * provider implementation will update EVERY row that matches.)
	 * 
	 * @param data
	 * @return number of rows altered
	 * @throws RemoteException
	 */
	public int updateTagsWithID(TagsData data) throws RemoteException {
		String selection = "_id = ?";
		String[] selectionArgs = { String.valueOf(data.KEY_ID) };
		return updateTagsData(data, selection, selectionArgs);
	}

}