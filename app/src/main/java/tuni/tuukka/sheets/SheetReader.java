package tuni.tuukka.sheets;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public class SheetReader extends AsyncTask<SheetRequestsInfo, Void, Void> {
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
    protected Void doInBackground(SheetRequestsInfo... info) {
        SheetRequestsInfo sheetInfo = info[0];
                List<List<Object>> values = readFromSheet(sheetInfo.sheetID, sheetInfo.range);

        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            System.out.println(values);
            for (List row : values) {
                System.out.print(row);
            }
        }
        return null;
    }
}
