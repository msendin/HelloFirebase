package demos.eps.udl.hellofirebase;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private SharedPreferences shPref;

    DatabaseReference myUser, myToken, myTokenBis;

    private FirebaseAuth mAuth;

    String uID;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class).build();
            WorkManager.getInstance(this).beginWith(work).enqueue();
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        sendNotification(remoteMessage.getNotification().getBody());
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        createDeviceOnDB(token);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //@SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                //PendingIntent.FLAG_ONE_SHOT);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        NotificationChannel channel = new NotificationChannel(channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    public void createDeviceOnDB(String tk) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uID = mAuth.getCurrentUser().getUid();
            myUser = database.getReference("TKAuthUsers/" + uID);
            myUser.setValue(tk);
        }

        myToken = database.getReference("tokensReg");
        myToken.child(tk).setValue(true);

    }

        //String key = myToken.push().getKey();

        //DatabaseReference myRef = database.getReference("token");
        //DatabaseReference myRef = database.getReference("tokensdevices");

        //DatabaseReference tokensRef = myRef.child("token1");

        //createDevice(tk, tokensRef);
        //createDevice(tk, myRef);

        //shPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //if (!shPref.getBoolean("token", false)) {



            //myToken = database.getReference("tokensdevices");
            //String key = myToken.push().getKey();
            //myToken.child(key).setValue(tk);


            //  INCORRECTE:
            //myToken = database.getReference("token");
            //String key = myToken.push().getKey();
            //myToken.setValue(tk);

            //  Aquest no funcionava pq no feia el setValue(true)
            // = database.getReference("registreT");
            //myTokenBis.child(tk);

            //SharedPreferences.Editor ed = shPref.edit();
            //ed.putBoolean("token", true);
            //ed.apply();



    //}

}
