package tuni.tuukka.google;

import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteRangeRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190323
 * @since       1.8
 *
 * Holds static methods and interfaces for calling Sheets Api. All calls to Google are made in
 * AsyncTask. Interfaces are used to act accordingly to results from Google.
 */
public class SheetApi {
    /**
     * Reads range from Sheet. Gives results to parameter DoAfter if successful, if not
     * calls DoAfters onFail() method.
     * @param info Spreadsheets id and range to read.
     * @param sheetInterface DoAfter interface to act accordingly for results from Google.
     */
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
                    } else {
                        return null;
                    }
                } catch (GoogleJsonResponseException e){
                    sheetInterface.onFail();
                }  catch (IOException e) {
                    sheetInterface.onFail();
                } catch (GeneralSecurityException e) {
                    sheetInterface.onFail();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Removes given row fro0m given sheet and gives results to given DoAfter interface
     *
     * @param sheetId Sheet id to remove row from
     * @param row Row to remove
     * @param doAfter Interface to react to results.
     */
    public static void deleteRow(String sheetId, int row, DoAfter<Void> doAfter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    if(Token.getToken().isPresent()) {
                        Sheets service = SheetsService.createSheetService(Token.getToken().get());
                        Spreadsheet spreadsheet = service.spreadsheets().get(sheetId).execute();
                        int gridInt = spreadsheet.getSheets().get(0).getProperties().getSheetId();

                        DeleteRangeRequest deleteRequest = new DeleteRangeRequest()
                                .setRange(new GridRange()
                                        .setSheetId(gridInt)
                                        .setStartRowIndex(row)
                                        .setEndRowIndex(row+1)
                                        .setEndColumnIndex(null)
                                        .setStartColumnIndex(null))
                                .setShiftDimension("ROWS");

                        BatchUpdateSpreadsheetRequest batchUpdate = new BatchUpdateSpreadsheetRequest();
                        Request request = new Request().setDeleteRange(deleteRequest);
                        batchUpdate.setRequests(Collections.singletonList(request));

                        BatchUpdateSpreadsheetResponse response =
                                service.spreadsheets().batchUpdate(sheetId, batchUpdate).execute();
                        doAfter.onSuccess(null);

                    } else {
                        return null;
                    }
                } catch (GoogleJsonResponseException e){
                    e.printStackTrace();
                    doAfter.onFail();
                }  catch (IOException e) {
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

    /**
     * Reads ranges from Sheet. Gives results to parameter DoAfter if successful, if not
     * calls DoAfters onFail() method.
     * @param info Spreadsheets id and range to read.
     * @param doAfter DoAfter interface to act accordingly for results from Google.
     */
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
                    doAfter.onFail();
                }  catch (IOException e) {
                    doAfter.onFail();
                } catch (GeneralSecurityException e) {
                    doAfter.onFail();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Appends given data to spreadsheet. Gets spreadsheet to append id and range from parameter.
     * @param data Data to put into spreadsheet.
     */
    public static void appendSheet(Data data, DoAfter<AppendValuesResponse> doAfter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ValueRange values = new ValueRange();
                List<Object> objects = new ArrayList<>();

                if(data instanceof DataTime) {
                    DataTime time = (DataTime) data;
                    objects.add(time.time);
                    objects.add(time.date);
                    objects.add(time.category);
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
                    doAfter.onSuccess(response);

                } catch (IOException e) {
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

    /**
     * Clears row from given range.
     * @param info Contains id of spreadsheet and range to clear.
     * @param doAfter DoAfter interface to act accordingly for results from Google.
     */
    public static void clearRow(SheetRequestsInfo info, DoAfter<ClearValuesResponse> doAfter) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ClearValuesRequest requestBody = new ClearValuesRequest();

                    Sheets sheets = SheetsService.createSheetService(Token.getToken().get());
                    ClearValuesResponse response = sheets.spreadsheets()
                            .values().clear(info.sheetID, info.range,requestBody).execute();

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

    /**
     * Add new tab to a sheet with given name.
     * @param sheetId SheetId to append tabs
     * @param firstTabName First tab name
     * @param secondTabName Second tab name
     * @param doAfter Interface for results from Google
     */
    public static void appendTab(String sheetId, String firstTabName, String secondTabName, DoAfter doAfter) {
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                AddSheetRequest addFirst = new AddSheetRequest();
                addFirst.setProperties(new SheetProperties().setTitle(firstTabName));
                Request firstRequest = new Request().setAddSheet(addFirst);

                AddSheetRequest addSecond = new AddSheetRequest();
                addSecond.setProperties(new SheetProperties().setTitle(secondTabName));
                Request secondRequest = new Request().setAddSheet(addSecond);

                DeleteSheetRequest delete = new DeleteSheetRequest();
                delete.setSheetId(0);
                Request deleteRequest = new Request().setDeleteSheet(delete);

                BatchUpdateSpreadsheetRequest batchUpdate = new BatchUpdateSpreadsheetRequest();
                List<Request> requests = new ArrayList<>();
                requests.add(firstRequest);
                requests.add(secondRequest);
                requests.add(deleteRequest);
                batchUpdate.setRequests(requests);

                try {
                    Sheets sheets = SheetsService.createSheetService(Token.getToken().get());
                    BatchUpdateSpreadsheetResponse response =
                            sheets.spreadsheets().batchUpdate(sheetId, batchUpdate).execute();
                    doAfter.onSuccess(response.getSpreadsheetId());
                } catch (IOException e) {
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
}
