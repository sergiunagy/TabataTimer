package com.moonapps.moonmoon.tabatatimer.timer_running_behavior;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moonapps.moonmoon.tabatatimer.R;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TabataConfigurationActivity;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TimerConfigurationValues;
import com.moonapps.moonmoon.tabatatimer.notifications.NotificationsManager;
import com.moonapps.moonmoon.tabatatimer.utils.TimerTicker;

public class TabataRunActivity extends AppCompatActivity {

    /*
    Timer interval
     */
    public static final int ONE_SECOND = 1000;
    /*
    default pre-playLongSound counter value - runs first, before the rounds and pauses begin
     */
    public static final int PRESTART_COUNTER_DEFAULT = 5;
    /*
    value for the volume of the sound: 0..1
     */
    private static final float DEFAULT_ROUND_BELL_VOLUME = NotificationsManager.getMaxNotificationVolume();

    /*
    configuration values associated with the timer
     */
    private TimerConfigurationValues configValues;
    private boolean lockScreenSwitch;
    private TimerTicker tabataTimerTicker;
    /*
    private set of configuration values - this is redundant with the configValues - eliminate one or the other
     */
    private int roundStartValue;
    private int pauseStartValue;
    private int setsCounterMaxValue;
    private char preStartCounteStartValue;
    /*
    the actual timer counters for the intervals(pre-playLongSound, rounds and pauses)
     */

    private int setCounter;
    private int timerCounter;
    /*
    the current state of the timer
     */
    private TabataTimerStates timerState;
    /*
    context saved at pause. Co-exists with the config values because the user may choose to restart the session, so the original values are still needed
     */
    private TabataTimerPauseContext pauseContext;

    /*
    flags that indicate transition between round and pause
     */
    private boolean isRoundFinishedFlag;
    private boolean isPauseFinishedFlag;
    private TabataTimerStates timerNextState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        /*************************************
         * GET ACTIVITY PARAMETERS
         **************************************/
        Intent dataInput = getIntent();
         /*
        get the passed timer configuration values
         */
        configValues = dataInput.getParcelableExtra(TabataConfigurationActivity.TABATA_RUN_COMMAND_REQ);
         /*
        import screen orientation preference
         */
        lockScreenSwitch = dataInput.getBooleanExtra(TabataConfigurationActivity.LOCK_SCREEN, false);

