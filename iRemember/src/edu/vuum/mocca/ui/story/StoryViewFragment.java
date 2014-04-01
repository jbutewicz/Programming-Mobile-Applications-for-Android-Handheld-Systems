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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import edu.vanderbilt.mooc.R;
import edu.vuum.mocca.orm.MoocResolver;
import edu.vuum.mocca.orm.StoryData;

public class StoryViewFragment extends Fragment {

	private static final String LOG_TAG = StoryViewFragment.class
			.getCanonicalName();

	private MoocResolver resolver;
	public final static String rowIdentifyerTAG = "index";

	private OnOpenWindowInterface mOpener;

	StoryData storyData;

	TextView titleTV;
	TextView bodyTV;
	Button audioButton;
	VideoView videoLinkView;
	TextView imageNameTV;
	ImageView imageMetaDataView;
	TextView storyTimeTV;
	TextView latitudeTV;
	TextView longitudeTV;

	// buttons for edit and delete
	Button editButton;
	Button deleteButton;

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.button_story_view_to_delete:
				deleteButtonPressed();
				break;
			case R.id.button_story_view_to_edit:
				editButtonPressed();
				break;
			default:
				break;
			}
		}
	};

	public static StoryViewFragment newInstance(long index) {
		StoryViewFragment f = new StoryViewFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putLong(rowIdentifyerTAG, index);
		f.setArguments(args);

		return f;
	}

	// this fragment was attached to an activity

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOpener = (OnOpenWindowInterface) activity;
			resolver = new MoocResolver(activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnOpenWindowListener");
		}
	}

	// this fragment is being created.

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	// this fragment is creating its view before it can be modified
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.story_view_fragment, container,
				false);
		container.setBackgroundColor(Color.GRAY);
		return view;
	}

	// this fragment is modifying its view before display
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		titleTV = (TextView) getView()
				.findViewById(R.id.story_view_value_title);
		bodyTV = (TextView) getView().findViewById(R.id.story_view_value_body);
		audioButton = (Button) getView().findViewById(
				R.id.story_view_value_audio_link);
		videoLinkView = (VideoView) getView().findViewById(
				R.id.story_view_value_video_link);
		imageNameTV = (TextView) getView().findViewById(
				R.id.story_view_value_image_name);
		imageMetaDataView = (ImageView) getView().findViewById(
				R.id.story_view_value_image_meta_data);
		storyTimeTV = (TextView) getView().findViewById(
				R.id.story_view_value_story_time);
		latitudeTV = (TextView) getView().findViewById(
				R.id.story_view_value_latitude);
		longitudeTV = (TextView) getView().findViewById(
				R.id.story_view_value_longitude);

		titleTV.setText("" + "");
		bodyTV.setText("" + "");
		imageNameTV.setText("" + "");
		storyTimeTV.setText("" + 0);
		latitudeTV.setText("" + 0);
		longitudeTV.setText("" + 0);

		editButton = (Button) getView().findViewById(
				R.id.button_story_view_to_edit);
		deleteButton = (Button) getView().findViewById(
				R.id.button_story_view_to_delete);

		editButton.setOnClickListener(myOnClickListener);
		deleteButton.setOnClickListener(myOnClickListener);

		try {
			setUiToStoryData(getUniqueKey());
		} catch (RemoteException e) {
			Toast.makeText(getActivity(),
					"Error retrieving information from local data store.",
					Toast.LENGTH_LONG).show();
			Log.e(LOG_TAG, "Error getting Story data from C.P.");
			// e.printStackTrace();
		}
	}

	public void setUiToStoryData(long getUniqueKey) throws RemoteException {
		Log.d(LOG_TAG, "setUiToStoryData");
		storyData = resolver.getStoryDataViaRowID(getUniqueKey);
		if (storyData == null) {
			getView().setVisibility(View.GONE);
		} else { // else it just displays empty screen
			Log.d(LOG_TAG,
					"setUiToStoryData + storyData:" + storyData.toString());
			titleTV.setText(String.valueOf(storyData.title).toString());
			bodyTV.setText(String.valueOf(storyData.body).toString());

			String audioLinkPath = String.valueOf(storyData.audioLink)
					.toString();

			// Set up audio to play back on click. For this part we can easily
			// parse the audio as a ringtone and play it back as such. Use the
			// RingtonManager function getRingtone on the audioLinkPath to
			// create the ringtone

			final Ringtone ringtone = RingtoneManager.getRingtone(
					getActivity(), Uri.parse(audioLinkPath));

			audioButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					// Play the ringtone
					ringtone.play();

				}
			});

			// Display the video

			String videoLinkPath = String.valueOf(storyData.videoLink)
					.toString();

			// Set up video playback using the MediaController android widget
			// and the video view already set up in the layout file.

			// Create a new MediaController for this activity
			MediaController mediaController = new MediaController(getActivity()
					.getApplicationContext());

			// The MediaController needs an anchorview. Anchor the Media
			// Controller to the VideoView, videoLinkView, with the function
			// setAnchorView()
			mediaController.setAnchorView(videoLinkView);

			// Now the VideoView, videoLinkView, needs to have a Media
			// Controller set to it use the setMediaController function from the
			// VideoView to set it to the new Media Controller
			videoLinkView.setMediaController(mediaController);

			// Now we need to set the URI for the VideoView, use the setVideoURI
			// function on the videoLinkPath string from before.
			if (!videoLinkPath.isEmpty())
				videoLinkView.setVideoURI(Uri.parse(videoLinkPath));

			// Start the video, using the start function on the VideoView
			if (!videoLinkPath.isEmpty())
				videoLinkView.start();

			// Display the image data

			imageNameTV.setText(String.valueOf(storyData.imageName).toString());

			String imageMetaDataPath = String.valueOf(storyData.imageLink)
					.toString();

			// Set the URI of the ImageView to the image path stored in the
			// string imageMetaDataPath, using the setImageURI function from the
			// ImageView
			if (!imageMetaDataPath.isEmpty())
				imageMetaDataView.setImageURI(Uri.parse(imageMetaDataPath));

			Long time = Long.valueOf(storyData.storyTime);
			storyTimeTV.setText(StoryData.FORMAT.format(time));

			latitudeTV.setText(Double.valueOf(storyData.latitude).toString());
			longitudeTV.setText(Double.valueOf(storyData.longitude).toString());
		}
	}

	// action to be performed when the edit button is pressed
	private void editButtonPressed() {
		mOpener.openEditStoryFragment(storyData.KEY_ID);
	}

	// action to be performed when the delete button is pressed
	private void deleteButtonPressed() {
		String message;

		message = getResources().getString(
				R.string.story_view_deletion_dialog_message);

		new AlertDialog.Builder(getActivity())
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.story_view_deletion_dialog_title)
				.setMessage(message)
				.setPositiveButton(R.string.story_view_deletion_dialog_yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									resolver.deleteAllStoryWithRowID(storyData.KEY_ID);
								} catch (RemoteException e) {
									Log.e(LOG_TAG, "RemoteException Caught => "
											+ e.getMessage());
									e.printStackTrace();
								}
								mOpener.openListStoryFragment();
								if (getResources().getBoolean(R.bool.isTablet) == true) {
									mOpener.openViewStoryFragment(-1);
								} else {
									getActivity().finish();
								}
							}

						})
				.setNegativeButton(R.string.story_view_deletion_dialog_no, null)
				.show();
	}

	public long getUniqueKey() {
		return getArguments().getLong(rowIdentifyerTAG, 0);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mOpener = null;
		resolver = null;
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			setUiToStoryData(getUniqueKey());
		} catch (RemoteException e) {
			Toast.makeText(getActivity(),
					"Error retrieving information from local data store.",
					Toast.LENGTH_LONG).show();
			Log.e(LOG_TAG, "Error getting Story data from C.P.");
		}
	}
}