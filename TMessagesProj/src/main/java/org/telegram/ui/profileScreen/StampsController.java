package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.dynamicanimation.animation.SpringForce;

//import org.telegram.messenger.R;

import java.util.ArrayList;

class Stamp {
    final ParticlesGeometry.Particle particle;
    final ViewOnSpring viewOnSpring;

    public Stamp(ParticlesGeometry.Particle particle, ViewOnSpring viewOnSpring) {
        this.particle = particle;
        this.viewOnSpring = viewOnSpring;
    }
}

public class StampsController {
    private static final CartesianCoordinates marginShift = new CartesianCoordinates(0, Adjust.Header.topMargin);

    static StampsController make(Context context, int color) {
        ArrayList<Stamp> stamps = new ArrayList<Stamp>();
        double[][] foxData = {
                {535.5,108.5},
                {674.5,68.5},
                {827.5,140.5},
                {712.5,201.5},
                {938.5,305.5},
                {794.5,303.5},
                {717.5,406.5},
                {839.5,472.5},
                {671.5,539.5},
                {539.5,495.5},
                {407.5,539.5},
                {239.5,472.5},
                {361.5,406.5},
                {404.5,68.5},
                {366.5,201.5},
                {284.5,304.5},
                {251.5,140.5},
                {140.5,304.5},
        };

        for (double[] fox : foxData) {
            double x = fox[0];
            double y = fox[1];
            double radians = Math.atan2(y - 304, x - 540);
            double distance = Math.hypot(x - 540, y - 304);
            PolarCoordinates position = new PolarCoordinates((float)radians, (float)distance / 300);
            ImageView imageView = new ImageView(context);
            imageView.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Adjust.Stamp.size, Adjust.Stamp.size);
            imageView.setLayoutParams(params);
            stamps.add(new Stamp(
                    new ParticlesGeometry.Particle(position),
                    new ViewOnSpring(
                            imageView,
                            Adjust.Stamp.size,
                            SpringForce.DAMPING_RATIO_LOW_BOUNCY,
                            SpringForce.STIFFNESS_HIGH
                    )
            ));
        }

        return new StampsController(stamps, color);
    }

    public StampsController(ArrayList<Stamp> stamps, int color) {
        this.stamps = stamps;
        this.color = color;
    }

    final ArrayList<Stamp> stamps;
    private final int color;

    public void handleHeaderGeometryChange(HeaderGeometry headerGeometry) {
        if (headerGeometry.scrollDownOffset == null || headerGeometry.size == null) {
            return;
        }
        Float scrollDownProgress = headerGeometry.scrollDownProgress;
        CartesianCoordinates middle = headerGeometry.size
                .scale(0.5f)
                .multiply(new CartesianCoordinates(1, Adjust.Header.verticalCenterShift))
                .add(marginShift);

        TimingFunction.Delay delay = new TimingFunction.Delay(0);
        TimingFunction.Compound timingFunction = new TimingFunction.Compound(
                delay,
                new TimingFunction.SpeedUp(3.5f),
                TimingFunction.Bezier.easeIn
        );

        for (Stamp stamp : stamps) {
            delay.delay = (stamp.particle.position.length - 1.5f) * 0.2f;
            float time = timingFunction.execute(scrollDownProgress);
            float spread = 1 - time;

            PolarCoordinates viewPosition = stamp.particle.position.scaled(spread * Adjust.Header.particlesSpread);
            CartesianCoordinates cartesian = viewPosition.toCartesian();
            CartesianCoordinates position = cartesian.add(middle);
            stamp.viewOnSpring.moveSpring(position);
        }
    }

    public void updateStampBitmap(Bitmap bitmap) {
        for (Stamp stamp : stamps) {
            ((ImageView)stamp.viewOnSpring.view).setImageBitmap(bitmap);
        }
    }

    public void updateStampDrawable(Drawable drawable) {
        for (Stamp stamp : stamps) {
            ((ImageView)stamp.viewOnSpring.view).setImageDrawable(drawable);
        }
    }
}

