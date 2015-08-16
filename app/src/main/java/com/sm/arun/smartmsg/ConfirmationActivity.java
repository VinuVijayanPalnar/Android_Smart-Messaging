package com.sm.arun.smartmsg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.RequestParams;

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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import DataBaseSQlite.DBAdapter;

/**
 * Created by Arun on 07-27-2015.
 */
public class ConfirmationActivity  extends Activity{
    private static final String MY_PREFS_NAME = "MyPrefs";
    String UserName,E_mail;
    EditText ConfirmationCode;
    boolean ShouldRetrieveOldMsg;
     Button ConfirmBtn;
    DBAdapter db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layoutview);
        db= new DBAdapter(this);
        ConfirmationCode=(EditText)findViewById(R.id.ETxtConfirmationCode);
        ConfirmBtn=(Button)findViewById(R.id.butnConfirm);
        UserName=getIntent().getExtras().getString("USR_Nme");
        E_mail=getIntent().getExtras().getString("EMAIL");
        Intent i=getIntent();
        ShouldRetrieveOldMsg = i.getBooleanExtra("ShouldRetrieveOldMsg", false);
     }
    public void OnclickConfirm(View view) {
        LoginCheck(UserName, E_mail);
    }

    private void LoginCheck(String UserName, String E_mail) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
          @Override
            protected String doInBackground(String... params) {

                BufferedReader br = null;
                StringBuilder sb = new StringBuilder("");
              SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
              String UserName=Preferences.getString("UserName", "");
              String email=Preferences.getString("Email","");
                HttpURLConnection c = null;
                try {
                    URL u = new URL("http://192.168.1.130/smartchat/public/loginCheck?username="+UserName+"&password="+ConfirmationCode.getText().toString());
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

                } catch (MalformedURLException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    return sb.toString();
                } catch (IOException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    return sb.toString();
                } finally {
                    if (c != null) {
                        try {
                            c.disconnect();
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
                    String Status = jsObj.getString("login_status");
                    switch (Status) {
                        case "login_failed":
                            Toast.makeText(ConfirmationActivity.this, "Incorrect Code", Toast.LENGTH_LONG).show();
                            break;
                        case "login_success":
                            if(ShouldRetrieveOldMsg) {
                                GetoldMessages();
                            }

                            Toast.makeText(ConfirmationActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            Intent Intend = new Intent(ConfirmationActivity.this, MessagingActivity.class);
                            startActivity(Intend);
                            finish();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSon Exception occured");
                }
            }else {
                    Toast.makeText(ConfirmationActivity.this, result, Toast.LENGTH_LONG).show();
                }

            }
        }.execute();

    }

    private void GetoldMessages() {
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
                SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                String UserName=Preferences.getString("UserName", "");
                try {
//                    http://192.168.1.130/smartchat/public/getOldMessages?username=bineesh
                    URL u = new URL("http://192.168.1.130/smartchat/public/getOldMessages?username="+UserName);
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
//            Toast.makeText(RegistrationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
                        JSONArray ar=new JSONArray(result);
                        if(ar.length()>0)
                        {   db.open();
                            for(int i=0;i<ar.length();i++) {
                                JSONObject jsObj = new JSONObject(ar.getString(i));
                                String message=jsObj.getString("msg_text");
                                String date=jsObj.getString("created_at");
                                String adminName=jsObj.getString("sender");
                                Integer AdminId=Integer.parseInt(jsObj.getString("id"));

                               db.insertMessges(message,date,adminName,AdminId);
                            }
                            db.close();
                        }
//                       exportDatabse("MyDB",ConfirmationActivity.this);

//                        if(!db.checkBeforeInsert(jsObj.getString("username"))) {
//                            UserDbId=  db.insertProfileDeatils(jsObj.getString("username"), jsObj.getString("email"),
//                                    jsObj.getString("first_name"), jsObj.getString("last_name"), jsObj.getString("mobile_number"), jsObj.getString("photo"));
//                            Toast.makeText(RegistrationActivity.this, "Db id:" + UserDbId, Toast.LENGTH_LONG).show();
//
//                        }
//                        else{
//                            SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
//                            UserDbId=Preferences.getLong("UserId", 0);
//                            boolean undate=db.updateprofile(UserDbId, jsObj.getString("first_name"),jsObj.getString("last_name"),Long.parseLong(jsObj.getString("mobile_number")), jsObj.getString("photo"));
//                        }
//                        db.close();
//                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
//                        editor.putString("UserName", userName.getText().toString());
//                        editor.putString("Email", Email.getText().toString());
//                        editor.putLong("UserId", UserDbId);
//                        editor.commit();
//                        exportDatabse("MyDB", applicationContext);
                        Toast.makeText(ConfirmationActivity.this, "done", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("JSon Exception occured");
                    }
                }else {
                    Toast.makeText(ConfirmationActivity.this, result, Toast.LENGTH_LONG).show();
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
    public static void exportDatabse(String databaseName,Context context) {
        try {


            System.out.println("Enter ");
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/"+context.getPackageName()+"/databases/"+databaseName+"";
                String backupDBPath = "BackMyDb.db";
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
