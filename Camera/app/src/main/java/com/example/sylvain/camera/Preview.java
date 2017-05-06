package com.example.sylvain.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by sylvain on 19/01/16.
 */
public class Preview extends SurfaceView implements SurfaceHolder.Callback{

    public Camera camera;
    private SurfaceHolder holder;
    private String TAG = new String("Preview Class ");

    public Preview(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null){
            camera = Camera.open();
            try{
                camera.setPreviewDisplay(holder);
            } catch(IOException ioe){
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters param = camera.getParameters();
        Log.i(TAG, "Picture W=" + param.getPictureSize().width + "  H=" + param.getPictureSize().height);

        List<String> supportedFocusMode = param.getSupportedFocusModes();
        for(String i:supportedFocusMode)
            Log.i(TAG, "\tSupported Focus mode " + i);
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<Camera.Size> formats = param.getSupportedPictureSizes();
        for(Camera.Size i:formats)
            Log.i(TAG, "\t Available Size : W=" + i.width + " H=" + i.height);

        List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
        for(Camera.Size i :previewSizes)
            Log.i(TAG,"\t Preview Size : W=" + i.width + " H=" + i.height);

        //Select Picture Size
        param.setPictureSize(1280,720);
        param.setPreviewSize(1920,1440);
        camera.setParameters(param);
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}
