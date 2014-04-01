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

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import edu.vuum.mocca.provider.MoocSchema;

/**
 * TagsCreator is a helper class that does convenience functions for converting
 * between the Custom ORM objects, ContentValues, and Cursors.
 * 
 * @author Michael A. Walker
 * 
 */
public class TagsCreator {

	/**
	 * Create a ContentValues from a provided TagsData.
	 * 
	 * @param data
	 *            StoryData to be converted.
	 * @return ContentValues that is created from the StoryData object
	 */
	public static ContentValues getCVfromTags(final TagsData data) {
		ContentValues rValue = new ContentValues();
		rValue.put(MoocSchema.Tags.Cols.LOGIN_ID, data.loginId);
		rValue.put(MoocSchema.Tags.Cols.STORY_ID, data.storyId);
		rValue.put(MoocSchema.Tags.Cols.TAG, data.tag);
		return rValue;
	}

	/**
	 * Get all of the TagsData from the passed in cursor.
	 * 
	 * @param cursor
	 *            passed in cursor
	 * @return ArrayList<TagsData\> The set of TagsData
	 */
	public static ArrayList<TagsData> getTagsDataArrayListFromCursor(
			Cursor cursor) {
		ArrayList<TagsData> rValue = new ArrayList<TagsData>();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					rValue.add(getTagsDataFromCursor(cursor));
				} while (cursor.moveToNext() == true);
			}
		}
		return rValue;
	}

	/**
	 * Get the first TagsData from the passed in cursor.
	 * 
	 * @param cursor
	 *            passed in cursor
	 * @return TagsData object
	 */
	public static TagsData getTagsDataFromCursor(Cursor cursor) {

		long rowID = cursor.getLong(cursor
				.getColumnIndex(MoocSchema.Tags.Cols.ID));
		long loginId = cursor.getLong(cursor
				.getColumnIndex(MoocSchema.Tags.Cols.LOGIN_ID));
		long storyId = cursor.getLong(cursor
				.getColumnIndex(MoocSchema.Tags.Cols.STORY_ID));
		String tag = cursor.getString(cursor
				.getColumnIndex(MoocSchema.Tags.Cols.TAG));

		// construct the returned object
		TagsData rValue = new TagsData(rowID, loginId, storyId, tag);

		return rValue;
	}
}
