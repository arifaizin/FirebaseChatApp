package id.co.imastudio.firebasefcm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "MainActivity";
    private static final String MESSAGES_CHILD = "pesanchat";
    private RecyclerView messageRecyclerView;
    private EditText editText;
    private ImageView btnSend;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mUsername, mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;
    private long mTimestamp;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO 1: Desain Awal
        initView();

        //TODO 3 : Tes Firebase Notification
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("Sender", "Arif")
                        .add("Message", editText.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("http://192.168.95.40/firebasefcm/push_notification.php")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "on" +
                                "Failure: Gagal Push Notif ", e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, "onResponse: Berhasil Push Notif"+ response);
                    }
                });
            }
        });

        //TODO 5: Cek Autentication
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //cek apakah user login? jika tidak maka login terlebih dahulu
        if (mFirebaseUser == null) {
            Toast.makeText(this, "Anda harus login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            Toast.makeText(this, "Login sebagai " + mFirebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        //TODO 6: Firebase Database - Insert
        //initialize database
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editText.getText().toString();
                mTimestamp = new Date().getTime();
                //cek apakah teks ada isinya
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivity.this, "Tidak bisa mengirim teks kosong", Toast.LENGTH_SHORT).show();
                } else {
                    ChatModel chatMessage = new ChatModel(message, mUsername, mPhotoUrl, mTimestamp);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(chatMessage, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(MainActivity.this, "Gagal Terkirim.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Terkirim.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                sendNotif(editText.getText().toString());
                editText.setText("");
            }

        });

        //TODO 7: Firebase Database - Read
        messageRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatModel, MessageViewHolder>(
                ChatModel.class,
                R.layout.item_chat_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              ChatModel model, int position) {
                if (model.getText() != null) {

                    viewHolder.messageTextView.setText(model.getText());
                    viewHolder.messengerTextView.setText(model.getName());
                    viewHolder.timestamp.setReferenceTime(model.getTimestamp());

                    String nama = model.getName();
                    if (nama != null && nama.equals(mUsername)) {
                        viewHolder.messageTextView.setGravity(Gravity.RIGHT);
                        viewHolder.layoutbackground.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.balon_message));
                        viewHolder.viewpucuk.setBackground(ContextCompat.getDrawable(MainActivity.this, android.R.color.holo_blue_light));
//                        viewHolder.layoututama.setGravity(Gravity.END);
                    } else {
                        viewHolder.messageTextView.setGravity(Gravity.LEFT);
                        viewHolder.layoutbackground.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.balon_message_lawan));
                        viewHolder.viewpucuk.setBackground(ContextCompat.getDrawable(MainActivity.this, R.color.colorAccent));
//                        viewHolder.layoututama.setGravity(Gravity.START);
                    }


                    Log.i("", "+" + model.getText());
                    if (model.getPhotoUrl() == null) {
                        viewHolder.messengerImageView
                                .setImageDrawable(ContextCompat
                                        .getDrawable(MainActivity.this,
                                                R.drawable.ic_account_round));
                    } else {
                        Glide.with(MainActivity.this)
                                .load(model.getPhotoUrl())
                                .asBitmap()
                                .centerCrop()
                                .error(R.drawable.ic_account_round)
                                .into(new BitmapImageViewTarget(viewHolder.messengerImageView) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable rounded =
                                                RoundedBitmapDrawableFactory.create(MainActivity.this.getResources(), resource);
                                        rounded.setCircular(true);
                                        viewHolder.messengerImageView.setImageDrawable(rounded);
                                    }
                                });
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Tidak ada data", Toast.LENGTH_SHORT).show();
                }
            }
        };


        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int modelCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (modelCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        messageRecyclerView.setLayoutManager(mLinearLayoutManager);
        messageRecyclerView.setAdapter(mFirebaseAdapter);

        //TODO 8 ADMOB

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("7535AF4EB2936B47ED5B7A27A0547F7F")
                .build();
        //banner
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        //interstial
        final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-8525281163605718/6290308626");
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });
        interstitialAd.loadAd(adRequest);
    }

    private void sendNotif(String pesan) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Sender", mUsername)
                .add("Message", pesan)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.95.40/firebasefcm/push_notification.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: Gagal Push Notif ", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: Berhasil Push Notif"+ response);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.cleanup();
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mFirebaseAuth != null) {
//            mFirebaseAuth.removeAuthStateListener(this);
//        }
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Koneksi gagal" + connectionResult, Toast.LENGTH_SHORT).show();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public ImageView messengerImageView;
        public RelativeTimeTextView timestamp;
        public LinearLayout layoutbackground;
        public LinearLayout layoututama;
        public View viewpucuk;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (ImageView) itemView.findViewById(R.id.messengerImageView);
            timestamp = (RelativeTimeTextView) itemView.findViewById(R.id.timestamp);
            layoutbackground = (LinearLayout) itemView.findViewById(R.id.layoutbackground);
            layoututama = (LinearLayout) itemView.findViewById(R.id.layoututama);
            viewpucuk = (View) itemView.findViewById(R.id.viewpucuk);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        messageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        editText = (EditText) findViewById(R.id.edit_text);
        btnSend = (ImageView) findViewById(R.id.btnSend);
    }
}
