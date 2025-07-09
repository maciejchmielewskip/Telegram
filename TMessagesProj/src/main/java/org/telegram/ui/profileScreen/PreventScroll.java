package org.telegram.ui.profileScreen;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class PreventScroll extends FrameLayout {
    public PreventScroll(@NonNull Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
