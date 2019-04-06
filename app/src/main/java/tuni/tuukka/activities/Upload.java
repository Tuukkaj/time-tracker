package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;




import tuni.tuukka.R;

public class Upload extends AppCompatActivity {
    private String id;
    private String name;
    private float time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent extras = getIntent();
        setContentView(R.layout.activity_upload);
        name = extras.getStringExtra("sheetName");
        id = extras.getStringExtra("sheetId");
        time = extras.getExtras().getFloat("time");
        ((TextView) findViewById(R.id.upload_sheet_name)).setText(name);
        ((TextView) findViewById(R.id.upload_time_spent)).setText("Time: " + time);
    }

    public void clickUpload(View v) {

    }

    public void clickCancel(View v) {

    }

}
