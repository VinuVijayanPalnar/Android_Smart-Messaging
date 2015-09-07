package com.sm.arun.smartmsg;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.lang.reflect.Array;
import java.util.HashMap;

/**
 * Created by Arun on 07-29-2015.
 */
public class GCMNotificationIntentServer  extends IntentService{
    // Sets an ID for the notification, so it can be updated
    public static int notifyID = 0;
    HashMap<String, String> hashMap;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentServer() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
//        String msg = extras.toString();
        String message=intent.getStringExtra("message");
        String timeStamp = intent.getStringExtra("timestamp");
        String AdminName = intent.getStringExtra("admin-username");
        String AdminId = intent.getStringExtra("admin-id");
         hashMap = new HashMap<String, String>();
        hashMap.put("message", message);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("AdminName", AdminName);
        hashMap.put("AdminId", AdminId);
//       msg = new String[]{
//               message,
//               timeStamp,
//               AdminName,
//               AdminId,
//
//       };
         if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
//                sendNotification("Message Received from Google GCM Server:\n\n"
//                        + extras.get(ApplicationConstants.MSG_KEY));
//                sendNotification("Message Received from Google GCM Server:\n\n"
//                        + msg);
                sendNotification("Message Received from Google GCM Server:\n\n"
                        + hashMap);
//                updateMyActivity(this,hashMap);

//                sendNotification("Message Received from Google GCM Server:\n\n"
//                        + extras.toString());
             }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String mesagesg) {
        Intent resultIntent = new Intent(getApplicationContext(), MessagingActivity.class);
        resultIntent.putExtra("msg", hashMap);
        updateMyActivity(this,hashMap);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Smart Message")
                .setContentText("You've received new message.")
                .setSmallIcon(R.mipmap.ic_launcher);
        // Set pending intent
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        // Set the content for Notification
        mNotifyBuilder.setContentText("New message from Server");
        // Set autocancel
        mNotifyBuilder.setAutoCancel(true);
        // Post a notification
//        static int notifyID = 0;

        if (notifyID > 1073741824) {
            notifyID = 0;
        }
       mNotificationManager.notify(notifyID++, mNotifyBuilder.build());
    }
    static void updateMyActivity(Context context, HashMap message) {

        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("msg", message);

        //send broadcast
        context.sendBroadcast(intent);
    }
}