        /*************************************
         * GUI ADJUSTMENTS
         **************************************/
        setContentView(R.layout.tabata_run_timer);
        setScreenOrientation(lockScreenSwitch);
        /*
        text size adjuster - adjust size of display according to number of digits
         */
        adjustDisplayForDigits(configValues.getRoundDuration());
        /*
        disable the restart session button
         */
        hideRestartSessionButton();
        /*
        load the configuration values into the Timer
         */
        initializeTimer(configValues);
        /*
        create a periodic trigger and associate it with the current timer/
         */
        tabataTimerTicker = new TimerTicker(ONE_SECOND, this);
        /*
        create the notifications manager and pass it a reference to this Activity
         */
        NotificationsManager.create(this);
        /*
        start the timer
         */
        startTimer();
    }

    /***
     * Initialize the Timer with configuration values from the parameteres received
     * from the configuration Activity
     * initialize the state machine .
     *
     * @param configValues
     */
    private void initializeTimer(TimerConfigurationValues configValues) {

        /*
        set the Timer thresholds
         */
        preStartCounteStartValue = PRESTART_COUNTER_DEFAULT;
        roundStartValue = Integer.parseInt(configValues.getRoundDuration());
        pauseStartValue = Integer.parseInt(configValues.getPauseDuration());
        setsCounterMaxValue = Integer.parseInt(configValues.getSetsNumber());

        /*
        load the Timer with the initial values in the
         */
        setCounter = 0;
        timerCounter = preStartCounteStartValue;
        timerState = TabataTimerStates.PRE_START;
        timerNextState = TabataTimerStates.ROUND_RUNNING;
        isRoundFinishedFlag = false;
        isPauseFinishedFlag = false;
    }

    /***
     * start the Timer
     * . set pre-conditions
     * . playLongSound the ticker
     */
    private void startTimer() {

        tabataTimerTicker.startTicking();
    }

    /**
     * restart session on User action
     * @param view
     */
    public void restartSessionActions(View view) {

        tabataTimerTicker.stopTicking();
        /*
        perform extra timer configurations
         */
        hideRestartSessionButton();
         /*
        load the configuration values into the Timer
         */
        initializeTimer(configValues);
        startTimer();
    }

    /***
     * Hook functions allowing the ticker to trigger the periodic actions of the Timer
     *
     * @param
     */
    public void timerTickManager() {

        /*
        check if Bell interval - leave a x second gap so the sound does not overlap the timer intervals
         */
        if (isRoundBellPlaying()) {
            /*
            keep playing until the Notifications Manager resets the flag
             */
            playRoundBell();
            return;
        }

        if (isRoundFinishedFlag) {
            playPauseBell();
            /*
            move to the next timer state
             */
            timerMoveToNextState();
            /*
            on each round end increment round counter - it's only displayed during the round
             */
            setCounter++;
            isRoundFinishedFlag = false;
            return;
        }

        if (isPauseFinishedFlag) {
            playRoundBell();
            timerMoveToNextState();
            isPauseFinishedFlag = false;
            return;
        }
         /*
        refresh the user display information
         */
        updateSessionStatusInfoTags();
        /*
        execute timer specific tick actions
         */
        runTimerCounters();
    }

    /**
     * move the timer to the next state and re-load the counter
     */
    private void timerMoveToNextState() {
        switch (timerNextState) {
            case PAUSE_RUNNING:
                timerCounter = pauseStartValue;
                break;
            case ROUND_RUNNING:
                timerCounter = roundStartValue;
                break;
            default:
                /*
                for STOPPED or Pause timer value is irelevant
                 */
                break;
        }
        timerState = timerNextState;
    }

    /***
     * method will trigger the call to the long long sound play method of the Notifications Manager
     */
    private void playRoundBell() {

        NotificationsManager.playLongSound(DEFAULT_ROUND_BELL_VOLUME);
    }

    /**
     * play the pause sound
     * TODO: create dedicate pause sound
     */
    private void playPauseBell() {
        NotificationsManager.playLongSound(DEFAULT_ROUND_BELL_VOLUME);
    }


    private int runTimerCounters() {

        switch (timerState) {
            case PRE_START:
                timerCounter--;
                if (timerCounter == 0) {
                    isRoundFinishedFlag = true;
                    timerNextState = TabataTimerStates.ROUND_RUNNING;
                }
                return timerCounter;

            case PAUSE_RUNNING:
                timerCounter--;
                if (timerCounter == 0) {
                    isPauseFinishedFlag = true;
                    timerNextState = TabataTimerStates.ROUND_RUNNING;
                    setCounter++;
                }
                return timerCounter;

            case ROUND_RUNNING:
                timerCounter--;
                if (timerCounter == 0) {
                    if (isLastRound()) {
                        isRoundFinishedFlag = true;
                        timerNextState = TabataTimerStates.STOPPED;
                        return timerCounter;
                    }
                    timerNextState = TabataTimerStates.PAUSE_RUNNING;
                    isRoundFinishedFlag = true;
                }
                return timerCounter;

            case STOPPED:
                /*
                stop the ticker
                 */
                tabataTimerTicker.stopTicking();
                /*
                display message
                 */
                displaySessionFinishedMessage();
                /*
                allow user to restart
                 */
                showRestartSessionButton();
                return 0;
            default:
                tabataTimerTicker.stopTicking();
                return 0;
        }
    }

    private void displaySessionFinishedMessage() {
        TextView timerDisplay = (TextView) findViewById(R.id.displayTimer);
        timerDisplay.setTextSize(80);
        timerDisplay.setText(R.string.session_finished);
    }


    /**
     * adjust display size to provide best viewing for given digit number
     *
     * @param roundDuration
     */
    private void adjustDisplayForDigits(String roundDuration) {
        int scale = Integer.parseInt(roundDuration) / 100;
        if (scale > 0) {
            setLetterSize(180);
        } else {
            setLetterSize(220);
        }
    }

    /*
    decrease the letter size for the display round timer
     */
    private void setLetterSize(float size) {
        TextView roundDisplay = (TextView) findViewById(R.id.displayTimer);
        roundDisplay.setTextSize(size); /*180dp*/
    }

    /**
     * @return true if a Round bell is currently playing
     */
    private boolean isRoundBellPlaying() {
        return NotificationsManager.isLongSoundPlaying();
    }

    /***
     * update the information displayed to the user
     */
    private void updateSessionStatusInfoTags() {

        TextView timerDisplay = (TextView) findViewById(R.id.displayTimer);
        timerDisplay.setText("" + timerCounter);
        /*
        Set Prepare-Round-Pause tags
         */
        updateStatusBar();
        /*
         Set round number
         */
        TextView displaySetsCounter = (TextView) findViewById(R.id.displaySetNumber);
        displaySetsCounter.setText("" + setCounter);
    }

    /**
     * update the information displayed on the top text view
     */
    private void updateStatusBar() {
        TextView statusTag = (TextView) findViewById(R.id.setNumberTag);
        TextView setNumber = (TextView) findViewById(R.id.displaySetNumber);

        switch (timerState) {

            case PRE_START:
                statusTag.setText("Get Ready");
                setNumber.setVisibility(View.INVISIBLE);
                break;
            case ROUND_RUNNING:
                statusTag.setText("Round ");
                setNumber.setVisibility(View.VISIBLE);
                break;
            case PAUSE_RUNNING:
                statusTag.setText("Rest ");
                setNumber.setVisibility(View.INVISIBLE);
                break;
            case PAUSED:
                statusTag.setText("Paused ");
                setNumber.setVisibility(View.INVISIBLE);
                break;
            case STOPPED:
                statusTag.setText("");
                setNumber.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


    private boolean isLastRound() {
        return (setCounter == setsCounterMaxValue);
    }


    /*
    hide the restart button while timer is running
     */
    private void hideRestartSessionButton() {
        RelativeLayout restartSession = (RelativeLayout) findViewById(R.id.bottom_display_layout);
        restartSession.setVisibility(View.INVISIBLE);
    }

    /*
    hide the restart button while timer is paused or stopped
     */
    private void showRestartSessionButton() {
        RelativeLayout restartSession = (RelativeLayout) findViewById(R.id.bottom_display_layout);
        restartSession.setVisibility(View.VISIBLE);    }

    /***
     * send the configuration values used back to the parent activity
     */
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent(this, TabataRunActivity.class);
        returnIntent.putExtra(TabataConfigurationActivity.TABATA_RUNNING_RETURN_CONFIG, configValues);
        returnIntent.putExtra(TabataConfigurationActivity.TABATA_RUNNING_RETURN_LOCK, lockScreenSwitch);
        setResult(RESULT_OK, returnIntent);
        tabataTimerTicker.stopTicking();
        finish();
    }

    /***
     * if the user clicks of the digit display set behavior for pause and playLongSound
     *
     * @param view
     */
    public void timerClickAction(View view) {
        switch (timerState) {
            case ROUND_RUNNING:
            case PAUSE_RUNNING:
            case PRE_START:
                /*
                save the context
                 */
                savePausedTimerContext();
                /*
                show option to restart the session
                 */
                showRestartSessionButton();
                /*
                disable the 1 second trigger of the timer
                 */
                disableTicker();

                timerState = TabataTimerStates.PAUSED;
                /*
                update status bar
                 */
                updateStatusBar();

                break;

            case PAUSED:
                restorePausedTimerContext();
                hideRestartSessionButton();
                updateStatusBar();
                startTimerFromPause();
                break;
            default:
                break;
        }

    }

    private void startTimerFromPause() {
        tabataTimerTicker.startTicking();
    }

    private void disableTicker() {
        tabataTimerTicker.stopTicking();
    }


    private void savePausedTimerContext() {
        pauseContext = new TabataTimerPauseContext();
        pauseContext.saveContext(timerCounter,
                setCounter,
                timerState,
                timerNextState,
                isPauseFinishedFlag,
                isRoundFinishedFlag);
    }

    private void restorePausedTimerContext() {
        timerCounter = pauseContext.getTimerCounter();
        setCounter = pauseContext.getSetCounter();
        timerState = pauseContext.getTimerState();
        timerNextState = pauseContext.getTimerNextState();
        isPauseFinishedFlag = pauseContext.getPauseFinishedFlag();
        isRoundFinishedFlag = pauseContext.getRoundFinishedFlag();
    }



    private void setScreenOrientation(boolean lockScreen) {

        if (lockScreen) {
            setRequestedOrientation(this.getResources().getConfiguration().orientation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    @Override
    protected void onDestroy() {
        tabataTimerTicker.stopTicking();
        super.onDestroy();
    }
}