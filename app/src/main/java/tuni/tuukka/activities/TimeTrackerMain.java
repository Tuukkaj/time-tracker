package tuni.tuukka.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import tuni.tuukka.R;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
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
        setContentView(R.layout.activity_time_tracker_main);
        startActivity(new Intent(this, Authorization.class));
    }
}
