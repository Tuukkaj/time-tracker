package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class DriveFolder extends AsyncTask<Void, Void, Void> {
    private void getDriveFolders() {
        try {
            Drive drive = DriveService.createDriveService(Token.getToken().get());
            FileList list = drive.files().list().execute();
            List<File> files = list.getFiles();
            System.out.println(files);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void doInBackground(Void... voids) {
        getDriveFolders();
        return null;
    }
}
