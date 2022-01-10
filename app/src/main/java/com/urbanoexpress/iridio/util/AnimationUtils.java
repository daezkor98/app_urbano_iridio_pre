package com.urbanoexpress.iridio.util;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;

/**
 * Created by mick on 28/06/16.
 */
public class AnimationUtils {

    public static void animationScaleVisibility(boolean show, int duration,
                                                Interpolator interpolator, final View view) {
        Animation animScale = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        if (!show) { // hide
            animScale = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            animScale.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        animScale.setDuration(duration);
        animScale.setInterpolator(interpolator);
        animScale.setFillAfter(true);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animScale);
    }

    public static void animationScale(int duration,
                                      Interpolator interpolator,
                                      final View view,
                                      float fromX, float toX, float fromY, float toY) {
        Animation animScale = new ScaleAnimation(fromX, toX, fromY, toY,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        animScale.setDuration(duration);
        animScale.setInterpolator(interpolator);
        animScale.setFillAfter(true);
        view.startAnimation(animScale);
    }

    public static ValueAnimator animationColorMultiple(int colorFrom, int colorTo, long duration, final View... views) {
        final ValueAnimator colorAnimator =
                ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

        colorAnimator.setDuration(duration);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = 0; i < views.length; i++) {
                    views[i].setBackgroundColor((int) colorAnimator.getAnimatedValue());
                }
            }
        });

        return colorAnimator;
    }

    public static void setAnimationBlinkEffect(View view) {
        ObjectAnimator anim = ObjectAnimator.ofInt(view, "backgroundColor",
                Color.WHITE, Color.parseColor("#50187bcf"), Color.WHITE);
        anim.setDuration(500);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(3);
        anim.start();
    }

    public static class AnimateActionBar {

        private Activity activity;

        private AnimatorSet animatorSet = new AnimatorSet();

        private ValueAnimator colorAnimatorViews;
        private ValueAnimator colorAnimatorStatusBar;

        private long animDuration = 1000;

        public AnimateActionBar(Activity activity) {
            this.activity = activity;
        }

        public void animationColorViews(int colorFrom, int colorTo, final View... views) {
            colorAnimatorViews = animationColorMultiple(colorFrom, colorTo, animDuration, views);
        }

        public void animationColorStatusBar(int colorFrom, int colorTo) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAnimatorStatusBar = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

                colorAnimatorStatusBar.setDuration(animDuration);
                colorAnimatorStatusBar.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        activity.getWindow().setStatusBarColor((int) colorAnimatorStatusBar.getAnimatedValue());
                    }
                });
            }
        }

        public void setDuration(long duration) {
            animDuration = duration;
        }

        public void start() {
            if (colorAnimatorStatusBar != null) {
                animatorSet.play(colorAnimatorViews).with(colorAnimatorStatusBar);
                animatorSet.start();
            } else {
                colorAnimatorViews.start();
            }
        }

    }

}
