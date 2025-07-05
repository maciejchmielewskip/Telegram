package org.telegram.ui.profileScreen;

import androidx.annotation.NonNull;

public class PolarCoordinates {
    final float phase;
    final float length;

    public PolarCoordinates(float phase, float length) {
        this.phase = phase;
        this.length = length;
    }

    public PolarCoordinates scaled(float scalar) {
        return new PolarCoordinates(
                phase,
                length * scalar
        );
    }

    public PolarCoordinates twisted(float shift) {
        return new PolarCoordinates(
                phase + shift,
                length
        );
    }

    public CartesianCoordinates toCartesian() {
        float x = (float) (length * Math.cos(phase));
        float y = (float) (length * Math.sin(phase));
        return new CartesianCoordinates(x, y);
    }

    @NonNull
    @Override
    public String toString() {
        return "(p: " + phase + ", l: " + length + ")";
    }
}
