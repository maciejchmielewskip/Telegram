package org.telegram.ui.profileScreen;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

class ScaleTouchAnimator {
    private final View target;
    private float scale = 1.0f;
    private ValueAnimator scaleAnimator;

    public ScaleTouchAnimator(View target) {
        this.target = target;
    }

    public float getScale() {
        return scale;
    }

    public void onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                animateScale(0.9f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                animateScale(1.0f);
                break;
        }
    }

    private void animateScale(final float to) {
        if (scaleAnimator != null && scaleAnimator.isRunning())
            scaleAnimator.cancel();
        scaleAnimator = ValueAnimator.ofFloat(scale, to);
        scaleAnimator.setDuration(120);
        scaleAnimator.setInterpolator(new DecelerateInterpolator());
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();
                target.invalidate();
            }
        });
        scaleAnimator.start();
    }
}