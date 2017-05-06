package com.example.sylvain.nico;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    private Button btn;
    private ProgressBar bar;
    private int friend = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView)findViewById(R.id.textView);
        btn = (Button)findViewById(R.id.button);
        bar = (ProgressBar)findViewById(R.id.progressBar);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friend == 0){
                    text.setText(String.format("Bonjour Nicolas"));
                    bar.setVisibility(View.INVISIBLE);
                    friend = 1;
                }else{
                    text.setText(String.format("Bonjour Tristan"));
                    bar.setVisibility(View.VISIBLE);
                    friend = 0;
                }
            }
        });
    }
}
