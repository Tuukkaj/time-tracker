package tuni.tuukka.activity_helper;

import android.app.Activity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import tuni.tuukka.R;

public class LoadingScreenHelper {
    public static void start(Activity activity)  {
        activity.setContentView(R.layout.loading_screen);
        ((ImageView) activity.findViewById(R.id.loading)).setAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotation));
    }
}
