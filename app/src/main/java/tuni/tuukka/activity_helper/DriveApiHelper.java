package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.model.File;

import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DriveApi;

/**
 * Holds interface implementations for Authorization Activity
 */
public class DriveApiHelper {
    public static DriveApi.CheckFoldersInterface interfaceGetFiles(Activity activity, GoogleAccountCredential credential) {
        return new DriveApi.CheckFoldersInterface() {
            @Override
            public void onSuccess(String value) {
                System.out.println("FOLDER FOUND, CAN PROCEED");
                // Do something with value
                System.out.println(value);
            }

            @Override
            public void onFail() {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show());
                AccountAuthorization.authorize(activity, credential);
            }

            @Override
            public void onNoFolderFound() {
                System.out.println("NO FOLDER, CREATING NEW");
                DriveApi.createNewFolder(
                        () -> activity.runOnUiThread(() -> Toast.makeText(activity,
                                "Creating folder failed", Toast.LENGTH_SHORT).show()));
            }
        };
    }

    public static DriveApi.CreateNewSheetInterface interfaceCreateSheet(Activity activity, GoogleAccountCredential credential) {
        return new DriveApi.CreateNewSheetInterface() {
            @Override
            public void onFail() {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show());
                AccountAuthorization.authorize(activity, credential);
            }

            @Override
            public void onFileAlreadyCreated() {
                //Do something with ui here
                System.out.println("FILE ALREADY CREATED");
            }

            @Override
            public void onSuccess(File file) {
                //Do something with ui here
                System.out.println("FILE CREATED" + file);
            }
        };
    }
}
