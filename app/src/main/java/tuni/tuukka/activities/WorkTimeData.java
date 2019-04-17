package tuni.tuukka.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.TimeDataRecyclerView;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;

public class WorkTimeData extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_data);

        String name = getIntent().getStringExtra("sheetName");
        String id = getIntent().getStringExtra("sheetId");

        getSupportActionBar().setTitle(name.substring(13));

        SheetApi.readRange(new SheetRequestsInfo(id, "work"), createInterface());
    }

    private DoAfter<List<List<Object>>> createInterface() {
        return new DoAfter<List<List<Object>>>() {
            @Override
            public void onSuccess(List<List<Object>> value) {
                WorkTimeData.this.runOnUiThread(() -> {
                    recyclerView = findViewById(R.id.recycler_view);
                    recyclerView.setHasFixedSize(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(WorkTimeData.this));
                    ArrayList<SheetInformation> infos = new ArrayList<>();

                    if(value != null && value.size() > 0) {
                        for (int i = 0; i < value.size(); i++) {
                            infos.add(new SheetInformation(Float.parseFloat(String.valueOf(value.get(i).get(0))),
                                    (String) value.get(i).get(1),
                                    (String) value.get(i).get(2)));
                        }

                        recyclerView.setAdapter(new TimeDataRecyclerView(infos));
                    } else {
                        infos.add(new SheetInformation(0, "No data present", "-"));
                        recyclerView.setAdapter(new TimeDataRecyclerView(infos));
                    }
                });
            }

            @Override
            public void onFail() {
                WorkTimeData.this.runOnUiThread(() -> Toast.makeText(WorkTimeData.this, "Please try again later", Toast.LENGTH_SHORT).show());
            }
        };
    }

    public class SheetInformation {
        public float time;
        public String date;
        public String comment;
        public String category;

        public SheetInformation(float time, String date, String comment) {
            this.time = time;
            this.date = date;
            this.comment = comment;
            this.category = category;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home: {
                startActivity(Authorization.toParentIntent(this));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
