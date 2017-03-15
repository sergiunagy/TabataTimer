package com.moonapps.moonmoon.tabatatimer.timer_running_behavior;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.moonapps.moonmoon.tabatatimer.R;
import com.moonapps.moonmoon.tabatatimer.notifications.NotificationsManager;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TabataConfigurationActivity;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TimerConfigurationValues;
import com.moonapps.moonmoon.tabatatimer.utils.NoPaddingTextView;
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
    public static final int RUN_DISPLAY_LETTER_SIZE_2_DIGITS = 300;
    public static final int RUN_DISPLAY_LETTER_SIZE_3_DIGITS = 220;
    /*
    value for the volume of the sound: 0..1
     */
    private static final float DEFAULT_ROUND_BELL_VOLUME = NotificationsManager.getMaxNotificationVolume();
    public static final String STATUS_IND_PRESTART = "PRESTART";
    private static final String STATUS_IND_ROUND = "ROUND";
    private static final String STATUS_IND_PAUSE = "PAUSE";
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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


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
        /*
        stop screen from turning off
         */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*************************************
         * GUI ADJUSTMENTS
         **************************************/
        setContentView(R.layout.run_timer_activity);
        setScreenOrientation(lockScreenSwitch);
        /*
        text size adjuster - adjust size of display according to number of digits
         */
        NoPaddingTextView display = (NoPaddingTextView) findViewById(R.id.run_act_timer_display_tv);
        Typeface font = Typeface.createFromAsset(getAssets(), "newscycle-regular.ttf");
        display.setTypeface(font);
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        updateSessionStatusInfoTags();
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
     *
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
        /*
        reset the display size- it was modified on stop
         */
        adjustDisplayForDigits(configValues.getRoundDuration());
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
                if (timerCounter == 0) {
                    isRoundFinishedFlag = true;
                    timerNextState = TabataTimerStates.ROUND_RUNNING;
                }
                timerCounter--;
                return timerCounter;

            case PAUSE_RUNNING:
                if (timerCounter == 0) {
                    isPauseFinishedFlag = true;
                    timerNextState = TabataTimerStates.ROUND_RUNNING;
                    //setCounter++;
                }
                timerCounter--;
                return timerCounter;

            case ROUND_RUNNING:
                if (timerCounter == 0) {
                    if (isLastRound()) {
                        isRoundFinishedFlag = true;
                        timerNextState = TabataTimerStates.STOPPED;
                        return timerCounter;
                    }
                    timerNextState = TabataTimerStates.PAUSE_RUNNING;
                    isRoundFinishedFlag = true;
                }
                timerCounter--;
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
        NoPaddingTextView timerDisplay = (NoPaddingTextView) findViewById(R.id.run_act_timer_display_tv);
        AppCompatImageButton restartButton= (AppCompatImageButton) findViewById(R.id.run_act_restart_button);

        /*
        message displayed on session finish
         */
        timerDisplay.setTextSize(80);
        timerDisplay.setText(R.string.session_finished);
        /*
        hide the countdown for sets
         */
        RelativeLayout countdownDisplay = (RelativeLayout) findViewById(R.id.run_act_set_countdown_layout);
        countdownDisplay.setVisibility(View.INVISIBLE);
        /*
        display finished icon
         */
        ImageView statusIcon = (ImageView) findViewById(R.id.run_act_status_icon);
        statusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_run_finish_32p, null));
        /*
        set the restart button attributes
         */
        restartButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_run_reset, null));
        restartButton.setClickable(true);
    }


    /**
     * adjust display size to provide best viewing for given digit number
     *
     * @param roundDuration
     */
    private void adjustDisplayForDigits(String roundDuration) {

        if (hasMoreThan3Digits(roundDuration)) {
            setLetterSize(RUN_DISPLAY_LETTER_SIZE_3_DIGITS);
        } else {
            setLetterSize(RUN_DISPLAY_LETTER_SIZE_2_DIGITS);
        }
    }

    /**
     * check if number of digits is larger than 3
     *
     * @param value
     * @return true if more than 3 digits
     */
    private boolean hasMoreThan3Digits(String value) {
        int scale = Integer.parseInt(value) / 100;
        if (scale > 0) {
            return true;
        }
        return false;
    }

    /*
    decrease the letter size for the display round timer
     */
    private void setLetterSize(float size) {
        NoPaddingTextView roundDisplay = (NoPaddingTextView) findViewById(R.id.run_act_timer_display_tv);
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

        NoPaddingTextView timerDisplay = (NoPaddingTextView) findViewById(R.id.run_act_timer_display_tv);
        timerDisplay.setText("" + timerCounter);
        /*
        Set Prepare-Round-Pause tags
         */
        updateStatusBar();
    }

    /***
     * switch the icons to action status during action and to toolbar on pause
     * also configure reset button to show on pause
     * @param viewId,state
     */
    private void loadActionStatusDuringAction(int viewId, String state){
        AppCompatImageButton restartButton= (AppCompatImageButton) findViewById(R.id.run_act_restart_button);
        ImageView statusIcon = (ImageView) findViewById(R.id.run_act_status_icon);

        restartButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), viewId, null));
        restartButton.setVisibility(View.VISIBLE);
        restartButton.setClickable(false);
        statusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),android.R.color.transparent, null));
        statusIcon.setTag(state);
    }

  /***
     * switch the icons to action status during action and to toolbar on pause
     * also configure reset button to show on pause
     * @param
     */
    private void loadActionStatusDuringPause(){
        AppCompatImageButton restartButton= (AppCompatImageButton) findViewById(R.id.run_act_restart_button);
        ImageView statusIcon = (ImageView) findViewById(R.id.run_act_status_icon);

        String parentState = (String) statusIcon.getTag();
        switch (parentState){
            case STATUS_IND_PRESTART:
                statusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_run_ready_32p, null));
                break;
            case STATUS_IND_ROUND:
                statusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_run_action_32p, null));
                break;
            case STATUS_IND_PAUSE:
                statusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_run_rest_32p, null));
                break;
        }
        restartButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ico_run_reset, null));
        restartButton.setClickable(true);
    }

    /**
     * update the information displayed on the top text view
     */
    private void updateStatusBar() {
        TextView statusTag = (TextView) findViewById(R.id.run_act_status_tv);
        RelativeLayout actionLayout = (RelativeLayout) findViewById(R.id.run_act_bottom_display_layout);
        AppCompatTextView setsCountdownDisplay = (AppCompatTextView) findViewById(R.id.run_act_set_countdown);

        int setCountdownValue = setsCounterMaxValue - setCounter + 1;

        switch (timerState) {

            case PRE_START:
                statusTag.setText("Get Ready");
                actionLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorActionIndicatorPreStart, null));
                setsCountdownDisplay.setText(String.valueOf(setsCounterMaxValue));
                loadActionStatusDuringAction(R.drawable.ico_run_ready_64p,STATUS_IND_PRESTART);
                break;
            case ROUND_RUNNING:
                statusTag.setText("Round " + setCounter);
                actionLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorActionIndicatorAction, null));
                loadActionStatusDuringAction(R.drawable.ico_run_action_64p,STATUS_IND_ROUND);
                break;
            case PAUSE_RUNNING:
                statusTag.setText("Rest ");
                actionLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorActionIndicatorPause, null));
                 setsCountdownDisplay.setText(String.valueOf(setCountdownValue));
                loadActionStatusDuringAction(R.drawable.ico_run_rest_64p,STATUS_IND_PAUSE);
                break;
            case PAUSED:

                loadActionStatusDuringPause();
                statusTag.setText("Paused ");
                break;
            case STOPPED:
                statusTag.setText("");
                actionLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorActionIndicatorPause, null));
                /*
                disable screen ON
                 */
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        ImageButton restartSession = (ImageButton) findViewById(R.id.run_act_restart_button);
        restartSession.setVisibility(View.INVISIBLE);
    }

    /*
    hide the restart button while timer is paused or stopped
     */
    private void showRestartSessionButton() {
        ImageButton restartSession = (ImageButton) findViewById(R.id.run_act_restart_button);
        restartSession.setVisibility(View.VISIBLE);
    }

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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TabataRun Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
