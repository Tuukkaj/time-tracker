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

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Activity for showing Sheets time values.
 */
public class TimeList extends AppCompatActivity {

    /**
     * Recycler view for holding and showing Sheets time values.
     */
    private RecyclerView recyclerView;

    private final String EXTRA_START_ANIMATION = "START_ANIMATION";

    private String sheetId;
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if (extras.getBoolean(EXTRA_START_ANIMATION, true)) {
            LoadingScreenHelper.start(this);
        }

        String name = extras.getString(SheetList.EXTRA_SHEETNAME);
        sheetId = extras.getString(SheetList.EXTRA_SHEETID);

        getSupportActionBar().setTitle(name.substring(13));

        SheetApi.readRange(new SheetRequestsInfo(sheetId, SheetRequestsInfo.WORK_TAB), createInterface());
    }

    private DoAfter<Void> createDeleteTimeInterface() {
        return new DoAfter<Void>() {
            @Override
            public void onSuccess(Void value) {
                TimeList.this.runOnUiThread(() -> {
                    Intent currentIntent = TimeList.this.getIntent();
                    currentIntent.putExtra(EXTRA_START_ANIMATION, false);

                    TimeList.this.runOnUiThread(() -> {
                        Toast.makeText(
                                TimeList.this,
                                TimeList.this.getString(R.string.timelist_remove_data_success),
                                Toast.LENGTH_SHORT).show();
                    });

                    TimeList.this.finish();
                    TimeList.this.startActivity(currentIntent);
                });
            }

            @Override
            public void onFail() {
                Intent currentIntent = TimeList.this.getIntent();
                currentIntent.putExtra(EXTRA_START_ANIMATION, false);

                TimeList.this.runOnUiThread(() -> {
                    Toast.makeText(
                            TimeList.this,
                            TimeList.this.getString(R.string.timelist_remove_data_failure),
                            Toast.LENGTH_SHORT).show();
                });

                Intent i = new Intent(TimeList.this, Authorization.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        };
    }

    /**
     * Creates interface reacting to reading of files time information from Google Drive. If
     * successful creates recycler view from information. If fails creates toast informing user of
     * failure of reading.
     * @return Interface for reacting to result of reading data from Google Sheets.
     */
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
                        recyclerView.setAdapter(new TimeDataAdapter(TimeList.this, sheetId, infos, createDeleteTimeInterface()));
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

    /**
     * Starts activity Authorization and removes clears activity stack.
     * @param v Clicked view. Not in use.
     */
    public void clickBack(View v) {
        Intent intent = new Intent(this, Authorization.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Calculates average time spent working on Sheet from parameter.
     * @param values Sheets time information.
     * @return Average of time spent working on a Sheet. Rounded to one decimal.
     */
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

    /**
     * Calculates sum of time spent working on Sheet from parameter.
     * @param values Sheets time information.
     * @return Sum of time spent working on a Sheet. Rounded to one decimal.
     */
    private float calcSum(List<SheetInformation> values) {
        float all = 0;

        for(SheetInformation info: values) {
            if(info.time >= 0) {
                all += info.time;
            }
        }

        return Math.round(all * 10f) / 10f;
    }

    /**
     * Creates SheetInformation from parameter. If one data is in incorrect from it is skipped.
     * @param values Values gotten from Google Sheets Api.
     * @return SheetInformation list created from parameter.
     */
    private ArrayList<SheetInformation> toSheetInformation(List<List<Object>> values) {
        ArrayList<SheetInformation> temp = new ArrayList<>();

        for (int index = 0; index < values.size(); index++) {
            try {
                temp.add(new SheetInformation(
                        index,
                        Float.parseFloat(String.valueOf(values.get(index).get(0)).replace(",",".")),
                        (String) values.get(index).get(1),
                        values.get(index).size() >= 3 ?  (String) values.get(index).get(2): ""));
            } catch (Exception e) {
            }
        }

        return temp;
    }

    /**
     * POJO for holding Sheets information.
     */
    public class SheetInformation {
        /**
         * Time spent.
         */
        public float time;

        /**
         * Date when time was recorded.
         */
        public String date;

        /**
         * Comment of user about time.
         */
        public String comment;

        /**
         * Index of data entry in Sheets
         */
        public int index;

        /**
         * Sets parameters to variables.
         * @param time Time spent.
         * @param date Date when time was recorded.
         * @param comment Comment of user about time.
         */
        public SheetInformation(int index, float time, String date, String comment) {
            this.index = index;
            this.time = time;
            this.date = date;
            this.comment = comment;
        }
    }
}
