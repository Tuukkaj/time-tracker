package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import tuni.tuukka.R;
import tuni.tuukka.activities.CreateSheet;
import tuni.tuukka.activities.ManualTimeInput;
import tuni.tuukka.activities.SheetList;
import tuni.tuukka.activities.Timer;
import tuni.tuukka.activities.TimeList;

public class SheetDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SheetList.SheetInformation> data;
    private Activity activity;
    private int mode;

    public static final int MODE_TIMER = 0;
    public static final int MODE_MANUAL_INPUT = 1;
    public static final int MODE_SHOW_TIME = 2;

    private static final int TYPE_LIST_ITEM = 0;
    private static final int TYPE_BUTTON = 1;

    public SheetDataAdapter(List<SheetList.SheetInformation> data, Activity activity, int mode) {
        this.data = data;
        this.activity = activity;
        this.mode = mode;
    }

    @Override
    public int getItemViewType(int position) {
        return position != data.size() - 1 ?  TYPE_LIST_ITEM : TYPE_BUTTON;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        switch (viewType) {
            case TYPE_BUTTON: {
                LinearLayout layout = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_sheet_add, viewGroup, false);

                return new ButtonViewHolder(layout);
            }
            case TYPE_LIST_ITEM: {
                CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_sheet_view, viewGroup, false);
                return new ListItemViewHolder(cardView);
            }
            default: {
                CardView cardView = (CardView) LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.list_sheet_view, viewGroup, false);
                return new ListItemViewHolder(cardView);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case TYPE_BUTTON: {
                ButtonViewHolder view = (ButtonViewHolder) viewHolder;
                ((FloatingActionButton)view.layout.findViewById(R.id.list_sheet_add_button)).setOnClickListener(click -> {
                    activity.runOnUiThread(() -> {
                        activity.startActivity(new Intent(activity, CreateSheet.class));
                    });
                });

                break;
            }

            case TYPE_LIST_ITEM: {
                ListItemViewHolder view = (ListItemViewHolder) viewHolder;

                if(mode == MODE_TIMER) {
                    ((CardView) view.cardView.findViewById(R.id.list_sheet_cardview)).setOnClickListener(event -> {
                        Intent intent = new Intent(activity, Timer.class);
                        intent.putExtra(Timer.EXTRA_SHEETID, data.get(i).id);
                        intent.putExtra(Timer.EXTRA_SHEETNAME, data.get(i).name);
                        activity.startActivity(intent);
                    });
                } else if(mode == MODE_MANUAL_INPUT) {
                    ((CardView) view.cardView.findViewById(R.id.list_sheet_cardview)).setOnClickListener(event -> {
                        Intent intent = new Intent(activity, ManualTimeInput.class);
                        intent.putExtra(SheetList.EXTRA_SHEETID, data.get(i).id);
                        intent.putExtra(SheetList.EXTRA_SHEETNAME, data.get(i).name);
                        activity.startActivity(intent);
                    });
                } else if(mode == MODE_SHOW_TIME) {
                    ((CardView) view.cardView.findViewById(R.id.list_sheet_cardview)).setOnClickListener(event -> {
                        Intent intent = new Intent(activity, TimeList.class);
                        intent.putExtra(SheetList.EXTRA_SHEETID, data.get(i).id);
                        intent.putExtra(SheetList.EXTRA_SHEETNAME, data.get(i).name);
                        activity.startActivity(intent);
                    });
                }

                ((TextView) view.cardView.findViewById(R.id.sheet_name)).setText(data.get(i).name.substring(13));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ListItemViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ListItemViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;

        public ButtonViewHolder(@NonNull LinearLayout layout) {
            super(layout);

            this.layout = layout;
        }
    }
}
