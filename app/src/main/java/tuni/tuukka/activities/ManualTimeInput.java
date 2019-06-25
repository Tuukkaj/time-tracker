package tuni.tuukka.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import java.util.Calendar;

import tuni.tuukka.R;
import tuni.tuukka.google.DataTime;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;
import tuni.tuukka.google.SheetRequestsInfo;


/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Activity for creating new Sheet file.
 */
public class ManualTimeInput extends AppCompatActivity {
    /**
     * Text view of time inputted by user
     */
    private TextView timeView;

    /**
     * Add button from layout.
     */
    private FloatingActionButton addButton;

    /**
     * Id of Sheet file in Google Drive.
     */
    private String id;

    /**
     * Integers for day, month and year when time is inserted to Sheet. Modified by DatePicker in UI.
     */
    private Integer day, month, year;

    /**
     * Hours worked by user. Modified by hours slider in UI.
     */
    private int hours;

    /**
     * Minutes worked by user. Modified by minutes slider in UI.
     */
    private int minutes;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualtimeinput);

        day = new Integer(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        month = new Integer(Calendar.getInstance().get(Calendar.MONTH)) + 1;
        year = new Integer(Calendar.getInstance().get(Calendar.YEAR));

        dateChanged();

        timeView = (TextView) findViewById(R.id.manualtimeinput_time);
        addButton = (FloatingActionButton) findViewById(R.id.manualtimeinput_addbutton);

        addButton.setEnabled(false);
        addButton.setBackgroundTintList(getColorStateList(R.color.shadow));

        String name = getIntent().getStringExtra(SheetList.EXTRA_SHEETNAME);
        id = getIntent().getStringExtra(SheetList.EXTRA_SHEETID);

        getSupportActionBar().setTitle(name.substring(13));
        setOnChangeListener(R.id.manualtimeinput_hour, value -> hours = value);
        setOnChangeListener(R.id.manualtimeinput_minute, value -> minutes = value);

    }

    /**
     * Changes date text view to represent Integers day, month and year.
     */
    private void dateChanged() {
        ((TextView) findViewById(R.id.manualtimeinput_current_date)).setText(dateToString());
    }

    /**
     * Creates date to use in Google Sheets and in date text view.
     * @return String format of date created from Integers day, month and year.
     */
    private String dateToString() {
        StringBuilder builder = new StringBuilder();

        return builder.append(day.intValue() < 10? "0" + day.toString(): day.toString())
                .append("-")
                .append(month.intValue() < 10? "0" + month.toString():month.toString())
                .append("-")
                .append(year.toString())
                .toString();
    }

    /**
     * Sets time text in UI.
     * @param hours Hours from hours slider.
     * @param minutes Minutes from minutes slider.
     */
    private void setTimeText(Integer hours, Integer minutes) {
        String hourText = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minuteText = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        timeView.setText(hourText+":"+minuteText);
    }

    /**
     * Sets listener to given SeekBar. Sets time text view based on time in SeekBar
     * @param id Id of  SeekBar
     * @param set Interface for setting progress to ManualTimeInputs variables.
     */
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

    /**
     * Creates new DatePickerDialog.
     * @param v Clicked view. Not in use.
     */
    public void timePickerClicked(View v) {
        new DatePickerDialog(this, createDateSetListener(),
                year.intValue(), month.intValue(), day.intValue()).show();
    }

    /**
     * Creates listener for DatePickerDialog. Sets picked values to Integers day, month and year.
     * @return listener for DatePickerDialog.
     */
    private DatePickerDialog.OnDateSetListener createDateSetListener() {
        return (datePicker, year, month, day) -> {
            ManualTimeInput.this.day = new Integer(day);
            ManualTimeInput.this.month = new Integer(month);
            ManualTimeInput.this.year = new Integer(year);

            dateChanged();
        };
    }

    /**
     * Tries to upload inserted date to Google Sheets. Sets content view to loading screen.
     * @param v Clicked view. Not in use.
     */
    public void addClicked(View v) {
        String comment = ((EditText) findViewById(R.id.manualtimeinput_comment)).getText().toString();
        float decimalMinutes = minutes / 60f;
        DataTime dataTime = new DataTime(Math.round((hours+decimalMinutes) * 100f) / 100f, comment, dateToString(), new SheetRequestsInfo(id, SheetRequestsInfo.WORK_TAB));
        SheetApi.appendSheet(dataTime, createInterface());
        setContentView(R.layout.loading_screen);
        ((ImageView) findViewById(R.id.loading)).setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
    }


    /**
     * Creates interface for uploading data to Google Sheets. If upload is success starts Authorization
     * activity and creates toast. If not recreates this activity and creates toast.
     * @return
     */
    private DoAfter<AppendValuesResponse> createInterface() {
        return new DoAfter<AppendValuesResponse>() {
            @Override
            public void onSuccess(AppendValuesResponse value) {
                ManualTimeInput.this.startActivity(new Intent(ManualTimeInput.this, Authorization.class));
                ManualTimeInput.this.runOnUiThread(()  -> {
                    Toast.makeText(ManualTimeInput.this, getString(R.string.manualtimeinput_success), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail() {
                ManualTimeInput.this.runOnUiThread(() -> {
                    Toast.makeText(ManualTimeInput.this, getString(R.string.manualtimeinput_error_general), Toast.LENGTH_SHORT).show();
                    ManualTimeInput.this.finish();
                    ManualTimeInput.this.startActivity(getIntent());
                });
            }
        };
    }

    /**
     * Interface for communicating with time sliders.
     */
    private interface SetValue {
        /**
         * Sets slider value to variable.
         * @param value Value of slider.
         */
        void setValue(int value);
    }
}
