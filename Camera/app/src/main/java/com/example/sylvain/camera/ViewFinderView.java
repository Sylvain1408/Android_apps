package com.example.sylvain.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by sylvain on 19/01/16.
 */
public class ViewFinderView extends View {

    private int H;
    private int W;

    public ViewFinderView(Context context) {
        super(context);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        W = w;
        H = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint painter = new Paint();
        painter.setColor(0x60FF0000);
        painter.setStrokeWidth(5);
        canvas.drawLine(0,H/2,W,H/2,painter);
    }
}
