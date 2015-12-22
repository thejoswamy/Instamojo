package com.sample.instamojo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private final static String TAG = "LoginActivity";

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Checking for active network connection
            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            if (!isConnected) {
                Toast.makeText(this, getString(R.string.network_disconnected), Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // Checking whether the user already exists or not
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", username);

            query.findInBackground(new FindCallbackImpl(this, username, password));
        }
    }

    private boolean isUsernameValid(String username) {
        // Can add more conditions
        return !username.contains(" ");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private static class FindCallbackImpl implements FindCallback<ParseUser> {

        private final WeakReference<LoginActivity> mActivity;
        private final String mUsername;
        private final String mPassword;

        public FindCallbackImpl(LoginActivity activity, String username, String password) {
            mActivity = new WeakReference<LoginActivity>(activity);
            mUsername = username;
            mPassword = password;
        }

        @Override
        public void done(List<ParseUser> parseUsers, ParseException e) {
            LoginActivity activity = mActivity.get();
            if (activity != null) {
                if (e == null) {
                    if (parseUsers.size() > 0) { // User exists
                        ParseUser.logInInBackground(mUsername, mPassword, new LogInCallbackImpl(activity));
                    } else { // Signup
                        final ParseUser user = new ParseUser();
                        user.setUsername(mUsername);
                        user.setPassword(mPassword);
                        user.signUpInBackground(new SignUpCallbackImpl(activity));
                    }
                } else {
                    activity.showProgress(false);
                    Log.e(TAG, "Authentication failed due to some error");
                    activity.mUsernameView.setError(activity.getString(R.string.error_login_failed));
                    activity.mUsernameView.requestFocus();
                }
            }
        }
    }

    private static class LogInCallbackImpl implements LogInCallback {

        private final WeakReference<LoginActivity> mActivity;

        public LogInCallbackImpl(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void done(ParseUser user, ParseException e) {
            LoginActivity activity = mActivity.get();
            activity.showProgress(false);
            if (activity != null) {
                if (user != null) {
                    activity.navigateToHome();
                } else {
                    Log.e(TAG, "Login failed!!");
                    activity.mPasswordView.setError(activity.getString(R.string.error_incorrect_password));
                    activity.mPasswordView.requestFocus();
                }
            }

        }
    }

    private static class SignUpCallbackImpl implements SignUpCallback {

        private final WeakReference<LoginActivity> mActivity;

        public SignUpCallbackImpl(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void done(ParseException e) {
            LoginActivity activity = mActivity.get();
            activity.showProgress(false);
            if (activity != null) {
                if (e == null) {
                    // Signup successful!
                    activity.navigateToDetails();
                } else {
                    Log.e(TAG, "Sign Up failed!!");
                    activity.mUsernameView.setError(activity.getString(R.string.error_login_failed));
                    activity.mUsernameView.requestFocus();
                }
            }
        }
    }

    private void navigateToHome() {
        // Fire the main activity
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToDetails() {
        Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

