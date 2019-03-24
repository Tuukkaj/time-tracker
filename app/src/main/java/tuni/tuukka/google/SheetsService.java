package tuni.tuukka.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190324
 * @since       1.8
 *
 * Used to create Sheets for SheetApi class
 */
public class SheetsService {
    /**
     * Name of app.
     */
    private static final String APP_NAME = "time-tracker";

    /**
     * Creates Sheets service from given token. Used by SheetApi class to get Sheets class.
     *
     * @param token Token to initialize creation of Sheets
     * @return Sheets service created with given Token
     * @throws IOException Thrown if communication with Google fails.
     * @throws GeneralSecurityException Thrown if there is security exception occurs.
     */
    protected static Sheets createSheetService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Sheets.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                credential).setApplicationName(APP_NAME).build();
    }
}
