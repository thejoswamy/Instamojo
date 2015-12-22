package com.sample.instamojo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;

public class DetailsActivity extends AppCompatActivity {

    private final static String TAG = "DetailsActivity";

    private EditText mFirstName;
    private EditText mLastName;
    private Button mUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.update || id == EditorInfo.IME_NULL) {
                    updateDetails();
                    return true;
                }
                return false;
            }
        });
        mUpdate = (Button) findViewById(R.id.updateButton);

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });
    }

    private void updateDetails() {
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError(getString(R.string.error_field_required));
            mFirstName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError(getString(R.string.error_field_required));
            mLastName.requestFocus();
            return;
        }
        ParseObject detailsObject = new ParseObject("UserDetails");
        detailsObject.put("firstName", firstName);
        detailsObject.put("lastName", lastName);
        detailsObject.saveInBackground();

        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
