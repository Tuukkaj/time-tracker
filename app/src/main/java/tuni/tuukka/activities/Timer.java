package tuni.tuukka.activities;



import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import tuni.tuukka.R;

public class Timer extends AppCompatActivity {
    long start;
    String name;
    String id;
    boolean runTimer;

    AsyncTask<Void,Void,Void> timeTask;

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
        Button startButton = (Button) findViewById(R.id.start_button);
        Button endButton = (Button) findViewById(R.id.end_button);

        if(v.getId() == R.id.start_button) {
            startButton.setEnabled(false);
            endButton.setEnabled(true);
            ((TextView) findViewById(R.id.start_text)).setText(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) +":"+Calendar.getInstance().get(Calendar.MINUTE));
            start = System.currentTimeMillis() / 1000;
            startTime();
        } else if (v.getId() == R.id.end_button){
            startButton.setEnabled(true);
            endButton.setEnabled(false);
            stopTime();
            ((TextView) findViewById(R.id.timer_time_text)).setText(DateUtils.formatElapsedTime( (System.currentTimeMillis() /1000) - start));
            Intent nextActivity = new Intent(this, Upload.class);
            nextActivity.putExtra("sheetName", name);
            nextActivity.putExtra("sheetId", id);
            float hoursWorked = ((System.currentTimeMillis() / 1000) - start)/ 3600f;
            nextActivity.putExtra("time", hoursWorked);
            startActivity(nextActivity);
        }
    }

    public void startTime() {
        TextView timeText = (TextView) findViewById(R.id.timer_time_text);
        runTimer = true;

        timeTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                new Thread(() -> {
                    while(runTimer) {
                        Timer.this.runOnUiThread(() -> {
                            timeText.setText(DateUtils.formatElapsedTime((System.currentTimeMillis() / 1000) - start));
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return null;
            }
        };

        timeTask.execute();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "time-tracker")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Started tracking work time")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        getSystemService(NotificationManager.class).notify(123, builder.build());
    }

    public void stopTime() {
        runTimer = false;
        timeTask.cancel(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("PAUSE" + start);
        String text = ((TextView) findViewById(R.id.start_text)).getText().toString();
        getSharedPreferences("time-tracker", Context.MODE_PRIVATE).edit().putLong("start",start).apply();
        getSharedPreferences("time-tracker", Context.MODE_PRIVATE).edit().putString("time-text",text).apply();

        stopTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long time = System.currentTimeMillis() / 1000;
        start = getSharedPreferences("time-tracker", Context.MODE_PRIVATE).getLong("start",time);
        String text = getSharedPreferences("time-tracker", Context.MODE_PRIVATE).getString("time-text","");

        if(time != start) {
            startTime();
            ((TextView) findViewById(R.id.start_text)).setText(text);
        }

        getSharedPreferences("time-tracker", Context.MODE_PRIVATE).edit().remove("start").apply();
        getSharedPreferences("time-tracker", Context.MODE_PRIVATE).edit().remove("time-text").apply();


        System.out.println("RESUME" + start);
    }
}
