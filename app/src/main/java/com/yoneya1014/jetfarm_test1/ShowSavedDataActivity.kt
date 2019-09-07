package com.yoneya1014.jetfarm_test1

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ShowSavedDataActivity : AppCompatActivity() {

    private var docRef: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showsaveddata)
        val intent = intent
        val dataName = intent.getStringExtra("dataname")
        title = dataName
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("お待ちください")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = false
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()
        if (netWorkCheck(this)) {
            val temp = findViewById<TextView>(R.id.textView5)
            val humid = findViewById<TextView>(R.id.textView6)
            val camera = findViewById<TextView>(R.id.textView7)
            val time = findViewById<TextView>(R.id.savedtime)
            val imageView = findViewById<ImageView>(R.id.imageView2)
            docRef = FirebaseFirestore.getInstance().collection("userSavedData").document(dataName)
            docRef!!.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss秒", Locale.JAPAN)
                        temp.text = String.format("温度：%s℃", document.getDouble("temp")!!.toString())
                        humid.text = String.format("湿度：%s％", document.getDouble("humid")!!.toString())
                        time.text = String.format("保存日時：%s", sdf.format(document.getTimestamp("timestamp")!!.toDate()))
                        camera.text = String.format(Locale.JAPAN, "カメラ番号：%1.0f", document.getDouble("cameranumber"))
                        val imagearray = Base64.decode(document.getString("base64image"), Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imagearray, 0, imagearray.size)
                        imageView.setImageBitmap(bitmap)
                    }
                }
                progressDialog.dismiss()
            }
        } else {
            val builder = AlertDialog.Builder(applicationContext)
            builder.setTitle("エラー")
                    .setCancelable(false)
                    .setMessage("コネクションの確立に失敗しました")
                    .setPositiveButton("OK") { _, _ ->
                        progressDialog.dismiss()
                        finish()
                    }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.showsaveddatamenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu6 -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.isIndeterminate = false
                progressDialog.setMessage("データ削除中")
                progressDialog.setCancelable(false)
                progressDialog.show()
                docRef!!.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                SavedDataSelectMenuActivity.deleteFrag = true
                                progressDialog.dismiss()
                                finish()
                                Toast.makeText(applicationContext, "データ削除完了", Toast.LENGTH_LONG).show()
                            }
                        }
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
