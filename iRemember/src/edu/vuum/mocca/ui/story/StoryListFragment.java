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

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import edu.vanderbilt.mooc.R;
import edu.vuum.mocca.orm.MoocResolver;
import edu.vuum.mocca.orm.StoryData;
import edu.vuum.mocca.provider.MoocSchema;

/**
 * Fragment to hold all the UI components and related Logic for Listing
 * StoryData.
 * 
 * @author Michael A. Walker
 * 
 */
public class StoryListFragment extends ListFragment {

	static final String LOG_TAG = StoryListFragment.class.getCanonicalName();

	OnOpenWindowInterface mOpener;
	MoocResolver resolver;
	ArrayList<StoryData> StoryData;
	private StoryDataArrayAdaptor aa;

	EditText filterET;

	/**
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.d(LOG_TAG, "onAttach start");
		super.onAttach(activity);
		try {
			mOpener = (OnOpenWindowInterface) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnOpenWindowListener" + e.getMessage());
		}
		Log.d(LOG_TAG, "onAttach end");
	}

	@Override
	/**
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	public void onDetach() {
		super.onDetach();
		mOpener = null;
	}

	/**
	 * The system calls this when creating the fragment. Within your
	 * implementation, you should initialize essential components of the
	 * fragment that you want to retain when the fragment is paused or stopped,
	 * then resumed.
	 */
	@Override
	/**
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	public void onCreate(Bundle savedInstanceState) {
		Log.d(LOG_TAG, "onCreate");
		super.onCreate(savedInstanceState);
		resolver = new MoocResolver(getActivity());
		StoryData = new ArrayList<StoryData>();
		setRetainInstance(true);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater
	 * , android.view.ViewGroup, android.os.Bundle)
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.story_listview, container, false);
		// get the ListView that will be displayed
		ListView lv = (ListView) view.findViewById(android.R.id.list);

		filterET = (EditText) view
				.findViewById(R.id.story_listview_tags_filter);
		
		filterET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				updateStoryData();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});

		// customize the ListView in whatever desired ways.
		lv.setBackgroundColor(Color.GRAY);
		// return the parent view
		return view;
	}

	//
	// This function is called every time the filter EditText is changed
	// This function should update the ListView to match the specified
	// filter text.
	//
	
	public void updateStoryData() {
		Log.d(LOG_TAG, "updateStoryData");
		try {
			StoryData.clear();

			String filterWord = filterET.getText().toString();

			// create String that will match with 'like' in query
			filterWord = "%" + filterWord + "%";

			ArrayList<StoryData> currentList2 = resolver.queryStoryData(null,
					MoocSchema.Story.Cols.TITLE + " LIKE ? ",
					new String[] { filterWord }, null);
			
			StoryData.addAll(currentList2);
			aa.notifyDataSetChanged();
		} catch (Exception e) {
			Log.e(LOG_TAG,
					"Error connecting to Content Provider" + e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	public void onActivityCreated(Bundle savedInstanceState) {
		// create the custom array adapter that will make the custom row
		// layouts
		super.onActivityCreated(savedInstanceState);
		Log.d(LOG_TAG, "onActivityCreated");
		aa = new StoryDataArrayAdaptor(getActivity(),
				R.layout.story_listview_custom_row, StoryData);

		// update the back end data.
		updateStoryData();

		setListAdapter(aa);

		Button createNewButton = (Button) getView().findViewById(
				R.id.story_listview_create);
		createNewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mOpener.openCreateStoryFragment();
			}
		});
	}
	
	/*
	 * Refresh story list on fragment resume (rather than having to manually click a refresh button)
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		updateStoryData();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(LOG_TAG, "onListItemClick");
		Log.d(LOG_TAG,
				"position: " + position + "id = "
						+ (StoryData.get(position)).KEY_ID);
		mOpener.openViewStoryFragment((StoryData.get(position)).KEY_ID);
	}

}
