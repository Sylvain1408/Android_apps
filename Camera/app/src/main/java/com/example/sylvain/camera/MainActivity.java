package com.example.sylvain.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.FrameLayout;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.PictureCallback;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    private Preview preview;
    private String TAG = "Camera TAG";
    private Bitmap bmp;
    private ViewFinderView linesView;

    /***Callback***/
    private ShutterCallback shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };
    private PictureCallback rawCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };
    private PictureCallback jpegCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "jpegCallback " + data.length);
            if(data != null) {
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                Intent intent = new Intent(MainActivity.this, Display.class);
                Display.image = bmp;
                startActivity(intent);

                try {
                    FileOutputStream file = getBaseContext().openFileOutput("image.jpg",MODE_PRIVATE);
                    file.write(data,0,data.length);
                } catch(FileNotFoundException fnfe){
                    fnfe.printStackTrace();
                } catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }
    };
    /***end callback***/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = new Preview(this);
        frameLayout = (FrameLayout)findViewById(R.id.preview);
        frameLayout.addView(preview);
        linesView = new ViewFinderView(this);
        frameLayout.addView(linesView);

        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                return;
            }
        });


    }
}
