package com.example.sylvain.vonkoch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SurfaceView surface;
    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surface = (SurfaceView)findViewById(R.id.surfaceView);
        text = (TextView)findViewById(R.id.textView2);

        surface.setX(surface.getX() + 10);
        text.setText(String.format("X : %f", surface.getX()));

        SurfaceHolder holder = surface.getHolder();

        Canvas canvas = new Canvas();
        Paint paint = new Paint();
        paint.setColor(112233);
        Rect rect = new Rect();
        rect.set(10,10,10,10);
        canvas.drawRect(rect, paint);
        surface.draw(canvas);
    }
}
