package tuni.tuukka.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.SheetDataAdapter;

public class SheetList extends AppCompatActivity {
    private RecyclerView recyclerView;

    public static final String EXTRA_SHEETNAME = "sheetName";
    public static final String EXTRA_SHEETID = "sheetId";

    public static final String EXTRA_ARRAY_SHEETNAMES = "names";
    public static final String EXTRA_ARRAY_SHEETIDS = "ids";
    public static final String EXTRA_MODE = "mode";


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mode = getIntent().getIntExtra(EXTRA_MODE,0);

        ArrayList<SheetInformation> info = createSheetInformation();

        if(info.isEmpty()) {
            setContentView(R.layout.activity_sheet_list_empty);
        } else {
            setContentView(R.layout.activity_sheet_list);

            info.add(new SheetInformation("NULL", "EXTRA ITEM FOR BUTTON"));
            setUpRecyclerView(info, mode);
        }

        setContentTitle(mode);
    }

    public void createSheet(View view) {
        startActivity(new Intent(this, CreateSheet.class));
    }

    private ArrayList<SheetInformation> createSheetInformation() {
        ArrayList<SheetInformation> data = new ArrayList<>();
        ArrayList<String> names = getIntent().getStringArrayListExtra(EXTRA_ARRAY_SHEETNAMES);
        ArrayList<String> ids = getIntent().getStringArrayListExtra(EXTRA_ARRAY_SHEETIDS);

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
                setTitle(getString(R.string.sheetlist_title_timer));
                break;
            }

            case SheetDataAdapter.MODE_MANUAL_INPUT: {
                setTitle(getString(R.string.sheetlist_title_time_input));

                break;
            }

            case SheetDataAdapter.MODE_SHOW_TIME: {
                setTitle(getString(R.string.sheetlist_title_show_time));

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
