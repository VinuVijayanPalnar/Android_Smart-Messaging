package com.sm.arun.smartmsg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by Arun on 07-31-2015.
 */
public class SplashActivity extends Activity {
    public static final String REG_ID = "regId";
    Context applicationContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        applicationContext = getApplicationContext();
// METHOD 1



        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5000);

                    // After 5 seconds redirect to another intent
                    SharedPreferences prefs = getSharedPreferences("UserDetails",
                            Context.MODE_PRIVATE);
                    String registrationId = prefs.getString(REG_ID, "");
                    if (!TextUtils.isEmpty(registrationId)) {
                        Intent i = new Intent(applicationContext, MessagingActivity.class);
                        i.putExtra("regId", registrationId);
                        startActivity(i);
                    }else {
                        Intent i = new Intent(applicationContext, RegistrationActivity.class);
                        i.putExtra("regId", registrationId);
                        startActivity(i);
                    }

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

    }
