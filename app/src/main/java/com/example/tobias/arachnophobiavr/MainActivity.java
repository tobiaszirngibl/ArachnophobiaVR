package com.example.tobias.arachnophobiavr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final int DEBUG_LEVEL = 2;

    private UnityConnection unityConn;

    private int patient_type;

    private TextView fearLevel;

    public static final int UNITY_ACTION_PAUSE_SPIDER = 0;
    public static final int UNITY_ACTION_RESET_SPIDER = 1;
    public static final int UNITY_ACTION_REMOVE_SPIDER = 2;

    private Button resumeButton;
    private Button comfortButton;
    private Button syringeButton;
    private Button stopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fearLevel = (TextView) findViewById(R.id.fearLevel);


        patient_type = getIntent().getIntExtra("Type", 0);

        resumeButton = (Button) findViewById(R.id.resumeButton);
        comfortButton = (Button) findViewById(R.id.comfortButton);
        syringeButton = (Button) findViewById(R.id.syringeButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        unityConn = new UnityConnection();
        unityConn.init("192.168.137.1");
        if (DEBUG_LEVEL > 0) Log.d("UNITY", "Success! Unity connection initialized!");
        setDataToGraph();
    }

    private void setDataToGraph() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fearLevel.setText(""+calcPFL(unityConn.receive(),patient_type));
                        }
                    });
            }
        }, 0, 1000);
    }


    @Override
    public void onResume() {
        super.onResume();

            setDataToGraph();

    }

    @Override
    public void onPause() {
        super.onPause();
    }
    




    private int calcPFL(double distance, int patientType) {
        double pflScore = 0;

        // Different function for different types
        switch (patientType) {
            case 0:
                pflScore = Math.exp((Math.sqrt(distance) * distance) / 270) - 1;
                break;
            case 1:
                pflScore = Math.exp((distance/10) / 230) + (0.05*distance);
                break;
            case 2:
                pflScore = Math.exp((Math.sqrt(distance) * distance) / 232) - 5 + (0.2*distance);
                break;
            default:
                break;
        }
        return (int) pflScore;
    }




    public void resumeClicked(View view) {
        short r = unityConn.receive();
        Log.d("BUTTON", "received: " + r);
        unityConn.send(UNITY_ACTION_RESET_SPIDER);
        comfortButton.setVisibility(View.VISIBLE);
        syringeButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.GONE);
    }

    public void comfortClicked(View view) {
        unityConn.send(UNITY_ACTION_PAUSE_SPIDER);
        comfortButton.setVisibility(View.GONE);
        syringeButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);
    }

    public void syringeClicked(View view) {
        unityConn.send(UNITY_ACTION_PAUSE_SPIDER);
        comfortButton.setVisibility(View.GONE);
        syringeButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);
    }

    public void stopClicked(View view) {
        unityConn.send(UNITY_ACTION_REMOVE_SPIDER);
        Intent intent = new Intent(MainActivity.this, StartScreen.class);
        startActivity(intent);
    }


}
