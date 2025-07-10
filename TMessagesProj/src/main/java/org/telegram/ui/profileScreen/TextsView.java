package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextsView extends LinearLayout {
    private static final LinearSpace alignmentSpace = new LinearSpace(1, 0);
    private final TextView titleView;
    private final TextView subtitleView;

    public TextsView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.TOP);

        titleView = new TextView(context);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 27);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            titleView.setTypeface(Typeface.create(Typeface.DEFAULT, 500, false));
        }
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setGravity(Gravity.START | Gravity.TOP);
        titleView.setEllipsize(TextUtils.TruncateAt.END);
        titleView.setSingleLine(true);

        subtitleView = new TextView(context);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        subtitleView.setTypeface(Typeface.DEFAULT);
        subtitleView.setTextColor(0x99FFFFFF);
        subtitleView.setGravity(Gravity.START | Gravity.TOP);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        subtitleView.setSingleLine(true);
//        subtitleView.setShadowLayer(15.0f, 1, 3, 0x22000000);

        addView(titleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(subtitleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void setTitle(CharSequence text) {
        titleView.setText(text);
    }

    public void setSubtitle(CharSequence text) {
        subtitleView.setText(text);
    }

    public void updateAlignment(float progress, float leftMargin, float titleScale) {
        progress = Math.max(0, Math.min(1, progress));
        float finalProgress = progress;
        final float scale = titleScale;
        final float margin = (1 - progress) * leftMargin;
        post(() -> {
            int parentWidth = getWidth();

            float titleWidth = titleView.getPaint().measureText(titleView.getText().toString()) * scale;
            float subtitleWidth = subtitleView.getPaint().measureText(subtitleView.getText().toString());

            float centerTitleLeft = (parentWidth - titleWidth) / 2f;
            float centerSubtitleLeft = (parentWidth - subtitleWidth) / 2f;

            float titleLeft = margin * (1 - finalProgress) + centerTitleLeft * finalProgress;
            float subtitleLeft = margin * (1 - finalProgress) + centerSubtitleLeft * finalProgress;

            titleView.setTranslationX(titleLeft);
            subtitleView.setTranslationX(subtitleLeft);
        });
    }

    public TextView getTitleView() {
        return titleView;
    }

    public TextView getSubtitleView() {
        return subtitleView;
    }

    public void handleHeaderGeometryChange(HeaderGeometry headerGeometry) {
        Float scrollUpProgress = headerGeometry.scrollUpProgress;
        if (scrollUpProgress > 0) {
            float curved = TimingFunction.Bezier.easeInOut.execute(scrollUpProgress);
            float scale = LinearSpace.unit.convert(curved, new LinearSpace(24f / 27f, 1f));
            titleView.setPivotX(0);
            titleView.setPivotY(titleView.getHeight() / 2f);
            titleView.setScaleX(scale);
            titleView.setScaleY(scale);
            subtitleView.setPadding(0,0,0,0);
            updateAlignment(LinearSpace.unit.convert(curved, alignmentSpace), 55, scale);
        } else {
            float progress = Math.min(1.0f, (headerGeometry.scrollDownProgress * 2.0f));
            float curved = TimingFunction.Bezier.easeInOutSoft.execute(progress);
            float scale = LinearSpace.unit.convert(curved, new LinearSpace(24f / 27f, 20f / 27f));
            titleView.setPivotX(0);
            titleView.setPivotY(titleView.getHeight() / 2f);
            titleView.setScaleX(scale);
            titleView.setScaleY(scale);

            float topShift = LinearSpace.unit.convert(curved, new LinearSpace(0, -16));
            subtitleView.setPadding(0,(int)topShift,0,0);
            updateAlignment(LinearSpace.unit.convert(curved, alignmentSpace), 211, scale);
        }
    }
}