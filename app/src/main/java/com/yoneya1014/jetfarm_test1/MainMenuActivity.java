package com.yoneya1014.jetfarm_test1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainMenuActivity extends AppCompatActivity {

    public static List<String> datalist = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;
    private ImageButton imageButton6;
    private ImageButton imageButton7;
    private ImageButton imageButton8;
    private ImageButton imageButton9;
    private boolean progressFrag = false;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        setTitle("メインメニュー");
        mAuth = FirebaseAuth.getInstance();
        final Intent intent = new Intent(getApplication(), ShowInfoActivity.class);
        Button button = findViewById(R.id.button2);
        imageButton1 = findViewById(R.id.imageButton);
        imageButton2 = findViewById(R.id.imageButton2);
        imageButton3 = findViewById(R.id.imageButton3);
        imageButton4 = findViewById(R.id.imageButton4);
        imageButton5 = findViewById(R.id.imageButton5);
        imageButton6 = findViewById(R.id.imageButton6);
        imageButton7 = findViewById(R.id.imageButton7);
        imageButton8 = findViewById(R.id.imageButton8);
        imageButton9 = findViewById(R.id.imageButton9);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent savedDataView = new Intent(getApplication(), SavedDataSelectMenuActivity.class);
                startActivity(savedDataView);
            }
        });
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 1);
                startActivity(intent);
            }
        });
        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 2);
                startActivity(intent);
            }
        });
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 3);
                startActivity(intent);
            }
        });
        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 4);
                startActivity(intent);
            }
        });
        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 5);
                startActivity(intent);
            }
        });
        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 6);
                startActivity(intent);
            }
        });
        imageButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 7);
                startActivity(intent);
            }
        });
        imageButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 8);
                startActivity(intent);
            }
        });
        imageButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("camera_number", 9);
                startActivity(intent);
            }
        });
        mStorageRef = FirebaseStorage.getInstance().getReference();
        if (netWorkCheck(this)) {
            datalist.clear();
            final ProgressDialog progressDialog = new ProgressDialog(MainMenuActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("お待ちください");
            progressDialog.show();
            for (int progress_value = 1; progress_value <= 9; progress_value++) {
                StorageReference imageRef = mStorageRef.child("artboard" + progress_value + ".png");
                final long size = 1024 * 1024;
                final int finalProgress_value = progress_value;
                imageRef.getBytes(size).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        byte[] data = task.getResult();
                        assert data != null;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        switch (finalProgress_value) {
                            case 1:
                                imageButton1.setImageBitmap(bitmap);
                                break;
                            case 2:
                                imageButton2.setImageBitmap(bitmap);
                                break;
                            case 3:
                                imageButton3.setImageBitmap(bitmap);
                                break;
                            case 4:
                                imageButton4.setImageBitmap(bitmap);
                                break;
                            case 5:
                                imageButton5.setImageBitmap(bitmap);
                                break;
                            case 6:
                                imageButton6.setImageBitmap(bitmap);
                                break;
                            case 7:
                                imageButton7.setImageBitmap(bitmap);
                                break;
                            case 8:
                                imageButton8.setImageBitmap(bitmap);
                                break;
                            case 9:
                                imageButton9.setImageBitmap(bitmap);
                                progressFrag = true;
                                break;
                        }
                        if (progressFrag) {
                            progressDialog.dismiss();
                            progressFrag = false;
                        }
                    }
                });
            }
            FirebaseFirestore.getInstance().collection("userSavedData")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Date date = document.getTimestamp("timestamp").toDate();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN);
                                    datalist.add(format.format(date));
                                }
                            } else {
                                Log.d("Result", "Error");
                            }
                        }
                    });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("通信エラー")
                    .setMessage("コネクションの確立に失敗しました")
                    .setCancelable(false)
                    .setPositiveButton("終了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            moveTaskToBack(true);
                        }
                    }).create();
            builder.show();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (netWorkCheck(this)) {
            datalist.clear();
            final ProgressDialog progressDialog = new ProgressDialog(MainMenuActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("お待ちください");
            progressDialog.show();
            for (int progress_value = 1; progress_value <= 9; progress_value++) {
                StorageReference imageRef = mStorageRef.child("artboard" + progress_value + ".png");
                final long size = 1024 * 1024;
                final int finalProgress_value = progress_value;
                imageRef.getBytes(size).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        byte[] data = task.getResult();
                        assert data != null;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        switch (finalProgress_value) {
                            case 1:
                                imageButton1.setImageBitmap(bitmap);
                                break;
                            case 2:
                                imageButton2.setImageBitmap(bitmap);
                                break;
                            case 3:
                                imageButton3.setImageBitmap(bitmap);
                                break;
                            case 4:
                                imageButton4.setImageBitmap(bitmap);
                                break;
                            case 5:
                                imageButton5.setImageBitmap(bitmap);
                                break;
                            case 6:
                                imageButton6.setImageBitmap(bitmap);
                                break;
                            case 7:
                                imageButton7.setImageBitmap(bitmap);
                                break;
                            case 8:
                                imageButton8.setImageBitmap(bitmap);
                                break;
                            case 9:
                                imageButton9.setImageBitmap(bitmap);
                                progressFrag = true;
                                break;
                        }
                        if (progressFrag) {
                            progressDialog.dismiss();
                            progressFrag = false;
                        }
                    }
                });
            }
            FirebaseFirestore.getInstance().collection("userSavedData")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Date date = document.getTimestamp("timestamp").toDate();
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN);
                                    datalist.add(format.format(date));
                                }
                            } else {
                                Log.d("Result", "Error");
                            }
                        }
                    });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("通信エラー")
                    .setMessage("コネクションの確立に失敗しました")
                    .setCancelable(false)
                    .setPositiveButton("終了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            moveTaskToBack(true);
                        }
                    }).create();
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenumenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu3:
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                if (netWorkCheck(this)) {
                    datalist.clear();
                    final ProgressDialog progressDialog = new ProgressDialog(MainMenuActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("お待ちください");
                    progressDialog.show();
                    for (int progress_value = 1; progress_value <= 9; progress_value++) {
                        StorageReference imageRef = mStorageRef.child("artboard" + progress_value + ".png");
                        final long size = 1024 * 1024;
                        final int finalProgress_value = progress_value;
                        imageRef.getBytes(size).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                            @Override
                            public void onComplete(@NonNull Task<byte[]> task) {
                                byte[] data = task.getResult();
                                assert data != null;
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                switch (finalProgress_value) {
                                    case 1:
                                        imageButton1.setImageBitmap(bitmap);
                                        break;
                                    case 2:
                                        imageButton2.setImageBitmap(bitmap);
                                        break;
                                    case 3:
                                        imageButton3.setImageBitmap(bitmap);
                                        break;
                                    case 4:
                                        imageButton4.setImageBitmap(bitmap);
                                        break;
                                    case 5:
                                        imageButton5.setImageBitmap(bitmap);
                                        break;
                                    case 6:
                                        imageButton6.setImageBitmap(bitmap);
                                        break;
                                    case 7:
                                        imageButton7.setImageBitmap(bitmap);
                                        break;
                                    case 8:
                                        imageButton8.setImageBitmap(bitmap);
                                        break;
                                    case 9:
                                        imageButton9.setImageBitmap(bitmap);
                                        progressFrag = true;
                                        break;
                                }
                                if (progressFrag) {
                                    progressDialog.dismiss();
                                    progressFrag = false;
                                }
                            }
                        });
                    }
                    FirebaseFirestore.getInstance().collection("userSavedData")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Date date = document.getTimestamp("timestamp").toDate();
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN);
                                            datalist.add(format.format(date));
                                        }
                                    } else {
                                        Log.d("Result", "Error");
                                    }
                                }
                            });
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("通信エラー")
                            .setMessage("コネクションの確立に失敗しました")
                            .setCancelable(false)
                            .setPositiveButton("終了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    moveTaskToBack(true);
                                }
                            }).create();
                    builder.show();
                }
                break;
            case R.id.menu4:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("バージョン情報")
                        .setMessage("ハウス環境確認アプリ Ver 0.0.1β")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                builder.show();
                break;
            case R.id.menu5:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        SharedPreferences preferences = getSharedPreferences("jetfarm-test1-Data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String savedData = preferences.getString("tokenUUID", "NoData");
        if (savedData != "NoData") {
            mDb.collection("userData").document(savedData)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());
                                builder.setTitle("通信エラー")
                                        .setMessage("サーバーと通信できません\nアプリを終了します")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        }).create().show();
                            }
                        }
                    });
        }
        editor.remove("tokenUUID");
        editor.apply();
        mAuth.signOut();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
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
