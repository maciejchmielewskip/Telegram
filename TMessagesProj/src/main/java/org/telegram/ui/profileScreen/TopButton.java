package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class TopButton extends FrameLayout {
    private ImageView iconView;
    private ScaleTouchAnimator scaleTouchAnimator;
    private Runnable tapListener;

    public TopButton(Context context, int iconResId) {
        super(context);
        init(context, iconResId);
    }
    public TopButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, 0);
    }
    public TopButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, 0);
    }
    private void init(Context context, int iconResId) {
        setWillNotDraw(false);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        setPadding(padding, padding, padding, padding);
        setClickable(true);
        setFocusable(true);
        iconView = new ImageView(context);
        int size = 26;
        LayoutParams lp = new LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, getResources().getDisplayMetrics()), android.view.Gravity.CENTER);
        iconView.setLayoutParams(lp);
        if (iconResId != 0) iconView.setImageResource(iconResId);
        addView(iconView);
        scaleTouchAnimator = new ScaleTouchAnimator(this);
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        canvas.save();
        canvas.scale(scaleTouchAnimator.getScale(), scaleTouchAnimator.getScale(), cx, cy);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleTouchAnimator.onTouch(event);
        if (event.getAction() == MotionEvent.ACTION_UP && isPressed()) {
            handleTap();
        }
        return super.onTouchEvent(event);
    }
    public void setIcon(int resId) {
        iconView.setImageResource(resId);
    }
    public ImageView getIconView() {
        return iconView;
    }
    public void setOnTapListener(Runnable listener) {
        tapListener = listener;
    }
    public void handleTap() {
        if (tapListener != null) {
            tapListener.run();
        }
    }
}