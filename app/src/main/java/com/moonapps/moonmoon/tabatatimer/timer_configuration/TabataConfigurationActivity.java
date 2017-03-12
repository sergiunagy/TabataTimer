package com.moonapps.moonmoon.tabatatimer.timer_configuration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.moonapps.moonmoon.tabatatimer.R;
import com.moonapps.moonmoon.tabatatimer.timer_running_behavior.TabataRunActivity;
import com.moonapps.moonmoon.tabatatimer.user_preferences.PreferencesActivity;
import com.moonapps.moonmoon.tabatatimer.utils.TabataGestureListener;
import com.moonapps.moonmoon.tabatatimer.utils.TabataGestureListenerInterface;
import com.moonapps.moonmoon.tabatatimer.utils.TimerConfigurationsLoader;

public class TabataConfigurationActivity extends AppCompatActivity implements TabataGestureListenerInterface {

    public static final String TABATA_RUN_COMMAND_REQ = "TABATA RUN";
    public static final String LOCK_SCREEN = "LOCK SCREEN";
    public static final String TABATA_RUNNING_RETURN_CONFIG = "RETURN CONFIG FROM RUNNIG TABATA";
    public static final String TABATA_RUNNING_RETURN_LOCK = "RETURN LOCK STATE FROM RUNNING TABATA";
    private static final int TABATA_RUNNING_ACTIVITY_REQ_CODE = 1;

    /*
   configuration values for the selected timer
    */
    private TimerConfigurationValues configurationValues;

    /*
    lock screen rotation flag;
     */
    private boolean lockScreenRotation;

