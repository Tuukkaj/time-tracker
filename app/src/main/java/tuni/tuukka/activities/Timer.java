package tuni.tuukka.activities;



import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;


import java.util.Calendar;


import tuni.tuukka.R;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Activity for timing. Saves time for
 */
public class Timer extends AppCompatActivity {
    /**
     * Key of Sheet name in preferences
     */
    public final static String PREF_SHEETNAME = "sheetName";

    /**
     * Key of Sheet id in preferences.
     */
    public final static String PREF_SHEETID = "sheetId";

    /**
     * Key of timer start time in preferences.
     */
    public final static String PREF_START = "start";

    /**
     * Key of timer start time in HH:MM string in preferences.
     */
    private final static String PREF_START_TEXT = "startText";

    /**
     * Key of Sheet name extra given to Timer activity.
     */
    public final static String EXTRA_SHEETNAME = "extraSheetName";

    /**
     * Key of Sheet id extra given to Timer activity.
     */
    public final static String EXTRA_SHEETID = "extraSheetId";

    /**
     * Key of Sheet name extra given to Upload activity.
     */
    public final static String EXTRA_UPLOAD_SHEETNAME = "sheetName";

    /**
     * Key of Sheet id extra given to Upload activity.
     */
    public final static String EXTRA_UPLOAD_SHEETID = "sheetId";

    /**
     * Key of Sheet time extra given to Upload activity.
     */
    public final static String EXTRA_UPLOAD_TIME = "time";

    /**
     * Timestamp when timer was started.
     */
    private long start;

    /**
     * Name of Sheet to record time to.
     */
    private String name;

    /**
     * Id of Sheet to record time to.
     */
    private String id;

    /**
     * Boolean if time text view should be changed UI from thread.
     */
    private boolean runTimer;

    /**
     * Boolean if time data should be saved to preferences.
     */
    private boolean saveTime = false;

    /**
     * AsyncTask for changing time text view in UI.
     */
    private AsyncTask<Void,Void,Void> timeTask;

    /**
     * Sets name and id variable given in Intent.
     * @param savedInstanceState Not in use.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        name = getIntent().getStringExtra(EXTRA_SHEETNAME);
        id = getIntent().getStringExtra(EXTRA_SHEETID);

        if(name != null && id != null) {
            getSupportActionBar().setTitle(name.substring(13));
        }
    }

    /**
     * Starts running timer if saveTime is false. If not, starts Activity Upload.
     * @param v View Clicked. Not in use.
     */
    public void onClick(View v) {
        if(!saveTime) {
            saveTime = true;
            changeButtonState();
            ((TextView) findViewById(R.id.start_text)).setText(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) +":"+
                    (Calendar.getInstance().get(Calendar.MINUTE) < 10?"0"+Calendar.getInstance().get(Calendar.MINUTE):Calendar.getInstance().get(Calendar.MINUTE)));
            start = System.currentTimeMillis() / 1000;
            startTime();
            createNotification();
        } else  {
            stopTime();
            Intent nextActivity = new Intent(this, Upload.class);
            nextActivity.putExtra(EXTRA_UPLOAD_SHEETNAME, name);
            nextActivity.putExtra(EXTRA_UPLOAD_SHEETID, id);
            float hoursWorked = ((System.currentTimeMillis() / 1000) - start);
            nextActivity.putExtra(EXTRA_UPLOAD_TIME, hoursWorked);
            startActivity(nextActivity);
        }
    }

    /**
     * Starts thread that changes time text view in UI. Thread can be killed by setting runTimer
     * boolean to false.
     */
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

    /**
     * Changes Start/End button icon and txt view of the button.
     */
    private void changeButtonState() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.end_button);
        TextView textView = (TextView) findViewById(R.id.timer_txt_button);
        if(saveTime) {
            button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_close_black_24dp));
            textView.setText(getString(R.string.timer_text_end));
        } else {
            button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_timer_white_24dp));
            textView.setText(getString(R.string.timer_text_start));
        }
    }

    /**
     * Creates notification to user that timer has been started.
     */
    private void createNotification() {
        Intent resultIntent = new Intent(this, Timer.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "time-tracker")
                .setSmallIcon(R.drawable.ic_timer)
                .setContentTitle(getString(R.string.notification_content))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        getSystemService(NotificationManager.class).notify(123, builder.build());
    }

    /**
     * Sets runTimer to false and kills thread updating time text view.
     */
    public void stopTime() {
        runTimer = false;
    }

    /**
     * If saveTime is true, saves time information to preferences. If not, clears preferences.
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

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

    /**
     * If preferences contains time information, continues timer. If not, sets Timer activity to
     * default mode.
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        long tempTime = preferences.getLong(PREF_START,0);
        String tempId = preferences.getString(PREF_SHEETID, "");

        if(tempTime != 0 && !tempId.isEmpty()) {
            saveTime = true;

            start = tempTime;
            id = tempId;
            name = preferences.getString(PREF_SHEETNAME, name);
            String text = preferences.getString(PREF_START_TEXT,"");

            startTime();
            changeButtonState();

            getSupportActionBar().setTitle(name.substring(13));
            ((TextView) findViewById(R.id.start_text)).setText(text);
            ((FloatingActionButton) findViewById(R.id.end_button)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_close_black_24dp));
        } else {
            ((FloatingActionButton) findViewById(R.id.end_button)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_timer_white_24dp));
            saveTime = false;
        }

        preferences.edit().clear().commit();
    }
}
