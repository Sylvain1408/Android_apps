package com.example.sylvain.fft2;

import android.content.res.Configuration;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.rtp.AudioCodec;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.channels.Channel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FFTActivity";

    /*****UI*****/
    private Button btnTest;
    private EditText textEdit;
    private ToggleButton btnRecord;
    private TextView textLog;
    private MainView mainView;
    private SeekBar seekBar;

    /*****common*****/
    private AudioRecord recorder;
    private int samplingRate = 44100;
    private int bufferSize = 4096;
    private int bufferSizeInShort = bufferSize/2;
    private short[] sampleTab =new short[bufferSizeInShort];


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //your code
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //your code

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnTest = (Button)findViewById(R.id.btnTest);
        textEdit = (EditText)findViewById(R.id.textEdit);
        btnRecord = (ToggleButton)findViewById(R.id.btnRecord);
        textLog = (TextView)findViewById(R.id.textView);
        mainView = (MainView)findViewById(R.id.mainView);
        seekBar = (SeekBar)findViewById(R.id.seekBar);

        btnRecord.setText(String.format("Start Record"));
        btnRecord.setTextOff(String.format("Start Record"));
        btnRecord.setTextOn(String.format("Stop Record"));

        seekBar.setMax(700);
        seekBar.setProgress(0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, Integer.parseInt(textEdit.getText().toString()), AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

                    if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                        if (btnRecord.isChecked()) {//ON
                            Toast.makeText(getApplicationContext(), "Recorder correctly initialized", Toast.LENGTH_SHORT).show();
                            textLog.setText(String.format("Started"));
                            textEdit.setEnabled(false);

                            /*****Start Record*****/
                            recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                                @Override
                                public void onMarkerReached(AudioRecord recorder) {
                                    Log.i(TAG, "Marker Reached");
                                }

                                @Override
                                public void onPeriodicNotification(AudioRecord recorder) {
                                    Log.i(TAG, "Periodic notification reached");
                                    recorder.read(sampleTab, 0, bufferSizeInShort);
                                    mainView.reDraw(sampleTab);
                                    //textLog.setText(String.format("%x %x %x -> %d", (((byte)mainView.R)&0xFF)<<16, (((byte)mainView.G)&0xFF)<<8, (((byte)mainView.B)&0xFF), mainView.RGB));
                                    textLog.setText(String.format("Fundamental is %d\tHz", mainView.getFundamental(), mainView.spaces,mainView.mPreviousX));
                                    mainView.setVariationColor(seekBar.getProgress());
                                }
                            });
                            recorder.setPositionNotificationPeriod(bufferSizeInShort);
                            recorder.startRecording();

                        } else {//OFF
                            /***Stop Record***/
                            recorder.stop();
                            recorder.release();

                            textEdit.setEnabled(true);
                            recorder.release();
                            recorder = null;
                            btnRecord.setChecked(false);
                            textLog.setText(String.format("Stopped"));
                            return;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Recorder failure", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException nfe) {
                    Toast.makeText(getApplicationContext(), "Bad sampling rate", Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException iae) {
                    Toast.makeText(getApplicationContext(), "Illegal argument", Toast.LENGTH_SHORT).show();
                }
            }
        });
        textEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*int tmp = Integer.parseInt(s.toString());
                if ((tmp == 44100) || (tmp == 22050) || (tmp == 16000) || (tmp == 11025)) {
                    samplingRate = tmp;
                    textLog.setText(String.format("Correct Sampling Rate"));
                } else{
                    textLog.setText(String.format("Bad Sampling Rate"));
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }


}
