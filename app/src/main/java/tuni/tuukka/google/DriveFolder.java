package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class DriveFolder {
    public static void checkFolders(CheckFoldersInterface checkReady, String folderName) {
        AsyncTask task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name = '"+folderName+"'").execute().getFiles();
                    try {
                        JSONArray array = new JSONArray(files.toString());
                        JSONObject object = array.getJSONObject(0);
                        String name = object.getString("name");
                        String folderId = object.getString("id");
                        System.out.println("Name: " + name + " Id" + folderId);
                        if(name.equalsIgnoreCase(folderName)) {
                            checkReady.doAfter(folderId);
                        } else {
                            checkReady.onNoFolderFound();
                        }
                    } catch (JSONException e) {
                        checkReady.onNoFolderFound();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    checkReady.onFail();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    checkReady.onFail();
                } catch (Exception e) {
                    e.printStackTrace();
                    checkReady.onFail();
                }
                return null;
            }
        }.execute();
    }

    public interface CheckFoldersInterface {
        void doAfter(String folderId);
        void onFail();
        void onNoFolderFound();
    }
}
