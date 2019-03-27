package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.drive.model.File;

import java.util.List;

import tuni.tuukka.google.AccountAuthorization;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.DriveApi;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Holds interface implementations for Authorization Activity to avoid interface implementions in
 * activitys.
 */
public class DriveApiHelper {

    /**
     * Creates interface to use in DriveApi.CheckFolders(). If fails, authorizes users account.
     * !Incomplete, will be modified in later releases.
     * @param activity Activity where DriveApi.checkFolders() is called.
     * @param credential Users credentials.
     * @return Usable interface in Authorization activity.
     */
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

    /**
     * Creates interface to use in DriveApi.CheckFolders(). If fails, authorizes users account.
     * !Incomplete, will be modified in later releases.
     * @param activity Activity where DriveApi.checkFolders() is called.
     * @param credential Users credentials.
     * @return Usable interface in Authorization activity.
     */
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
            public void onSuccess(String fileId) {
                //Do something with ui here
                System.out.println("FILE CREATED" + fileId);
            }
        };
    }

    public static DoAfter<List<File>> interfaceListFiles(Activity activity, GoogleAccountCredential credential) {
        return new DoAfter<List<File>>() {
            @Override
            public void onSuccess(List<File> list) {
                list.forEach(System.out::println);
            }

            @Override
            public void onFail() {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Please try again", Toast.LENGTH_SHORT).show());
                AccountAuthorization.authorize(activity, credential);
            }
        };
    }
}
