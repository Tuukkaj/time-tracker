package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import tuni.tuukka.R;
import tuni.tuukka.google.DataTime;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;

public class ManualTimeInput extends AppCompatActivity {
    private TextView timeView;
    private FloatingActionButton addButton;

    private String id;
    private int hours;
    private int minutes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualtimeinput);

        timeView = (TextView) findViewById(R.id.manualtimeinput_time);
        addButton = (FloatingActionButton) findViewById(R.id.manualtimeinput_addbutton);

        addButton.setEnabled(false);
        addButton.setBackgroundTintList(getColorStateList(R.color.shadow));

        String name = getIntent().getStringExtra("sheetName");
        id = getIntent().getStringExtra("sheetId");

        ((TextView) findViewById(R.id.manualtimeinput_sheetName)).setText(name.substring(13));
        ((TextView) findViewById(R.id.manualtimeinput_sheetId)).setText(id);

        setOnChangeListener(R.id.manualtimeinput_hour, value -> hours = value);
        setOnChangeListener(R.id.manualtimeinput_minute, value -> minutes = value);

    }

    private void setTimeText(Integer hours, Integer minutes) {
        String hourText = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minuteText = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        timeView.setText(hourText+":"+minuteText);
    }

    public void setOnChangeListener(int id, SetValue set) {
        SeekBar bar = (SeekBar) findViewById(id);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                set.setValue(progress);
                setTimeText(hours, minutes);

                if(hours <= 0 && minutes <= 0) {
                    addButton.setEnabled(false);
                    addButton.setBackgroundTintList(getColorStateList(R.color.shadow));
                } else {
                    addButton.setEnabled(true);
                    addButton.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    public void addClicked(View v) {
        String comment = ((EditText) findViewById(R.id.manualtimeinput_comment)).getText().toString();
        float decimalMinutes = minutes / 60f;
        DataTime dataTime = new DataTime(Math.round((hours+decimalMinutes) * 100f) / 100f, comment, "REPLACE", new SheetRequestsInfo(id, "work"));
        SheetApi.appendSheet(dataTime, createInterface());
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

    private interface SetValue {
        void setValue(int value);
    }
}
