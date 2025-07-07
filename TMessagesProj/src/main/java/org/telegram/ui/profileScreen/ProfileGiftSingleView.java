package org.telegram.ui.profileScreen;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.ui.Stars.StarsController.findAttribute;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import androidx.annotation.NonNull;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Stars.StarsController;

import java.util.ArrayList;
import java.util.HashSet;

public class ProfileGiftSingleView extends View {

    private final int currentAccount;
    private final long dialogId;

    public ProfileGiftSingleView(Context context, int currentAccount, long dialogId) {
        super(context);
        this.currentAccount = currentAccount;
        this.dialogId = dialogId;
    }

    public final class Gift {
        public final long id;
        public final TLRPC.Document document;
        public final long documentId;
        public final int color;
        public final String slug;

        public Gift(TL_stars.TL_starGiftUnique gift) {
            id = gift.id;
            document = gift.getDocument();
            documentId = document == null ? 0 : document.id;
            final TL_stars.starGiftAttributeBackdrop backdrop = findAttribute(gift.attributes, TL_stars.starGiftAttributeBackdrop.class);
            color = backdrop.center_color | 0xFF000000;
            slug = gift.slug;
        }

        public Gift(TLRPC.TL_emojiStatusCollectible status) {
            id = status.collectible_id;
            document = null;
            documentId = status.document_id;
            color = status.center_color | 0xFF000000;
            slug = status.slug;
        }

        public RadialGradient gradient;
        public final Matrix gradientMatrix = new Matrix();
        public Paint gradientPaint;
        public AnimatedEmojiDrawable emojiDrawable;
        public final RectF bounds = new RectF();

        public void draw(Canvas canvas, float cx, float cy) {
            final float gsz = dp(45);
            bounds.set(cx - gsz / 2, cy - gsz / 2, cx + gsz / 2, cy + gsz / 2);
            canvas.save();
            canvas.translate(cx, cy);
            if (gradientPaint != null) {
                gradientPaint.setAlpha(0xFF);
                canvas.drawRect(-gsz / 2.0f, -gsz / 2.0f, gsz / 2.0f, gsz / 2.0f, gradientPaint);
            }
            if (emojiDrawable != null) {
                final int sz = dp(24);
                emojiDrawable.setBounds(-sz / 2, -sz / 2, sz / 2, sz / 2);
                emojiDrawable.setAlpha(0xFF);
                emojiDrawable.draw(canvas);
            }
            canvas.restore();
        }
    }

    private final ArrayList<Gift> gifts = new ArrayList<>();
    private final HashSet<Long> giftIds = new HashSet<>();
    private int giftIndex = 0;

    public void setGiftIndex(int idx) {
        if (giftIndex != idx) {
            giftIndex = idx;
            invalidate();
        }
    }

    public int getGiftIndex() {
        return giftIndex;
    }

    public void update() {
        gifts.clear();
        giftIds.clear();
        final TLRPC.EmojiStatus emojiStatus;
        if (dialogId >= 0) {
            final TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(dialogId);
            emojiStatus = user == null ? null : user.emoji_status;
        } else {
            final TLRPC.User chat = MessagesController.getInstance(currentAccount).getUser(-dialogId);
            emojiStatus = chat == null ? null : chat.emoji_status;
        }
        if (emojiStatus instanceof TLRPC.TL_emojiStatusCollectible) {
            giftIds.add(((TLRPC.TL_emojiStatusCollectible) emojiStatus).collectible_id);
        }
        StarsController.GiftsList list = StarsController.getInstance(currentAccount).getProfileGiftsList(dialogId);
        if (list != null) {
            for (int i = 0; i < list.gifts.size(); i++) {
                TL_stars.SavedStarGift savedGift = list.gifts.get(i);
                if (!savedGift.unsaved && savedGift.pinned_to_top && savedGift.gift instanceof TL_stars.TL_starGiftUnique) {
                    Gift gift = new Gift((TL_stars.TL_starGiftUnique) savedGift.gift);
                    if (!giftIds.contains(gift.id)) {
                        gifts.add(gift);
                        giftIds.add(gift.id);
                    }
                }
            }
        }
        for (Gift g : gifts) {
            g.gradient = new RadialGradient(0, 0, dp(22.5f), new int[]{g.color, Theme.multAlpha(g.color, 0.0f)}, new float[]{0, 1}, Shader.TileMode.CLAMP);
            g.gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            g.gradientPaint.setShader(g.gradient);
            if (g.document != null) {
                g.emojiDrawable = AnimatedEmojiDrawable.make(currentAccount, AnimatedEmojiDrawable.CACHE_TYPE_MESSAGES, g.document);
            } else {
                g.emojiDrawable = AnimatedEmojiDrawable.make(currentAccount, AnimatedEmojiDrawable.CACHE_TYPE_MESSAGES, g.documentId);
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (gifts.isEmpty()) return;
        if (giftIndex < 0 || giftIndex >= gifts.size()) return;
        float cx = getWidth() / 2.0f;
        float cy = getHeight() / 2.0f;
        gifts.get(giftIndex).draw(canvas, cx, cy);
    }
}