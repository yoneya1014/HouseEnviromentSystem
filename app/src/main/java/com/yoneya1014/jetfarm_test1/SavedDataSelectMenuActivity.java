package com.yoneya1014.jetfarm_test1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SavedDataSelectMenuActivity extends AppCompatActivity {

    public static Boolean deleteFrag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saveddataselectmenu);
        ListView listView = findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MainMenuActivity.datalist);
        listView.setAdapter(adapter);
        setTitle("登録データ一覧");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list = (ListView) parent;
                String text = (String) list.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ShowSavedDataActivity.class);
                intent.putExtra("dataname", text);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (deleteFrag) {
            deleteFrag = false;
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
