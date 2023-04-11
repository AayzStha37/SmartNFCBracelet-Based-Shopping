package com.example.smartnfccheckout;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * File: ShopActivity.java
 * Author: Aayush Shrestha
 * Date: March 17, 2023
 *
 * Description: This class represents the shopping activity of the application. It pings the localhost URL at a regular
 * interval, and if the response is OK, it downloads and displays product information from a text file.
 * The user can click on a button to proceed to the next product.
 */
public class ShopActivity extends AppCompatActivity {
    //Initializing global variables for UI elements as well as constants for local host URLS

    private static final String IP_CONFIG = "http://134.190.148.123"; //localhost ipv4 address
    private static final String PING_URL = IP_CONFIG + "/test/Ping.txt"; //location of the file that dictates when the ProdSpec file is to be picked up
    private static final String DOWNLOAD_URL = IP_CONFIG + "/test/ProdSpec_"; //location of the ProdSpec file
    private static final int PING_INTERVAL_MS = 2000; // 2 seconds
    private TextView prodNameTextView;
    private TextView prodPriceTextView;
    private Button proceedButton;
    private int fileCount;
    private Vibrator vibrator;
    private boolean vibrate = true;

    /**
     * Initializes the activity and its components, sets up the button listener, and starts pinging the URL.
     *
     * @param savedInstanceState the saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_mode_activity);
        prodNameTextView = findViewById(R.id.shop_prod_name);
        prodPriceTextView = findViewById(R.id.shop_prod_price);
        proceedButton = findViewById(R.id.proceed_button);
        proceedButton.setEnabled(false);
        fileCount = 1;
        //initializing vibrator object for haptic feedback
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileCount < 10)
                    fileCount++;
                else
                    fileCount = 1;

                prodNameTextView.setText("");
                prodPriceTextView.setText("");

                proceedButton.setEnabled(false);
                vibrate = true;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start pinging the URL
        new PingTask().execute(PING_URL);
    }

    /**
     * An AsyncTask that pings localhost URL at regular intervals and downloads and displays product information from a text file.
     * If the ping response is OK, it calls the DownloadFileTask to download the product information from the file.
     */
    private class PingTask extends AsyncTask<String, Void, Boolean> {

        /**
         * Pings the localhost URL at regular intervals and downloads and displays product information from a text file.
         *
         * @param urls the URL to be pinged
         * @return true if the URL is successfully pinged and the file is downloaded, false otherwise
         */
        @Override
        protected Boolean doInBackground(String... urls) {
            while (true) {
                try {
                    // Ping the URL
                    URL url = new URL(PING_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    int statusCode = urlConnection.getResponseCode();
                    // Check if the response code is HTTP_OK
                    if (statusCode == HttpURLConnection.HTTP_OK) {
                        // Check if the DownloadFileTask for PING_URL is successful
                        if (DownloadFileTask(PING_URL)) {
                            // Download the file for the next count and sleep for PING_INTERVAL_MS
                            DownloadFileTask(DOWNLOAD_URL + Integer.toString(fileCount) + ".txt");
                            Thread.sleep(PING_INTERVAL_MS);
                        } else {
                            // Update UI to show connection status as FALSE
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //  Toast.makeText(ShopActivity.this, "Connection FALSE", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Update UI to show NO Connection status
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ShopActivity.this, "NO Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Pings the localhost URL at regular intervals of 2 seconds.
         * If the value of the file is 1 then it downloads the prodSpec file that is next in line from he local host server
         * Finally it displays product information (name and price) from the prodSpec file.
         *
         * @param finalURL the localhost URL to be pinged
         * @return true if the URL is successfully pinged and the file is downloaded, false otherwise
         */
        private boolean DownloadFileTask(String finalURL) {
            try {
                // Download the file
                URL url = new URL(finalURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                urlConnection.disconnect();
                final String result = stringBuilder.toString();
                final String[] parts = result.split(":");

                // Check if the finalURL is for a product
                if (finalURL.contains(DOWNLOAD_URL)) {
                    // Update the UI with product details
                    runOnUiThread(new Runnable() {
                        public void run() {
                            prodNameTextView.setText(parts[0]);
                            prodPriceTextView.setText(String.format("$ %s", parts[1]));

                            proceedButton.setEnabled(true);
                            if (vibrate) {
                                vibrate = false;
                                vibrator.vibrate(500);
                            }
                        }
                    });
                } else {
                    // Return true if the file contains 1
                    return (result.equals("1") || result.equals("1\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}

