package org.telegram.ui.profileScreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MaskedView extends FrameLayout {

    private Path maskPath = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MaskedView(Context context) {
        super(context);
        init();
    }

    public MaskedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    // Set the mask path from outside
    public void setMaskPath(Path path) {
        maskPath = path;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Save layer to clip children
        int c = canvas.save();
        if (maskPath != null) {
            canvas.clipPath(maskPath);
        }
        super.dispatchDraw(canvas);
        canvas.restoreToCount(c);
    }
}