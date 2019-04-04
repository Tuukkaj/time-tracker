package tuni.tuukka.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import tuni.tuukka.R;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Prints authors name and changes activity to Authorization.class.
 * Placeholder functionality. Will be changed in later releases.
 */
public class TimeTrackerMain extends AppCompatActivity {

    /**
     * Prints authors name and starts activity Authorization.class.
     *
     * @param savedInstanceState Saved instance. Not in use.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Author", "Author: Tuukka Juusela - Email: <tuukka.juusela@tuni.fi>");
        createNotificationChannel();
        setContentView(R.layout.activity_time_tracker_main);
        startActivity(new Intent(this, Authorization.class));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "time-tracker";
            String description = "Used to inform user about elapsed work time";
            NotificationChannel channel = new NotificationChannel("time-tracker", name, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
