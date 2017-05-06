package com.example.sylvain.testsensor;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.nio.channels.SelectableChannel;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor luxmeter;
    private TextView txt, txtLight, txtSensors;
    private SeekBar barX,barY,barZ;
    private Display display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        txt = (TextView)findViewById(R.id.editText);
        txtLight = (TextView)findViewById(R.id.editText2);
        txtSensors = (TextView)findViewById(R.id.editText3);
        barX = (SeekBar)findViewById(R.id.seekBarX);
        barY = (SeekBar)findViewById(R.id.seekBarY);
        barZ = (SeekBar)findViewById(R.id.seekBarZ);

        barX.setMax(20);
        barY.setMax(20);
        barZ.setMax(20);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        luxmeter = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor i:sensors)
            txtSensors.append(i.getName() + "\n");

        display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();



    }

    @Override
    protected void onPause(){
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, luxmeter);
        super.onPause();
    }

    @Override
    protected  void onResume(){
        if(accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, luxmeter, SensorManager.SENSOR_DELAY_UI);
        }
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            switch(display.getRotation()){
                case Surface.ROTATION_0:
                    x = event.values[0];
                    y = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    x = event.values[1];
                    y = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    x = -event.values[0];
                    y = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    x = -event.values[1];
                    y = -event.values[0];
                    break;
            }

            txt.setText(String.format("x = %+.4f  y = %+.4f  z = %+.4f\n",x,y,z));

            barX.setProgress((int) x+10);
            barY.setProgress((int) y+10);
            barZ.setProgress((int)z+10);
        } else if(event.sensor.getType() == Sensor.TYPE_LIGHT){
            txtLight.setText(String.format("%+.4f light unity\n",event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
