package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tuni.tuukka.R;
import tuni.tuukka.activities.ManualTimeInput;
import tuni.tuukka.activities.SheetList;
import tuni.tuukka.activities.Timer;
import tuni.tuukka.activities.TimeList;

public class SheetDataAdapter extends RecyclerView.Adapter<SheetDataAdapter.ViewHolder> {
    private List<SheetList.SheetInformation> data;
    private Activity activity;
    private int mode;
    public static final int MODE_TIMER = 0;
    public static final int MODE_MANUAL_INPUT = 1;
    public static final int MODE_SHOW_TIME = 2;
    public SheetDataAdapter(List<SheetList.SheetInformation> data, Activity activity, int mode) {
        this.data = data;
        this.activity = activity;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_sheet_view, viewGroup, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(mode == MODE_TIMER) {
            ((CardView) viewHolder.cardView.findViewById(R.id.list_sheet_cardview)).setOnClickListener(event -> {
                Intent intent = new Intent(activity, Timer.class);
                intent.putExtra(Timer.EXTRA_SHEETID, data.get(i).id);
                intent.putExtra(Timer.EXTRA_SHEETNAME, data.get(i).name);
                activity.startActivity(intent);
            });
        } else if(mode == MODE_MANUAL_INPUT) {
            ((CardView) viewHolder.cardView.findViewById(R.id.list_sheet_cardview)).setOnClickListener(event -> {
                Intent intent = new Intent(activity, ManualTimeInput.class);
                intent.putExtra("sheetId", data.get(i).id);
                intent.putExtra("sheetName", data.get(i).name);
                activity.startActivity(intent);
            });
        } else if(mode == MODE_SHOW_TIME) {
            ((CardView) viewHolder.cardView.findViewById(R.id.list_sheet_cardview)).setOnClickListener(event -> {
                Intent intent = new Intent(activity, TimeList.class);
                intent.putExtra("sheetId", data.get(i).id);
                intent.putExtra("sheetName", data.get(i).name);
                activity.startActivity(intent);
            });
        }

        ((TextView) viewHolder.cardView.findViewById(R.id.sheet_name)).setText(data.get(i).name.substring(13));
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
