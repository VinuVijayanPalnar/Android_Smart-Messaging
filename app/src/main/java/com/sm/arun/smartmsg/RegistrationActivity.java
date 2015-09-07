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
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import DataBaseSQlite.DBAdapter;
import DataBaseSQlite.DatabaseOperations;
import DataBaseSQlite.TableData;

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
    DatabaseOperations Db;
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
        Db=new DatabaseOperations(applicationContext);
        db= new DBAdapter(this);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait........");
        prgDialog.setCancelable(false);

        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        regId = prefs.getString(REG_ID, "");

    }

  public void RegisterUser(View view) {
    String emailID = Email.getText().toString();
        if (!TextUtils.isEmpty(Email.getText().toString()) && Utility.validate(Email.getText().toString())) {
// === Check if Google Play Service is installed in Device Play services is needed to handle GCM stuffs
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
//                    storeRegIdinSharedPref(applicationContext, regId, emailID);
                    Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();
                   new ServiceTask().execute();
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
                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/getUser?username="+userName.getText().toString());
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
                        if(!Db.IsUserPresent(Db,jsObj.getString("username"))){

                        Db.InsertUserDetails(Db,jsObj.getString("username"), jsObj.getString("email"),
                                    jsObj.getString("first_name"), jsObj.getString("last_name"), jsObj.getString("mobile_number"), jsObj.getString("photo"));
                        }
                        else{
                            SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                            Long ph_No=Long.parseLong(jsObj.getString("mobile_number"));
                            Db.UpdateUserDetails(Db, jsObj.getString("username"), jsObj.getString("first_name"), jsObj.getString("last_name"), ph_No, jsObj.getString("photo"));
                      }
                      SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                        editor.putString("UserName", userName.getText().toString());
                        editor.putString("Email", Email.getText().toString());
                        editor.putLong("UserId", UserDbId);
                        editor.commit();
                        exportDatabse(TableData.TableInfo.DATABASE_NAME, applicationContext);

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
            URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/addUser?username="+userName.getText().toString()+"&email="+Email.getText().toString()+"&first_name=sa&last_name=a&mobile_number=1&user_group_id=1&device_type_id=2&device_id="+regId+"&is_verified=1&photo=aacccccc");

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
                    if(!isSqlitepopulated)
//                        storeRegIdinSharedPref(applicationContext, regId, Email.getText().toString());
                        GetUser(userName.getText().toString());
//                        GetAllAdminImages();
                        Toast.makeText(RegistrationActivity.this, "Registered User", Toast.LENGTH_LONG).show();
                        Intent myIntend = new Intent(applicationContext, ConfirmationActivity.class);
                        myIntend.putExtra("ShouldRetrieveOldMsg", true);
                        myIntend.putExtra("regId", regId);
                        myIntend.putExtra("EMAIL", Email.getText().toString());
                        myIntend.putExtra("USR_Nme", userName.getText().toString());
                        startActivity(myIntend);
                        finish();
                        break;
                    case "OK":
//                        storeRegIdinSharedPref(applicationContext, regId, Email.getText().toString());
                        Db.InsertUserDetails(Db,userName.getText().toString(), Email.getText().toString(),null,null,null,null);
                        Toast.makeText(applicationContext, "Db id:" + "Registration Successful", Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                        editor.putString("UserName", userName.getText().toString());
                        editor.putString("Email", Email.getText().toString());
                        editor.commit();
                        Intent Intend = new Intent(RegistrationActivity.this, ConfirmationActivity.class);
                        Intend.putExtra("ShouldRetrieveOldMsg", false);
                        Intend.putExtra("regId", regId);
                        Intend.putExtra("USR_Nme", userName.getText().toString());
                        Intend.putExtra("EMAIL", Email.getText().toString());
                        startActivity(Intend);
                        finish();
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
            Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show();
//            deligate.processFinish(result);
        }


    }
}
    //=====WRONG FUNCTION====
//    private void GetAllAdminImages() {
//        new AsyncTask<String, Void, String>() {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//            @Override
//            protected String doInBackground(String... params) {
//
//                BufferedReader br = null;
//                StringBuilder sb = new StringBuilder("");
//                HttpURLConnection c = null;
//                try {
//                    URL u = new URL(ApplicationConstants.APP_SERVER_URL+"/smartchat/public/getAllAdmins");
//                    c = (HttpURLConnection) u.openConnection();
//                    c.setRequestMethod("GET");
//                    c.setRequestProperty("Content-length", "0");
//                    c.setRequestProperty("Content-Type", "application/json");
//                    c.setUseCaches(false);
//                    c.setAllowUserInteraction(false);
//                    c.connect();
//                    int status = c.getResponseCode();
//                    switch (status) {
//                        case 200:
//                        case 201:
//                            br = new BufferedReader(new InputStreamReader(c.getInputStream()));
//                            sb = new StringBuilder();
//                            String line;
//                            while ((line = br.readLine()) != null) {
//                                sb.append(line + "\n");
//                            }
//                            br.close();
//                            return sb.toString();
//                    }
//                }catch (ConnectException e) {
//                    Log.e("Exception Caught", e.getMessage());
////            Toast.makeText(RegistrationActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
//                    sb.append(e.getMessage());
//                    return sb.toString();
//
//                }
//                catch (MalformedURLException ex) {
//                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//                    sb.append(ex.getMessage());
//                    return sb.toString();
//                } catch (IOException ex) {
//                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//                    sb.append(ex.getMessage());
//                    return sb.toString();
//                } finally {
//                    if (c != null) {
//                        try {
//                            c.disconnect();
//                        } catch (Exception ex) {
//                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//                            sb.append(ex.getMessage());
//                            return sb.toString();
//                        }
//                    }
//
//                }
//                return sb.toString();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                boolean isjason=isJSONValid(result);
//                if(isjason) {
//                    try {
//
//                        JSONArray ar=new JSONArray(result);
//                        if(ar.length()>0)
//                        {
//                            for(int i=0;i<ar.length();i++) {
//                                JSONObject jsObj = new JSONObject(ar.getString(i));
//                            }
//                        }
//
//                        JSONObject jsObj = new JSONObject(result.toString());
//                        if(!Db.IsUserPresent(Db,jsObj.getString("username"))){
//
//                            Db.InsertUserDetails(Db,jsObj.getString("username"), jsObj.getString("email"),
//                                    jsObj.getString("first_name"), jsObj.getString("last_name"), jsObj.getString("mobile_number"), jsObj.getString("photo"));
//                        }
//                        else{
//                            SharedPreferences Preferences= getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
//                            Long ph_No=Long.parseLong(jsObj.getString("mobile_number"));
//                            Db.UpdateUserDetails(Db, jsObj.getString("username"), jsObj.getString("first_name"), jsObj.getString("last_name"), ph_No, jsObj.getString("photo"));
//                        }
//                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
//                        editor.putString("UserName", userName.getText().toString());
//                        editor.putString("Email", Email.getText().toString());
//                        editor.putLong("UserId", UserDbId);
//                        editor.commit();
//                        exportDatabse(TableData.TableInfo.DATABASE_NAME, applicationContext);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        System.out.println("JSon Exception occured");
//                    }
//                }else {
//                    Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }.execute();
//
//    }
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