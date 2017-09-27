package com.example.tobias.arachnophobiavr;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private LineGraphSeries<DataPoint> mSeries1;
    private int patient_type;

    private Button resumeButton;
    private Button comfortButton;
    private Button syringeButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);
        patient_type = getIntent().getIntExtra("Type", 0);

        resumeButton = (Button) findViewById(R.id.resumeButton);
        comfortButton = (Button) findViewById(R.id.comfortButton);
        syringeButton = (Button) findViewById(R.id.syringeButton);
        stopButton = (Button) findViewById(R.id.stopButton);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                mSeries1.resetData(generateData());
                mHandler.postDelayed(this, 300);
            }
        };

        mHandler.postDelayed(mTimer1, 300);

    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    private DataPoint[] generateData() {
        int count = 100;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }


    private int calcPFL(double distance, int patientType) {
        double pflScore = 0;

        // Different function for different types
        switch (patientType) {
            case 1:
                pflScore = Math.exp((Math.sqrt(distance) * distance) / 270) - 1;
                break;
            case 2:
                pflScore = Math.exp((distance/10) / 230) + (0.05*distance);
                break;
            case 3:
                pflScore = Math.exp((Math.sqrt(distance) * distance) / 232) - 5 + (0.2*distance);
                break;
            default:
                break;
        }
        return (int) pflScore;
    }

    Random mRand = new Random();

    private double getRandom() {
        return mRand.nextDouble()*0.5 - 0.25;
    }

    public void showFullscreen(View view) {
        Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
        intent.putExtra(FullscreenExample.ARG_ID, FullscreenExample.REALTIME_SCROLLING.name());
        startActivity(intent);
    }

    public void resumeClicked(View view) {
        comfortButton.setVisibility(View.VISIBLE);
        syringeButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.GONE);
    }

    public void comfortClicked(View view) {
        comfortButton.setVisibility(View.GONE);
        syringeButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);
    }

    public void syringeClicked(View view) {
        comfortButton.setVisibility(View.GONE);
        syringeButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.VISIBLE);
    }

    public void stopClicked(View view) {
        Intent intent = new Intent(MainActivity.this, StartScreen.class);
        startActivity(intent);
    }



}