    /*
    Handler for gestures
     */
    private GestureDetectorCompat gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabata_configuration_activity);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.tabata_toolbar);
        setSupportActionBar(mToolbar);
        /*
        todo: create screen layouts for landscape
         */

        /*
        playLongSound with the Primary Timer
         */
        configurationValues = TimerConfigurationsLoader.getDefaultCurrent();
        updateConfigurationValuesDisplayed();
       /*
       set gesture listener
        */
        gestureListener = new GestureDetectorCompat(this, new TabataGestureListener(this));
        /*
        customize keyboard - show numbers keboard
         */
        selectKeyboardTypeForEdits();


    }


    @Override
    protected void onResume() {
        super.onResume();
        /*
        get preferences
         */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lockScreenRotation = prefs.getBoolean(getString(R.string.pref_lockscreen), true);
        setLockScreen(lockScreenRotation);
    }

    /**
     * method will trigger the customized gesture listener
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureListener.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * set the numerical keyboard for enetering values
     */
    private void selectKeyboardTypeForEdits() {
        EditText roundEdit = (EditText) findViewById(R.id.config_round_timer);
        roundEdit.setRawInputType(Configuration.KEYBOARD_12KEY);
        EditText pauseEdit = (EditText) findViewById(R.id.config_pause_timer);
        pauseEdit.setRawInputType(Configuration.KEYBOARD_12KEY);
        EditText setNoEdit = (EditText) findViewById(R.id.config_round_number);
        setNoEdit.setRawInputType(Configuration.KEYBOARD_12KEY);
    }

    /***
     * update the text boxes containing configuration values with the current configuration values
     * stored by the configurationValues property
     */
    private void updateConfigurationValuesDisplayed() {

        setRoundDisplayValue(configurationValues.getRoundDuration());
        setPauseDisplayValue(configurationValues.getPauseDuration());
        setSetNoDisplayValue(configurationValues.getSetsNumber());
        setTimerTitleText(configurationValues.getTimerName());

    }

    /***
     * on Run button press trigger the Run Tabata Activity
     * configurationValues for the Timer
     * lockScreenRotation flag
     *
     * @param
     */

    public void runClickHandler(View view) {

        configurationValues = getConfigurationValuesFromUserInput();
        Intent runIntent = new Intent(this, TabataRunActivity.class);

        runIntent.putExtra(TABATA_RUN_COMMAND_REQ, configurationValues);
        runIntent.putExtra(LOCK_SCREEN, lockScreenRotation);

        startActivityForResult(runIntent, TABATA_RUNNING_ACTIVITY_REQ_CODE);
    }

    /***
     * extract the configuration values from the user input
     *
     * @return configuration values
     */
    public TimerConfigurationValues getConfigurationValuesFromUserInput() {
        TimerConfigurationValues returnValues = new TimerConfigurationValues();

        EditText userRound = (EditText) findViewById(R.id.config_round_timer);
        returnValues.setRoundDuration(userRound.getText().toString());

        EditText userPause = (EditText) findViewById(R.id.config_pause_timer);
        returnValues.setPauseDuration(userPause.getText().toString());

        EditText userRoundNo = (EditText) findViewById(R.id.config_round_number);
        returnValues.setSetsNumber(userRoundNo.getText().toString());

        returnValues.setTimerName(configurationValues.getTimerName());
        return returnValues;
    }

    /***
     * get the configration values used by the Tabata Timer Activity
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ((requestCode == TABATA_RUNNING_ACTIVITY_REQ_CODE) && (resultCode == RESULT_OK)) {

            configurationValues = data.getParcelableExtra(TABATA_RUNNING_RETURN_CONFIG);
            updateConfigurationValuesDisplayed();

            lockScreenRotation = data.getBooleanExtra(TABATA_RUNNING_RETURN_LOCK, false);
            setLockScreen(lockScreenRotation);

        }
    }

    /**
     * lock or unlock the screen orientation according to the value of the parameter
     *
     * @param lockScreenRotation
     */
    private void setLockScreen(boolean lockScreenRotation) {
        ImageButton lock = (ImageButton) findViewById(R.id.screen_lock);
        /*
        set field
         */
        this.lockScreenRotation = lockScreenRotation;
        /*
        configure activity level screen rotation
         */
        if (lockScreenRotation) {
            setRequestedOrientation(this.getResources().getConfiguration().orientation);
            lock.setImageDrawable(getResources().getDrawable(R.drawable.lock_closed));
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            lock.setImageDrawable(getResources().getDrawable(R.drawable.lock_open));
        }
    }

    @Override
    /**
     * onSwipe will allow to browse through default or user defined timers:
     * - UP or DOWN - browse through default timers
     * - LEFT or RIGHT browse through custom timers
     */
    public void onSwipe(SwipeGestures direction) {

        switch (direction) {
            case UP:
                configurationValues = TimerConfigurationsLoader.getDefaultNext();
                setTimerTitleText(configurationValues.getTimerName());
                updateConfigurationValuesDisplayed();
                break;
            case DOWN:
                configurationValues = TimerConfigurationsLoader.getDefaultPrevious();
                setTimerTitleText(configurationValues.getTimerName());
                updateConfigurationValuesDisplayed();
                break;
            case LEFT:
//                changeTimer(CUSTOM_NEXT);
                break;
            case RIGHT:
//                changeTimer(CUSTOM_PREVIOUS);
                break;
        }
    }

    private void setTimerTitleText(String timerName) {
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(timerName);
    }

    private void setRoundDisplayValue(String value) {
        EditText roundEditText = (EditText) findViewById(R.id.config_round_timer);
        roundEditText.setText(value);
    }

    public void setPauseDisplayValue(String pauseDisplayValue) {
        EditText newPause = (EditText) findViewById(R.id.config_pause_timer);
        newPause.setText(pauseDisplayValue);
    }

    public void setSetNoDisplayValue(String setNoDisplayValue) {
        EditText newSetsNo = (EditText) findViewById(R.id.config_round_number);
        newSetsNo.setText(setNoDisplayValue);
    }

    public void runLockMessage(View view) {
        String message;
        if(lockScreenRotation){
            message = "Screen is locked";
        }else{
            message = "Screen is unlocked";
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();


    }

    /**
     * start the Praferences activity
     * @param view
     */
    public void runGetPreferences(View view) {
        Intent settingsIntent = new Intent(this, PreferencesActivity.class);
        startActivity(settingsIntent);
    }
}