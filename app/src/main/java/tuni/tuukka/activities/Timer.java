package tuni.tuukka.activities;



import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.Calendar;


import tuni.tuukka.R;

public class Timer extends AppCompatActivity {
    private final static String TAG = "Timer";
    private final static String PREF_SHEETNAME = "sheetName";
    private final static String PREF_SHEETID = "sheetId";
    private final static String PREF_START = "start";
    private final static String PREF_START_TEXT = "startText";
    public final static String EXTRA_SHEETNAME = "extraSheetName";
    public final static String EXTRA_SHEETID = "extraSheetId";

    private long start;
    private String name;
    private String id;
    private boolean runTimer;
    private boolean saveTime = true;
    private AsyncTask<Void,Void,Void> timeTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        name = getIntent().getStringExtra(EXTRA_SHEETNAME);
        id = getIntent().getStringExtra(EXTRA_SHEETID);


        if (getIntent().getBooleanExtra("serviceOn", false)) {
            ((Button) findViewById(R.id.start_button)).setEnabled(false);
            ((Button) findViewById(R.id.end_button)).setEnabled(true);
        }

        if(name != null && id != null) {
            ((TextView) findViewById(R.id.timer_sheetName)).setText(name.substring(13));
            ((TextView) findViewById(R.id.timer_sheetId)).setText(id);
        }
    }

    public void onClick(View v) {
        Button startButton = (Button) findViewById(R.id.start_button);
        Button endButton = (Button) findViewById(R.id.end_button);

        if(v.getId() == R.id.start_button) {
            startButton.setEnabled(false);
            endButton.setEnabled(true);
            ((TextView) findViewById(R.id.start_text)).setText(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) +":"+
                    (Calendar.getInstance().get(Calendar.MINUTE) < 10?"0"+Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)));
            start = System.currentTimeMillis() / 1000;
            startTime();
            createNotification();
        } else if (v.getId() == R.id.end_button){
            startButton.setEnabled(true);
            endButton.setEnabled(false);
            stopTime();

            Intent nextActivity = new Intent(this, Upload.class);
            nextActivity.putExtra("sheetName", name);
            nextActivity.putExtra("sheetId", id);
            float hoursWorked = ((System.currentTimeMillis() / 1000) - start);
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
    }

    public void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "time-tracker")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Started tracking time")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        getSystemService(NotificationManager.class).notify(123, builder.build());
    }

    public void stopTime() {
        runTimer = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

        if(saveTime) {
            String text = ((TextView) findViewById(R.id.start_text)).getText().toString();
            editor.putLong(PREF_START, start);
            editor.putString(PREF_START_TEXT, text);
            editor.putString(PREF_SHEETNAME, name);
            editor.putString(PREF_SHEETID, id);
            editor.commit();
        } else {
            editor.clear().commit();
        }

        stopTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);

        long tempTime = preferences.getLong(PREF_START,0);
        String tempId = preferences.getString(PREF_SHEETID, "");

        if(tempTime != 0 && !tempId.isEmpty()) {
            start = tempTime;
            id = tempId;
            name = preferences.getString(PREF_SHEETNAME, name);
            String text = preferences.getString(PREF_START_TEXT,"");

            startTime();

            ((TextView) findViewById(R.id.timer_sheetName)).setText(name.substring(13));
            ((TextView) findViewById(R.id.timer_sheetId)).setText(id);
            ((TextView) findViewById(R.id.start_text)).setText(text);
            ((Button) findViewById(R.id.start_button)).setEnabled(false);
            ((Button) findViewById(R.id.end_button)).setEnabled(true);
        }

        preferences.edit().clear().commit();
    }
}
