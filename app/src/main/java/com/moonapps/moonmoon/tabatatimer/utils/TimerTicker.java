package com.moonapps.moonmoon.tabatatimer.utils;

import android.os.AsyncTask;

import com.moonapps.moonmoon.tabatatimer.timer_running_behavior.TabataRunActivity;

/**
 * A customizable ticker for the Timers.
 * . allows a Timer to be associated
 * . provides a hook function for the Timer to add specific actions
 * . provides playLongSound/stop commands for the Timer
 */

public class TimerTicker {
    public static final int ONE_SECOND = 1000;
    TabataRunActivity runActivity;
    /*
    flag inidcating that ticker is started
     */
    private boolean isRunningTicker;


    /***
     * create a new Ticker around a period and the associated Timer activity
     *
     * @param tickPeriod
     * @param runActivity
     */

    public TimerTicker(int tickPeriod, TabataRunActivity runActivity) {
        this.runActivity = runActivity;
        isRunningTicker = false;
    }

    /***
     * playLongSound the ticker. Executes hook function provided by the Timer
     */
    public void startTicking() {
        isRunningTicker = true;
        tick();
    }

    private void tick() {
        TabataAsyncTask oneSecondTask = new TabataAsyncTask();
        oneSecondTask.execute();
    }

    /***
     * stops the Ticker
     */
    public void stopTicking() {
        isRunningTicker = false;
    }

    /***
     * implement a task to execute Timer operations in background
     * - Parameters received : String with start system time
     * - Calls context activity timer actions hook method
     */
    private class TabataAsyncTask extends AsyncTask<Void, Long, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            /*
            set the initial timestamp
             */
            long startTime = System.currentTimeMillis();
            /*
            get the context activity
             */
            publishProgress(startTime);
            /*
            calculate the duration of the actions
             */
            long duration = System.currentTimeMillis() - startTime;
            try {
                /*
                activate next tick one second after this one STARTED
                 */
                Thread.sleep(ONE_SECOND - duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
               /*
                trigger activity actions
                */
            runActivity.timerTickManager();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            
            if(isRunningTicker){
                tick();
            }
        }
    }
}
