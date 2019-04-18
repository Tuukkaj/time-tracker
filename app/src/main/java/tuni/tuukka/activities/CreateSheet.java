package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.LoadingScreenHelper;
import tuni.tuukka.google.DriveApi;

public class CreateSheet extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sheet);
        getSupportActionBar().setTitle("Create sheet");
    }

    public void createClicked(View v) {
        String text = ((EditText) findViewById(R.id.createsheet_sheet_name)).getText().toString();

        if(text.length() > 1) {
            DriveApi.createNewSheet(text, createInterface());
            LoadingScreenHelper.start(this);
        } else {
            Toast.makeText(this, "Enter longer sheet name", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelClicked(View v) {
        startActivity(new Intent(this, Authorization.class));
    }

    private DriveApi.CreateNewSheetInterface createInterface() {
        return new DriveApi.CreateNewSheetInterface() {
            @Override
            public void onFileAlreadyCreated() {
                CreateSheet.this.runOnUiThread(() -> {
                    CreateSheet.this.setContentView(R.layout.activity_create_sheet);
                    Toast.makeText(CreateSheet.this,
                            "File with that name already exists", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onSuccess(String value) {
                CreateSheet.this.runOnUiThread(() ->
                        Toast.makeText(CreateSheet.this,
                                "File created", Toast.LENGTH_SHORT).show());
                CreateSheet.this.startActivity(new Intent(CreateSheet.this, Authorization.class));
            }

            @Override
            public void onFail() {
                CreateSheet.this.runOnUiThread(() -> {
                    CreateSheet.this.setContentView(R.layout.activity_create_sheet);
                    Toast.makeText(CreateSheet.this,
                                "Failed to create file. Check your connection status", Toast.LENGTH_SHORT).show();
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home: {
                startActivity(Authorization.toParentIntent(this));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
