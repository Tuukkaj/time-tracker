package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetApi {
    public static void readRange(SheetRequestsInfo info, ReadRangeInterface sheetInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    if(Token.getToken().isPresent()) {
                        Sheets service = SheetsService.createSheetService(Token.getToken().get());
                        ValueRange response = service.spreadsheets().values()
                                .get(info.sheetID, info.range)
                                .execute();
                        sheetInterface.onSuccess(response.getValues());
                        System.out.println(response.getValues());
                    } else {
                        return null;
                    }
                } catch (GoogleJsonResponseException e){
                    e.printStackTrace();
                    System.out.println("EXCPETION GoogleJsonResponseException");
                    sheetInterface.onFail();
                }  catch (IOException e) {
                    sheetInterface.onFail();
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    sheetInterface.onFail();
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void readRanges(SheetRequestsInfo info, ReadRangesInterface readInterface) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    if(Token.getToken().isPresent()) {
                        Sheets service = SheetsService.createSheetService(Token.getToken().get());
                        BatchGetValuesResponse response = service.spreadsheets().values()
                                .batchGet(info.sheetID)
                                .setRanges(info.ranges)
                                .execute();
                        readInterface.onSuccess(response.getValueRanges());
                    } else {
                        return null;
                    }
                } catch (GoogleJsonResponseException e){
                    e.printStackTrace();
                    System.out.println("EXCPETION GoogleJsonResponseException");
                    readInterface.onFail();
                }  catch (IOException e) {
                    readInterface.onFail();
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    readInterface.onFail();
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public interface ReadRangesInterface {
        void onFail();
        void onSuccess(List<ValueRange> values);
    }
    public interface ReadRangeInterface {
        public void onFail();
        public void onSuccess(List<List<Object>> values);
    }
}
