package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Path;
import android.util.Log;
import android.view.WindowInsets;

import java.util.ArrayList;
import java.util.List;

class AvatarMetaball {
    private static final float avatarBallShrink = 0.8164f; // changes with metaball threshold
    static final LinearSpace avatarSize = new LinearSpace(Adjust.Avatar.size / avatarBallShrink, Adjust.Avatar.size / avatarBallShrink * Adjust.Avatar.fadeScale);

    public final MetaballBlob notchesBlob = new MetaballBlob();
    public final MetaballBlob edgeBlob = new MetaballBlob();
    public final MetaballBlob giftsBlob = new MetaballBlob();
    private MetaballBlob.Ball avatarBall = new MetaballBlob.Ball(0, 0 + Adjust.Avatar.size / 2, Adjust.Avatar.size / 2);
    private MetaballBlob.Ball edgeBall = new MetaballBlob.Ball(0, -Adjust.Header.edgeBallSize, Adjust.Header.edgeBallSize);
    private List<Notch> nothes = new ArrayList<>();
    private final ArrayList<MetaballBlob.Ball> giftsBalls = new ArrayList<>();
    private int giftsCount = 0;

    public AvatarMetaball(Context context) {
        notchesBlob.balls.add(avatarBall);
        edgeBlob.balls.add(avatarBall);
        edgeBlob.balls.add(edgeBall);
    }

    public void insetsDidResolve(WindowInsets insets, Context context) {
        for (Notch n : nothes) {
            notchesBlob.balls.remove(n.ball);
        }
        nothes.clear();

        List<Notch> foundNotches = Notch.resolveNotches(context, insets);

        for (Notch notch : foundNotches) {
            notch.layout(0);
            notchesBlob.balls.add(notch.ball);
            nothes.add(notch);
        }
    }

    public Path handleHeaderGeometryChange(HeaderGeometry headerGeometry, List<Gift> gifts) {
        Float offset = headerGeometry.scrollDownOffset;
        float scrollDownProgress = headerGeometry.scrollDownProgress;

        avatarBall.x = headerGeometry.size.x / 2;
        avatarBall.y = (headerGeometry.size.y / 2 * Adjust.Header.verticalCenterShift) - 3;
        avatarBall.r = LinearSpace.unit.convert(scrollDownProgress, AvatarMetaball.avatarSize) / 2;

        edgeBall.x = headerGeometry.size.x / 2;
        edgeBall.y = -edgeBall.r + offset;

        for (Notch notch : nothes) {
            notch.layout(offset.intValue());
        }

        giftsBlob.balls.clear();
        giftsBlob.balls.add(avatarBall);
        giftsBalls.clear();
        for (Gift gift : gifts) {
            giftsBalls.add(new MetaballBlob.Ball(
                    gift.currentPosition.x,
                    gift.currentPosition.y - Adjust.Header.topMargin,
                    Adjust.Gift.size / 3.5f
            ));
        }
        updateGiftsCount(this.giftsCount);

        return getPath(headerGeometry);

    }

    private Path getPath(HeaderGeometry headerGeometry) {
        int width = (int)headerGeometry.size.x;
        int height = (int)headerGeometry.size.y;

        Path unionPath = new Path();
        for (Path p : notchesBlob.computePaths(width, height)) {
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                unionPath.op(p, Path.Op.UNION);
            } else {
                unionPath.addPath(p);
            }
        }
        for (Path p : edgeBlob.computePaths(width, height)) {
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                unionPath.op(p, Path.Op.UNION);
            } else {
                unionPath.addPath(p);
            }
        }
        for (Path p : giftsBlob.computePaths(width, height)) {
            if (android.os.Build.VERSION.SDK_INT >= 19) {
                unionPath.op(p, Path.Op.UNION);
            } else {
                unionPath.addPath(p);
            }
        }
        return unionPath;
    }

    public void updateGiftsCount(int count) {
        this.giftsCount = count;
        if (giftsBalls.isEmpty()) {
            return;
        }
        giftsBlob.balls.clear();
        giftsBlob.balls.add(avatarBall);
        if (count == 0) {
            return;
        }
        for (int i = 0; i < Math.min(count, 6); i++) {
            giftsBlob.balls.add(giftsBalls.get(i));
        }
    }
}