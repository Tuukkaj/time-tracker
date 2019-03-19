package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class DriveFolder {
    public static void checkFolders(FolderCheckReady checkReady, String folderName) {
        AsyncTask task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Drive drive = DriveService.createDriveService(Token.getToken().get());
                    List<File> files = drive.files().list().setQ("name = '"+folderName+"'").execute().getFiles();
                    System.out.println(files);
                    checkReady.doAfter(true);
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

    public interface FolderCheckReady {
        void doAfter(boolean result);
        void onFail();
    }
}
