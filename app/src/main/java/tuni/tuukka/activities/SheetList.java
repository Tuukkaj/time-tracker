package tuni.tuukka.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activity_helper.SheetRecyclerViewAdapter;

public class SheetList extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<SheetInformation> data = new ArrayList<>();
        data.add(new SheetInformation("Kaljan juonti", "sdjfhdsuhfa7234"));
        data.add(new SheetInformation("Koodaus", "asfojdfiu328ry9ha9f123"));
        data.add(new SheetInformation("Syöminen", "dig239r8323eh119xj21"));
        data.add(new SheetInformation("Työskentely", "dt4uhjfjh348yf438373"));

        recyclerView.setAdapter(new SheetRecyclerViewAdapter(data));
    }

    public class SheetInformation {
        public String id;
        public String name;

        public SheetInformation(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
