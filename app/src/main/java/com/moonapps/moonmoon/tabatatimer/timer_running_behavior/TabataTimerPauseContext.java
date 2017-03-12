package com.moonapps.moonmoon.tabatatimer.timer_running_behavior;

/**
 * Created by Moon Moon on 2/18/2017.
 */

public class TabataTimerPauseContext {

    private int timerCounter;
    private int setCounter;
    private boolean isPauseFinishedFlag;
    private boolean isRoundFinishedFlag;

    private TabataTimerStates timerState;
    private TabataTimerStates timerNextState;

    /****
     * save the context existing when the timer was paused
     * @param timerCounter
     * @param setCounter
     * @param timerState
     * @param timerNextState
     * @param isPauseFinishedFlag
     * @param isRoundFinishedFlag
     */
    public void saveContext(int timerCounter,
                            int setCounter,
                            TabataTimerStates timerState,
                            TabataTimerStates timerNextState,
                            boolean isPauseFinishedFlag,
                            boolean isRoundFinishedFlag){

        this.timerCounter = timerCounter;
        this.setCounter = setCounter;
        this.timerState = timerState;
        this.timerNextState = timerNextState;
        this.isPauseFinishedFlag = isPauseFinishedFlag;
        this.isRoundFinishedFlag = isRoundFinishedFlag;

    }

    public int getTimerCounter() {
        return timerCounter;
    }

    public boolean getPauseFinishedFlag() {
        return isPauseFinishedFlag;
    }

    public boolean getRoundFinishedFlag() {
        return isRoundFinishedFlag;
    }

    public int getSetCounter() {

        return setCounter;
    }

    public TabataTimerStates getTimerState() {

        return timerState;
    }

    public TabataTimerStates getTimerNextState() {
        return timerNextState;
    }
}
