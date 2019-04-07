package tuni.tuukka.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        setContentView(R.layout.activity_list_files);

        String name = getIntent().getStringExtra("sheetName");
        String id = getIntent().getStringExtra("sheetId");

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

                    for(int i = 0; i < value.size(); i++) {
                        infos.add(new SheetInformation(Float.parseFloat(String.valueOf(value.get(i).get(0))),
                                (String) value.get(i).get(1),
                                (String) value.get(i).get(2),
                                (String) value.get(i).get(3)));
                    }

                    recyclerView.setAdapter(new TimeDataRecyclerView(infos));
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

        public SheetInformation(float time, String date, String comment, String category) {
            this.time = time;
            this.date = date;
            this.comment = comment;
            this.category = category;
        }
    }
}
