//package id.co.imastudio.firebasefcm.fcm;
//
//import android.util.Log;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//
//import java.io.IOException;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//
///**
// * Created by idn on 9/26/2017.
// */
////TODO 2 : Firebase Notification
//public class MyFirebaseInstanceidService extends FirebaseInstanceIdService {
//    private static final String TAG = "FirebaseInstanceidServi";
//
//    @Override
//    public void onTokenRefresh() {
//        // Get updated InstanceID token.
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "Refreshed token: " + refreshedToken);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // Instance ID token to your app server.
//        sendRegistrationToServer(refreshedToken);
//    }
//
//    private void sendRegistrationToServer(String refreshedToken) {
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("Token", refreshedToken)
//                .build();
//
//        Request request = new Request.Builder()
//                .url("http://192.168.95.40/firebasefcm/register.php")
//                .post(body)
//                .build();
//
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
