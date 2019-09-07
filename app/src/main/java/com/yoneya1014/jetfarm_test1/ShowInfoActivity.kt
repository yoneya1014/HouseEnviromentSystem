package com.yoneya1014.jetfarm_test1

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ShowInfoActivity : AppCompatActivity() {

    private var cameraNumber = 0
    private var tempValue: Double? = 0.0
    private var humidValue: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showinfo)
        val intent = intent
        cameraNumber = intent.getIntExtra("cameraNumber", 0)
        title = "カメラ$cameraNumber"
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        if (netWorkCheck(this)) {
            val temp = findViewById<TextView>(R.id.textView3)
            val humid = findViewById<TextView>(R.id.textView4)
            val timeNow = findViewById<TextView>(R.id.time_now)
            val camera = findViewById<ImageView>(R.id.imageView)
            val docRef = FirebaseFirestore.getInstance().collection("houseEnvironment").document("camera$cameraNumber")
            val mStorageRef = FirebaseStorage.getInstance().reference.child("artboard$cameraNumber.png")
            val size = (1024 * 1024).toLong()
            mStorageRef.getBytes(size).addOnCompleteListener { task ->
                val data = task.result
                assert(data != null)
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                camera.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Toast.makeText(this, "データの取得に失敗しました", Toast.LENGTH_LONG).show()
                finish()
            }
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        tempValue = document.getDouble("temp")
                        humidValue = document.getDouble("humid")
                        temp.text = String.format("温度：%s℃", tempValue!!.toString())
                        humid.text = String.format("湿度：%s％", humidValue!!.toString())
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "データの取得に失敗しました", Toast.LENGTH_LONG).show()
                finish()
            }
            val timestamp = Timestamp(System.currentTimeMillis())
            val sdf = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN)
            timeNow.text = String.format("最終更新時刻：%s", sdf.format(timestamp))
        } else {
            Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.showinfomenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu1 -> if (netWorkCheck(this)) {
                val temp = findViewById<TextView>(R.id.textView3)
                val humid = findViewById<TextView>(R.id.textView4)
                val time_now = findViewById<TextView>(R.id.time_now)
                val camera = findViewById<ImageView>(R.id.imageView)
                val docRef = FirebaseFirestore.getInstance().collection("houseEnvironment").document("camera$cameraNumber")
                val mStorageRef = FirebaseStorage.getInstance().reference.child("artboard$cameraNumber.png")
                val size = (1024 * 1024).toLong()
                mStorageRef.getBytes(size).addOnCompleteListener { task ->
                    val data = task.result
                    assert(data != null)
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                    camera.setImageBitmap(bitmap)
                }
                docRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document!!.exists()) {
                            tempValue = document.getDouble("temp")
                            humidValue = document.getDouble("humid")
                            temp.text = String.format("温度：%s℃", tempValue!!.toString())
                            humid.text = String.format("湿度：%s％", humidValue!!.toString())
                        }
                    }
                }
                val timestamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN)
                time_now.text = String.format("最終更新時刻：%s", sdf.format(timestamp))
            } else {
                Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show()
            }
            R.id.menu2 -> if (netWorkCheck(this)) {
                val progressDialog = ProgressDialog(this@ShowInfoActivity)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.isIndeterminate = false
                progressDialog.setCancelable(false)
                progressDialog.setMessage("お待ちください")
                progressDialog.show()
                val timestamp = Timestamp(System.currentTimeMillis())
                val format = SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN)
                val imageView = findViewById<ImageView>(R.id.imageView)
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                val putData = HashMap<String, Any>()
                putData["temp"] = tempValue!!
                putData["humid"] = humidValue!!
                putData["base64image"] = encodedImage
                putData["timestamp"] = timestamp
                putData["cameranumber"] = cameraNumber
                val docRef = FirebaseFirestore.getInstance().collection("userSavedData").document(format.format(timestamp))
                docRef.set(putData)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@ShowInfoActivity, "データ登録完了", Toast.LENGTH_LONG).show()
                        }
            } else {
                Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun netWorkCheck(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info?.isConnected ?: false
    }
}
