package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DriveFolder;

/**
 * Holds interface implementations for Authorization Activity
 */
public class HelpDriveFolder {
    public static DriveFolder.CheckFoldersInterface interfaceGetFiles(Activity activity, GoogleAccountCredential credential) {
        return new DriveFolder.CheckFoldersInterface() {
            @Override
            public void doAfter(String folderId) {
                System.out.println("FOLDER FOUND, CAN PROCEED");
                System.out.println(folderId);
            }

            @Override
            public void onFail() {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show());
                AccountAuthorization.authorize(activity, credential);
            }

            @Override
            public void onNoFolderFound() {
                System.out.println("NO FOLDER, CREATING NEW");
                DriveFolder.createNewFolder("time-tracker",
                        () -> activity.runOnUiThread(() -> Toast.makeText(activity,
                                "Creating folder failed", Toast.LENGTH_SHORT).show()));
            }
        };
    }
}
