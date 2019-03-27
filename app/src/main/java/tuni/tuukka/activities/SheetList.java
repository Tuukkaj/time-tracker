package tuni.tuukka.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.SheetRecyclerViewAdapter;

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
            infos.add(new SheetInformation(names.get(i), ids.get(i)));
        }
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new SheetRecyclerViewAdapter(infos));
    }

    public class SheetInformation {
        public String id;
        public String name;

        public SheetInformation(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
