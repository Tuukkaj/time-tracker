package tuni.tuukka.sheets;

import android.os.AsyncTask;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetReader extends AsyncTask<Void, Void, Void> {
    private final String APPLICATION_NAME = "time-tracker";
    private final String SHEET_ID = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";

    public Sheets getSheetsService(String token) throws IOException, GeneralSecurityException {
        Credential credential = new GoogleCredential().setAccessToken(token);
        return new Sheets.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(APPLICATION_NAME).build();
    }


    public List<List<Object>> readFromSheet(String sheetID, String range){
        try{
            if(Token.getToken().isPresent()) {
                Sheets service = SheetsService.createSheetService(Token.getToken().get());
                ValueRange response = service.spreadsheets().values()
                        .get(sheetID, range)
                        .execute();
                return response.getValues();
            } else {
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        List<List<Object>> values = readFromSheet("11CrV_44G1pAWHT4SgZ3q7p7xeY-_3L16i4XugniOsqM", "Sheet1");

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println("Name, Major");
            for (List row : values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.print(row);
            }
        }
        return null;
    }
/*
    public UpdateValuesResponse writeSomethingToSheet(String token) throws IOException {
        ValueRange body = new ValueRange()
                .setValues(Arrays.asList(
                        Arrays.asList("Expenses January"),
                        Arrays.asList("books", "30"),
                        Arrays.asList("pens", "10"),
                        Arrays.asList("Expenses February"),
                        Arrays.asList("clothes", "20"),
                        Arrays.asList("shoes", "5")));
        UpdateValuesResponse result = null;
        try {
            result = getSheetsService(token).spreadsheets().values()
                    .update(SHEET_ID, "A1", body)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }*/
}
