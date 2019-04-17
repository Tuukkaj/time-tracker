package tuni.tuukka.activity_helper;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import tuni.tuukka.R;
import tuni.tuukka.activities.WorkTimeData;

public class TimeDataRecyclerView extends RecyclerView.Adapter<TimeDataRecyclerView.ViewHolder> {
    ArrayList<WorkTimeData.SheetInformation> data;

    public TimeDataRecyclerView(ArrayList<WorkTimeData.SheetInformation> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_time_data, viewGroup, false);
        return new TimeDataRecyclerView.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeDataRecyclerView.ViewHolder viewHolder, int i) {
        ((TextView)viewHolder.cardView.findViewById(R.id.list_time_time)).setText(String.valueOf(data.get(i).time) + "h");
        ((TextView)viewHolder.cardView.findViewById(R.id.list_time_date)).setText(String.valueOf(data.get(i).date));
        ((TextView)viewHolder.cardView.findViewById(R.id.list_time_comment)).setText(String.valueOf(data.get(i).comment));
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
