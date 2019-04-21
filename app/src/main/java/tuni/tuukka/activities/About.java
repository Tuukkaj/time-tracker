package tuni.tuukka.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tuni.tuukka.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle("About");
    }

}
