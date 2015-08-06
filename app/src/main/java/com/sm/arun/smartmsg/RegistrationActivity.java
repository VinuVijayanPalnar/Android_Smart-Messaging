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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

import static android.widget.Toast.makeText;


public class RegistrationActivity extends Activity {
//==========================================
    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    String regId = "";
    private static String TAG = RegistrationActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    AsyncTask<Void, Void, String> createRegIdTask;

    public static final String REG_ID = "regId";
    public static final String EMAIL_ID = "eMailId";
    Button RegisterBtn;
    EditText userName,Email;
    private ImageView txtResponse;
    DBAdapter db;
    public static final String MY_PREFS_NAME = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_view);
//        ============================
        applicationContext = getApplicationContext();
//        =============
        RegisterBtn=(Button)findViewById(R.id.butnRegister);
        userName=(EditText)findViewById(R.id.ETxtUsername);
        Email=(EditText)findViewById(R.id.ETxtEmail);
        txtResponse = (ImageView) findViewById(R.id.ResponseText);
        db= new DBAdapter(this);
//        ================
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait........");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


//        SharedPreferences prefs = getSharedPreferences("UserDetails",
//                Context.MODE_PRIVATE);
//        String registrationId = prefs.getString(REG_ID, "");
//        if (!TextUtils.isEmpty(registrationId)) {
//            Intent i = new Intent(applicationContext,MessagingActivity.class);
//            i.putExtra("regId", registrationId);
//            startActivity(i);
//            finish();
//        }
//        =================

    }
//    ====================================================================
    // When Register Me button is clicked
    public void RegisterUser(View view) {
        String emailID = Email.getText().toString();

//=======================  test paste===========================

                db.open();
                String usNme=userName.getText().toString();
                long Dbid =db.insertProfileDeatils(usNme, emailID, "", "", "");
                Toast.makeText(RegistrationActivity.this,"Db id:"+Dbid,Toast.LENGTH_LONG).show();
                db.close();
                SharedPreferences.Editor editor=getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putString("UserName", userName.getText().toString());
                editor.putString("Email", Email.getText().toString());
                editor.commit();
                new ServiceTask().execute();
//                AppUser usr=Users.get(0);
//                txtResponse.setImageBitmap(usr.photo);
//          getJSON("http://api.androidhive.info/volley/person_object.json",0);

//                Intent myIntend= new Intent(RegistrationActivity.this,ConfirmationActivity.class);
//                String message = userName.getText().toString();
//                myIntend.putExtra("EXTRA_MESSAGE", message);
//                startActivity(myIntend);
//   =======================  test paste============================

//   =======================  test comment============================
//        if (!TextUtils.isEmpty(emailID) && Utility.validate(emailID)) {
//
//            // Check if Google Play Service is installed in Device
//            // Play services is needed to handle GCM stuffs
//            if (checkPlayServices()) {
//
//                // Register Device in GCM Server
//                registerInBackground(emailID);
//            }
//        }
//        // When Email is invalid
//        else {
//            Toast.makeText(applicationContext, "Please enter valid email",
//                    Toast.LENGTH_LONG).show();
//        }
  //   =======================  test comment==========================
    }
 private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
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
        storeRegIdinServer();

    }

    // Share RegID with GCM Server Application (Php)
    private void storeRegIdinServer() {
        prgDialog.show();
        params.put("regId", regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://192.168.1.172/smartchat/public/getAllUsers", null,
                new AsyncHttpResponseHandler() {
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) {
                        // Hide Progress Dialog
                        Log.e("Response",response);
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        Toast.makeText(applicationContext,
                                "Reg Id shared successfully with Web App ",
                                Toast.LENGTH_LONG).show();
//                        Intent i = new Intent(applicationContext,
//                                UserProfileActivity.class);
//                        i.putExtra("regId", regId);
//                        startActivity(i);
                        finish();
                    }

                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        // Hide Progress Dialog
                        Log.e("Response",error.getMessage());
                        prgDialog.hide();
                        if (prgDialog != null) {
                            prgDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(applicationContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(applicationContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                                    applicationContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
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
//    =====================================================================================================
       public static List<AppUser> Users =new List<AppUser>() {
    @Override
    public void add(int location, AppUser object) {

    }

    @Override
    public boolean add(AppUser object) {
        return false;
    }

    @Override
    public boolean addAll(int location, Collection<? extends AppUser> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends AppUser> collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean contains(Object object) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public AppUser get(int location) {
        return null;
    }

    @Override
    public int indexOf(Object object) {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NonNull
    @Override
    public Iterator<AppUser> iterator() {
        return null;
    }

    @Override
    public int lastIndexOf(Object object) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator<AppUser> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<AppUser> listIterator(int location) {
        return null;
    }

    @Override
    public AppUser remove(int location) {
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public AppUser set(int location, AppUser object) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @NonNull
    @Override
    public List<AppUser> subList(int start, int end) {
        return null;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return null;
    }
};

    public class ServiceTask extends AsyncTask<String, Void, String> {
 @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
// http://stackoverflow.com/questions/20689356/android-os-networkonmainthreadexception-and-java-lang-reflect-invocationtargetex
    @Override
    protected String doInBackground(String... params) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");

        HttpURLConnection c = null;
        try {

           int timeout=30;

            URL u = new URL("http://192.168.1.172/smartchat/public/getAllUsers");
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            // c.setRequestProperty("Content-length", "0");
            c.setRequestProperty("Content-Type", "application/json");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
//            c.setConnectTimeout(timeout);
//            c.setReadTimeout(timeout);
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
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
            //ArrayList<HashMap<String, String>> medecins = new ArrayList<HashMap<String, String>>();
// ========THIS IS USED IF THE RESPONSE IS A JSON ARRAY=======================
            JSONArray js = new JSONArray(result.toString());
           for(int i=0; i<js.length(); i++){
                JSONObject jsObj = js.getJSONObject(i);
                int id = Integer.parseInt(jsObj.getString("id"));
                String username = jsObj.getString("username");
                String email = jsObj.getString("email");
                String firstname = jsObj.getString("first_name");
                String lastname = jsObj.getString("last_name");
                String mobile_no = jsObj.getString("mobile_number");
                String blob = jsObj.getString("photo");
                Bitmap bm =  decodeBase64(blob);
               txtResponse.setImageBitmap(bm);

                AppUser user= new AppUser(id,username,email,firstname,lastname,mobile_no,bm);

                Users.add(i,user);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Erreur 4");
        }
    }
}
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.e("LOOK", imageEncoded);
        return imageEncoded;
    }
}
