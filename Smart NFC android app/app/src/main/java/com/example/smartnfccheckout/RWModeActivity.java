package com.example.smartnfccheckout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * File: RWModeActivity.java
 * Author: Aayush Shrestha
 * Date: March 17, 2023
 *
 * Description: This activity allows the user to select between read and write mode for NFC operations.
 */

public class RWModeActivity extends Activity {

    /**
     * onCreate method is called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then
     * this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        setContentView(R.layout.rw_mode_activity);

        // Get references to the read and write buttons
        Button readButton = findViewById(R.id.read_button);
        Button writeButton = findViewById(R.id.write_button);

        // Set onClickListeners for the read and write buttons
        readButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the read button is clicked.
             * It launches the DisplayNFCData activity.
             *
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RWModeActivity.this, DisplayNFCData.class);
                startActivity(intent);
            }
        });

        writeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This method is called when the write button is clicked.
             * It launches the WriteNFCData activity.
             *
             * @param view The view that was clicked.
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RWModeActivity.this, WriteNFCData.class);
                startActivity(intent);
            }
        });

    }
}
