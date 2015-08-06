package com.sm.arun.smartmsg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Locale;

import DataBaseSQlite.DBAdapter;

/**
 * Created by Arun on 07-31-2015.
 */
public class SplashActivity extends Activity {
    public static final String REG_ID = "regId";
    RelativeLayout Parent;
    Context applicationContext;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        applicationContext = getApplicationContext();

//========checking the language and determining the backgroung=====
        Parent=(RelativeLayout)findViewById(R.id.SplashLayout);
        if(Locale.getDefault().getLanguage().equals("ml"))
        Parent.setBackgroundResource(R.drawable.icon_listview);
        else
        Parent.setBackgroundResource(R.drawable.userimage);

        db= new DBAdapter(this);
// METHOD 1
 /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(100);

                    // After 5 seconds redirect to another intent
//                    db.open();
//        Cursor cursor=db.getProfileDetails("vinu");
//        if(cursor.moveToFirst())
//        {
//            do{
//                Toast.makeText(SplashActivity.this, "id:" + cursor.getString(0) + "/n" + "User Name:" + cursor.getString(1) + "/n" + "Emailid:" + cursor.getString(2) + "/n" +
//                        "First Name:" + cursor.getString(3) + "/n" + "LastName:" + cursor.getString(4) + "/n" + "Phone No" + cursor.getString(5), Toast.LENGTH_LONG).show();
//            }while (cursor.moveToNext());
//        }
//        db.close();
                    SharedPreferences prefs = getSharedPreferences("UserDetails",
                            Context.MODE_PRIVATE);
                    String registrationId = prefs.getString(REG_ID, "");
                    if (!TextUtils.isEmpty(registrationId)) {
                        Intent i = new Intent(applicationContext, MessagingActivity.class);
                        i.putExtra("regId", registrationId);
                        startActivity(i);
                        finish();
                    }else {
                        Intent i = new Intent(applicationContext, RegistrationActivity.class);
                        i.putExtra("regId", registrationId);
                        startActivity(i);
                        finish();
                    }

                } catch (Exception e) {
                    Toast.makeText(SplashActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();

                }
            }
        };

        // start thread
        background.start();
    }

    }
