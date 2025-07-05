package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class GiftsViews {
    public final List<GiftView> giftViews = new ArrayList<>(6);

    public GiftsViews(Context context, int currentAccount, long dialogId, FrameLayout avatarContainer, ProfileActivity.AvatarImageView avatarImage, Theme.ResourcesProvider resourcesProvider) {
        giftViews.add(new GiftView(context, 0, currentAccount, dialogId, resourcesProvider));
        giftViews.add(new GiftView(context, 1, currentAccount, dialogId, resourcesProvider));
        giftViews.add(new GiftView(context, 2, currentAccount, dialogId, resourcesProvider));
        giftViews.add(new GiftView(context, 3, currentAccount, dialogId, resourcesProvider));
        giftViews.add(new GiftView(context, 4, currentAccount, dialogId, resourcesProvider));
        giftViews.add(new GiftView(context, 5, currentAccount, dialogId, resourcesProvider));
    }

    public void setExpandProgress(float v) {
        for (GiftView g : giftViews) {
            g.setExpandProgress(v);
        }
    }

    public void setActionBarActionMode(float value) {
        for (GiftView g : giftViews) {
            g.setActionBarActionMode(value);
        }
    }

    public void setExpandCoords(int i, boolean writeButtonVisible, float v) {
        for (GiftView g : giftViews) {
            g.setExpandCoords(i, writeButtonVisible, v);
        }
    }

    public void invalidate() {
        for (GiftView g : giftViews) {
            g.invalidate();
        }
    }

    public void update() {
        for (GiftView g : giftViews) {
            g.update();
        }
    }

    public void setAlpha(float v) {
        for (GiftView g : giftViews) {
            g.setAlpha(v);
        }
    }

    public void setBounds(float aleft, float aright, float v, boolean b) {
        for (GiftView g : giftViews) {
            g.setBounds(aleft, aright, v, b);
        }
    }

    public void setProgressToStoriesInsets(float avatarAnimationProgress) {
        for (GiftView g : giftViews) {
            g.setProgressToStoriesInsets(avatarAnimationProgress);
        }
    }

    public void animateAlpha(ArrayList<Animator> animators, float v) {
        for (GiftView g : giftViews) {
            animators.add(ObjectAnimator.ofFloat(g, View.ALPHA, v));
        }
    }
}
