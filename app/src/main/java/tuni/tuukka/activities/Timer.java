package tuni.tuukka.activities;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.Calendar;

import tuni.tuukka.R;
import tuni.tuukka.services.TimerService;

public class Timer extends AppCompatActivity {
    int seconds = 0;
    String name;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        name = getIntent().getStringExtra("sheetName");
        id = getIntent().getStringExtra("sheetId");


        if (getIntent().getBooleanExtra("serviceOn", false)) {
            ((Button) findViewById(R.id.start_button)).setEnabled(false);
            ((Button) findViewById(R.id.end_button)).setEnabled(true);
        }

        ((TextView) findViewById(R.id.timer_sheetName)).setText(name.substring(13));
        ((TextView) findViewById(R.id.timer_sheetId)).setText(id);

    }

    public void onClick(View v) {
        Button start = (Button) findViewById(R.id.start_button);
        Button end = (Button) findViewById(R.id.end_button);
        Intent serviceIntent = new Intent(this, TimerService.class);

        if(v.getId() == R.id.start_button) {
            start.setEnabled(false);
            end.setEnabled(true);
            serviceIntent.putExtra(TimerService.TIMER_START_PARAM, seconds);
            serviceIntent.putExtra("sheetName", name);
            serviceIntent.putExtra("sheetId", id);

            startService(serviceIntent);
        } else if (v.getId() == R.id.end_button){
            start.setEnabled(true);
            end.setEnabled(false);
            stopService(serviceIntent);
        }
    }
}
