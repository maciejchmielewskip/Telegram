package org.telegram.ui.profileScreen;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class PhysicalWorld {
    private final List<PhysicalForce> forces = new ArrayList<>();

    PhysicalWorld(PhysicalForce... forces) {
        Collections.addAll(this.forces, forces);
    }

    float resolve(float location, float speed) {
        float totalForce = 0f;
        for (PhysicalForce force : forces) {
            totalForce += force.force(location, speed);
        }
        return totalForce;
    }
}

abstract class PhysicalForce {
    abstract float force(float location, float velocity);

    static class OneSide extends PhysicalForce {
        private final PhysicalForce force;
        boolean left;
        float threshold;

        OneSide(PhysicalForce force, boolean left, float threshold) {
            this.force = force;
            this.left = left;
            this.threshold = threshold;
        }

        @Override
        float force(float location, float velocity) {
            if (left) {
                return location < threshold ? force.force(location, velocity) : 0;
            } else {
                return location > threshold ? force.force(location, velocity) : 0;
            }
        }
    }

    static class Friction extends PhysicalForce {
        float strength;

        Friction(float strength) {
            this.strength = strength;
        }

        @Override
        float force(float location, float velocity) {
            return strength * -velocity;
        }
    }

    static class Switch extends PhysicalForce {
        private final PhysicalForce force;
        boolean isOn;

        Switch(PhysicalForce force, boolean isOn) {
            this.force = force;
            this.isOn = isOn;
        }

        @Override
        float force(float location, float velocity) {
            return isOn ? force.force(location, velocity) : 0;
        }
    }

    static class Spring extends PhysicalForce {
        float stiffness;
        float damping;
        float anchor;

        public Spring(float stiffness, float damping, float anchor) {
            this.stiffness = stiffness;
            this.damping = damping;
            this.anchor = anchor;
        }

        static Spring scrollEdge(float anchor) {
            return new Spring(600, 60, anchor);
        }

        public float force(float location, float velocity) {
            return -stiffness * (location - anchor) - damping * velocity;
        }
    }
}