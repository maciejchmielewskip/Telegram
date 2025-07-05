package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

public class ExpandingAvatar extends View {
    private static final int blurHeight = 270;
    private static final int blurFade = 180;

    private Bitmap avatarImage;
    private float circleMidX, circleMidY, circleRadius;
    private RectF rectBounds = new RectF();
    private float progress = 0f;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path clipPath = new Path();
    private Bitmap blurredBottomAvatar;
    private Bitmap blurredFadeAvatar;
    private final float BLUR_RADIUS = 25f;
    private int blurAlpha = 0;

    public ExpandingAvatar(Context context) {
        super(context);
    }

    public ExpandingAvatar(Context context, Bitmap avatar, float midX, float midY, float radius, float rectX, float rectY, float rectW, float rectH) {
        super(context);
        this.avatarImage = avatar;
        this.circleMidX = midX;
        this.circleMidY = midY;
        this.circleRadius = radius;
        this.rectBounds.set(rectX, rectY, rectX + rectW, rectY + rectH);
        updateBlurredAvatar();
    }

    public void setAvatarImage(Bitmap bmp) {
        avatarImage = bmp;
        updateBlurredAvatar();
        invalidate();
    }

    public void setCircleState(float midX, float midY, float radius) {
        circleMidX = midX;
        circleMidY = midY;
        circleRadius = radius;
        invalidate();
    }

    public void setRectState(float x, float y, float w, float h) {
        rectBounds.set(x, y, x + w, y + h);
        invalidate();
    }

    public void setProgress(float p) {
        progress = Math.max(0f, Math.min(1f, p));
        invalidate();
    }

    public void updateBlur(double progress) {
        blurAlpha = (int)(progress * 255);
        invalidate();
    }

    private void updateBlurredAvatar() {
        if (avatarImage == null) {
            blurredBottomAvatar = null;
            blurredFadeAvatar = null;
            return;
        }
        int width = avatarImage.getWidth();
        int height = avatarImage.getHeight();
        int blurSolidHeight = Math.min(blurHeight, height);
        int fadeHeight = Math.min(blurFade, height - blurSolidHeight);
        int totalBlurHeight = blurSolidHeight + fadeHeight;
        if (totalBlurHeight <= 0) {
            blurredFadeAvatar = avatarImage.copy(Bitmap.Config.ARGB_8888, true);
            return;
        }
        Bitmap blurSrc = Bitmap.createBitmap(avatarImage, 0, height - totalBlurHeight, width, totalBlurHeight);

        int scaleFactor = 4;
        Bitmap smallBlurSrc = Bitmap.createScaledBitmap(blurSrc, width / scaleFactor, totalBlurHeight / scaleFactor, true);
        Bitmap smallBlurred = blur(getContext(), smallBlurSrc, BLUR_RADIUS);
        Bitmap blurred = Bitmap.createScaledBitmap(smallBlurred, width, totalBlurHeight, true);

        Bitmap mask = Bitmap.createBitmap(width, totalBlurHeight, Bitmap.Config.ARGB_8888);
        Canvas maskCanvas = new Canvas(mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawRect(0, fadeHeight, width, totalBlurHeight, paint);
        if (fadeHeight > 0) {
            LinearGradient gradient = new LinearGradient(
                    0, 0, 0, fadeHeight,
                    0x00FFFFFF, 0xFFFFFFFF,
                    Shader.TileMode.CLAMP
            );
            paint.setShader(gradient);
            maskCanvas.drawRect(0, 0, width, fadeHeight, paint);
            paint.setShader(null);
        }

        Bitmap maskedBlur = Bitmap.createBitmap(width, totalBlurHeight, Bitmap.Config.ARGB_8888);
        Canvas maskedCanvas = new Canvas(maskedBlur);
        maskedCanvas.drawBitmap(blurred, 0, 0, null);
        Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        maskedCanvas.drawBitmap(mask, 0, 0, maskPaint);
        maskPaint.setXfermode(null);

        blurredFadeAvatar = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(blurredFadeAvatar);
        resultCanvas.drawBitmap(avatarImage, 0, 0, null);
        resultCanvas.drawBitmap(maskedBlur, 0, height - totalBlurHeight, null);
    }

    private static Bitmap blur(Context context, Bitmap image, float radius) {
        Bitmap input = image.copy(Bitmap.Config.ARGB_8888, true);
        RenderScript rs = RenderScript.create(context);
        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createTyped(rs, inputAlloc.getType());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(inputAlloc);
        script.forEach(outputAlloc);
        outputAlloc.copyTo(input);
        rs.destroy();
        return input;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float left = lerp(circleMidX - circleRadius, rectBounds.left, progress);
        float top = lerp(circleMidY - circleRadius, rectBounds.top, progress);
        float right = lerp(circleMidX + circleRadius, rectBounds.right, progress);
        float bottom = lerp(circleMidY + circleRadius, rectBounds.bottom, progress);
        float width = right - left;
        float height = bottom - top;
        float r = ((width < height ? width : height) / 2f) * (1f - progress);
        clipPath.reset();
        if (progress < 1f) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                clipPath.addRoundRect(left, top, right, bottom, r, r, Path.Direction.CW);
            }
        } else {
            clipPath.addRect(left, top, right, bottom, Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(clipPath);
        if (avatarImage != null) {
            RectF dst = new RectF(left, top, right, bottom);
            float imgW = avatarImage.getWidth();
            float imgH = avatarImage.getHeight();
            float scale;
            float dx = 0, dy = 0;
            if (imgW / imgH > width / height) {
                scale = height / imgH;
                dx = (width - imgW * scale) / 2;
            } else {
                scale = width / imgW;
                dy = (height - imgH * scale) / 2;
            }
            float drawLeft = left + dx;
            float drawTop = top + dy;
            float drawRight = drawLeft + imgW * scale;
            float drawBottom = drawTop + imgH * scale;
            RectF drawRect = new RectF(drawLeft, drawTop, drawRight, drawBottom);

            canvas.save();
            canvas.clipRect(drawLeft, drawTop, drawRight, drawBottom);
            paint.setAlpha(255);
            canvas.drawBitmap(avatarImage, null, drawRect, paint);
            if (blurredFadeAvatar != null && blurAlpha > 0) {
                int saveAlpha = paint.getAlpha();
                paint.setAlpha(blurAlpha);
                canvas.drawBitmap(blurredFadeAvatar, null, drawRect, paint);
                paint.setAlpha(saveAlpha);
            }
            canvas.restore();
        }
        canvas.restore();
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public void handleHeaderGeometryChange(HeaderGeometry headerGeometry) {
        float scrollUpProgress = headerGeometry.scrollUpProgress;
        if (scrollUpProgress <= 0) {
            this.setVisibility(INVISIBLE);
        } else {
            this.setVisibility(VISIBLE);
            circleMidX = headerGeometry.size.x / 2;
            circleMidY = (Adjust.Header.height / 2 * Adjust.Header.verticalCenterShift) + Adjust.Header.topMargin;
            rectBounds.right = headerGeometry.size.x;
            rectBounds.bottom = headerGeometry.size.x * Adjust.Header.expandedRatio;
            float progress = TimingFunction.Bezier.easeInOut.execute(scrollUpProgress);
            this.setProgress(progress);
            this.updateBlur(progress);
        }
    }
}