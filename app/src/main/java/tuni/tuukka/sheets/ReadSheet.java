package tuni.tuukka.sheets;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadSheet extends AsyncTask<Void, Void, List<String>> {
    Sheets sheetService = null;

    public ReadSheet(GoogleAccountCredential credential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory factory = JacksonFactory.getDefaultInstance();
        sheetService = new Sheets.Builder(transport, factory, credential).setApplicationName("time-tracker").build();

    }

    @Override
    protected List<String> doInBackground(Void... voids)  {
        try {
            return getSheetInformation();
        } catch (IOException e) {
            Log.d("tuksu", "doInBackground: ERROR ERROR"  + e.getMessage() + e.getCause());

            return null;
        }


    }

    private List<String> getSheetInformation() throws IOException {
        String spreadSheetID = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
        String range = "Class Data!A2:E";
        List<String> results = new ArrayList<String>();
        ValueRange response = this.sheetService.spreadsheets().values().get(spreadSheetID, range).execute();

        List<List<Object>> values = response.getValues();

        if(values!=null) {
            results.add("category, description");
        }

        for(List row : values) {
            results.add(row.get(2) + " " + row.get(3));
        }
        Log.d("tuksu", "RESULT" + results);
        return results;
    }
}
