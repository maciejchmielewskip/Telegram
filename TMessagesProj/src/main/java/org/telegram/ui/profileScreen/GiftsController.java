package org.telegram.ui.profileScreen;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.telegram.messenger.R;

import java.util.ArrayList;

class Gift {
    final ParticlesGeometry.Particle particle;
    final ViewOnSpring viewOnSpring;
    public CartesianCoordinates currentPosition;

    public Gift(ParticlesGeometry.Particle particle, ViewOnSpring viewOnSpring) {
        this.particle = particle;
        this.viewOnSpring = viewOnSpring;
    }
}

class GiftsController {
    private static final CartesianCoordinates marginShift = new CartesianCoordinates(0, Adjust.Header.topMargin);

    static GiftsController make(Context context) {
        ArrayList<Gift> gifts = new ArrayList<Gift>();
        double[][] giftData = {
                {342.5, 133.5},
                {231.5, 252.5},
                {332.5, 351.5},
                {762.5, 332.5},
                {863.5, 223.5},
                {746.5, 141.5},
        };

        final double cx = 540;
        final double cy = 260;
        int i = -1;
        for (double[] gift : giftData) {
            i += 1;
            double x = gift[0];
            double y = gift[1];
            double radians = Math.atan2(y - cy, x - cx);
            double distance = Math.hypot(x - cx, y - cy);
            PolarCoordinates position = new PolarCoordinates((float)radians, (float)distance / 300);
            ImageView view = new ImageView(context);
            switch (i % 3) {
                case 0: view.setImageResource(R.drawable.gift_1);break;
                case 1: view.setImageResource(R.drawable.gift_2);break;
                case 2: view.setImageResource(R.drawable.gift_3);break;
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Adjust.Gift.size, Adjust.Gift.size);
            view.setLayoutParams(params);
            gifts.add(new Gift(
                    new ParticlesGeometry.Particle(position),
                    new ViewOnSpring(view, Adjust.Gift.size)
            ));
        }

        return new GiftsController(gifts);
    }

    public GiftsController(ArrayList<Gift> gifts) {
        this.gifts = gifts;
    }

    final ArrayList<Gift> gifts;

    public void handleHeaderGeometryChange(HeaderGeometry headerGeometry) {
        if (headerGeometry.scrollDownOffset == null || headerGeometry.size == null) {
            return;
        }
        float scrollDownProgress = headerGeometry.scrollDownProgress;
        CartesianCoordinates middle = headerGeometry.size
                .scale(0.5f)
                .multiply(new CartesianCoordinates(1, Adjust.Header.verticalCenterShift))
                .add(marginShift);

        TimingFunction.Delay delay = new TimingFunction.Delay(0);
        TimingFunction timingFunction = new TimingFunction.Compound(
                delay,
                new TimingFunction.SpeedUp(3.5f),
                TimingFunction.Bezier.easeIn
            );

        for (Gift gift : gifts) {
            delay.delay = (gift.particle.position.length - 1.2f) * 0.2f;
            float time = timingFunction.execute(scrollDownProgress);
            float twist = time * 1.1f;
            float spread = 1 - time;
            PolarCoordinates viewPosition = gift.particle.position
                    .scaled(spread * Adjust.Header.particlesSpread)
                    .twisted(twist);
            CartesianCoordinates cartesian = viewPosition.toCartesian();
            CartesianCoordinates position = cartesian.add(middle);
            gift.currentPosition = position;
            gift.viewOnSpring.moveSpring(position);
        }
    }
}

