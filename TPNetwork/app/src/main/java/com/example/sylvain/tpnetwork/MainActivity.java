package com.example.sylvain.tpnetwork;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private String TAG = new String("CamViewAPP");
    private Button btnStart;
    private ToggleButton btnLight;
    private Thread thread, thread1;
    private static final int BUFF_LENGTH = 1024;
    byte[] buff = new byte[BUFF_LENGTH];
    private int value = 0;
    private Runnable lightManager = new Runnable() {
        @Override
        public void run() {
            try {

                Socket socket = new Socket("192.168.17.50", 80);
                String str = new String("GET /axis-cgi/io/lightcontrol.cgi?action=L1:-" + String.valueOf(value) + " HTTP/1.1\r\n\r\n");
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                os.write(str.getBytes(), 0, str.getBytes().length);

                boolean ended = false;

                Log.i(TAG, "\nTEST 0\n");
                Log.i(TAG, "TEST 1\r");
                Log.i(TAG, "TEST 2\r\n");

                while(!ended){
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException ie){ie.printStackTrace();}
                    try{
                        int nb = is.read(buff,0,buff.length);
                        if(nb > 0){
                            Log.i(TAG," wait nb " + nb);
                            String newstring = new String(buff,0,nb);
                            if(newstring.contains("HTTP/1.0 200 OK"))
                                Log.i(TAG, "contains ok");
                            ended = true;
                        }
                    } catch(IOException ioe){ioe.printStackTrace();}
                }

            } catch(UnknownHostException uhe){
                uhe.printStackTrace();
            } catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    };
    private Runnable imgManager = new Runnable() {
        @Override
        public void run() {
            try {
                Socket socket = new Socket("192.168.17.50", 80);
                String str = new String("GET /jpg/image.jpg HTTP/1.1\r\n\r\n");
                OutputStream os = socket.getOutputStream();
                InputStream is = socket.getInputStream();
                os.write(str.getBytes(), 0, str.getBytes().length);
                byte[] buffer = new byte[BUFF_LENGTH];

                boolean ended = false;

                while(!ended){
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException ie){ie.printStackTrace();}
                    try{
                        int nb = is.read(buffer,0,buffer.length);
                        if(nb > 0){
                            String str1 = new String(buffer,"UTF-8");

                            if(str1.contains("Content-Type: image/jpeg")) {
                                Log.i(TAG, "Contains header jpeg");
                                String[] tmp = str1.split("\r\n");

                                for(int i=0;i<7;i++)
                                    Log.i(TAG,tmp[i]);

                                if (str1.contains("Content-Length:")) {
                                    Log.i(TAG, "Contains header length");

                                    String[] tmp1 = tmp[6].split(" ",8);

                                    Log.i(TAG, "packet : " + Integer.parseInt(tmp1[1]));

                                    byte[] jpeg = new byte[Integer.parseInt(tmp1[1])];
                                    String[] tmp2 = str1.split("" + tmp1[1]);
                                    Log.i(TAG, "Total=" + str1.length() + " image part is " + tmp2[1].length());

                                    ended = true;
                                }
                            }
                            buffer = new byte[BUFF_LENGTH];
                        }
                    } catch(IOException ioe){ioe.printStackTrace();}
                }
            } catch(UnknownHostException uoe){uoe.printStackTrace();}
            catch(IOException ioe){ioe.printStackTrace();}
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnLight = (ToggleButton)findViewById(R.id.btnLight);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread1 = new Thread(imgManager);
                if(thread1 != null) {
                    Toast.makeText(getApplicationContext(), "Successfully started img", Toast.LENGTH_SHORT).show();
                    thread1.start();
                }
                else
                    Toast.makeText(getApplicationContext(), "Thread instanciation error", Toast.LENGTH_SHORT).show();
            }
        });

        btnLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                thread = new Thread(lightManager);
                if(thread != null) {
                    Toast.makeText(getApplicationContext(), "Successfully started light", Toast.LENGTH_SHORT).show();
                    if(isChecked)
                        value = 40;
                    else
                        value = 0;
                    thread.start();
                }
                else
                    Toast.makeText(getApplicationContext(), "Thread instanciation error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
