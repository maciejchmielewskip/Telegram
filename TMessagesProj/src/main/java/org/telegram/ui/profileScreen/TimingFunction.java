package org.telegram.ui.profileScreen;

import android.os.Build;
import android.view.animation.PathInterpolator;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class TimingFunction {
    abstract float execute(float time);

    static class Delay extends TimingFunction {
        float delay;

        Delay(float delay) {
            this.delay = delay;
        }

        @Override
        float execute(float time) {
            return Math.max(0, time - delay);
        }
    }

    static class Compound extends TimingFunction {
        private final List<TimingFunction> functions;

        public Compound(TimingFunction... functions) {
            this.functions = new ArrayList();
            Collections.addAll(this.functions, functions);
        }

        @Override
        public float execute(float time) {
            float result = time;
            for (TimingFunction func : functions) {
                result = func.execute(result);
            }
            return result;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static class Bezier extends TimingFunction {
        final PathInterpolator interpolator;

        Bezier(PathInterpolator interpolator) {
            this.interpolator = interpolator;
        }

        static final Bezier easeIn = new Bezier(new PathInterpolator(0.8f, 0, 1, -0.1f));
        static final Bezier easeInLighter = new Bezier(new PathInterpolator(0.8f, 0, 1, 0));
        static final Bezier easeInOut = new Bezier(new PathInterpolator(1,-0.2f,.8f,1.2f));
        static final Bezier easeInOutSoft = new Bezier(new PathInterpolator(0.7f,0,.3f,1));

        @Override
        float execute(float time) {
            return interpolator.getInterpolation(time);
        }
    }

    static class SpeedUp extends TimingFunction {
        final float scalar;

        SpeedUp(float scalar) {
            this.scalar = scalar;
        }

        @Override
        float execute(float time) {
            return time * scalar;
        }
    }
}
