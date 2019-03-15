package tuni.tuukka.sheets;

import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetReader extends AsyncTask<SheetRequestsInfo, Void, Void> {
    AuthenticationFailed call;

    public SheetReader(AuthenticationFailed call) {
        this.call = call;
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
        } catch (GoogleJsonResponseException e){
            System.out.println("EXCPETION GoogleJsonResponseException");
            call.call();
        }  catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Void doInBackground(SheetRequestsInfo... info) {
        SheetRequestsInfo sheetInfo = info[0];
                List<List<Object>> values = readFromSheet(sheetInfo.sheetID, sheetInfo.range);

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println(values);
        }
        return null;
    }
}
