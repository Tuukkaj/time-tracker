package tuni.tuukka.activity_helper;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import tuni.tuukka.R;
/**
 * @author      Tuukka Juusela <tuukka.juusela@tuni.fi>
 * @version     20190422
 * @since       1.8
 *
 * Used to change activity's content view to loading screen.
 */
public class LoadingScreenHelper {

    /**
     * Sets content view to loading screen in given activity.
     * @param activity Activity to set content view to loading screen.
     */
    public static void start(Activity activity)  {
        activity.setContentView(R.layout.loading_screen);
        ImageView view = (ImageView) activity.findViewById(R.id.loading);
        view.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotation));

        int colorFrom = activity.getResources().getColor(R.color.colorPrimary);
        int colorTo = activity.getResources().getColor(R.color.colorAccent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                DrawableCompat.setTint(view.getDrawable(), (int)animator.getAnimatedValue());

            }

        });
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimation.start();

    }
}
