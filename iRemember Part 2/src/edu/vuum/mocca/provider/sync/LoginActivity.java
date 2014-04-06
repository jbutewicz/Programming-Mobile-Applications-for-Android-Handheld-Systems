// ST:BODY:start 

package edu.vuum.mocca.provider.sync;

import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import edu.vanderbilt.mooc.mooc_cp.R;

/**
 * Activity that gets Launched when Adding an Account to the SyncManager
 * <p>
 * An Activity to prompt for a Server Address, a UserName, and a Password. Uses
 * these to verify user login credentials and to create a 'token' identifying
 * the user account on the device.
 * 
 * @author Michael A. Walker
 * @Date 2012-11-23
 */
public class LoginActivity extends AccountAuthenticatorActivity {

    public final static String LOG_TAG = LoginActivity.class.getCanonicalName();

    /**************************************************************
     * UI component variables
     *************************************************************/
    EditText mServerIP;
    EditText mUsername;
    EditText mPassword;
    Button mLoginButton;

    /**************************************************************
     * Examples of constants for message passing on the AsyncTask
     *************************************************************/
    public final static String PARAM_AUTHTOKEN_TYPE = "";
    public final static String PARAM_ACCOUNT_TYPE = "";

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate() called");
        setContentView(R.layout.authenticator_layout);

        mServerIP = (EditText) findViewById(R.id.server);
        mUsername = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);

        mLoginButton = (Button) findViewById(R.id.login);

        // might be able to make this go away and be in the XML
        // just easier to put here for now.
        mLoginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                loginButtonPressed(v);
            }
        });
    }

    // start the background threaded action to authenticate the user
    public void loginButtonPressed(View v) {
        Log.d(LOG_TAG, "onCreate() called");
        String user = mUsername.getText().toString().trim()
                .toLowerCase(Locale.ENGLISH);
        String password = mPassword.getText().toString().trim()
                .toLowerCase(Locale.ENGLISH);

        if (user.length() > 0 && password.length() > 0) {
            LoginTask t = new LoginTask(LoginActivity.this);
            t.execute(user, password);
        }
    }

    /**
     * This is the background logic behind the user login credentials checking
     * 
     * @author Michael A. Walker
     * 
     */
    private class LoginTask extends AsyncTask<String, Void, Boolean> {

        Context mContext;
        ProgressDialog mDialog;

        // constructor
        LoginTask(Context c) {
            Log.d(LOG_TAG, "LoginTask() constructed");
            mContext = c;
            mLoginButton.setEnabled(false);
            // display a dialog with a spinner showing progress being worked on
            mDialog = ProgressDialog.show(c, "",
                    getString(R.string.authenticating), true, false);
            mDialog.setCancelable(true);
        }

        // Threaded background work (doesn't block UI)
        @Override
        public Boolean doInBackground(String... params) {
            Log.d(LOG_TAG, "LoginTask.doInBackground() called");
            String user = params[0];
            String pass = params[1];

            // TODO:Do something internetty HERE
            try {
                Thread.sleep(2000);
            } catch (Exception e) {

                e.printStackTrace();
            }

            Bundle result = null;
            Account account = new Account(user,
                    mContext.getString(R.string.ACCOUNT_TYPE));
            AccountManager am = AccountManager.get(mContext);
            if (am.addAccountExplicitly(account, pass, null)) {
                result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                setAccountAuthenticatorResult(result);
                return true;
            } else {
                return false;
            }
        }

        // post processing of login verification
        @Override
        public void onPostExecute(Boolean result) {
            Log.d(LOG_TAG, "LoginTask.onPostExecute() called");
            mLoginButton.setEnabled(true);
            mDialog.dismiss();
            if (result)
                finish();
        }
    }
}
// ST:BODY:end 