package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DriveFolder;

public class AuthorizationDriveFolder {
    public static DriveFolder.FolderCheckReady interfaceGetFiles(Activity activity, GoogleAccountCredential credential) {
        return new DriveFolder.FolderCheckReady() {
            @Override
            public void doAfter(boolean result, String folderId) {
                System.out.println(result);
                System.out.println(folderId);
            }

            @Override
            public void onFail() {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show());
                AccountAuthorization.authorize(activity, credential);
            }

            @Override
            public void onNoFolderFound() {

            }
        };
    }
}
