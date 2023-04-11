package com.example.smartnfccheckout;

import android.app.Activity;
import android.os.Bundle;

/**
 * File: ReadNFCData.java
 * Author: Aayush Shrestha
 * Date: February 25, 2023
 *
 * Description: This class provides functionality to read NFC data and display it on the screen.
 */

public class ReadNFCData extends Activity {

    /**
     * This method is called when the activity is created.
     * It sets the layout for the activity and initializes the NFC adapter.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for the activity
        setContentView(R.layout.read_nfc_data_prompt);

        // TODO: Initialize the NFC adapter (if needed)
    }

    // TODO: Add more methods for reading and processing NFC data (if needed)
}
