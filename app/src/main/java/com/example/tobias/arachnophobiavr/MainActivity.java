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
    private TextView curRewardLevel;
    private TextView totRewardLevel;

    public static final int UNITY_ACTION_PAUSE_SPIDER = 0;
    public static final int UNITY_ACTION_RESET_SPIDER = 1;
    public static final int UNITY_ACTION_REMOVE_SPIDER = 2;

    private Button resumeButton;
    private Button comfortButton;
    private Button syringeButton;
    private Button stopButton;

    private RewardScore rs;

    private int fearLevelScore = 0;
    private int currentRewardScore = 0;
    private int totalRewardScore = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fearLevel = (TextView) findViewById(R.id.fearLevel);
        curRewardLevel = (TextView) findViewById(R.id.lastRewardLevel);
        totRewardLevel = (TextView) findViewById(R.id.totalRewardScore);
        rs = new RewardScore();

        patient_type = getIntent().getIntExtra("Type", 0);

        resumeButton = (Button) findViewById(R.id.resumeButton);
        comfortButton = (Button) findViewById(R.id.comfortButton);
        syringeButton = (Button) findViewById(R.id.syringeButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        unityConn = new UnityConnection();
        unityConn.init("192.168.137.1");
        if (DEBUG_LEVEL > 0) Log.d("UNITY", "Success! Unity connection initialized!");
        unityConn.send(UNITY_ACTION_RESET_SPIDER);
        setDataToGraph();
    }

    private void setDataToGraph() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fearLevelScore = Math.max(0, calcPFL(unityConn.receiveData(), patient_type) - totalRewardScore);
                            fearLevel.setText(""+fearLevelScore);
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
                pflScore = Math.exp((Math.sqrt(distance) * distance) / 232) + (0.2*distance) + 5;
                break;
            case 2:
                pflScore = Math.exp(distance/10) / 230 + (0.05*distance);
                break;
            default:
                break;
        }
        return (int) pflScore;
    }


    public void resumeClicked(View view) {
        unityConn.send(UNITY_ACTION_RESET_SPIDER);
        comfortButton.setVisibility(View.VISIBLE);
        syringeButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.GONE);

        rs.stopCalming();
        currentRewardScore = rs.getFearRV();
        totalRewardScore = Math.min(100, totalRewardScore + currentRewardScore);

        totRewardLevel.setText(""+totalRewardScore);
        curRewardLevel.setText(""+currentRewardScore);
    }

    public void comfortClicked(View view) {
        unityConn.send(UNITY_ACTION_PAUSE_SPIDER);
        comfortButton.setVisibility(View.GONE);
        syringeButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);

        rs.startCalming(rs.METHOD_CALMING, fearLevelScore);
    }

    public void syringeClicked(View view) {
        unityConn.send(UNITY_ACTION_PAUSE_SPIDER);
        comfortButton.setVisibility(View.GONE);
        syringeButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);

        rs.startCalming(rs.METHOD_SYRINGE, fearLevelScore);

    }

    public void stopClicked(View view) {
        unityConn.send(UNITY_ACTION_REMOVE_SPIDER);
        Intent intent = new Intent(MainActivity.this, StartScreen.class);
        startActivity(intent);
        unityConn.close();
        finish();
    }
}
