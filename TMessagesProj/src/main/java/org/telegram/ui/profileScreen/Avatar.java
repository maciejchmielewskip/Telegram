package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class Avatar extends AppCompatImageView {
    private static final LinearSpace avatarScale = new LinearSpace(1, Adjust.Avatar.fadeScale);

    private Bitmap originalBitmap;
    private Bitmap blurredBitmap;
    private float blurAlpha = 0f; // 0 = clear, 1 = full blur
    private Paint blurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float overlayAlpha = 0f; // 0 = no overlay, 1 = fully black
    private Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap tempMosaicBitmap;
    private Canvas tempMosaicCanvas;
    private Paint maskPaint;

    // For black overlay with soft edge
    private Bitmap overlayBitmap;
    private Canvas overlayCanvas;

    public Avatar(Context context) { super(context); }
    public Avatar(Context context, AttributeSet attrs) { super(context, attrs); }
    public Avatar(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); }

    @Override
    protected void onDraw(Canvas canvas) {
        if (originalBitmap == null || blurredBitmap == null) {
            super.onDraw(canvas);
            return;
        }

        int width = getWidth();
        int height = getHeight();

        // 1. Prepare an offscreen bitmap and canvas (size of your View)
        if (tempMosaicBitmap == null || tempMosaicBitmap.getWidth() != width || tempMosaicBitmap.getHeight() != height) {
            tempMosaicBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            tempMosaicCanvas = new Canvas(tempMosaicBitmap);
        }
        tempMosaicBitmap.eraseColor(Color.TRANSPARENT);

        // 2. DRAW all your mosaic stuff to tempMosaicCanvas (not canvas)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float left = i * Adjust.Avatar.size;
                float top = j * Adjust.Avatar.size;
                float right = (i + 1) * Adjust.Avatar.size;
                float bottom = (j + 1) * Adjust.Avatar.size;
                float centerX = left + Adjust.Avatar.size / 2;
                float centerY = top + Adjust.Avatar.size / 2;
                Matrix matrix = new Matrix();
                int sx = 1, sy = 1;
                if (i == 0 || i == 2) sx = -1;
                if (j == 0 || j == 2) sy = -1;
                matrix.postScale(sx, sy, centerX, centerY);

                RectF destRect = new RectF(left, top, right, bottom);

                tempMosaicCanvas.save();
                tempMosaicCanvas.concat(matrix);
                tempMosaicCanvas.drawBitmap(originalBitmap, null, destRect, null);
                tempMosaicCanvas.restore();

                blurPaint.setAlpha((int) (255 * blurAlpha));
                tempMosaicCanvas.save();
                tempMosaicCanvas.concat(matrix);
                tempMosaicCanvas.drawBitmap(blurredBitmap, null, destRect, blurPaint);
                tempMosaicCanvas.restore();
            }
        }

        // 3. Create A Soft Circular Mask as a Bitmap
        if (maskPaint == null) maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float radius = Math.min(width, height) * 0.25f;
        float cx = width / 2f, cy = height / 2f;
        float fadeWidth = radius * 0.2f; // width of fading edge

        // Radial gradient: opaque at center, transparent at edge
        RadialGradient gradient = new RadialGradient(
                cx, cy, radius,
                new int[] { 0xFFFFFFFF, 0x00FFFFFF },
                new float[] { 1.0f - (fadeWidth / radius), 1.0f },
                Shader.TileMode.CLAMP
        );
        maskPaint.setShader(gradient);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // 4. Mask the avatar mosaic
        Canvas maskCanvas = new Canvas(tempMosaicBitmap);
        maskCanvas.drawRect(0, 0, width, height, maskPaint);

        // 5. Draw masked avatar mosaic onto view canvas
        canvas.drawBitmap(tempMosaicBitmap, 0, 0, null);

        maskPaint.setXfermode(null);

        // 6. Black overlay: masked the same way (draw to temp bitmap, mask, then draw)
        if (overlayAlpha > 0f) {
            // Prepare overlay bitmap
            if (overlayBitmap == null || overlayBitmap.getWidth() != width || overlayBitmap.getHeight() != height) {
                overlayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                overlayCanvas = new Canvas(overlayBitmap);
            }
            overlayBitmap.eraseColor(Color.TRANSPARENT);

            // Draw solid black circle
            overlayPaint.setColor(Color.BLACK);
            overlayPaint.setAlpha((int) (255 * overlayAlpha));
            overlayCanvas.drawCircle(cx, cy, radius, overlayPaint);

            // Mask overlay with radial gradient (DST_IN)
            Paint overlayMaskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            overlayMaskPaint.setShader(gradient);
            overlayMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            overlayCanvas.drawRect(0, 0, width, height, overlayMaskPaint);
            overlayMaskPaint.setXfermode(null);

            // Draw the masked overlay onto the main canvas
            canvas.drawBitmap(overlayBitmap, 0, 0, null);
        }
    }

    private void prepareBlurredBitmap() {
        Bitmap srcBitmap = getBitmapFromDrawable();
        if (srcBitmap != null) {
            originalBitmap = srcBitmap;
            blurredBitmap = createBlurredBitmap(srcBitmap);
            invalidate();
        }
    }

    private void updateBlur(float alpha) {
        if (originalBitmap == null || blurredBitmap == null) {
            prepareBlurredBitmap();
        }
        blurAlpha = alpha;
        invalidate();
    }

    private void updateOverlay(float opacity) {
        overlayAlpha = Math.max(0f, Math.min(1f, opacity));
        invalidate();
    }

    private Bitmap getBitmapFromDrawable() {
        Drawable drawable = getDrawable();
        if (drawable == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(Adjust.Avatar.size, Adjust.Avatar.size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, Adjust.Avatar.size, Adjust.Avatar.size);
        drawable.draw(canvas);
        return bitmap;
    }

    private Bitmap createBlurredBitmap(Bitmap src) {
        int targetWidth = Adjust.Avatar.size / Adjust.Avatar.blurStrength;
        int targetHeight = targetWidth;
        Bitmap scaledDownBitmap = Bitmap.createScaledBitmap(src, targetWidth, targetHeight, false);
        Bitmap inputBitmap = Bitmap.createScaledBitmap(scaledDownBitmap, Adjust.Avatar.size, Adjust.Avatar.size, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(getContext());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation inAlloc = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation outAlloc = Allocation.createFromBitmap(rs, outputBitmap);

        blurScript.setRadius(25f);
        blurScript.setInput(inAlloc);
        blurScript.forEach(outAlloc);
        outAlloc.copyTo(outputBitmap);

        rs.destroy();

        scaledDownBitmap.recycle();
        inputBitmap.recycle();

        return outputBitmap;
    }

    public void handleHeaderGeometryChange(HeaderGeometry headerGeometry) {
        Float scrollDownProgress = headerGeometry.scrollDownProgress;
        float scale = LinearSpace.unit.convert(scrollDownProgress, Avatar.avatarScale);
        this.setScaleX(scale);
        this.setScaleY(scale);

        float blur = scrollDownProgress * Adjust.Avatar.blurFadeSpeed;
        updateBlur(blur);

        float darken = Adjust.Avatar.darken.execute(scrollDownProgress);
        updateOverlay(darken);
    }
}