package com.agritech_nithc.client_app

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ShowInfoActivity : AppCompatActivity(), Runnable {

    private var id = 0
    private var temperatureValue: Double? = 0.0
    private var humidityValue: Double? = 0.0
    private var soilHumidityValue: Double? = 0.0
    private var illuminateValue: Double? = 0.0
    private var barometricValue: Double? = 0.0
    private var timeStampValue: Date? = Date()
    private val handler = Handler()
    private var mStorageRef: StorageReference? = null
    private var docRef: DocumentReference? = null
    private var context: Context? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showinfo)
        val progressDialog = ProgressDialog(this)
        context = applicationContext
        progressDialog.setMessage("お待ちください")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = false
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()
        val intent = intent
        id = intent.getIntExtra("id", 0)
        title = "カメラ$id"
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        if (netWorkCheck(this)) {
            val temperature = findViewById<TextView>(R.id.show_temperature)
            val humidity = findViewById<TextView>(R.id.show_humidity)
            /*val soilHumidity = findViewById<TextView>(R.id.show_soil_humidity)
            val illuminate = findViewById<TextView>(R.id.show_illuminate)
            val barometricPressure = findViewById<TextView>(R.id.show_barometric)*/
            val timeStamp = findViewById<TextView>(R.id.show_timestamp)
            val camera = findViewById<ImageView>(R.id.imageView)
            docRef = FirebaseFirestore.getInstance().collection("houseEnvironment").document("data$id")
            mStorageRef = FirebaseStorage.getInstance().reference.child("artboard$id.png")
            val size = (256 * 256).toLong()
            mStorageRef!!.getBytes(size).addOnCompleteListener { task ->
                val data = task.result
                assert(data != null)
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                camera.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Toast.makeText(this, "データの取得に失敗しました", Toast.LENGTH_LONG).show()
                finish()
            }
            docRef!!.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN)
                        temperatureValue = document.getDouble("temperature")
                        humidityValue = document.getDouble("humidity")
                        /* soilHumidityValue = document.getDouble("soilHumidity")
                         illuminateValue = document.getDouble("illuminate")
                         barometricValue = document.getDouble("barometric")*/
                        timeStampValue = document.getDate("timestamp")
                        temperature.text = String.format("気温：%s℃", temperatureValue!!.toString())
                        humidity.text = String.format("湿度：%s％", humidityValue!!.toString())
                        timeStamp.text = String.format("最終更新時刻：%s", sdf.format(timeStampValue))
                        /*soilHumidity.text = String.format("土壌湿度：%s％", soilHumidityValue!!.toString())
                        illuminate.text = String.format("照度：%s％", illuminateValue!!.toString())
                        barometricPressure.text = String.format("気圧：%shPa", barometricValue!!.toString())*/
                    }
                    progressDialog.dismiss()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "データの取得に失敗しました", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            Toast.makeText(this, "コネクションの確立に失敗しました", Toast.LENGTH_LONG).show()
        }
        handler.post(this)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(this)
    }

    override fun run() {
        val temperature = findViewById<TextView>(R.id.show_temperature)
        val humidity = findViewById<TextView>(R.id.show_humidity)
        val timeStamp = findViewById<TextView>(R.id.show_timestamp)
        val camera = findViewById<ImageView>(R.id.imageView)
        val docRef = FirebaseFirestore.getInstance()
                .collection("houseEnvironment")
                .document("data$id")
        val mStorageRef = FirebaseStorage.getInstance()
                .reference
                .child("artboard$id.png")
        val size = (64 * 64).toLong()
        mStorageRef.getBytes(size).addOnCompleteListener { task ->
            val data = task.result
            assert(data != null)
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
            camera.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context, "データの取得に失敗しました", Toast.LENGTH_LONG).show()
            finish()
        }
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    val sdf = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN)
                    temperatureValue = document.getDouble("temperature")
                    humidityValue = document.getDouble("humidity")
                    /* soilHumidityValue = document.getDouble("soilHumidity")
                     illuminateValue = document.getDouble("illuminate")
                     barometricValue = document.getDouble("barometric")*/
                    timeStampValue = document.getDate("timestamp")
                    temperature.text = String.format("気温：%s℃", temperatureValue!!.toString())
                    humidity.text = String.format("湿度：%s％", humidityValue!!.toString())
                    timeStamp.text = String.format("最終更新時刻：%s", sdf.format(timeStampValue))
                    /*soilHumidity.text = String.format("土壌湿度：%s％", soilHumidityValue!!.toString())
                    illuminate.text = String.format("照度：%s％", illuminateValue!!.toString())
                    barometricPressure.text = String.format("気圧：%shPa", barometricValue!!.toString())*/
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "データの取得に失敗しました", Toast.LENGTH_LONG).show()
            finish()
        }
        handler.postDelayed(this, 5000)
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
                putData["temperature"] = temperatureValue!!
                putData["humidity"] = humidityValue!!
                putData["soilHumidity"] = soilHumidityValue!!
                putData["illuminate"] = illuminateValue!!
                putData["barometric"] = barometricValue!!
                putData["base64image"] = encodedImage
                putData["timestamp"] = timestamp
                putData["id"] = id
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
