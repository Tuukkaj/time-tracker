package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import tuni.tuukka.R;
import tuni.tuukka.activities.ManualTimeInput;
import tuni.tuukka.activities.SheetList;
import tuni.tuukka.activities.Timer;

public class SheetRecyclerViewAdapter extends RecyclerView.Adapter<SheetRecyclerViewAdapter.ViewHolder> {
    private List<SheetList.SheetInformation> data;
    private Activity activity;
    private int mode;
    public static final int MODE_TIMER = 0;
    public static final int MODE_MANUAL_INPUT = 1;
    public static final int MODE_SHOW_TIME = 2;
    public SheetRecyclerViewAdapter(List<SheetList.SheetInformation> data, Activity activity, int mode) {
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
            ((Button) viewHolder.cardView.findViewById(R.id.button_select)).setOnClickListener(event -> {
                Intent intent = new Intent(activity, Timer.class);
                intent.putExtra("sheetId", data.get(i).id);
                intent.putExtra("sheetName", data.get(i).name);
                activity.startActivity(intent);
            });
        } else if(mode == MODE_MANUAL_INPUT) {
            ((Button) viewHolder.cardView.findViewById(R.id.button_select)).setOnClickListener(event -> {
                Intent intent = new Intent(activity, ManualTimeInput.class);
                intent.putExtra("sheetId", data.get(i).id);
                intent.putExtra("sheetName", data.get(i).name);
                activity.startActivity(intent);
            });
        } else if(mode == MODE_SHOW_TIME) {
            //TODO IMPLEMENT LATER
            System.out.println("MODE SHOW TIME" + MODE_SHOW_TIME);
        }

        ((TextView) viewHolder.cardView.findViewById(R.id.sheet_name)).setText(data.get(i).name.substring(13));
        ((TextView) viewHolder.cardView.findViewById(R.id.sheetId)).setText(data.get(i).id);
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
