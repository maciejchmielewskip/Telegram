package org.telegram.ui.profileScreen;

import androidx.annotation.NonNull;

public class CartesianCoordinates {
    final float x;
    final float y;

    public CartesianCoordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @NonNull
    @Override
    public String toString() {
        return "(x: " + x + "y: " + y + ")";
    }

    public CartesianCoordinates scale(float scalar) {
        return new CartesianCoordinates(x * scalar, y * scalar);
    }

    public CartesianCoordinates add(CartesianCoordinates other) {
        return new CartesianCoordinates(x + other.x, y + other.y);
    }

    public CartesianCoordinates multiply(CartesianCoordinates other) {
        return new CartesianCoordinates(x * other.x, y * other.y);
    }
}