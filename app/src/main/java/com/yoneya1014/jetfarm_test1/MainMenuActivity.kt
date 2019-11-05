@file:Suppress("DEPRECATION")

package com.yoneya1014.jetfarm_test1

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class MainMenuActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var imageButton1: ImageButton? = null
    private var imageButton2: ImageButton? = null
    private var imageButton3: ImageButton? = null
    private var imageButton4: ImageButton? = null
    private var imageButton5: ImageButton? = null
    private var imageButton6: ImageButton? = null
    private var imageButton7: ImageButton? = null
    private var imageButton8: ImageButton? = null
    private var imageButton9: ImageButton? = null
    private var progressFrag = false
    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainmenu)
        title = "メインメニュー"
        mAuth = FirebaseAuth.getInstance()
        val intent = Intent(application, ShowInfoActivity::class.java)
        val button = findViewById<Button>(R.id.button2)
        imageButton1 = findViewById(R.id.imageButton)
        imageButton2 = findViewById(R.id.imageButton2)
        imageButton3 = findViewById(R.id.imageButton3)
        imageButton4 = findViewById(R.id.imageButton4)
        imageButton5 = findViewById(R.id.imageButton5)
        imageButton6 = findViewById(R.id.imageButton6)
        imageButton7 = findViewById(R.id.imageButton7)
        imageButton8 = findViewById(R.id.imageButton8)
        imageButton9 = findViewById(R.id.imageButton9)
        button.setOnClickListener {
            val savedDataView = Intent(application, SavedDataSelectMenuActivity::class.java)
            startActivity(savedDataView)
        }
        imageButton1!!.setOnClickListener {
            intent.putExtra("id", 1)
            startActivity(intent)
        }
        imageButton2!!.setOnClickListener {
            intent.putExtra("id", 2)
            startActivity(intent)
        }
        imageButton3!!.setOnClickListener {
            intent.putExtra("id", 3)
            startActivity(intent)
        }
        imageButton4!!.setOnClickListener {
            intent.putExtra("id", 4)
            startActivity(intent)
        }
        imageButton5!!.setOnClickListener {
            intent.putExtra("id", 5)
            startActivity(intent)
        }
        imageButton6!!.setOnClickListener {
            intent.putExtra("id", 6)
            startActivity(intent)
        }
        imageButton7!!.setOnClickListener {
            intent.putExtra("id", 7)
            startActivity(intent)
        }
        imageButton8!!.setOnClickListener {
            intent.putExtra("id", 8)
            startActivity(intent)
        }
        imageButton9!!.setOnClickListener {
            intent.putExtra("id", 9)
            startActivity(intent)
        }
        mStorageRef = FirebaseStorage.getInstance().reference
        if (netWorkCheck(this)) {
            dataList.clear()
            val progressDialog = ProgressDialog(this@MainMenuActivity)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.isIndeterminate = false
            progressDialog.setCancelable(false)
            progressDialog.setMessage("お待ちください")
            progressDialog.show()
            for (progress_value in 1..9) {
                val imageRef = mStorageRef!!.child("artboard$progress_value.png")
                val size = (256 * 256).toLong()
                imageRef.getBytes(size).addOnCompleteListener { task ->
                    val data = task.result
                    assert(data != null)
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                    when (progress_value) {
                        1 -> imageButton1!!.setImageBitmap(bitmap)
                        2 -> imageButton2!!.setImageBitmap(bitmap)
                        3 -> imageButton3!!.setImageBitmap(bitmap)
                        4 -> imageButton4!!.setImageBitmap(bitmap)
                        5 -> imageButton5!!.setImageBitmap(bitmap)
                        6 -> imageButton6!!.setImageBitmap(bitmap)
                        7 -> imageButton7!!.setImageBitmap(bitmap)
                        8 -> imageButton8!!.setImageBitmap(bitmap)
                        9 -> {
                            imageButton9!!.setImageBitmap(bitmap)
                            progressFrag = true
                        }
                    }
                    if (progressFrag) {
                        progressDialog.dismiss()
                        progressFrag = false
                    }
                }
            }
            FirebaseFirestore.getInstance().collection("userSavedData")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val date = document.getTimestamp("timestamp")!!.toDate()
                                val format = SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN)
                                dataList.add(format.format(date))
                            }
                        } else {
                            Log.d("Result", "Error")
                        }
                    }
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("通信エラー")
                    .setMessage("コネクションの確立に失敗しました")
                    .setCancelable(false)
                    .setPositiveButton("終了") { _, _ ->
                        finish()
                        moveTaskToBack(true)
                    }.create()
            builder.show()
        }
    }

    public override fun onRestart() {
        super.onRestart()
        if (netWorkCheck(this)) {
            dataList.clear()
            val progressDialog = ProgressDialog(this@MainMenuActivity)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.isIndeterminate = false
            progressDialog.setCancelable(false)
            progressDialog.setMessage("お待ちください")
            progressDialog.show()
            for (progress_value in 1..9) {
                val imageRef = mStorageRef!!.child("artboard$progress_value.png")
                val size = (256 * 256).toLong()
                imageRef.getBytes(size).addOnCompleteListener { task ->
                    val data = task.result
                    assert(data != null)
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                    when (progress_value) {
                        1 -> imageButton1!!.setImageBitmap(bitmap)
                        2 -> imageButton2!!.setImageBitmap(bitmap)
                        3 -> imageButton3!!.setImageBitmap(bitmap)
                        4 -> imageButton4!!.setImageBitmap(bitmap)
                        5 -> imageButton5!!.setImageBitmap(bitmap)
                        6 -> imageButton6!!.setImageBitmap(bitmap)
                        7 -> imageButton7!!.setImageBitmap(bitmap)
                        8 -> imageButton8!!.setImageBitmap(bitmap)
                        9 -> {
                            imageButton9!!.setImageBitmap(bitmap)
                            progressFrag = true
                        }
                    }
                    if (progressFrag) {
                        progressDialog.dismiss()
                        progressFrag = false
                    }
                }
            }
            FirebaseFirestore.getInstance().collection("userSavedData")
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                val date = document.getTimestamp("timestamp")!!.toDate()
                                val format = SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN)
                                dataList.add(format.format(date))
                            }
                        } else {
                            Log.d("Result", "Error")
                        }
                    }
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("通信エラー")
                    .setMessage("コネクションの確立に失敗しました")
                    .setCancelable(false)
                    .setPositiveButton("終了") { _, _ ->
                        finish()
                        moveTaskToBack(true)
                    }.create()
            builder.show()
        }
    }

    override fun onBackPressed() {
        finish()
        moveTaskToBack(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mainmenumenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu3 -> {
                val mStorageRef = FirebaseStorage.getInstance().reference
                if (netWorkCheck(this)) {
                    dataList.clear()
                    val progressDialog = ProgressDialog(this@MainMenuActivity)
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    progressDialog.isIndeterminate = false
                    progressDialog.setCancelable(false)
                    progressDialog.setMessage("お待ちください")
                    progressDialog.show()
                    for (progress_value in 1..9) {
                        val imageRef = mStorageRef.child("artboard$progress_value.png")
                        val size = (256 * 256).toLong()
                        imageRef.getBytes(size).addOnCompleteListener { task ->
                            val data = task.result
                            assert(data != null)
                            val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                            when (progress_value) {
                                1 -> imageButton1!!.setImageBitmap(bitmap)
                                2 -> imageButton2!!.setImageBitmap(bitmap)
                                3 -> imageButton3!!.setImageBitmap(bitmap)
                                4 -> imageButton4!!.setImageBitmap(bitmap)
                                5 -> imageButton5!!.setImageBitmap(bitmap)
                                6 -> imageButton6!!.setImageBitmap(bitmap)
                                7 -> imageButton7!!.setImageBitmap(bitmap)
                                8 -> imageButton8!!.setImageBitmap(bitmap)
                                9 -> {
                                    imageButton9!!.setImageBitmap(bitmap)
                                    progressFrag = true
                                }
                            }
                            if (progressFrag) {
                                progressDialog.dismiss()
                                progressFrag = false
                            }
                        }
                    }
                    FirebaseFirestore.getInstance().collection("userSavedData")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    for (document in task.result!!) {
                                        val date = document.getTimestamp("timestamp")!!.toDate()
                                        val format = SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN)
                                        dataList.add(format.format(date))
                                    }
                                } else {
                                    Log.d("Result", "Error")
                                }
                            }
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("通信エラー")
                            .setMessage("コネクションの確立に失敗しました")
                            .setCancelable(false)
                            .setPositiveButton("終了") { _, _ ->
                                finish()
                                moveTaskToBack(true)
                            }.create()
                    builder.show()
                }
            }
            R.id.menu4 -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("バージョン情報")
                        .setMessage("ハウス環境確認アプリ Ver 1.0.0")
                        .setCancelable(false)
                        .setPositiveButton("OK") { _, _ -> }.create()
                builder.show()
            }
            R.id.menu5 -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOut() {
        val mDb = FirebaseFirestore.getInstance()
        val preferences = getSharedPreferences("jetfarm-test1-Data", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val savedData = preferences.getString("tokenUUID", "NoData")
        if (savedData !== "NoData") {
            mDb.collection("userData").document(savedData!!)
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
        }
        editor.remove("tokenUUID")
        editor.apply()
        mAuth!!.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun netWorkCheck(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info?.isConnected ?: false
    }

    companion object {

        var dataList: MutableList<String> = ArrayList()
    }
}
