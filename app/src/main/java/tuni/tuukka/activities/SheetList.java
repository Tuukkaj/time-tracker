package tuni.tuukka.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.SheetDataAdapter;


/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Activity for showing users Sheet files in Google Drive. Redirects to Timer, TimeList or
 * ManualTimeInput activities depending on mode given in intent extras.
 */
public class SheetList extends AppCompatActivity {
    /**
     * Recycler view holding Sheet card views.
     */
    private RecyclerView recyclerView;

    /**
     * Final variable to use in giving target activity sheet name.
     */
    public static final String EXTRA_SHEETNAME = "sheetName";

    /**
     * Final variable to use in giving target activity sheet id.
     */
    public static final String EXTRA_SHEETID = "sheetId";

    /**
     * Final variable to use in giving SheetList names of Sheet files.
     */
    public static final String EXTRA_ARRAY_SHEETNAMES = "names";

    /**
     * Final variable to use in giving SheetList ids of Sheet files.
     */
    public static final String EXTRA_ARRAY_SHEETIDS = "ids";

    /**
     * Final variable to use in giving SheetList mode to operate. See SheetDataAdapter for mode
     * variables.
     */
    public static final String EXTRA_MODE = "mode";


    /**
     * Sets content view depending on if SheetList received list of Sheet files.
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int mode = getIntent().getIntExtra(EXTRA_MODE,0);

        ArrayList<SheetInformation> info = createSheetInformation();

        if(info.isEmpty()) {
            setContentView(R.layout.activity_sheet_list_empty);
        } else {
            setContentView(R.layout.activity_sheet_list);

            info.add(new SheetInformation("NULL", "EXTRA ITEM FOR BUTTON"));
            setUpRecyclerView(info, mode);
        }

        setContentTitle(mode);
    }

    /**
     * Starts activity CreateSheet
     * @param view Clicked view. Not in use.
     */
    public void createSheet(View view) {
        startActivity(new Intent(this, CreateSheet.class));
    }

    /**
     * Creates array list of SheetInformation from names and ids of users SheetFiles given in
     * Intent.
     * @return ArrayList of users Sheet files names and ids.
     */
    private ArrayList<SheetInformation> createSheetInformation() {
        ArrayList<SheetInformation> data = new ArrayList<>();
        ArrayList<String> names = getIntent().getStringArrayListExtra(EXTRA_ARRAY_SHEETNAMES);
        ArrayList<String> ids = getIntent().getStringArrayListExtra(EXTRA_ARRAY_SHEETIDS);

        for(int i = 0; names != null && ids != null && i < names.size(); i++) {
            data.add(new SheetInformation(ids.get(i),names.get(i)));
        }

        return data;
    }

    /**
     * Sets data of recycler view.
     * @param data ArrayList of users Sheet files names and ids.
     * @param mode Activity to direct to when recycler views item is clicked.
     */
    private void setUpRecyclerView(ArrayList<SheetInformation> data, int mode) {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new SheetDataAdapter(data, this, mode));
    }

    /**
     * Sets title of activity based on mode.
     * @param mode Activity to direct to when recycler view item is clicked.
     */
    private void setContentTitle(int mode) {
        switch (mode) {
            case SheetDataAdapter.MODE_TIMER: {
                setTitle(getString(R.string.sheetlist_title_timer));
                break;
            }

            case SheetDataAdapter.MODE_MANUAL_INPUT: {
                setTitle(getString(R.string.sheetlist_title_time_input));

                break;
            }

            case SheetDataAdapter.MODE_SHOW_TIME: {
                setTitle(getString(R.string.sheetlist_title_show_time));

                break;
            }
        }
    }

    /**
     * Inner class for holding Sheet files name and id.
     */
    public class SheetInformation {
        /**
         * Id of Sheet in Google Drive.
         */
        public String id;

        /**
         * Name of Sheet in Google Drive.
         */
        public String name;

        /**
         * Sets name and id of the Sheet.
         * @param id Id of Sheet.
         * @param name Name of Sheet.
         */
        public SheetInformation(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
