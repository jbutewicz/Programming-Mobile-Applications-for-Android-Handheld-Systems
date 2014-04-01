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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Fragments require a Container Activity, this is the one for the Edit
 * StoryData, also handles launching intents for audio/video capture.
 */
public class CreateStoryActivity extends StoryActivityBase {

	private final static String LOG_TAG = CreateStoryActivity.class
			.getCanonicalName();

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_AUDIO = 3;

	static final int CAMERA_PIC_REQUEST = 1;
	static final int CAMERA_VIDEO_REQUEST = 2;
	static final int MIC_SOUND_REQUEST = 3;

	private CreateStoryFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			fragment = CreateStoryFragment.newInstance();

			fragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, fragment).commit();
		}
	}

	/**
	 * Method to be called when Audio Clicked button is pressed.
	 */
	public void addAudioClicked(View aView) {
		launchSoundIntent();
	}

	/**
	 * Method to be called when Video Clicked button is pressed.
	 */
	public void addVideoClicked(View aView) {
		launchVideoCameraIntent();
	}

	/**
	 * Method to be called when Photo Clicked button is pressed.
	 */
	public void addPhotoClicked(View aView) {
		launchCameraIntent();
	}

	public void getDateClicked(View aView) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	public void getLocationClicked(View aView) {
		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.

				Toast.makeText(getApplicationContext(),
						"New Location obtained.", Toast.LENGTH_LONG).show();
				makeUseOfNewLocation(location);
				locationManager.removeUpdates(this);

			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(LOG_TAG, "locationManager.isProviderEnabled = true/gps");
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				makeUseOfNewLocation(location);
			} else {
				Toast.makeText(getApplicationContext(),
						"GPS has yet to calculate location.", Toast.LENGTH_LONG)
						.show();
			}

		} else {
			Toast.makeText(getApplicationContext(), "GPS is not enabled.",
					Toast.LENGTH_LONG).show();
		}
	}

	private void makeUseOfNewLocation(Location loc) {
		fragment.setLocation(loc);
	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type) {
		Log.d(LOG_TAG, "getOutputMediaFile() type:" + type);
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		// For future implementation: store videos in a separate directory
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "iRemember");

		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else if (type == MEDIA_TYPE_AUDIO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "AUD_" + timeStamp + ".3gp");
		} else {
			Log.e(LOG_TAG, "typ of media file not supported: type was:" + type);
			return null;
		}

		return mediaFile;
	}

	// This function creates a new Intent to launch the Audio Recording Activity

	private void launchSoundIntent() {

		// Create a new intent to launch the SoundRecordActivity activity
		Intent soundIntent = new Intent(this, SoundRecordActivity.class);

		// Use getOutputMediaFile() to create a new
		// filename for this specific sound file
		File fileSound = getOutputMediaFile(MEDIA_TYPE_AUDIO);

		// Add the filename to the Intent as an extra. Use the Intent-extra name
		// from the SoundRecordActivity class, EXTRA_OUTPUT
		soundIntent.putExtra(SoundRecordActivity.EXTRA_OUTPUT,
				fileSound.getAbsolutePath());

		// Start a new activity for result, using the new intent and the request
		// code MIC_SOUND_REQUEST
		startActivityForResult(soundIntent, MIC_SOUND_REQUEST);

	}

	// This function creates a new Intent to launch the built-in Camera activity

	private void launchCameraIntent() {

		// Create a new intent to launch the MediaStore, Image capture function
		// Hint: use standard Intent from MediaStore class See:
		// http://developer.android.com/reference/android/provider/MediaStore.html
		Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Set the imagePath for this image file using the pre-made function
		// getOutputMediaFile to create a new filename for this specific image;
		Uri imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		fragment.imagePath = imageUri;

		// Add the filename to the Intent as an extra. Use the Intent-extra name
		// from the MediaStore class, EXTRA_OUTPUT
		imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

		// Start a new activity for result, using the new intent and the request
		// code CAMERA_PIC_REQUEST
		startActivityForResult(imageIntent, CAMERA_PIC_REQUEST);

	}

	// This function creates a new Intent to launch the built-in Video Camera
	// activity

	private void launchVideoCameraIntent() {
		// Create a new intent to launch the MediaStore, Image capture function
		// Hint: use standard Intent from MediaStore class See:
		// http://developer.android.com/reference/android/provider/MediaStore.html
		Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		// Set the fileUri for this video file using the pre-made function
		// getOutputMediaFile to create a new filename for this specific video;
		Uri videoFileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

		// Add the filename to the Intent as an extra. Use the Intent-extra name
		// from the MediaStore class, EXTRA_OUTPUT
		videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri);

		// Specify as an extra that the video quality should be HIGH. Use the
		// Intent-extra name, EXTRA_VIDEO_QUALITY, from the MediaStore class
		// set the video image quality to high
		videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

		// Start a new activity for result, using the new intent and the request
		// code CAMERA_VIDEO_REQUEST
		startActivityForResult(videoIntent, CAMERA_VIDEO_REQUEST);

	}

}