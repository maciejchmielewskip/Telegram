package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class Notch {
    public float cx, cy, r;
    public MetaballBlob.Ball ball;

    public Notch(float cx, float cy, float r, MetaballBlob.Ball ball) {
        this.cx = cx;
        this.cy = cy;
        this.r = r;
        this.ball = ball;
    }

    public void layout(int offset) {
        ball.x = cx;
        ball.y = cy + offset;
        ball.r = r;
    }

    public static List<Notch> resolveNotches(Context context, WindowInsets insets) {
        List<Notch> notches = new ArrayList<>();

        if (insets == null || Build.VERSION.SDK_INT < 28) return notches;

        DisplayCutout cutout = insets.getDisplayCutout();
        if (cutout == null) return notches;
        List<Rect> cutoutRects = cutout.getBoundingRects();
        if (cutoutRects == null) return notches;

        int logicalWidth = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            logicalWidth = context.getSystemService(WindowManager.class)
                    .getCurrentWindowMetrics().getBounds().width();
        }
        if (logicalWidth == 0) {
            return notches;
        }

        for (Rect rect : cutoutRects) {
            float holePhysicalCenterX = rect.left + rect.width() / 2f;
            float physicalWidth = rect.left + rect.right;
            float ratio = physicalWidth / (float) logicalWidth;
            float logicalCx = holePhysicalCenterX / ratio;
            float logicalCy = rect.top + rect.height() - ((float)Math.min(rect.width(), rect.height()) / 2);
            float logicalRadius = Math.min(rect.width(), rect.height()) / 2f;

            Notch notch = new Notch(
                    logicalCx,
                    logicalCy,
                    logicalRadius,
                    new MetaballBlob.Ball(0, 0, 0)
            );
            notches.add(notch);
        }

        return notches;
    }
}