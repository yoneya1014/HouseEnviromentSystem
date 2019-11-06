package com.agritech_nithc.client_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*

@Suppress("NAME_SHADOWING")
class EmptyActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDb: FirebaseFirestore? = null

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emptylayout)
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()
    }

    public override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        updateUI(user)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
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
                val preferences = getSharedPreferences("Agritech-NITHC-Data", Context.MODE_PRIVATE)
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
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}
