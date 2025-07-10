package org.telegram.ui.profileScreen;

import android.view.View;

import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

class ViewOnSpring {
    View view;
    private SpringAnimation springAnimX;
    private SpringAnimation springAnimY;
    private float size;
    private boolean didTeleportInitially = false;

    ViewOnSpring(View view, float size, float damping, float stiffness) {
        this.view = view;
        this.size = size;

        springAnimX = new SpringAnimation(view, SpringAnimation.X);
        SpringForce springX = new SpringForce();
        springX.setDampingRatio(damping);
        springX.setStiffness(stiffness);
        springAnimX.setSpring(springX);

        springAnimY = new SpringAnimation(view, SpringAnimation.Y);
        SpringForce springY = new SpringForce();
        springY.setDampingRatio(damping);
        springY.setStiffness(stiffness);
        springAnimY.setSpring(springY);
    }

    ViewOnSpring(View view, float size) {
        this(
                view,
                size,
                SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY,
                SpringForce.STIFFNESS_MEDIUM
        );
    }

    void moveSpring(CartesianCoordinates position) {
        if (!didTeleportInitially) {
            didTeleportInitially = true;
            view.setX(position.x);
            view.setY(position.y);
        } else {
            springAnimX.getSpring().setFinalPosition(position.x - (size / 2));
            springAnimY.getSpring().setFinalPosition(position.y - (size / 2));
            if (!springAnimX.isRunning()) springAnimX.start();
            if (!springAnimY.isRunning()) springAnimY.start();
        }
    }
}