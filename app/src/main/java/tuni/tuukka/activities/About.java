package tuni.tuukka.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import tuni.tuukka.R;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Activity for showing general information about the application
 */
public class About extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle(R.string.about_title);
    }
}
