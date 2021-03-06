package com.sm.arun.smartmsg;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import DataBaseSQlite.DBAdapter;
import Model.AppUser;
import Model.AsyncResponse;

import static android.widget.Toast.makeText;


public class RegistrationActivity extends Activity  {
    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    String regId = "";
    long UserDbId;
    boolean isSqlitepopulated;

    AsyncTask<Void, Void, String> createRegIdTask;
    private static String TAG = RegistrationActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String REG_ID = "regId";
    public static final String IS_DB_POPULATE = "IsSqlitePopulated";
    public static final String EMAIL_ID = "eMailId";
    Button RegisterBtn;
    EditText userName,Email;
    DBAdapter db;
    public static final String MY_PREFS_NAME = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_view);
        Intent i=getIntent();
        isSqlitepopulated = i.getBooleanExtra("IsSqlitePopulated",false);

        applicationContext = getApplicationContext();
        RegisterBtn=(Button)findViewById(R.id.butnRegister);
        userName=(EditText)findViewById(R.id.ETxtUsername);
        Email=(EditText)findViewById(R.id.ETxtEmail);
        db= new DBAdapter(this);
//        ================
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait........");
        prgDialog.setCancelable(false);

        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        regId = prefs.getString(REG_ID, "");

    }

  public void RegisterUser(View view) {
    String emailID = Email.getText().toString();
        if (!TextUtils.isEmpty(Email.getText().toString()) && Utility.validate(Email.getText().toString())) {
// ================ Check if Google Play Service is installed in Device Play services is needed to handle GCM stuffs
             if (checkPlayServices()) {
              // Register Device in GCM Server
                registerInBackground(emailID);
            }
        } else {
            Toast.makeText(applicationContext, "Please enter valid email", Toast.LENGTH_LONG).show();
        }
  }


    // AsyncTask to register Device in GCM Server
    private void registerInBackground(final String emailID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    // Store RegId created by GCM Server in SharedPref
                    storeRegIdinSharedPref(applicationContext, regId, emailID);
                    Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    // Store  RegId and Email entered by User in SharedPref
    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(EMAIL_ID, emailID);
        editor.commit();
        new ServiceTask().execute();
//        storeRegIdinServer();

    }
//    ===== Check if Google Playservices is installed in Device or not
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
                        applicationContext,
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            Toast.makeText(
                    applicationContext,
                    "This device supports Play services, App will work normally",
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    // When Application is resumed, check for Play services support to make sure app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
    private void GetUser(String UserName) {
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
                    URL u = new URL("http://192.168.1.130/smartchat/public/getUser?username="+userName.getText().toString());
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
                        db.open();
                        if(!db.checkBeforeInsert(jsObj.getString("username"))) {
                            UserDbId=  db.insertProfileDeatils(jsObj.getString("username"), jsObj.getString("email"),
                                    jsObj.getString("first_name"), jsObj.getString("last_name"), jsObj.getString("mobile_number"), jsObj.getString("photo"));
                            Toast.makeText(RegistrationActivity.this, "Db id:" + UserDbId, Toast.LENGTH_LONG).show();

                        }
                        else{
                            SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                            UserDbId=Preferences.getLong("UserId", 0);
                            boolean undate=db.updateprofile(UserDbId, jsObj.getString("first_name"),jsObj.getString("last_name"),Long.parseLong(jsObj.getString("mobile_number")), jsObj.getString("photo"));
                        }
                        db.close();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                        editor.putString("UserName", userName.getText().toString());
                        editor.putString("Email", Email.getText().toString());
                        editor.putLong("UserId", UserDbId);
                        editor.commit();
//                        exportDatabse("MyDB", applicationContext);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("JSon Exception occured");
                    }
                }else {
                    Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG).show();
                }

            }
        }.execute();

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
    public class ServiceTask extends AsyncTask<String, Void, String> {
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
            URL u = new URL("http://192.168.1.130/smartchat/public/addUser?username="+userName.getText().toString()+"&email="+Email.getText().toString()+"&first_name=sa&last_name=a&mobile_number=1&user_group_id=1&device_type_id=2&device_id="+regId+"&is_verified=1&photo=aacccccc");

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
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();

                case 404:
                    sb.append("Requested resource not found");
                    return sb.toString();
                case 500:
                    sb.append("Something went wrong at server end");
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
//            Toast.makeText(applicationContext, ex.getCause().toString(), Toast.LENGTH_LONG).show();
            sb.append(ex.getMessage());
            return sb.toString();
         } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//            Toast.makeText(applicationContext, ex.getCause().toString(), Toast.LENGTH_LONG).show();
//            sb.append(ex.getMessage());
//            return sb.toString();
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
        String Status="";
        if(isjason) {
            try {
                JSONObject jsObj = new JSONObject(result.toString());
                Status = jsObj.getString("Status_Msg");
                switch (Status) {
                    case "1000":
                        Toast.makeText(RegistrationActivity.this, "UserName Already Exists", Toast.LENGTH_LONG).show();
                        break;
                    case "1001":
                        Toast.makeText(RegistrationActivity.this, "Email Id Already Exists", Toast.LENGTH_LONG).show();
                        break;
                    case "1002"://===MAKE SERVER CALL TO FETCH USER DETAILS MESSAGES ADMIN DETAILS ANS SO=====
                    if(!isSqlitepopulated) {
                        GetUser(userName.getText().toString());

                        try {
                            copyAppDbToDownloadFolder();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                        Toast.makeText(RegistrationActivity.this, "Registered User", Toast.LENGTH_LONG).show();
                        Intent myIntend = new Intent(RegistrationActivity.this, ConfirmationActivity.class);
                        myIntend.putExtra("ShouldRetrieveOldMsg", true);
                        myIntend.putExtra("EMAIL", Email.getText().toString());
                        startActivity(myIntend);
                        finish();
                        break;
                    case "OK":
                        db.open();
                        long Dbid = db.insertProfileDeatils(userName.getText().toString(), Email.getText().toString(), "", "", "","");
                        Toast.makeText(RegistrationActivity.this, "Db id:" + Dbid, Toast.LENGTH_LONG).show();
                        db.close();
                        try {
                            copyAppDbToDownloadFolder();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                        editor.putString("UserName", userName.getText().toString());
                        editor.putString("Email", Email.getText().toString());
                        editor.putLong("UserId", Dbid);
                        editor.commit();
                        Intent Intend = new Intent(RegistrationActivity.this, ConfirmationActivity.class);
                        Intend.putExtra("ShouldRetrieveOldMsg", false);
                        Intend.putExtra("USR_Nme", userName.getText().toString());
                        Intend.putExtra("EMAIL", Email.getText().toString());
                        startActivity(Intend);
//                                finish();
                        break;
                    case "NOK":
                        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("JSon Exception occured");
            }finally {
//                deligate.processFinish(Status);
            }
        }
        else{
            Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG).show();
//            deligate.processFinish(result);
        }


    }
}
    public void copyAppDbToDownloadFolder() throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MyDBbackup.db"); // for example "my_data_backup.db"
        File currentDB = getApplicationContext().getDatabasePath("MyDb.db"); //databaseName=your current application database name, for example "my_data.db"
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
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


    public static Bitmap decodeBase64(String input)
    {
        try {
            byte[] decodedByte = Base64.decode(input, 0);
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }catch (IllegalArgumentException e){
            return null;
        }
    }
    public static String encodeTobase64(Bitmap image) {
        try {


            Bitmap immagex = image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("LOOK", imageEncoded);
            return imageEncoded;
        } catch (IllegalArgumentException e) {
            return "" ;
        }
    }



}