package com.yoneya1014.jetfarm_test1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private EditText mEmail;
    private EditText mPass;
    private DialogFragment progress;

    public SignInActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        setTitle("サインイン");
        mAuth = FirebaseAuth.getInstance();
        Button button = findViewById(R.id.button);
        mEmail = findViewById(R.id.editText1);
        mPass = findViewById(R.id.editText2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmail.getText().toString(), mPass.getText().toString());
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("必須項目です");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        String pass = mPass.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            mPass.setError("必須項目です");
            valid = false;
        } else {
            mPass.setError(null);
        }
        return valid;
    }

    private void signIn(String email, String pass) {
        if (!validateForm()) {
            return;
        }
        progress = new SignInProgressFragment();
        progress.show(getSupportFragmentManager(), null);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mDb = FirebaseFirestore.getInstance();
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
                    Intent intent = new Intent(SignInActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignInActivity.this, "サインインできません\n入力内容をお確かめ下さい", Toast.LENGTH_LONG).show();
                }
                if (!task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "サインインできません\n入力内容をお確かめ下さい", Toast.LENGTH_LONG).show();
                }
                progress.dismiss();
            }
        });
    }
}
