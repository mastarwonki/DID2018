package com.example.ronk1.lamp_es;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Ronk1 on 10/12/17.
 */

public class LampView extends View {


    private Paint paint1;
    private float angle = 90;
    private Path path1;

    public LampView(Context ctx) {

        this(ctx, null, 0);
    }

    public LampView(Context ctx, AttributeSet attrs) {

        this(ctx, attrs, 0);

    }

    public LampView(Context ctx, AttributeSet attrs, int theme) {

        super(ctx, attrs, theme);
        paint1 = new Paint();
        paint1.setColor(0xffff0000);
        paint1.setStrokeWidth(3.0f);
        paint1.setStyle(Paint.Style.STROKE);

        //paint1.setPathEffect(new CornerPathEffect(15));
        paint1.setShadowLayer(25.0f, 7, 9, 0x80000000);

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
        path1.moveTo((float)(w/2-l/3+l/4*Math.cos(Math.PI - rad)), (float)(h/2+l/4*Math.sin(Math.PI - rad)));
        path1.lineTo(w/2-l/3, h/2);
        path1.lineTo(w/2-l/3, h/2-l/4);
        path1.lineTo(w/2+l/3, h/2-l/4);
        path1.lineTo(w/2+l/3, h/2);
        path1.lineTo((float)(w/2+l/3+l/4*Math.cos(rad)), (float)(h/2+l/4*Math.sin(rad)));
        path1.addCircle(w/2+l/3, h/2, 5, Path.Direction.CW);
        path1.addCircle(w/2-l/3, h/2, 5, Path.Direction.CW);

        canvas.drawPath(path1, paint1);
        /*
        canvas.drawLine(w/2-l/3, h/2-l/4, w/2+l/3, h/2-l/4, paint1);
        canvas.drawLine(w/2-l/3, h/2-l/4, w/2-l/3, h/2, paint1);
        canvas.drawLine(w/2+l/3, h/2-l/4, w/2+l/3, h/2, paint1);
        canvas.drawCircle(w/2+l/3, h/2, 5, paint1);
        canvas.drawCircle(w/2-l/3, h/2, 5, paint1);
/*
        //double rad = Math.toRadians(angle);
        canvas.drawLine(w/2+l/3, h/2, (float)(w/2+l/3+l/4*Math.cos(rad)), (float)(h/2+l/4*Math.sin(rad)), paint1);
        canvas.drawLine(w/2-l/3, h/2, (float)(w/2-l/3+l/4*Math.cos(Math.PI - rad)), (float)(h/2+l/4*Math.sin(Math.PI - rad)), paint1);

        */
    }

}
