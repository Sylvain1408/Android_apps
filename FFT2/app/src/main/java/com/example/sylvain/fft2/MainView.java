package com.example.sylvain.fft2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by sylvain on 12/01/16.
 */
public class MainView extends View {

    int W;
    int H;
    private int displayFromBot = 20;
    private short[] buffer;
    private int signalScaling = 80;
    private float fftScaling = 1;
    private jFft jfft;
    private int[] bufColor;
    private int variation;//color variation for spectrum background
    private short R = 0,G = 0,B = 0;
    private int Idx = 0;
    float mPreviousX = 0;
    float mPreviousY = 0;
    float spaces;

    public MainView(Context context, AttributeSet attrs){
        super(context, attrs);
        jfft = new jFft(2048);
    }

    public void setVariationColor(int var){
        variation = var;
    }

    public int getFundamental(){
        return (int)jfft.getMaxValue();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        W = w;
        H = h;
        bufColor = new int[W*H];
        for(int i=0;i<bufColor.length;i++)
            bufColor[i] = 0xFF404040;
    }

    private int CToRGB(double C){
        int ic = (int)C + variation;
        if(ic < 100){
            R = 0;
            G = 0;
            B = 0;
        }
        else if(ic >= 100 && ic < 256){
            R = (short)(100-0.82*ic);
            G = 0;
            B = (short)(100-0.82*ic);
        }
        else if(ic >= 256 && ic < 512){
            R = (short)(0.5*ic-1);
            G = 0;
            B = (short)(256-0.5*ic);
        }
        else if(ic >= 512 && ic < 768){
            R = 255;
            G = (short)(ic - 512);
            B = 0;
        }
        else{
            R = 255;
            G = 255;
            B = 0;
        }
        int color = 0xFF000000;
        color += (((byte)R)&0xFF)<<16;
        color += (((byte)G)&0xFF)<<8;
        color += (((byte)B)&0xFF);
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        Bitmap bmp = Bitmap.createBitmap(bufColor,W,H,Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(bmp, 0, 0, paint);



        /***Signal***/
        if(buffer != null)
            for(int i=0;i<buffer.length-1;i++){
                canvas.drawLine(i,buffer[i]/signalScaling+H/2,i+1,buffer[i+1]/signalScaling+H/2,paint);
            }

        /***Spectrum***/
        paint.setColor(Color.RED);
        int max = jfft.getMaxIndex();
        if(buffer != null) {
            float x = 0;
            for (int i = 0; i < jfft.Out.length-1; i++) {
                x += 1;
                //canvas.drawLine(x, H-displayFromBot-(float)jfft.Out[i]/fftScaling, x+1, H-displayFromBot-(float)jfft.Out[i+1]/fftScaling,paint);
                canvas.drawLine(x, H, x, H - displayFromBot - (float) jfft.Out[i] / fftScaling, paint);
                if(i == max){
                    paint.setColor(Color.YELLOW);
                    //canvas.drawText("Freq max", x, H - displayFromBot - (float) jfft.Out[i] / fftScaling, paint);
                    canvas.drawCircle(x,H - displayFromBot - (float) jfft.Out[i] / fftScaling,10,paint);
                    paint.setColor(Color.RED);
                }
                if(i == (jfft.Out.length/2)){
                    canvas.drawText("" + (i*23.5-30), x,H/2,paint);
                }
            }
        }


        /***Frequency***/
        int fftSize = (jfft.Out.length > H) ? H : jfft.Out.length;
        paint.setColor(Color.WHITE);
        paint.setTextSize(16f);
        for(int i=0;i<fftSize;i+=fftSize/10){
            canvas.drawLine(0,i,50,i,paint);
            canvas.drawText(String.format("%d",(int)(i*23.5-30)), 0, i ,paint);
        }
    }

    public void reDraw(short[] sample){
        buffer = new short[sample.length];
        for(int i=0;i<sample.length;i++){
            buffer[i] = sample[i];
        }

        jfft.Fft(buffer);


        //From left to right
        int fftSize = (jfft.Out.length > H) ? H : jfft.Out.length;
        for(int i=0, j=Idx ; i < fftSize; i++, j+=W){
            double color = 3*256*Math.log(jfft.Out[i])/Math.log(32768);
            bufColor[Idx+j] = CToRGB(color);
        }
        if(Idx == W/2-1)
            Idx = 0;
        else
            Idx++;


        this.invalidate();
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        float dx = x - mPreviousX;
        float dy = y - mPreviousY;
        spaces += dx;
        displayFromBot -= (int)dy;
        if(spaces < 1)spaces = 1;
        if(displayFromBot < 20)displayFromBot = 20;
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }*/
}
