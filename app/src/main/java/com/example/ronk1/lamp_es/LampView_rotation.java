package com.example.ronk1.lamp_es;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Ronk1 on 30/03/18.
 */

public class LampView_rotation extends View {

    private Paint paint1;
    private float angle;
    private Path path1;
    private int pref_width, pref_height;

    public LampView_rotation(Context ctx) {

        this(ctx, null, 0);
    }

    public LampView_rotation(Context ctx, AttributeSet attrs) {

        this(ctx, attrs, 0);

    }

    public LampView_rotation(Context ctx, AttributeSet attrs, int theme) {

        super(ctx, attrs, theme);
        paint1 = new Paint();
        paint1.setColor(0xFF3F51B5);
        paint1.setStrokeWidth(3.0f);
        paint1.setStyle(Paint.Style.STROKE);

        //paint1.setPathEffect(new CornerPathEffect(15));
        //paint1.setShadowLayer(25.0f, 7, 9, 0x80000000);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(metrics);
        pref_height = metrics.heightPixels;
        pref_width = metrics.widthPixels;

        path1 = new Path();
        setMinimumWidth(100);
        setMinimumHeight(100);
    }

    public void setAngle(float angle) {

        if(angle>= 0 && angle<=180 && this.angle != angle) {
            this.angle = angle;
            invalidate();
        }
    }

    public float getAngle() {
        return this.angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float h = canvas.getHeight();
        float w = canvas.getWidth();
        float l = Math.min(w,h)*0.8f;
        double rad = Math.toRadians(angle);
        path1.rewind();
        path1.moveTo(w/2, h/2);
        path1.addCircle(w/2, h/2, 5, Path.Direction.CW);
        path1.lineTo((float)(w/2 - l/3), (float)(h/2));
        path1.moveTo(w/2, h/2);
        path1.lineTo((float)(w/2 +l/3), (float)(h/2));

        canvas.save();
        canvas.rotate(angle + 90, w/2, h/2);
        canvas.drawPath(path1, paint1);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(pref_width, widthSize);
        } else {
            //Be whatever you want
            width = pref_width;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(pref_height, heightSize);
        } else {
            //Be whatever you want
            height = pref_height;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

}
