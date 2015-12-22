package com.sample.instamojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private TextView mWelcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWelcomeText = (TextView) findViewById(R.id.welcome);

        // Checking for logged user
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null || ParseAnonymousUtils.isLinked(currentUser)) {
            Log.d(TAG, "User is anonymous");
            navigateToLogin();
        } else {
            Log.d(TAG, "User name is:" + currentUser.getUsername());

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserDetails");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        mWelcomeText.setText("Welcome " + object.getString("firstName") + " " + object.getString("lastName") + "!");
                    } else {
                        Log.d(TAG, "Error in fetching user details from cloud");
                    }
                }
            });
        }
    }

    private void navigateToLogin() {
        // Launch login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
