package com.qtt.gcenter.cameraproject.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.qtt.gcenter.cameraproject.R;


/**
 * Created by dong on 2018/5/23.
 * kl
 */

public class CircleLayerView extends View {

    private Paint paint;
    private int borderWidth;
    private Path path;
    private Xfermode xfermode;
    private RectF detectAreaRectF;
    private RectF faceRectF;

    public CircleLayerView(Context context) {
        this(context, null);
    }

    public CircleLayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.TRANSPARENT);
        //圆形边框
        int borderColor = getResources().getColor(R.color.colorAccent);
        paint = new Paint();
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//防抖动

        path = new Path();
        xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        setBorderWidth((int) (context.getResources().getDisplayMetrics().density * 4.5f));
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        paint.setStrokeWidth(this.borderWidth);
    }

    public void setFaceRectF(RectF faceRectF) {
        this.faceRectF = faceRectF;
        postInvalidate();
    }

    private void checkRectF() {
        if (detectAreaRectF == null) {
            detectAreaRectF = new RectF();
            detectAreaRectF.top = 0;
            detectAreaRectF.left = 0;
            detectAreaRectF.right = getWidth();
            detectAreaRectF.bottom = getHeight();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        checkRectF();
        canvas.saveLayer(detectAreaRectF, null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        path.addCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, Path.Direction.CCW);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xfermode);
        canvas.drawPath(path, paint);
        paint.setXfermode(null);
        canvas.restore();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - this.borderWidth / 2f, paint);
        if (faceRectF != null) {
            canvas.drawRect(faceRectF, paint);
        }
    }
}
