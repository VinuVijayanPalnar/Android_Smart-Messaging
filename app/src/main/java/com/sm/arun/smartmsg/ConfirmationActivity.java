package com.sm.arun.smartmsg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Arun on 07-27-2015.
 */
public class ConfirmationActivity  extends Activity{
    private static final String MY_PREFS_NAME = "MyPrefs";
    String UserName,E_mail;
    EditText ConfirmationCode;
    Button ConfirmBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layoutview);
        ConfirmationCode=(EditText)findViewById(R.id.ETxtConfirmationCode);
        ConfirmBtn=(Button)findViewById(R.id.butnConfirm);
        UserName=getIntent().getExtras().getString("USR_Nme");
        E_mail=getIntent().getExtras().getString("EMAIL");
     }
    public void OnclickConfirm(View view) {
        LoginCheck(UserName,E_mail);
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
              String Username=Preferences.getString("UserName", "");
              String email=Preferences.getString("Email","");
                HttpURLConnection c = null;
                try {
                    URL u = new URL("http://192.168.1.172/smartchat/public/loginCheck?username="+Username+"&password="+ConfirmationCode.getText().toString());
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
                try {
                    JSONObject jsObj = new JSONObject(result.toString());
                    String Status = jsObj.getString("login_status");
                    switch (Status) {
                        case "login_failed":
                            Toast.makeText(ConfirmationActivity.this, "Incorrect Code", Toast.LENGTH_LONG).show();
                            break;
                        case "login_success":
                            Toast.makeText(ConfirmationActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            Intent Intend = new Intent(ConfirmationActivity.this, UserProfileActivity.class);
                            startActivity(Intend);
                            finish();
                            break;
               }
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("JSon Exception occured");
                }

            }
        }.execute();

    }


}
