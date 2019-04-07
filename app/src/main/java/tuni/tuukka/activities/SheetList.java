package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.SheetRecyclerViewAdapter;
import tuni.tuukka.google.DriveApi;
import tuni.tuukka.google.SheetApi;

public class SheetList extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        ArrayList<SheetInformation> infos = new ArrayList<>();
        ArrayList<String> names = getIntent().getStringArrayListExtra("names");
        ArrayList<String> ids = getIntent().getStringArrayListExtra("ids");

        for(int i = 0; names != null && ids != null && i < names.size(); i++) {
            infos.add(new SheetInformation(ids.get(i),names.get(i)));
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new SheetRecyclerViewAdapter(infos, this));
    }

    public class SheetInformation {
        public String id;
        public String name;

        public SheetInformation(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public void addSheet(View v) {
        startActivity(new Intent(this, CreateSheet.class));
    }
}
