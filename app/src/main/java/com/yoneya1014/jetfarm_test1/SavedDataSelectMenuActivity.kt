package com.yoneya1014.jetfarm_test1

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class SavedDataSelectMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.saveddataselectmenu)
        val listView = findViewById<ListView>(R.id.listView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MainMenuActivity.dataList)
        listView.adapter = adapter
        title = "登録データ一覧"
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val list = parent as ListView
            val text = list.getItemAtPosition(position) as String
            val intent = Intent(applicationContext, ShowSavedDataActivity::class.java)
            intent.putExtra("dataName", text)
            startActivity(intent)
        }
    }

    public override fun onResume() {
        super.onResume()
        if (deleteFrag!!) {
            deleteFrag = false
            finish()
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

    companion object {

        var deleteFrag: Boolean? = false
    }
}
