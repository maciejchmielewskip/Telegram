package org.telegram.ui.profileScreen;
import android.view.View;
import android.view.animation.PathInterpolator;

import java.util.ArrayList;
import java.util.List;

class ParticlesGeometry {
    public static class Particle {
        final PolarCoordinates position;

        public Particle(PolarCoordinates position) {
            this.position = position;
        }
    }

    private Double progress;
    private Double cx;
    private Double cy;

//    private List<SpringItem> items = new ArrayList<>();

//    public GiftsGeometry(RedRectangleView container) {
//        double[][] giftData = {
//                {342.5, 133.5},
//                {231.5, 252.5},
//                {332.5, 351.5},
//                {762.5, 332.5},
//                {863.5, 223.5},
//                {746.5, 141.5},
//        };
//
//        final double cx = 540;
//        final double cy = 260;
//        for (double[] gift : giftData) {
//            double x = gift[0];
//            double y = gift[1];
//            double radians = Math.atan2(y - cy, x - cx);
//            double distance = Math.hypot(x - cx, y - cy);
//            View v1 = new View(container.getContext());
//            v1.setBackgroundColor(0xFF00FFFF); // white
//            SpringItem item = container.addItem(v1, Adjust.stampSize, Adjust.stampSize, 0, 0, radians, (int)distance);
//            items.add(item);
//        }
//    }

//    public void updateProgress(double progress) {
//        progress = 1 - progress;
//        progress *= 5.0;
//        var interpolator = new PathInterpolator(0.8f, 0, 1, -0.1f);
//        float eased = interpolator.getInterpolation((float)progress);
//
//        eased = 1 - eased;
//
//
//        this.progress = (double)eased;
//        this.updateItems();
//    }

//    public void updateSize(double width, double height) {
//        this.cx = width / 2;
//        this.cy = height / 2 * Adjust.verticalCenterShift;
//        this.updateItems();
//    }

//    private void updateItems() {
//        if (this.progress == null) {
//            return;
//        }
//        if (this.cx == null) {
//            return;
//        }
//        if (this.cy == null) {
//            return;
//        }
//
//        for (SpringItem item : items) {
//            double distance = item.distance * progress;
//            double angle = item.angle;
//            double x = cx + distance * Math.cos(angle);
//            double y = cy + distance * Math.sin(angle);
//
//            item.move((int)x, (int)y);
//        }
//    }

    // Optional, in case you want to expose the items list
//    public List<SpringItem> getItems() {
//        return items;
//    }
}