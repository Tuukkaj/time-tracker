package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import tuni.tuukka.R;
import tuni.tuukka.google.DataTime;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;

public class ManualTimeInput extends AppCompatActivity {
    private String name;
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualtimeinput);
        name = getIntent().getStringExtra("sheetName");
        id = getIntent().getStringExtra("sheetId");
    }

    public void addClicked(View v) {
        String hoursText = ((EditText) findViewById(R.id.manualtimeinput_hours)).getText().toString();
        String minutesText =((EditText) findViewById(R.id.manualtimeinput_minutes)).getText().toString();
        String comment = ((EditText) findViewById(R.id.manualtimeinput_comment)).getText().toString();
        String category = ((EditText) findViewById(R.id.manualtimeinput_category)).getText().toString();

        if(hoursText.length() > 0 && minutesText.length() > 0) {
            float hours = Float.parseFloat(hoursText);
            int minutes = Integer.parseInt(minutesText);
            float decimalMinutes = minutes / 60f;
            if(decimalMinutes < 1) {
                DataTime dataTime = new DataTime(Math.round((hours+decimalMinutes) * 100f) / 100f, comment, category, new SheetRequestsInfo(id, "work"));
                SheetApi.appendSheet(dataTime, createInterface());
            }
        } else {
            Toast.makeText(this, "Enter time please", Toast.LENGTH_SHORT).show();
        }
    }

    private DoAfter<AppendValuesResponse> createInterface() {
        return new DoAfter<AppendValuesResponse>() {
            @Override
            public void onSuccess(AppendValuesResponse value) {
                ManualTimeInput.this.startActivity(new Intent(ManualTimeInput.this, Authorization.class));
                ManualTimeInput.this.runOnUiThread(()  -> {
                    Toast.makeText(ManualTimeInput.this, "Data uploaded", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail() {
                Toast.makeText(ManualTimeInput.this, "Check your connection status", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
