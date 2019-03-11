package tuni.tuukka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TimeTrackerMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Author", "Author: Tuukka Juusela - Email: <tuukka.juusela@tuni.fi>");
        setContentView(R.layout.activity_time_tracker_main);
        startActivity(new Intent(this, AccountHelper.class));
    }
}
