//package id.co.imastudio.firebasefcm.fcm;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Intent;
//import androidx.core.app.NotificationCompat;
//
//import android.os.Build;
//import android.util.Log;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import id.co.imastudio.firebasefcm.MainActivity;
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "MyFirebaseMessagingServ";
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // ...
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            showNotification(remoteMessage.getData().get("sender"), remoteMessage.getData().get("message"));
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//
////            NotificationManager managet = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
////            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
////            builder.setContentTitle(remoteMessage.getFrom());
////            builder.setContentText(remoteMessage.getNotification().getBody());
////            builder.setSmallIcon(android.R.drawable.ic_dialog_email);
////            managet.notify(0,builder.build());
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//
//    private void showNotification(String sender, String message) {
//
//        Intent intentmasuk = new Intent(this, MainActivity.class);
//        intentmasuk.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        int pending;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//             pending = PendingIntent.FLAG_IMMUTABLE;
//        } else {
//            pending = 0;
//        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentmasuk, pending);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setAutoCancel(true)
//                .setContentTitle("Pesan dari " + sender)
//                .setContentText(message)
//                .setSmallIcon(android.R.drawable.ic_dialog_email)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(0, builder.build());
//    }
//}
