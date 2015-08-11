package com.sm.arun.smartmsg;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import Model.Message;
import Model.MessageAdaptor;

/**
 * Created by Arun on 07-29-2015.
 */
public class MessagingActivity extends Activity {


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private MessageAdaptor adapter;
    private ArrayList<Message> chatHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
     // Intent Message sent from Broadcast Receiver
//        String str = getIntent().getStringExtra("msg");

        // Get Email ID from Shared preferences
//        SharedPreferences prefs = getSharedPreferences("UserDetails",
//                Context.MODE_PRIVATE);


        // Check if Google Play Service is installed in Device
        // Play services is needed to handle GCM stuffs
        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }
 // When Message sent from Broadcase Receiver is not empty
//        if (str != null) {
            // Set the message
//            msgET = (TextView) findViewById(R.id.message);
//            msgET.setText("");
//            msgET.setText(str);
            initControls();
//        }
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        loadDummyHistory();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                Message chatMessage = new Message();
                chatMessage.setId(122);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage(chatMessage);
            }
        });

    }

    public void displayMessage(Message message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<Message>();

        Message msg = new Message();
        msg.setId(1);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        Message msg1 = new Message();
        msg1.setId(2);
        msg1.setMe(false);
        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        adapter = new MessageAdaptor(MessagingActivity.this, new ArrayList<Message>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            Message message = chatHistory.get(i);
            displayMessage(message);
        }
    }
    // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();

        }
        return true;
    }

    // When Application is resumed, check for Play services support to make sure
    // app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        String str = getIntent().getStringExtra("msg");
//         if (!checkPlayServices()) {
//            Toast.makeText(
//                    getApplicationContext(),
//                    "This device doesn't support Play services, App will not work normally",
//                    Toast.LENGTH_LONG).show();
//        }
            if (str != null) {
            // Set the message
//           msgET.setText(msgET.getText().toString()+"/n"+str);
//            msgET.setText(str);

    }
}}
