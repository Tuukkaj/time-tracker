package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activities.TimeList;
import tuni.tuukka.google.DoAfter;
import tuni.tuukka.google.SheetApi;

public class TimeDataAdapter extends RecyclerView.Adapter<TimeDataAdapter.ViewHolder> {
    private ArrayList<TimeList.SheetInformation> data;
    private DoAfter<Void> deleteInterface;
    private String sheetId;
    private Activity activity;

    public TimeDataAdapter(Activity activity, String sheetId, ArrayList<TimeList.SheetInformation> data, DoAfter<Void> deleteInterface) {
        this.data = data;
        this.deleteInterface = deleteInterface;
        this.sheetId = sheetId;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_time_data, viewGroup, false);
        return new TimeDataAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeDataAdapter.ViewHolder viewHolder, int i) {
        ((TextView)viewHolder.cardView.findViewById(R.id.list_time_time)).setText(String.valueOf(data.get(i).time) + "h");
        ((TextView)viewHolder.cardView.findViewById(R.id.list_time_date)).setText(String.valueOf(data.get(i).date));
        ((TextView)viewHolder.cardView.findViewById(R.id.list_time_comment)).setText(String.valueOf(data.get(i).comment));
        ((ImageView)viewHolder.cardView.findViewById(R.id.list_time_delete)).setOnClickListener(v -> handleDeleteClick(i));
    }

    private void handleDeleteClick(int index) {
        int sheetIndex =  data.get(index).index; // Google Sheets indexes start at 1
        SheetApi.deleteRow(sheetId, sheetIndex, deleteInterface);
        activity.runOnUiThread(() -> LoadingScreenHelper.start(activity));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }
}
