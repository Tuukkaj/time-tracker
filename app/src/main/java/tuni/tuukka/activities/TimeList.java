package tuni.tuukka.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.LoadingScreenHelper;
import tuni.tuukka.activity_helper.TimeDataAdapter;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;

public class TimeList extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadingScreenHelper.start(this);

        String name = getIntent().getStringExtra(SheetList.EXTRA_SHEETNAME);
        String id = getIntent().getStringExtra(SheetList.EXTRA_SHEETID);

        getSupportActionBar().setTitle(name.substring(13));

        SheetApi.readRange(new SheetRequestsInfo(id, SheetRequestsInfo.WORK_TAB), createInterface());
    }

    private DoAfter<List<List<Object>>> createInterface() {
        return new DoAfter<List<List<Object>>>() {
            @Override
            public void onSuccess(List<List<Object>> value) {
                TimeList.this.runOnUiThread(() -> {
                    TimeList.this.setContentView(R.layout.activity_time_list);
                    recyclerView = findViewById(R.id.recycler_view);
                    recyclerView.setHasFixedSize(false);
                    recyclerView.setLayoutManager(new LinearLayoutManager(TimeList.this));
                    ArrayList<SheetInformation> infos;

                    TextView txtAll = (TextView) findViewById(R.id.time_list_txt_all);
                    TextView txtAverage = (TextView) findViewById(R.id.time_list_txt_average);

                    if(value != null && value.size() > 0) {
                        infos = toSheetInformation(value);
                        txtAll.setText(String.valueOf(calcSum(infos)) + "h");
                        txtAverage.setText(String.valueOf(calcAverage(infos)) + "h");
                        recyclerView.setAdapter(new TimeDataAdapter(infos));
                    } else {
                        TimeList.this.runOnUiThread(() -> {
                            TimeList.this.setContentView(R.layout.activity_timelist_empty);
                        });
                    }
                });
            }

            @Override
            public void onFail() {
                TimeList.this.runOnUiThread(() -> {
                    TimeList.this.setContentView(R.layout.activity_time_list);
                    Toast.makeText(TimeList.this, getString(R.string.timelist_error_fail_general), Toast.LENGTH_SHORT).show();
                });
            }
        };
    }

    public void clickBack(View v) {
        Intent intent = new Intent(this, Authorization.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private float calcAverage(List<SheetInformation> values) {
        float all = 0;
        int times = 0;

        for(SheetInformation info: values) {
            if(info.time >= 0) {
                all += info.time;
                times++;
            }
        }

        return Math.round((all / times) * 10f) / 10f;
    }

    private float calcSum(List<SheetInformation> values) {
        float all = 0;

        for(SheetInformation info: values) {
            if(info.time >= 0) {
                all += info.time;
            }
        }

        return Math.round(all * 10f) / 10f;
    }

    private ArrayList<SheetInformation> toSheetInformation(List<List<Object>> values) {
        ArrayList<SheetInformation> temp = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            try {
                temp.add(new SheetInformation(
                        Float.parseFloat(String.valueOf(values.get(i).get(0)).replace(",",".")),
                        (String) values.get(i).get(1),
                        values.get(i).size() >= 3 ?  (String) values.get(i).get(2): ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return temp;
    }

    public class SheetInformation {
        public float time;
        public String date;
        public String comment;

        public SheetInformation(float time, String date, String comment) {
            this.time = time;
            this.date = date;
            this.comment = comment;
        }
    }
}
