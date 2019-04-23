package com.yoneya1014.jetfarm_test1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ShowSavedDataActivity extends AppCompatActivity {

    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showsaveddata);
        Intent intent = getIntent();
        String dataName = intent.getStringExtra("dataname");
        setTitle(dataName);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("お待ちください");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        if (netWorkCheck(this)) {
            final TextView temp = findViewById(R.id.textView5);
            final TextView humid = findViewById(R.id.textView6);
            final TextView camera = findViewById(R.id.textView7);
            final TextView time = findViewById(R.id.savedtime);
            final ImageView imageView = findViewById(R.id.imageView2);
            docRef = FirebaseFirestore.getInstance().collection("userSavedData").document(dataName);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN);
                            temp.setText(String.format("温度：%s℃", document.getDouble("temp").toString()));
                            humid.setText(String.format("湿度：%s％", document.getDouble("humid").toString()));
                            time.setText(String.format("保存日時：%s", sdf.format(document.getTimestamp("timestamp").toDate())));
                            camera.setText(String.format(Locale.JAPAN, "カメラ番号：%1.0f", document.getDouble("cameranumber")));
                            byte[] imagearray = Base64.decode(document.getString("base64image"), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imagearray, 0, imagearray.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                    progressDialog.dismiss();
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("エラー")
                    .setCancelable(false)
                    .setMessage("コネクションの確立に失敗しました")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                            finish();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showsaveddatamenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu6:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(false);
                progressDialog.setMessage("データ削除中");
                progressDialog.setCancelable(false);
                progressDialog.show();
                docRef.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    SavedDataSelectMenuActivity.deleteFrag = true;
                                    progressDialog.dismiss();
                                    finish();
                                    Toast.makeText(getApplicationContext(), "データ削除完了", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
