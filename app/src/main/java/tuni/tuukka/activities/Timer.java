package tuni.tuukka.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tuni.tuukka.R;
import tuni.tuukka.services.TimerService;

public class Timer extends AppCompatActivity {
    int seconds = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        String name = getIntent().getStringExtra("sheetName");
        String id = getIntent().getStringExtra("sheetId");

        ((TextView) findViewById(R.id.timer_sheetName)).setText(name.substring(13));
        ((TextView) findViewById(R.id.timer_sheetId)).setText(id);

        LocalBroadcastManager.getInstance(this).registerReceiver(createBroadcastReceiver(),
                new IntentFilter(TimerService.TIMER_EVENT_NAME));
    }

    private BroadcastReceiver createBroadcastReceiver() {
        TextView timeDisplay = (TextView) findViewById(R.id.timer_time);

        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                seconds++;
                DateUtils.formatElapsedTime(seconds);
                timeDisplay.setText(DateUtils.formatElapsedTime(seconds));
            }
        };
    }

    public void onClick(View v) {
        Button start = (Button) findViewById(R.id.start_button);
        Button end = (Button) findViewById(R.id.end_button);
        Intent serviceIntent = new Intent(this, TimerService.class);

        if(v.getId() == R.id.start_button) {
            start.setEnabled(false);
            end.setEnabled(true);
            startService(serviceIntent);
        } else if (v.getId() == R.id.end_button){
            start.setEnabled(true);
            end.setEnabled(false);
            stopService(serviceIntent);
        }
    }
}
