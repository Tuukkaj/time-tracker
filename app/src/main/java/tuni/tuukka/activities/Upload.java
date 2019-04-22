package tuni.tuukka.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.api.services.sheets.v4.model.AppendValuesResponse;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.LoadingScreenHelper;
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
        name = extras.getStringExtra(Timer.EXTRA_UPLOAD_SHEETNAME);
        id = extras.getStringExtra(Timer.EXTRA_UPLOAD_SHEETID);
        time = extras.getExtras().getFloat(Timer.EXTRA_UPLOAD_TIME);
        time = Math.round((time / 3600) * 100f) / 100f;
        ((TextView) findViewById(R.id.upload_sheet_name)).setText(name.substring(13));
        ((TextView) findViewById(R.id.upload_time_spent)).setText("Time: " + time + "h");
    }

    public void clickUpload(View v) {
        String comment = ((EditText) findViewById(R.id.upload_comment_field)).getText().toString();
        DataTime data = new DataTime(time, comment, new SheetRequestsInfo(id, SheetRequestsInfo.WORK_TAB));
        LoadingScreenHelper.start(this);
        SheetApi.appendSheet(data, createDoAfter());
    }

    public DoAfter<AppendValuesResponse> createDoAfter() {
        return new DoAfter<AppendValuesResponse>() {
            @Override
            public void onSuccess(AppendValuesResponse value) {
                Upload.this.startActivity(new Intent(Upload.this, Authorization.class));
                PreferenceManager.getDefaultSharedPreferences(Upload.this).edit().clear().commit();
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
                Upload.this.runOnUiThread(() -> Toast.makeText(Upload.this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFail() {
                Upload.this.runOnUiThread(() -> {
                    Intent intent = Upload.this.getIntent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    Upload.this.runOnUiThread(() -> Upload.this.startActivity(intent));
                    Upload.this.finish();

                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
                    Toast.makeText(Upload.this, getString(R.string.upload_error_general), Toast.LENGTH_SHORT).show();
                });
            }
        };
    }

    public void clickCancel(View v) {
        finish();
    }

    public void clickDelete(View v) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
        Intent i = new Intent(this, Authorization.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
