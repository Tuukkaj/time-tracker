package tuni.tuukka.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.LoadingScreenHelper;
import tuni.tuukka.google.DriveApi;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Activity for creating new Sheet file.
 */
public class CreateSheet extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sheet);
        getSupportActionBar().setTitle(getString(R.string.createsheet_title));
    }

    /**
     * Method for handling create button clicks. Creates new file to Google Drive if file name field
     * is longer than 1 character. If not creates Toast warning user that filename should be longer.
     * @param v Clicked view. Not in use.
     */
    public void createClicked(View v) {
        String text = ((EditText) findViewById(R.id.createsheet_sheet_name)).getText().toString();

        if(text.length() > 1) {
            DriveApi.createNewSheet(text, createInterface());
            LoadingScreenHelper.start(this);
        } else {
            Toast.makeText(this, getString(R.string.createsheet_error_no_name), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method for handling cancel button clicks. Goes back to Authorization activity if clicked.
     * @param v Clicked view. Not in use.
     */
    public void cancelClicked(View v) {
        Intent intent = new Intent(this, Authorization.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Creates interface to use in reacting to results of communicating with Google Drive. If file
     * with same name is already created creates toast about it. If operation is success goes back
     * to Authorization activity and creates toast informing user that operation was successful. If
     * operation failed creates toast with warning.
     * @return Interface for .
     */
    private DriveApi.CreateNewSheetInterface createInterface() {
        return new DriveApi.CreateNewSheetInterface() {
            @Override
            public void onFileAlreadyCreated() {
                CreateSheet.this.runOnUiThread(() -> {
                    CreateSheet.this.setContentView(R.layout.activity_create_sheet);
                    Toast.makeText(CreateSheet.this,
                            getString(R.string.createsheet_error_name_already_taken), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onSuccess(String value) {
                CreateSheet.this.runOnUiThread(() ->
                        Toast.makeText(CreateSheet.this,
                                getString(R.string.createsheet_file_created), Toast.LENGTH_SHORT).show());
                Intent intent = new Intent(CreateSheet.this, Authorization.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CreateSheet.this.startActivity(intent);
            }

            @Override
            public void onFail() {
                CreateSheet.this.runOnUiThread(() -> {
                    CreateSheet.this.setContentView(R.layout.activity_create_sheet);
                    Toast.makeText(CreateSheet.this,
                                getString(R.string.createsheet_error_general), Toast.LENGTH_SHORT).show();
                });
            }
        };
    }
}
