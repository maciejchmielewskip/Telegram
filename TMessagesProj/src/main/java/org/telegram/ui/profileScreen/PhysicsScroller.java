package org.telegram.ui.profileScreen;

import android.view.MotionEvent;
import android.view.View;
import android.view.VelocityTracker;
import android.util.Log;

public class PhysicsScroller {
    public interface Listener {
        void onUpdate(float value, float velocity);
        void onEnd();
    }


    private static final float EPS = 0.3f;

    private float value = Adjust.Header.topMargin;
    private float velocity = 0f;
    private boolean running = false;
    private Listener listener;
    private View choreographerView;

    // Drag state
    private float lastFingerY = 0;
    private float fingerTarget = 0f;
    private long recentTime;

//    private VelocityTracker velocityTracker;
    private final PhysicalForce.Spring fingerSpring =
            new PhysicalForce.Spring(500, 20.0f, 0f);
    private final PhysicalForce.Switch fingerSwitch = new PhysicalForce.Switch(fingerSpring, false);
    private final PhysicalForce.Friction friction = new PhysicalForce.Friction(2);
    private final PhysicalForce.Spring bottomEdgeSpring = PhysicalForce.Spring.scrollEdge(3000);
    private final PhysicalForce.OneSide bottomEdgeOneSide = new PhysicalForce.OneSide(bottomEdgeSpring, false, 3000);
    private final PhysicalWorld world = new PhysicalWorld(
            fingerSwitch,
            friction,
            new PhysicalForce.OneSide(
                    PhysicalForce.Spring.scrollEdge(0),
                    true,
                    0
            ),
            new PhysicalForce.OneSide(
                new PhysicalForce.OneSide(
                        PhysicalForce.Spring.scrollEdge(Adjust.Header.topMargin),
                        true,
                        Adjust.Header.topMargin
                ),
                    false,
                    10
            ),
            bottomEdgeOneSide
        );

    public PhysicsScroller(View view) {
        choreographerView = view;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void handleTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                fingerSwitch.isOn = true;
                lastFingerY = ev.getY();
                fingerTarget = value;
                fingerSpring.anchor = fingerTarget;
                ensureRunning();
                break;

            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                float dy = y - lastFingerY;
                lastFingerY = y;
                fingerTarget -= dy;
                fingerSpring.anchor = fingerTarget;
                ensureRunning();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                fingerSwitch.isOn = false;
                break;
        }
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;

            long now = System.nanoTime();


            long diff = now - recentTime;
            recentTime = now;
            float totalTimeDiff = (float)diff / 1000000000;

            while(true) {
                float timeDiff = Math.min(totalTimeDiff, 0.016f);
                float totalForce = world.resolve(value, velocity);
                velocity += totalForce * timeDiff;
                value += velocity * timeDiff;
                totalTimeDiff -= timeDiff;
                if (totalTimeDiff <= 0) {
                    break;
                }
            }


            if (listener != null) listener.onUpdate(value, velocity);

            if (Math.abs(velocity) > EPS) {
                choreographerView.postOnAnimation(this);
            } else {
                velocity = 0;
                running = false;
                if (listener != null) listener.onEnd();
            }
        }
    };

    public float getValue() {
        return value;
    }

    private void ensureRunning() {
        if (running) {
            return;
        }
        recentTime = System.nanoTime();
        running = true;
        choreographerView.postOnAnimation(animationRunnable);
    }

    public void updateBottomEdge(int scrollableSpace) {
        int position = scrollableSpace + 1005;
        bottomEdgeOneSide.threshold = position;
        bottomEdgeSpring.anchor = position;
    }
}