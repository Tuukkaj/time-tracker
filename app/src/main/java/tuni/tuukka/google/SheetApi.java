package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class SheetApi {
    public static void readRange(SheetRequestsInfo info, DoAfter<List<List<Object>>> sheetInterface) {
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

    public static void readRanges(SheetRequestsInfo info, DoAfter<List<ValueRange>> doAfter) {
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
                        doAfter.onSuccess(response.getValueRanges());
                    } else {
                        return null;
                    }
                } catch (GoogleJsonResponseException e){
                    e.printStackTrace();
                    System.out.println("EXCPETION GoogleJsonResponseException");
                    doAfter.onFail();
                }  catch (IOException e) {
                    doAfter.onFail();
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    doAfter.onFail();
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void appendSheet(Data data) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ValueRange values = new ValueRange();
                List<Object> objects = new ArrayList<>();

                if(data instanceof DataTime) {
                    DataTime time = (DataTime) data;
                    objects.add(time.start);
                    objects.add(time.end);
                    objects.add(time.category);
                    objects.add(time.comment);
                } else if (data instanceof DataCategory) {
                    DataCategory category = (DataCategory) data;
                    objects.add(category.category);
                }

                List<List<Object>> list = new ArrayList<>();
                list.add(objects);
                values.setValues(list);

                try {
                    Sheets sheetService = SheetsService.createSheetService(Token.getToken().get());
                    Sheets.Spreadsheets.Values.Append request = sheetService.spreadsheets().values().append(data.info.sheetID, data.info.range, values);
                    request.setInsertDataOption("INSERT_ROWS");
                    request.setValueInputOption("RAW");
                    AppendValuesResponse response = request.execute();
                    System.out.println(response);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void clearRow(SheetRequestsInfo info, DoAfter<ClearValuesResponse> doAfter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ClearValuesRequest requestBody = new ClearValuesRequest();

                    Sheets sheets = SheetsService.createSheetService(Token.getToken().get());
                    ClearValuesResponse response = sheets.spreadsheets()
                            .values().clear(info.sheetID, info.range,requestBody).execute();

                    System.out.println(response);
                    doAfter.onSuccess(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    doAfter.onFail();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    doAfter.onFail();
                }
                return null;
            }
        }.execute();
    }
    public interface DoAfter<T> {
        void onFail();
        void onSuccess(T value);
    }
}
