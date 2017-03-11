package com.moonapps.moonmoon.tabatatimer.utils;

import android.os.Handler;

import com.moonapps.moonmoon.tabatatimer.TabataTimer.TabataRunActivity;

/**
 * A customizable ticker for the Timers.
 * . allows a Timer to be associated
 * . provides a hook function for the Timer to add specific actions
 * . provides playLongSound/stop commands for the Timer
 */

public class TimerTicker {
    private Handler handler;
    private Runnable runnable;
    private int tickPeriod;

    TabataRunActivity runActivity ;


    /***
     * create a new Ticker around a period and the associated Timer activity
     * @param tickPeriod
     * @param runActivity
     */

    public TimerTicker(int tickPeriod, TabataRunActivity runActivity) {
        this.tickPeriod = tickPeriod;
        this.runActivity = runActivity;
        createTicker();
    }

    /***
     * playLongSound the ticker. Executes hook function provided by the Timer
     */
    public void startTicking(){
        handler.postDelayed(runnable, tickPeriod);
   }

    /***
     * stops the Ticker
     */
    public void stopTicking(){
        handler.removeCallbacks(runnable);
    }

    /***
     * create a handler and runnable pair for the ticker
     */
    private void createTicker() {
        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                runActivity.timerTickManager();
                handler.postDelayed(this, tickPeriod);
            }
        };
    }
}
