package course.labs.activitylab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityOne extends Activity {

	private static final String RESTART_KEY = "restart";
	private static final String RESUME_KEY = "resume";
	private static final String START_KEY = "start";
	private static final String CREATE_KEY = "create";

	// String for LogCat documentation
	private final static String TAG = "Lab-ActivityOne";
	
	// Lifecycle counters
	int mCreate;
	int mStart;
	int mResume;
	int mRestart;

	// TextViews
	TextView mTvCreate;
	TextView mTvStart;
	TextView mTvResume;
	TextView mTvRestart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one);
		
		// Assign the appropriate TextViews to the TextView variables
		mTvCreate = (TextView) findViewById(R.id.create);
		mTvStart = (TextView) findViewById(R.id.start);
		mTvResume = (TextView) findViewById(R.id.resume);
		mTvRestart = (TextView) findViewById(R.id.restart);

		Button launchActivityTwoButton = (Button) findViewById(R.id.bLaunchActivityTwo); 
		launchActivityTwoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Launch Activity Two
				// Create an intent stating which Activity you would like to start
				Intent myIntent = new Intent(ActivityOne.this, ActivityTwo.class);
				
				// Launch the Activity using the intent
				startActivity(myIntent);
				
			}
		});
		
		// Check for previously saved state
		if (savedInstanceState != null) {

			// Restore value of counters from saved state
			mCreate = savedInstanceState.getInt(RESTART_KEY);
			mStart = savedInstanceState.getInt(RESUME_KEY);
			mResume = savedInstanceState.getInt(START_KEY);
			mRestart = savedInstanceState.getInt(CREATE_KEY);
		}

		// LogCat message
		Log.i(TAG, "Entered the onCreate() method.");		

		// Update the appropriate count variable
		mCreate++;

		// Update the user interface via the displayCounts() method
		displayCounts();

	}

	// Lifecycle callback overrides

	@Override
	public void onStart() {
		super.onStart();

		// LogCat message
		Log.i(TAG, "Entered the onStart() method.");

		// Update the appropriate count variable
		mStart++;

		// Update the user interface via the displayCounts() method
		displayCounts();

	}

	@Override
	public void onResume() {
		super.onResume();

		// Emit LogCat message
		Log.i(TAG, "Entered the onResume() method.");

		// Update the appropriate count variable
		mResume++;

		// Update the user interface via the displayCounts() method
		displayCounts();

	}

	@Override
	public void onPause() {
		super.onPause();

		// Emit LogCat message
		Log.i(TAG, "Entered the onPause() method.");

	}

	@Override
	public void onStop() {
		super.onStop();

		// Emit LogCat message
		Log.i(TAG, "Entered the onStop() method.");

	}

	@Override
	public void onRestart() {
		super.onRestart();

		// Emit LogCat message
		Log.i(TAG, "Entered the onRestart() method.");

		// Update the appropriate count variable
		mRestart++;

		// Update the user interface via the displayCounts() method
		displayCounts();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Emit LogCat message
		Log.i(TAG, "Entered the onDestroy() method.");

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
		// Counter state information with a collection of key-value pairs
	    savedInstanceState.putInt(RESTART_KEY, mCreate);
	    savedInstanceState.putInt(RESUME_KEY, mStart);
	    savedInstanceState.putInt(START_KEY, mResume);
	    savedInstanceState.putInt(CREATE_KEY, mRestart);
	    
	    // Documentation says to always call the following line.
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	// Updates the displayed counters
	public void displayCounts() {

		mTvCreate.setText("onCreate() calls: " + mCreate);
		mTvStart.setText("onStart() calls: " + mStart);
		mTvResume.setText("onResume() calls: " + mResume);
		mTvRestart.setText("onRestart() calls: " + mRestart);
	
	}
}
