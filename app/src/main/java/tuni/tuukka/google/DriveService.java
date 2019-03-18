package tuni.tuukka.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class DriveService {
    private static final String APP_NAME = "time-tracker";

    protected static Drive createDriveService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                credential).setApplicationName(APP_NAME).build();
    }
}