package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import tuni.tuukka.R;
import tuni.tuukka.google.DataTime;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;

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
        String comment = ((EditText) findViewById(R.id.upload_comment_field)).getText().toString();
        DataTime data = new DataTime(time, comment, "implemented later", new SheetRequestsInfo(id, "work"));
        SheetApi.appendSheet(data, createDoAfter());
    }

    public DoAfter<AppendValuesResponse> createDoAfter() {
        return new DoAfter<AppendValuesResponse>() {
            @Override
            public void onSuccess(AppendValuesResponse value) {
                Upload.this.startActivity(new Intent(Upload.this, Authorization.class));
                Upload.this.runOnUiThread(() -> Toast.makeText(Upload.this, "Upload successful", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFail() {
                Upload.this.runOnUiThread(() -> Toast.makeText(Upload.this, "Upload failed", Toast.LENGTH_SHORT).show());
            }
        };
    }

    public void clickCancel(View v) {

    }

}