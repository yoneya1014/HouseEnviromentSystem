package com.yoneya1014.jetfarm_test1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

class SignInActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDb: FirebaseFirestore? = null
    private var mEmail: EditText? = null
    private var mPass: EditText? = null
    private var progress: DialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin)
        title = "サインイン"
        mAuth = FirebaseAuth.getInstance()
        val button = findViewById<Button>(R.id.button)
        mEmail = findViewById(R.id.editText1)
        mPass = findViewById(R.id.editText2)
        button.setOnClickListener { signIn(mEmail!!.text.toString(), mPass!!.text.toString()) }
    }

    private fun validateForm(): Boolean {
        var valid = true
        val email = mEmail!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            mEmail!!.error = "必須項目です"
            valid = false
        } else {
            mEmail!!.error = null
        }
        val pass = mPass!!.text.toString()
        if (TextUtils.isEmpty(pass)) {
            mPass!!.error = "必須項目です"
            valid = false
        } else {
            mPass!!.error = null
        }
        return valid
    }

    private fun signIn(email: String, pass: String) {
        if (!validateForm()) {
            return
        }
        progress = SignInProgressFragment()
        progress!!.show(supportFragmentManager, null)
        mAuth!!.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                mDb = FirebaseFirestore.getInstance()
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val builder = AlertDialog.Builder(applicationContext)
                        builder.setTitle("通信エラー")
                                .setMessage("サーバーと通信できません\nアプリを終了します")
                                .setCancelable(false)
                                .setPositiveButton("OK") { _, _ -> finish() }.create().show()
                    }
                    val token = task.result!!.token
                    val userData = HashMap<String, Any>()
                    userData["token"] = token
                    val tokenPath = UUID.randomUUID().toString()
                    val preferences = getSharedPreferences("jetfarm-test1-Data", Context.MODE_PRIVATE)
                    val savedData = preferences.getString("tokenUUID", "NoData")
                    val editor = preferences.edit()
                    if (savedData === "NoData") {
                        editor.putString("tokenUUID", tokenPath)
                    } else {
                        mDb!!.collection("userData").document(savedData!!)
                                .delete()
                                .addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        val builder = AlertDialog.Builder(applicationContext)
                                        builder.setTitle("通信エラー")
                                                .setMessage("サーバーと通信できません\nアプリを終了します")
                                                .setCancelable(false)
                                                .setPositiveButton("OK") { _, _ -> finish() }.create().show()
                                    }
                                }
                        editor.remove("tokenUUID")
                        editor.putString("tokenUUID", tokenPath)
                    }
                    editor.apply()
                    mDb!!.collection("userData").document(tokenPath)
                            .set(userData)
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    val builder = AlertDialog.Builder(applicationContext)
                                    builder.setTitle("通信エラー")
                                            .setMessage("サーバーと通信できません\nアプリを終了します")
                                            .setCancelable(false)
                                            .setPositiveButton("OK") { _, _ -> finish() }.create().show()
                                }
                            }
                }
                val intent = Intent(this@SignInActivity, MainMenuActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@SignInActivity, "サインインできません\n入力内容をお確かめ下さい", Toast.LENGTH_LONG).show()
            }
            if (!task.isSuccessful) {
                Toast.makeText(this@SignInActivity, "サインインできません\n入力内容をお確かめ下さい", Toast.LENGTH_LONG).show()
            }
            progress!!.dismiss()
        }
    }
}
