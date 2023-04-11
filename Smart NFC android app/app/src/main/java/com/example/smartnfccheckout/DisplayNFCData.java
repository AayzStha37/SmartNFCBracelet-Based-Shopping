package com.example.smartnfccheckout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.smartnfccheckout.Constants.DEFAULT_LANGUAGE_CODE;
import static com.example.smartnfccheckout.Constants.NEW_TAG_FOUND_MESSAGE;
import static com.example.smartnfccheckout.Constants.NFC_ENABLED_MESSAGE;
import static com.example.smartnfccheckout.Constants.NFC_ERROR_MESSAGE;
import static com.example.smartnfccheckout.Constants.NO_NDEF_DATA_MESSAGE;
import static com.example.smartnfccheckout.Constants.OK_BUTTON_TEXT;
import static com.example.smartnfccheckout.Constants.PARSE_JSON_ERROR_MESSAGE;
import static com.example.smartnfccheckout.Constants.PRODUCTS_LABEL;
import static com.example.smartnfccheckout.Constants.PRODUCT_NAME_LABEL;
import static com.example.smartnfccheckout.Constants.PRODUCT_PRICE_FORMAT;
import static com.example.smartnfccheckout.Constants.PRODUCT_PRICE_LABEL;
import static com.example.smartnfccheckout.Constants.THANK_YOU_MESSAGE;
import static com.example.smartnfccheckout.Constants.TOTAL_LABEL;

/**
 * File: DisplayNFCData.java
 * Author: Aayush Shrestha
 * Date: February 25, 2023
 *
 * Description: This JAVA class is responsible for parsing the json data from the NFC reader to display
 * a list of items in the cart for the user to checkout.
 */

public class DisplayNFCData extends Activity {
    //Initializing global variables for UI elements as well as NFC Adapter configuration
    private NfcAdapter nfcAdapter;
    private PendingIntent mNfcPendingIntent;
    IntentFilter[] mReadTagFilters;
    private ListView listViewProdName;
    private ListView listViewProdPrice;
    private TextView cartTotalAmount;

