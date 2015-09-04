package com.sm.arun.smartmsg;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import DataBaseSQlite.DBAdapter;
import DataBaseSQlite.DatabaseOperations;
import DataBaseSQlite.TableData;
import Model.Message;
import Model.MessageAdaptor;
import Model.MessageListModel;
import Model.MsgAdaptor;

/**
 * Created by Arun on 07-29-2015.
 */
public class MessagingActivity extends Activity {


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ListView messagesContainer;
    private Button ProfDetails;
    Context applicationcontext;
    DatabaseOperations Db;
    private MessageAdaptor adapter;
    private MsgAdaptor adptor;
    private ArrayList<Message> chatHistory;
    private ArrayList<MessageListModel> Msgs;
    int AdminId;
    String regId,AdminName,AdminImage;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_layout);
        db = new DBAdapter(this);
        applicationcontext=getApplicationContext();
        Db=new DatabaseOperations(applicationcontext);
        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        regId = prefs.getString("regId", "");
        // Intent Message sent from Broadcast Receiver
        String str = getIntent().getStringExtra("msg");

//        initControls();

        Msgs = new ArrayList<MessageListModel>();
        chatHistory= new ArrayList<Message>();
        String date=null;
        Cursor Cr=Db.GetAllMessages(Db);
        if(Cr!=null && Cr.moveToFirst())
        {
            do{
                if(date!=null) {
                    if(compareDate(date,Cr.getString(3))!=0&&compareDate(date,Cr.getString(3))!=1) {
                        date=Cr.getString(3);
                        Message HdrMsg = new Message();
                        HdrMsg.setMessage(date);
                        HdrMsg.setType(1);
                        chatHistory.add(HdrMsg);
                    }
                        Message SingleMsg = new Message();
                        SingleMsg.setAdminId(Cr.getInt(0));
                        SingleMsg.setType(2);
                        SingleMsg.setAdminName(Cr.getString(1));
                        SingleMsg.setMessage(Cr.getString(2));
                        SingleMsg.setDate(Cr.getString(3));
                        chatHistory.add(SingleMsg);

                }else
                {
                    date=Cr.getString(3);
                    Message HdrMsg = new Message();
                    HdrMsg.setMessage(date);
                    HdrMsg.setType(1);
                    chatHistory.add(HdrMsg);
                    Message SingleMsg = new Message();
                    SingleMsg.setType(2);
                    SingleMsg.setAdminId(Cr.getInt(0));
                    SingleMsg.setAdminName(Cr.getString(1));
                    SingleMsg.setMessage(Cr.getString(2));
                    SingleMsg.setDate(Cr.getString(3));
                    chatHistory.add(SingleMsg);
                }

            }while (Cr.moveToNext());
        }
        Cr.close();
        Db.close();
// Fetch all admin Details in one go and save in DB
//        for (Message msg : chatHistory) {
//
//           AdminImage=Db.GetAdminPhoto(Db,Integer.toString(msg.getAdminId()));
//            if(AdminImage==null)
//            {
//                AdminName=msg.getAdminName();
//                AdminId=msg.getAdminId();
//                FetchAdminPhoto();
//                // Issue: The fetched image is not being inserted into the arraylist
//            }else{
//                Bitmap img=decodeBase64(AdminImage);
//                if(img!=null)
//                    msg.setAdminImage(img);
//            }
//        }
//        adapter = new MessageAdaptor(applicationcontext, new ArrayList<Message>());
        messagesContainer=(ListView)findViewById(R.id.messagesContainer);
