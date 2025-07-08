package org.telegram.ui.profileScreen;

import static android.view.Gravity.CENTER_HORIZONTAL;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconTextButton extends LinearLayout {
    private static final TimingFunction scaleTimingFunction = new TimingFunction.Compound(
            new TimingFunction.Delay(0.4f),
            new TimingFunction.SpeedUp(3.0f)
    );
    private static final TimingFunction opacityTimingFunction = new TimingFunction.Compound(
            scaleTimingFunction,
            new TimingFunction.SpeedUp(2.1f)
    );
    private static final TimingFunction bacgroundOpacityTimingFunction = new TimingFunction.Compound(
            new TimingFunction.Delay(0.5f),
            new TimingFunction.SpeedUp(7)
    );

    private ImageView iconView;
    private TextView textView;
    private int blendColor = 0x33FF00FF;
    private PorterDuff.Mode blendMode = PorterDuff.Mode.SCREEN;
    private GradientDrawable bgDrawable;
    private ScaleTouchAnimator scaleTouchAnimator;
    private float bgHeightProgress = 1f;
    private int originalBgHeight = -1;

    public IconTextButton(Context context, String title, int iconResId, int blendColor, PorterDuff.Mode blendMode) {
        super(context);
        setOrientation(VERTICAL);
        this.blendColor = blendColor;
        this.blendMode = blendMode;
        setWillNotDraw(false);
        bgDrawable = new GradientDrawable();
        bgDrawable.setColor(0x1A000000);
        bgDrawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        setBackground(null);
        iconView = new ImageView(context);
        LayoutParams iconParams = new LayoutParams(70, 70);
        iconParams.bottomMargin = 0;
        iconParams.gravity = CENTER_HORIZONTAL;
        iconView.setLayoutParams(iconParams);
        iconView.setImageResource(iconResId);
        addView(iconView);
        textView = new TextView(context);
        textView.setTextColor(0xFFFFFFFF);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        textView.setGravity(CENTER_HORIZONTAL);
        textView.setText(title);
        LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.topMargin = 4;
        textParams.gravity = CENTER_HORIZONTAL;
        textView.setLayoutParams(textParams);
        addView(textView);
        int padding = 0;
        setPadding(padding, 17, padding, padding);
        setClickable(true);
        setFocusable(true);
        scaleTouchAnimator = new ScaleTouchAnimator(this);
    }

    public IconTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(true);
        setFocusable(true);
        scaleTouchAnimator = new ScaleTouchAnimator(this);
    }

    public IconTextButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setFocusable(true);
        scaleTouchAnimator = new ScaleTouchAnimator(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (originalBgHeight <= 0 && h > 0) {
            originalBgHeight = h;
        }
    }

    public void setFadeOut(float progress) {
        float scale = 1f - scaleTimingFunction.execute(progress);
        float elementsAlpha = 1f - opacityTimingFunction.execute(progress);
        float backgroundAlpha = 1f - bacgroundOpacityTimingFunction.execute(progress);

        this.setAlpha(backgroundAlpha);

        if (iconView != null) {
            iconView.setPivotX(iconView.getWidth() / 2f);
            iconView.setPivotY(120);
            iconView.setScaleX(scale);
            iconView.setScaleY(scale);
            iconView.setAlpha(elementsAlpha);

            float iconTranslation = (1 - scale) * (iconView.getHeight() / 2f);
            iconView.setTranslationY(iconTranslation);
        }

        if (textView != null) {
            textView.setPivotX(textView.getWidth() / 2f);
            textView.setPivotY(90);
            textView.setScaleX(scale);
            textView.setScaleY(scale);
            textView.setAlpha(elementsAlpha);

            float textTranslation = (1 - scale) * (-textView.getHeight() / 2f);
            textView.setTranslationY(textTranslation);
        }

        bgHeightProgress = scale;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        float px = getWidth() / 2f, py = getHeight() / 2f;
        canvas.scale(scaleTouchAnimator.getScale(), scaleTouchAnimator.getScale(), px, py);

        int width = getWidth();
        int height = getHeight();

        if (bgDrawable != null && originalBgHeight > 0) {
            int bgHeight = (int) (originalBgHeight * bgHeightProgress);
            int top = height - bgHeight;
            bgDrawable.setBounds(0, top, width, height);
            bgDrawable.draw(canvas);
        }

        super.dispatchDraw(canvas);

        if (blendMode != null) {
            Paint blendPaint = new Paint();
            blendPaint.setColor(blendColor);
            blendPaint.setXfermode(new PorterDuffXfermode(blendMode));
            canvas.drawRect(0, 0, width, height, blendPaint);
        }
        canvas.restoreToCount(save);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleTouchAnimator.onTouch(event);
        return super.onTouchEvent(event);
    }
}
