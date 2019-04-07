package tuni.tuukka.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import tuni.tuukka.R;

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
        int hours = Integer.parseInt(((EditText) findViewById(R.id.manualtimeinput_hours)).getText().toString());
        int minutes = Integer.parseInt(((EditText) findViewById(R.id.manualtimeinput_minutes)).getText().toString());
        String comment = ((EditText) findViewById(R.id.manualtimeinput_comment)).getText().toString();
        String category = ((EditText) findViewById(R.id.manualtimeinput_category)).getText().toString();
        System.out.println(hours);
        System.out.println(minutes);
        System.out.println(comment);
        System.out.println(category);
    }
}
