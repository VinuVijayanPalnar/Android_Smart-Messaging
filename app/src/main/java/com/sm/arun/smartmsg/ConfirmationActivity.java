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
import DataBaseSQlite.DatabaseOperations;
import DataBaseSQlite.TableData;

/**
 * Created by Arun on 07-27-2015.
 */
public class ConfirmationActivity  extends Activity{
    Context applicationContext;
    private static final String MY_PREFS_NAME = "MyPrefs";
    String UserName,E_mail;
    String regId = "";
//    public static final String USER_NME = "userNme";
//    public static final String REG_ID = "regId";
//    public static final String EMAIL_ID = "eMailId";
    EditText ConfirmationCode;
    boolean ShouldRetrieveOldMsg;
     Button ConfirmBtn;
    DBAdapter db;

    DatabaseOperations Db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layoutview);
        applicationContext=getApplicationContext();
        Db=new DatabaseOperations(applicationContext);
        db= new DBAdapter(this);
        ConfirmationCode=(EditText)findViewById(R.id.ETxtConfirmationCode);
        ConfirmBtn=(Button)findViewById(R.id.butnConfirm);
        UserName=getIntent().getExtras().getString("USR_Nme");
        E_mail=getIntent().getExtras().getString("EMAIL");
        regId=getIntent().getExtras().getString("regId");
        Intent i=getIntent();
        ShouldRetrieveOldMsg = i.getBooleanExtra("ShouldRetrieveOldMsg", false);
     }
    public void OnclickConfirm(View view) {
        LoginCheck();
    }

    private void LoginCheck() {
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
//              String UserName=Preferences.getString("UserName", "");
              String email=Preferences.getString("Email","");
                HttpURLConnection c = null;
                try {
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/loginCheck?username="+UserName+"&password="+ConfirmationCode.getText().toString());
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
                            Toast.makeText(ConfirmationActivity.this, "Incorrect Code: Login Failed", Toast.LENGTH_LONG).show();
                            break;
                        case "login_success":

                            if(ShouldRetrieveOldMsg) {
//                               storeRegIdinSharedPref(applicationContext, regId, E_mail);
//                                GetoldMessages();
                                UpdateToken();

                            }
                            else
                            {
                                Toast.makeText(ConfirmationActivity.this, "Login Success:New User", Toast.LENGTH_LONG).show();
                                storeRegIdinSharedPref(applicationContext, regId,UserName, E_mail);
                                Intent Intend = new Intent(ConfirmationActivity.this, MessagingActivity.class);
                                startActivity(Intend);
                                finish();
                            }

                                GetAdminDetails();
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
    private void storeRegIdinSharedPref(Context context, String regId,
                                      String userName , String emailID) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", userName);
        editor.putString("regId", regId);
        editor.putString("emailId", emailID);
        editor.commit();

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
//                String UserName=Preferences.getString("UserName", "");
                try {
//                    http://192.168.1.130/smartchat/public/getOldMessages?username=bineesh
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/getOldMessages?username="+UserName);
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
                        {
                            for(int i=0;i<ar.length();i++) {
                                JSONObject jsObj = new JSONObject(ar.getString(i));
                                String message=jsObj.getString("msg_text");
                                String date=jsObj.getString("created_at");
                                String adminName=jsObj.getString("sender");
                                Integer AdminId=Integer.parseInt(jsObj.getString("id"));
                                Db.InsertMessageDetails(Db, message, date, adminName, AdminId);
                            }

                        }
                        Toast.makeText(ConfirmationActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                        Intent Intend = new Intent(ConfirmationActivity.this, MessagingActivity.class);
                        startActivity(Intend);
                        finish();

                       exportDatabse(TableData.TableInfo.DATABASE_NAME,applicationContext);
                        Toast.makeText(ConfirmationActivity.this, "done", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Intent Intend = new Intent(ConfirmationActivity.this, MessagingActivity.class);
                        startActivity(Intend);
                        finish();
                        System.out.println("JSon Exception occured"+e.getMessage());
                    }
                }else {
                    Toast.makeText(ConfirmationActivity.this, result, Toast.LENGTH_LONG).show();
                }

            }
        }.execute();

    }
    private void UpdateToken() {
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
//                String UserName=Preferences.getString("UserName", "");
                try {
//                    http://192.168.1.130/smartchat/public/getOldMessages?username=bineesh
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/updateDeviceID?username="+UserName+"&deviceID="+regId);
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
                        JSONObject jsObj = new JSONObject(result.toString());
                        String response = jsObj.getString("response");
                        switch (response) {
                            case "Updated successfully":
                                storeRegIdinSharedPref(applicationContext, regId,UserName, E_mail);
                                GetoldMessages();
                                Toast.makeText(ConfirmationActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
                                break;
                            case "Failed to Update":
                                     Toast.makeText(ConfirmationActivity.this, "Failed To Update Device Token", Toast.LENGTH_LONG).show();

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
    private void GetAdminDetails() {
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
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/getAllAdmins");
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
                        {
                            for(int i=0;i<ar.length();i++) {
                                JSONObject jsObj = new JSONObject(ar.getString(i));
                                Integer AdminId=Integer.parseInt(jsObj.getString("id"));
                                String AdminImage=jsObj.getString("photo");
                                Db.InsertAdminImg(Db, AdminId, AdminImage);
                            }

                        }
                        exportDatabse(TableData.TableInfo.DATABASE_NAME,applicationContext);
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
