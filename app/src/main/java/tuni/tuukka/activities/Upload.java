package tuni.tuukka.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;




import tuni.tuukka.R;

public class Upload extends AppCompatActivity {
    private String id;
    private String name;
    private long time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent extras = getIntent();
        setContentView(R.layout.activity_upload);
        name = extras.getStringExtra("sheetName");
        id = extras.getStringExtra("sheetId");
        time = extras.getExtras().getLong("time");
        ((TextView) findViewById(R.id.upload_sheet_name)).setText(name);
        ((TextView) findViewById(R.id.upload_time_spent)).setText("Time: " + time);

    }

}
