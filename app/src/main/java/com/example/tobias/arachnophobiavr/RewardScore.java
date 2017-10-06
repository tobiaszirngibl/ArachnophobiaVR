package com.example.tobias.arachnophobiavr;

import android.os.Handler;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tobias on 05.10.17.
 */

public class RewardScore {

    int fearReductionValue = 0;
    int METHOD_CALMING = 0;
    int METHOD_SYRINGE = 1;
    int methodInfo[][]  = {{5,15,30,5},{20,30,10,6}};

    int rewardScore = 0;
    int globalFearRV = 0;

    int method = 0;

    public int getFearRV () {
        return globalFearRV;
    }

    public void startCalming(int method, int fear) {
        this.method = method;
        int normalizedFear = (int) normalize(0,100,1,0.25f,fear);
        int min = methodInfo[method][0]*normalizedFear;
        int max = methodInfo[method][1]*normalizedFear;
        final int deltaTime = methodInfo[method][2];
        final int randomFearDelta = min + (int) (Math.random() * max);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fearReductionValue += randomFearDelta/deltaTime;
            }
        }, 0, 1000);
    }

    public void stopCalming() {
        globalFearRV = fearReductionValue/methodInfo[method][3];

    }

    private float normalize(float srcmin, float srcmax, float destmin, float destmax, float v) {
        float a = (destmax-destmin)/(srcmax-srcmin);
        float b = destmax-a*srcmax;
        return a*v+b;
    }
}
