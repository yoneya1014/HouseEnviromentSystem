package com.yoneya1014.jetfarm_test1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmptyActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptylayout);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
                    String token = task.getResult().getToken();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("token", token);
                    String tokenPath = UUID.randomUUID().toString();
                    SharedPreferences preferences = getSharedPreferences("jetfarm-test1-Data", MODE_PRIVATE);
                    String savedData = preferences.getString("tokenUUID", "NoData");
                    SharedPreferences.Editor editor = preferences.edit();
                    if (savedData == "NoData") {
                        editor.putString("tokenUUID", tokenPath);
                    } else {
                        mDb.collection("userData").document(savedData)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
                        editor.remove("tokenUUID");
                        editor.putString("tokenUUID", tokenPath);
                    }
                    editor.apply();
                    mDb.collection("userData").document(tokenPath)
                            .set(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
            });
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }
}
