package com.sm.arun.smartmsg;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import DataBaseSQlite.DBAdapter;
import Model.Message;
import Model.MessageAdaptor;
import Model.MessageListModel;

/**
 * Created by Arun on 07-29-2015.
 */
public class MessagingActivity extends Activity {


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ListView messagesContainer;
    private Button ProfDetails;
    private MessageAdaptor adapter;
    private ArrayList<Message> chatHistory;
    private ArrayList<MessageListModel> Msgs;
    String regId;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        db = new DBAdapter(this);
        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        regId = prefs.getString("regId", "");
        // Intent Message sent from Broadcast Receiver
        String str = getIntent().getStringExtra("msg");
        // When Message sent from Broadcase Receiver is not empty
//        if (str != null) {
        // Set the message
//            msgET = (TextView) findViewById(R.id.message);
//            msgET.setText("");
//            msgET.setText(str);

//         Get Email ID from Shared preferences
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

// and get that as a Date
        Date today = c.getTime();

//        initControls();

        Msgs = new ArrayList<MessageListModel>();
//        db.open();
//        Cursor cursor = db.getAllMessages();
//        if (cursor.moveToFirst()) {
//            do {
//
//                Toast.makeText(this, "id:" + cursor.getString(0) + "/n" + "User Name:" + cursor.getString(1) + "/n" + "Emailid:" + cursor.getString(2) + "/n" +
//                        "First Name:" + cursor.getString(3) + "/n" + "LastName:" + cursor.getString(4) + "/n" + "Phone No" + cursor.getString(5) + "/n" + "photo:" + cursor.getString(6), Toast.LENGTH_LONG).show();
//                int msgId = cursor.getInt(0);
//                String msg = cursor.getString(2);
//                String adminName = cursor.getString(3);
//                int AdminId = cursor.getInt(4);
//                String dateTime = cursor.getString(5);
//                MessageListModel modl = new MessageListModel();
//
////                modl.SectionHeader=
//            } while (cursor.moveToNext());
//        }
//        db.close();
        exportDatabse("MyDB",MessagingActivity.this);
        // Check if Google Play Service is installed in Device
        // Play services is needed to handle GCM stuffs
        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }


    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        ProfDetails = (Button) findViewById(R.id.profilrDetailsbtn);

        loadDummyHistory();
//        sendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String messageText = messageET.getText().toString();
//                if (TextUtils.isEmpty(messageText)) {
//                    return;
//                }
//
//                Message chatMessage = new Message();
//                chatMessage.setId(122);//dummy
//                chatMessage.setMessage(messageText);
//                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//                chatMessage.setMe(true);
//
//                messageET.setText("");
//
//                displayMessage(chatMessage);
//            }
//        });

    }

    public void ToProfileDetailsPg(View view) {
        Intent i = new Intent(MessagingActivity.this, UserProfileActivity.class);
        i.putExtra("regId", regId);
        startActivity(i);
//    finish();
    }

//    public void displayMessage(Message message) {
//        if (adapter == null)
//            adapter.add(message);
//        adapter.notifyDataSetChanged();
//        scroll();
//    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory() {

        chatHistory = new ArrayList<Message>();

        Message msg = new Message();
        msg.setId(1);

        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        Message msg1 = new Message();
        msg1.setId(2);

        msg1.setMessage("How r u doing???");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);

        adapter = new MessageAdaptor(getApplicationContext(), new ArrayList<MessageListModel>());
        messagesContainer.setAdapter(adapter);

        for (int i = 0; i < chatHistory.size(); i++) {
            Message message = chatHistory.get(i);
//            displayMessage(message);
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
        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }
        if (str != null) {
//             Set the message
//           msgET.setText(msgET.getText().toString()+"/n"+str);
//            msgET.setText(str);

        }

    }
    public static void exportDatabse(String databaseName,Context context) {
        try {
System.out.println("Enter ");
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/"+context.getPackageName()+"/databases/"+databaseName+"";
                String backupDBPath = "backMyDb.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd.getAbsoluteFile()+"/", backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    System.out.println("Done exis");

                }
            }

            System.out.println("Done ");
        } catch (Exception e) {
            System.out.println("EX Done ");
        }
    }
}