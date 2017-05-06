package com.example.sylvain.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by sylvain on 19/01/16.
 */
public class Display extends Activity {

    /***Intern Class***/
    class DispView extends View{

        public DispView(Context context) {

            super(context);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] pixArray = new int[image.getHeight()*image.getWidth()];
                    image.getPixels(pixArray,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());

                    byte R,G,B,grey;
                    for(int i=0 ; i<pixArray.length ; i++){
                        R = (byte)((pixArray[i]&0xFF0000)>>16);
                        G = (byte)((pixArray[i]&0xFF00)>>8);
                        B = (byte)(pixArray[i]&0xFF);

                        grey = (byte)((R*11+G*16+B*5)/32);

                        pixArray[i] = grey<<16 + grey<<8 + grey;
                    }

                    image = Bitmap.createBitmap(pixArray,0,image.getWidth(),image.getWidth(),image.getHeight(), Bitmap.Config.RGB_565);
                    v.invalidate();
                }
            });
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint p = new Paint();

            p.setAntiAlias(true);
            if(image != null){
                canvas.drawBitmap(image,new Rect(0,0,image.getWidth(),image.getHeight()),new Rect(0,0,getWidth(),getHeight()),p);
            }

        }
/*
        @Override
        public void setOnClickListener(OnClickListener l) {
            super.setOnClickListener(l);
            int[] pixArray = new int[image.getHeight()*image.getWidth()];
            image.getPixels(pixArray,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());

            short R,G,B,grey;
            for(int i=0 ; i<pixArray.length ; i++){
                R = (short)((pixArray[i]&0xFF0000)>>16);
                G = (short)((pixArray[i]&0xFF00)>>8);
                B = (short)(pixArray[i]&0xFF);

                grey = (short)((R*11+G*16+B*5)/32);

                pixArray[i] = grey<<16 + grey<<8 + grey;
            }
            Log.i("Refresh", " Ok2");

            image = Bitmap.createBitmap(pixArray,0,image.getWidth(),image.getWidth(),image.getHeight(), Bitmap.Config.RGB_565);
            this.refreshDrawableState();
        }*/
    };
    /***End Intern Class***/

    public static Bitmap image;
    private DispView dispView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dispView = new DispView(getBaseContext());
        setContentView(dispView);
    }
}
