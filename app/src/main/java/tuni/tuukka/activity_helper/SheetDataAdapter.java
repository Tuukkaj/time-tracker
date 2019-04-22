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

/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Used by SheetList activity to create RecyclerView of list of Sheet files in users Google Drive.
 */
public class SheetDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * Users Sheets in Drive.
     */
    private List<SheetList.SheetInformation> data;

    /**
     * Parent Activity.
     */
    private Activity activity;

    /**
     * Mode used to determine where user should be directed when Recycler view's item is clicked.
     */
    private int mode;

    /**
     * Mode used when user should be directed to Timer activity from SheetList.
     */
    public static final int MODE_TIMER = 0;

    /**
     * Mode used when user should be directed to ManualTimeInput activity from SheetList.
     */
    public static final int MODE_MANUAL_INPUT = 1;

    /**
     * Mode used when user should be directed to TimeList activity from SheetList.
     */
    public static final int MODE_SHOW_TIME = 2;

    /**
     * List item type in Recycler view. Used to determine which layout Recycler view item gets.
     */
    private static final int TYPE_LIST_ITEM = 0;

    /**
     * Button item type in Recycler view. Used to determine which layout Recycler view item gets.
     */
    private static final int TYPE_BUTTON = 1;

    /**
     * Sets given parameters to variables.
     * @param data Sheet files of user.
     * @param activity Parent activity.
     * @param mode Mode used to determine where user should be directed when Recycler view's item is clicked.
     */
    public SheetDataAdapter(List<SheetList.SheetInformation> data, Activity activity, int mode) {
        this.data = data;
        this.activity = activity;
        this.mode = mode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(int position) {
        return position != data.size() - 1 ?  TYPE_LIST_ITEM : TYPE_BUTTON;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * POJO class for holding Recycler views List item.
     */
    public static class ListItemViewHolder extends RecyclerView.ViewHolder {
        /**
         * Card view in List Item.
         */
        public CardView cardView;

        /**
         * Sets parameter to cardView variable.
         * @param cardView Card View in Recycler View.
         */
        public ListItemViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
        }
    }

    /**
     * POJO class for holding Recycler views Button item.
     */
    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        /**
         * Linear layout of Button view holder
         */
        public LinearLayout layout;

        /**
         * Sets parameter to layout variable.
         * @param layout LinearLayout in Recycler view.
         */
        public ButtonViewHolder(@NonNull LinearLayout layout) {
            super(layout);

            this.layout = layout;
        }
    }
}
