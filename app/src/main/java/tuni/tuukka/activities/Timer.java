package tuni.tuukka.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tuni.tuukka.R;

public class Timer extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        String name = getIntent().getStringExtra("sheetName");
        String id = getIntent().getStringExtra("sheetId");

        ((TextView) findViewById(R.id.timer_sheetName)).setText(name.substring(13));
        ((TextView) findViewById(R.id.timer_sheetId)).setText(id);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.start_button) {
            Button start = (Button) findViewById(R.id.start_button);
            Button end = (Button) findViewById(R.id.end_button);

            start.setEnabled(false);
            end.setEnabled(true);
        } else if (v.getId() == R.id.end_button){
            Button end = (Button) findViewById(R.id.end_button);
        }
    }
}
