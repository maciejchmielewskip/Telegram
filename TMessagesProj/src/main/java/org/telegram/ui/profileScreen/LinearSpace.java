package org.telegram.ui.profileScreen;

import android.util.Log;

public class LinearSpace {
    final float from;
    final float to;
    final float width;

    static final LinearSpace unit = new LinearSpace(0, 1);

    public LinearSpace(float from, float to) {
        this.from = from;
        this.to = to;
        this.width = to - from;
    }

    float convert(float value, LinearSpace toSpace) {
        float progress = (value - from) / width;
        return toSpace.from + (progress * toSpace.width);
    }
}

