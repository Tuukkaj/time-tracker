package tuni.tuukka.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.SheetDataAdapter;

public class SheetList extends AppCompatActivity {
    private RecyclerView recyclerView;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list);


        int mode = getIntent().getIntExtra("mode",0);

        setUpRecyclerView(createSheetInformation(), mode);
        setContentTitle(mode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sheet_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add: {
                startActivity(new Intent(this, CreateSheet.class));
                break;
            }

            case R.id.menu_home: {
                startActivity(Authorization.toParentIntent(this));
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<SheetInformation> createSheetInformation() {
        ArrayList<SheetInformation> data = new ArrayList<>();
        ArrayList<String> names = getIntent().getStringArrayListExtra("names");
        ArrayList<String> ids = getIntent().getStringArrayListExtra("ids");

        for(int i = 0; names != null && ids != null && i < names.size(); i++) {
            data.add(new SheetInformation(ids.get(i),names.get(i)));
        }

        return data;
    }

    private void setUpRecyclerView(ArrayList<SheetInformation> data, int mode) {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new SheetDataAdapter(data, this, mode));
    }

    private void setContentTitle(int mode) {
        switch (mode) {
            case SheetDataAdapter.MODE_TIMER: {
                setTitle("Timer");
                break;
            }

            case SheetDataAdapter.MODE_MANUAL_INPUT: {
                setTitle("Manual time input");

                break;
            }

            case SheetDataAdapter.MODE_SHOW_TIME: {
                setTitle("Show work time");

                break;
            }
        }
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
