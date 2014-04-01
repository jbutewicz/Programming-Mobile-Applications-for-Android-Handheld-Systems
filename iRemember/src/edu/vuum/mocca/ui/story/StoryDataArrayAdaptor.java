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


package edu.vuum.mocca.ui.story;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.mooc.R;
import edu.vuum.mocca.orm.StoryData;

public class StoryDataArrayAdaptor extends ArrayAdapter<StoryData> {

    private static final String LOG_TAG = StoryDataArrayAdaptor.class
            .getCanonicalName();

    int resource;

    public StoryDataArrayAdaptor(Context _context, int _resource,
            List<StoryData> _items) {
        super(_context, _resource, _items);
        Log.d(LOG_TAG, "constructor()");
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getView()");
        LinearLayout todoView = null;
        try {
            StoryData item = getItem(position);

            long KEY_ID = item.KEY_ID;
            String title = item.title;
            long creationTime = item.storyTime;
            
            if (convertView == null) {
                todoView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext()
                        .getSystemService(inflater);
                vi.inflate(resource, todoView, true);
            } else {
                todoView = (LinearLayout) convertView;
            }

            TextView KEY_IDTV = (TextView) todoView
            		.findViewById(R.id.story_listview_custom_row_KEY_ID_textView);
            
            TextView titleTV = (TextView) todoView
                    .findViewById(R.id.story_listview_custom_row_title_textView);
            TextView creationTimeTV = (TextView) todoView
                    .findViewById(R.id.story_listview_custom_row_creation_time_textView);
            
            KEY_IDTV.setText("" + KEY_ID);
            titleTV.setText("" + title);
            creationTimeTV.setText("" + StoryData.FORMAT.format(creationTime));
            Log.i("StoryDataArrayAdaptor", String.valueOf(item.creationTime));
            
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "exception in ArrayAdpter: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return todoView;
    }

}