    /**
     * This method is called when the activity is created.
     * It sets the layout for the activity and initializes the NFC adapter.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.display_data_nfc));
        listViewProdName = findViewById(R.id.list_view_prodName);
        listViewProdPrice = findViewById(R.id.list_view_prodPrice);
        cartTotalAmount = findViewById(R.id.cart_total_amount);
        Button checkoutButton = findViewById(R.id.checkout_button);

        checkoutButton.setOnClickListener(mTagWriter);

        initNFCAdapter();

        /* Handle foreground NFC scanning in this activity by creating a
         PendingIntent with FLAG_ACTIVITY_SINGLE_TOP flag so each new scan
         is not added to the Back Stack*/
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        /* Create intent filter to handle NDEF NFC tags detected from inside our
         application when in "read mode":*/
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        mReadTagFilters = new IntentFilter[] { ndefDetected };
    }

    /**
     * Click listener for the "checkout" button. When clicked, it displays a
     * prompt that thanks the user for shopping with the app and also provides an "OK" button
     * which when pressed directs the user to the home screen
     */
    private View.OnClickListener mTagWriter = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            new AlertDialog.Builder(DisplayNFCData.this)
                    .setTitle(THANK_YOU_MESSAGE )
                    .setPositiveButton(OK_BUTTON_TEXT , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(DisplayNFCData.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }).show();
        }
    };

    /**
     * Initializes the NFC adapter and checks if it's enabled or not.
     */
    private void initNFCAdapter() {
        // Get an instance of the NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            Toast.makeText(this, NFC_ENABLED_MESSAGE , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, NFC_ERROR_MESSAGE , Toast.LENGTH_SHORT).show();
            startActivity(new Intent( android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }


    /**
     * Called when the activity has detected an NFC tag and is in the foreground.
     * Sets up the foreground dispatch for reading from the NFC tag.
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        if (getIntent().getAction() != null) {
            // tag received when app is not running and not in the foreground:
            if (getIntent().getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
                NdefMessage[] msgs = getNdefMessagesFromIntent(getIntent());
                NdefRecord record = msgs[0].getRecords()[0];
                byte[] payload = record.getPayload();

                String payloadString = new String(payload);

                populateListViewWithParsedJSON(payloadString);
            }
        }

        // Enable priority for current activity to detect scanned tags
        nfcAdapter.enableForegroundDispatch(DisplayNFCData.this, mNfcPendingIntent, mReadTagFilters, null);

    }

    /**
     * This method populates a ListView with parsed JSON data that is received from the NFC reader.
     *
     * @param jsonData A String containing the JSON data to be parsed.
     */
    private void populateListViewWithParsedJSON(String jsonData) {
        try {
            // Remove the "en" tag from the JSON string
            if (jsonData.contains(DEFAULT_LANGUAGE_CODE )) {
                int index = jsonData.indexOf(DEFAULT_LANGUAGE_CODE );
                if (index != -1) {
                    jsonData = jsonData.substring(index + 2);
                }
            }

            // Convert the JSON string into a JSONObject
            JSONObject jsonObj = new JSONObject(jsonData);

            // Retrieve the "products" JSONArray and the cart total amount
            JSONArray products = jsonObj.getJSONArray(PRODUCTS_LABEL );
            double cartTotal = (double) jsonObj.get(TOTAL_LABEL);

            // Initialize two ArrayLists to store product names and prices
            List<String> productNameList = new ArrayList<>();
            List<String> productPriceList = new ArrayList<>();

            // Loop through the "products" JSONArray to extract product names and prices
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String productName = product.getString(PRODUCT_NAME_LABEL );
                String productPrice = product.getString(PRODUCT_PRICE_LABEL);

                productNameList.add(productName);

                // Format the product price with a dollar sign
                productPriceList.add(String.format(PRODUCT_PRICE_FORMAT , productPrice));
            }

            // Create ListAdapters for the product names and prices, and set them on the ListView
            ListAdapter prodNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productNameList);
            ListAdapter prodPriceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productPriceList);
            listViewProdName.setAdapter(prodNameAdapter);
            listViewProdPrice.setAdapter(prodPriceAdapter);

            // Display the cart total amount on a TextView
            cartTotalAmount.setText(String.format(PRODUCT_PRICE_FORMAT, cartTotal));
        } catch (JSONException e) {
            // If the JSON data cannot be parsed, display a toast message
            Toast.makeText(DisplayNFCData.this, PARSE_JSON_ERROR_MESSAGE , Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Called when the activity is no longer in the foreground and the NFC foreground dispatch
     * should be disabled.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * This is called for activities that set launchMode to "singleTop" or
     * "singleTask" in their manifest package, or if a client used the
     * FLAG_ACTIVITY_SINGLE_TOP flag when calling startActivity(Intent).
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        // Currently in tag READING mode
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
            confirmDisplayedContentOverwrite(msgs[0]);

        } else if (!intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Toast.makeText(this, NO_NDEF_DATA_MESSAGE , Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method retrieves NdefMessages from an Intent.
     *
     * @param intent The Intent to retrieve NdefMessages from.
     * @return An array of NdefMessages, or null if the Intent does not contain any NdefMessages.
     */
    NdefMessage[] getNdefMessagesFromIntent(Intent intent) {
        // Parse the Intent and initialize the NdefMessage array to null
        NdefMessage[] msgs = null;
        String action = intent.getAction();

        // If the Intent action is ACTION_TAG_DISCOVERED
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            // Retrieve the NdefMessages from the Intent
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            // If the NdefMessages are not null
            if (rawMsgs != null) {
                // Initialize the msgs array to the length of the rawMsgs array
                msgs = new NdefMessage[rawMsgs.length];

                // Loop through the rawMsgs array to cast each element to an NdefMessage and store it in the msgs array
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // If the NdefMessages are null, create an NdefMessage with an unknown tag type and return it in an array
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }
        } else {
            // If the Intent action is not ACTION_TAG_DISCOVERED, finish the current Activity
            finish();
        }

        // Return the NdefMessage array
        return msgs;
    }


    /**
     * This method displays an AlertDialog to confirm whether the user wants to overwrite the displayed content with the content from a new NdefMessage.
     *
     * @param msg The NdefMessage containing the new content to be displayed.
     */
    private void confirmDisplayedContentOverwrite(final NdefMessage msg) {
        new AlertDialog.Builder(this)
                .setTitle(NEW_TAG_FOUND_MESSAGE)
                .setPositiveButton(OK_BUTTON_TEXT , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Use the current values in the NDEF payload to update the text fields
                        String payload = new String(msg.getRecords()[0].getPayload());
                        populateListViewWithParsedJSON(payload);
                    }
                })
                .show();
    }

}
