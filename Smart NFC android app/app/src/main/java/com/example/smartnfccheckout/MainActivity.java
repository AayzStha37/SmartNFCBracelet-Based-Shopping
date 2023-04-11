package com.example.smartnfccheckout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * File: MainActivity.java
 * Author: Aayush Shrestha
 * Date: February 25, 2023
 *
 * Description: This class contains the main activity for the Smart NFC Checkout app.
 * The activity displays two buttons, one to navigate to the shop mode and another to navigate to the RW mode.
 */

public class MainActivity extends Activity {

    /**
     * This method is called when the activity is created.
     * It sets the layout for the activity and initializes the NFC adapter.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity
        setContentView(R.layout.activity_main);

        // Get references to the shop and RW mode buttons
        Button shopButton = findViewById(R.id.shop_button);
        Button rwButton = findViewById(R.id.rw_mode_button);

        // Set the onClickListener for the shop button
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the shop button is clicked, navigate to the ShopActivity
                Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListener for the RW mode button
        rwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When the RW mode button is clicked, navigate to the RWModeActivity
                Intent intent = new Intent(MainActivity.this, RWModeActivity.class);
                startActivity(intent);
            }
        });
    }
}
