package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.view.View;

import org.telegram.messenger.R;

//import org.telegram.messenger.R;

public class ButtonsLayout extends LinearLayout {
    private FrameLayout.LayoutParams buttonsLayoutParams;

    public ButtonsLayout(Context context) {
        super(context);
        int horizontalMargin = 40;
        int gapPx = 20;
        setOrientation(HORIZONTAL);

        buttonsLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        buttonsLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        buttonsLayoutParams.leftMargin = horizontalMargin;
        buttonsLayoutParams.rightMargin = horizontalMargin;
        buttonsLayoutParams.topMargin = 810;
        setLayoutParams(buttonsLayoutParams);

        String[] titles = new String[]{"Message", "Mute", "Call", "Video"};
        int[] icons = new int[]{R.drawable.message, R.drawable.mute, R.drawable.call, R.drawable.video};

        int buttonCount = 4;
        for (int i = 0; i < buttonCount; i++) {
            IconTextButton button = new IconTextButton(
                    context,
                    titles[i],
                    icons[i],
                    0xEAEAEA,
                    PorterDuff.Mode.DARKEN
            );
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    0,
                    160,
                    1f
            );
            if (i != buttonCount - 1) {
                btnParams.rightMargin = gapPx;
            }
            button.setLayoutParams(btnParams);
            addView(button);
        }
    }

    public FrameLayout.LayoutParams getButtonsLayoutParams() {
        return buttonsLayoutParams;
    }

    public void setFadeOut(float progress) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof IconTextButton) {
                try {
                    IconTextButton button = (IconTextButton) child;
                    button.setFadeOut(progress);
                } catch (Exception ignored) {}
            }
        }
    }
}