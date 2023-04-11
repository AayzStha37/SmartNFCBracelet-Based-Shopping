package com.example.smartnfccheckout;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;

import static android.content.ContentValues.TAG;
import static com.example.smartnfccheckout.Constants.DEFAULT_LANGUAGE_CODE;
import static com.example.smartnfccheckout.Constants.EMPTY_FIELD_ERROR_MESSAGE;
import static com.example.smartnfccheckout.Constants.EMPTY_STRING;
import static com.example.smartnfccheckout.Constants.NFC_ENABLED_MESSAGE;
import static com.example.smartnfccheckout.Constants.NFC_ERROR_MESSAGE;
import static com.example.smartnfccheckout.Constants.SUCCESS_PRODUCT_NAME_PREFIX;
import static com.example.smartnfccheckout.Constants.SUCCESS_PRODUCT_PRICE_PREFIX;
import static com.example.smartnfccheckout.Constants.WRITE_TAG_ERROR_MESSAGE;

/**
 * File: WriteNFCData.java
 * Author: Aayush Shrestha
 * Date: February 25, 2023
 *
 * Description: This activity is responsible for writing product information
 * to an NFC tag, so that it can be scanned by an NFC reader later on.
 */
public class WriteNFCData extends Activity {
    private NfcAdapter nfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mWriteTagFilters;
    private boolean mWriteMode = false;
    private EditText productName;
    private EditText productPrice;

    /**
     * A helper class to store product name and price.
     */
    private class ProductSpecs {
        private String productName;
        private float productPrice;

        private ProductSpecs(String productName, float productPrice) {
            this.productName= productName;
            this.productPrice = productPrice;
        }
    }

    /**
     * This method is called when the activity is created.
     * It sets the layout for the activity and initializes the NFC adapter.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_nfc_data_activity);

        // Get references to UI elements
        Button writeButton = findViewById(R.id.write_nfc_button);
        productName = findViewById(R.id.product_name_edittext);
        productPrice = findViewById(R.id.product_price_edittext);

        // Set click listener for the "write" button
        writeButton.setOnClickListener(mTagWriter);

        // Initialize the NFC adapter
        initNFCAdapter();

        /* Handle foreground NFC scanning in this activity by creating a
         PendingIntent with FLAG_ACTIVITY_SINGLE_TOP flag so each new scan
         is not added to the Back Stack*/
        mNfcPendingIntent = PendingIntent.getActivity(
                this,
                0,
                new Intent(this, getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
        );

        /* Create intent filter to detect any NFC tag when attempting to write
         to a tag in "write mode"*/
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        // Create IntentFilter arrays
        mWriteTagFilters = new IntentFilter[] { tagDetected };
    }

    /**
     * Click listener for the "write" button. It checks if the product name and
     * price fields are not empty, and then enables NFC foreground dispatch
     * to write the product information to an NFC tag.
     */
    private View.OnClickListener mTagWriter = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (productName.getText().toString().equals("")
                    || productPrice.getText().toString().equals("")) {
                Toast.makeText(
                        WriteNFCData.this,
                        EMPTY_FIELD_ERROR_MESSAGE,
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                mWriteMode = true;
                nfcAdapter.enableForegroundDispatch(
                        WriteNFCData.this,
                        mNfcPendingIntent,
                        mWriteTagFilters,
                        null);
            }
        }
    };

    /**
     * Initializes the NFC adapter and checks if it's enabled or not.
     */
    private void initNFCAdapter() {
        // Get an instance of the NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            // NFC is enabled and ready
            Toast.makeText(this, NFC_ENABLED_MESSAGE , Toast.LENGTH_SHORT).show();
        } else {
            // NFC is not supported or not enabled
            Toast.makeText(this, NFC_ERROR_MESSAGE , Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Converts a ProductSpecs object to a JSON string.
     *
     * @param productSpecs the ProductSpecs object to be converted
     * @return the JSON string representation of the ProductSpecs object
     */
    private String convertToJSON(ProductSpecs productSpecs) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(productSpecs);
        Log.d("JSON STRING", jsonString);
        return jsonString;
    }

    /**
     * This method is called when a new NFC tag is discovered while the activity is in the foreground.
     *
     * @param intent the Intent object that contains the information about the tag
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: " + intent);

        if (mWriteMode) {
            // Currently in tag WRITING mode
            if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                // Get the detected tag
                Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                // Write data to the tag
                writeDataToTag(buildNdefMessage(), detectedTag);
            }
        }
    }

    /**
     * Builds an NDEF message containing the product name and price as a JSON string.
     *
     * @return the NDEF message containing the product information
     */
    private NdefMessage buildNdefMessage() {
        // Check if any of the required text fields are empty
        if(productName.getText().toString().equals("") || productPrice.getText().toString().equals("")){
            Toast.makeText(this, EMPTY_FIELD_ERROR_MESSAGE , Toast.LENGTH_SHORT).show();
            finish();
        }

        // Create a new ProductSpecs object with the text field inputs
        ProductSpecs productSpecs = new ProductSpecs(productName.getText().toString(),Float.parseFloat(productPrice.getText().toString()));

        // Convert the ProductSpecs object to a JSON string
        NdefRecord record = NdefRecord.createTextRecord(DEFAULT_LANGUAGE_CODE , convertToJSON(productSpecs));

        // Return the NDEF message
        return new NdefMessage(new NdefRecord[] { record });
    }

    /**
     * Writes an NDEF message to an NFC tag.
     *
     * @param message The NDEF message to write to the tag.
     * @param tag The NFC tag to write the message to.
     */
    private void writeDataToTag(NdefMessage message, Tag tag) {
        // Get the Ndef object for the tag
        Ndef ndef = Ndef.get(tag);

        // Check if the tag supports NDEF
        if (ndef != null) {
            try {
                // Connect to the tag
                ndef.connect();

                // Write the NDEF message to the tag
                ndef.writeNdefMessage(message);

                // Display a success message with the product name and price
                Toast.makeText(this, SUCCESS_PRODUCT_NAME_PREFIX +productName.getText().toString()
                        +SUCCESS_PRODUCT_PRICE_PREFIX +productPrice.getText().toString(), Toast.LENGTH_SHORT).show();

                // Clear the text fields
                productName.setText(EMPTY_STRING);
                productPrice.setText(EMPTY_STRING);
            } catch (IOException | FormatException e) {
                // Catch any exceptions that occur while writing to the tag
                e.printStackTrace();
                Toast.makeText(this, WRITE_TAG_ERROR_MESSAGE , Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    // Close the connection to the tag
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when the activity has detected an NFC tag and is in the foreground.
     * Sets up the foreground dispatch for reading from the NFC tag.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Set up the foreground dispatch for reading from the NFC tag
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter[] filters = new IntentFilter[] { new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) };
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    /**
     * Called when the activity is no longer in the foreground and the NFC foreground dispatch
     * should be disabled.
     */
    @Override
    protected void onPause() {
        super.onPause();

        // Disable the foreground dispatch for reading from the NFC tag
        nfcAdapter.disableForegroundDispatch(this);
    }
}