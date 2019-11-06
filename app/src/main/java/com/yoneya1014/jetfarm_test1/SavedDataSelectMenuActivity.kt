package com.yoneya1014.jetfarm_test1

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SavedDataSelectMenuActivity : AppCompatActivity() {

    private var dataList: MutableList<String> = ArrayList()
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        dataList.clear()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saveddataselectmenu)
        listView = findViewById(R.id.listView)
    }

    public override fun onResume() {
        super.onResume()
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("リストを更新中です")
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = false
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.show()
        dataList.clear()
        FirebaseFirestore.getInstance().collection("userSavedData")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val date = document.getTimestamp("timestamp")!!.toDate()
                            val format = SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.JAPAN)
                            dataList.add(format.format(date))
                        }
                        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dataList)
                        listView!!.adapter = adapter
                        title = "登録データ一覧"
                        val actionBar = supportActionBar
                        actionBar!!.setDisplayHomeAsUpEnabled(true)
                        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                            val list = parent as ListView
                            val text = list.getItemAtPosition(position) as String
                            val intent = Intent(applicationContext, ShowSavedDataActivity::class.java)
                            intent.putExtra("dataName", text)
                            startActivity(intent)
                        }
                        progressDialog.dismiss()
                    } else {
                        Log.d("Result", "Error")
                    }
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