//        messagesContainer.setAdapter(adapter);
//        adapter.addAll(chatHistory);
//        for (int i = 0; i < chatHistory.size(); i++) {
//            Message msg = chatHistory.get(i);
//            displayMessage(msg);
//        }
//        Collections.sort(chatHistory,Collections.reverseOrder());
        adptor= new MsgAdaptor(applicationcontext,new ArrayList<Message>());

        messagesContainer.setAdapter(adptor);
        adptor.addAll(chatHistory);
        // When Message sent from Broadcase Receiver is not empty
        if (str != null) {
        Message msg= new Message();

//         Set the message
//            msgET = (TextView) findViewById(R.id.message);
//            msgET.setText("");
//            msgET.setText(str);
        }
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
    public int compareDate( String dte1,String dte2) {

             try {
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1 = f.parse(dte1);
                Date d2 = f.parse(dte2);
                DateFormat date = new SimpleDateFormat("MM/dd/yyyy");
                DateFormat time = new SimpleDateFormat("hh:mm:ss a");
                dte1=date.format(d1);
                dte2=date.format(d2);
                Date date1 =date.parse(dte1);
                Date date2 =date.parse(dte2);
                if(date1.compareTo(date2)>0){
                    Log.v("app", "Date1 is after Date2");
                    return 3;
                }else if(date1.compareTo(date2)<0){
                    Log.v("app","Date1 is before Date2");
                    return 2;
                }else if(date1.compareTo(date2)==0){
                    Log.v("app","Date1 is equal to Date2");
                    return 1;
                }

            } catch (ParseException e) {
                Log.v("DateCompare:","Exception "+e.getMessage()+" caught while comparing Date1 and Date2");
                e.printStackTrace();
            }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        applicationcontext .registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
        checkPlayServices();
        String str = getIntent().getStringExtra("msg");
        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }
        if (str != null) {

        }
    }

    //Must unregister onPause()
    @Override
    protected void onPause() {
        super.onPause();
        applicationcontext.unregisterReceiver(mMessageReceiver);
    }


    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
            String str = getIntent().getStringExtra("msg");
            Intent Intnt = getIntent();
            HashMap<String, String> hashMap = (HashMap<String, String>)intent.getSerializableExtra("msg");
            String message=hashMap.get("message");
            String timeStamp = hashMap.get("timeStamp");
            String AdminName = hashMap.get("AdminName");
            String AdminId = hashMap.get("AdminId");

            Db.InsertMessageDetails(Db, message, timeStamp, AdminName, Integer.parseInt(AdminId));

             Message SingleMsg = new Message();
            SingleMsg.setType(2);
            SingleMsg.setAdminId(Integer.parseInt(AdminId));
            SingleMsg.setAdminName(AdminName);
            SingleMsg.setMessage(message);
            SingleMsg.setDate(timeStamp);

            if(compareDate(chatHistory.get(0).getDate(),timeStamp)!=1&&compareDate(chatHistory.get(0).getDate(),timeStamp)!=0)
            {
                chatHistory.add(0, SingleMsg);
                Message HdrMsg = new Message();
                HdrMsg.setMessage(timeStamp);
                HdrMsg.setType(1);
                chatHistory.add(HdrMsg);
            }else{
                chatHistory.add(1,SingleMsg);
            }
//            displayMessage(SingleMsg);
            //do other stuff here
        }
    };
    public static Bitmap decodeBase64(String input)
    {
        try {
            byte[] decodedByte = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }catch (IllegalArgumentException e){
            return null;
        }
    }
    private void FetchAdminPhoto() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {

                BufferedReader br = null;
                StringBuilder sb = new StringBuilder("");
                HttpURLConnection c = null;
                try {
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/getAdminUserPhotoByUsername?adminUsername="+AdminName);
                    c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.setRequestProperty("Content-length", "0");
                    c.setRequestProperty("Content-Type", "application/json");
                    c.setUseCaches(false);
                    c.setAllowUserInteraction(false);
                    c.connect();
                    int status = c.getResponseCode();
                    switch (status) {
                        case 200:
                        case 201:
                            br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                            sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();
                            return sb.toString();
                    }
                }catch (ConnectException e) {
                    Log.e("Exception Caught", e.getMessage());
                    sb.append(e.getMessage());
                    return sb.toString();

                }
                catch (MalformedURLException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    sb.append(ex.getMessage());
                    return sb.toString();
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    sb.append(ex.getMessage());
                    return sb.toString();
                } finally {
                    if (c != null) {
                        try {
                            c.disconnect();
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            sb.append(ex.getMessage());
                            return sb.toString();
                        }
                    }

                }
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                boolean isjason=isJSONValid(result);
                if(isjason) {
                    try {
                            JSONObject jsObj = new JSONObject(result.toString());
                            AdminImage=jsObj.getString("photo");
                            Db.InsertAdminImg(Db, AdminId, AdminImage);
                            exportDatabse(TableData.TableInfo.DATABASE_NAME, applicationcontext);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("JSon Exception occured");
                    }
                }else {
                    Toast.makeText(MessagingActivity.this, result, Toast.LENGTH_LONG).show();
                }

            }
        }.execute();

    }
    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
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

    public void displayMessage(Message message) {
       if (adapter == null)
                adapter.add(message);
            adapter.notifyDataSetChanged();
            scroll();

    }

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

        adapter = new MessageAdaptor(getApplicationContext(), new ArrayList<Message>());
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


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkPlayServices();
        String str = getIntent().getStringExtra("msg");
        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }
        if (str != null) {
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