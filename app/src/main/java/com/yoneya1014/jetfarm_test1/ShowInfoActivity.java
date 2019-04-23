package com.yoneya1014.jetfarm_test1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ShowInfoActivity extends AppCompatActivity {

    private int camera_number = 0;
    private Double tempValue = 0.0;
    private Double humidValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showinfo);
        Intent intent = getIntent();
        camera_number = intent.getIntExtra("camera_number", 0);
        setTitle("カメラ" + camera_number);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (netWorkCheck(this)) {
            final TextView temp = findViewById(R.id.textView3);
            final TextView humid = findViewById(R.id.textView4);
            final TextView time_now = findViewById(R.id.time_now);
            final ImageView camera = findViewById(R.id.imageView);
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("houseEnvironment").document("camera" + camera_number);
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("artboard" + camera_number + ".png");
            final long size = 1024 * 1024;
            mStorageRef.getBytes(size).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    byte[] data = task.getResult();
                    assert data != null;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    camera.setImageBitmap(bitmap);
                }
            });
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            tempValue = document.getDouble("temp");
                            humidValue = document.getDouble("humid");
                            temp.setText(String.format("温度：%s℃", tempValue.toString()));
                            humid.setText(String.format("湿度：%s％", humidValue.toString()));
                        }
                    }
                }
            });
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN);
            time_now.setText(String.format("最終更新時刻：%s", sdf.format(timestamp)));
        } else {
            Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showinfomenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu1:
                if (netWorkCheck(this)) {
                    final TextView temp = findViewById(R.id.textView3);
                    final TextView humid = findViewById(R.id.textView4);
                    final TextView time_now = findViewById(R.id.time_now);
                    final ImageView camera = findViewById(R.id.imageView);
                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("houseEnvironment").document("camera" + camera_number);
                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("artboard" + camera_number + ".png");
                    final long size = 1024 * 1024;
                    mStorageRef.getBytes(size).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            byte[] data = task.getResult();
                            assert data != null;
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            camera.setImageBitmap(bitmap);
                        }
                    });
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    tempValue = document.getDouble("temp");
                                    humidValue = document.getDouble("humid");
                                    temp.setText(String.format("温度：%s℃", tempValue.toString()));
                                    humid.setText(String.format("湿度：%s％", humidValue.toString()));
                                }
                            }
                        }
                    });
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN);
                    time_now.setText(String.format("最終更新時刻：%s", sdf.format(timestamp)));
                } else {
                    Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.menu2:
                if (netWorkCheck(this)) {
                    final ProgressDialog progressDialog = new ProgressDialog(ShowInfoActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("お待ちください");
                    progressDialog.show();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN);
                    ImageView imageView = findViewById(R.id.imageView);
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                    Map<String, Object> putData = new HashMap<>();
                    putData.put("temp", tempValue);
                    putData.put("humid", humidValue);
                    putData.put("base64image", encodedImage);
                    putData.put("timestamp", timestamp);
                    putData.put("cameranumber", camera_number);
                    DocumentReference docRef = FirebaseFirestore.getInstance().collection("userSavedData").document(format.format(timestamp));
                    docRef.set(putData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ShowInfoActivity.this, "データ登録完了", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean netWorkCheck(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        } else {
            return false;
        }
    }
}
