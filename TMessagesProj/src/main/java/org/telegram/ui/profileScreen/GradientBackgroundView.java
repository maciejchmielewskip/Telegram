package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class GradientBackgroundView extends FrameLayout {
    private static final int CENTER_COLOR = 0xFF868FF2;
    private static final int EDGE_COLOR = 0xFF865FD3;
    private static final int MIDDLE_COLOR = 0xFF857AE4;

    private Paint paint = new Paint();

    public GradientBackgroundView(Context context) {
        super(context);
    }

    public GradientBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        if (width > 0 && height > 0) {
            float centerX = width / 2f;
            float centerY = height / 2f;
            float radius  = Math.max(width, height) * 0.61f;

            RadialGradient gradient = new RadialGradient(
                    centerX,
                    centerY,
                    radius,
                    new int[] { CENTER_COLOR, MIDDLE_COLOR, EDGE_COLOR },
                    new float[] { 0f, 0.3f, 1f },

                    Shader.TileMode.CLAMP
            );

            paint.setShader(gradient);
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);
        }

        super.dispatchDraw(canvas);
    }
}