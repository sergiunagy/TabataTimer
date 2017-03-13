package com.moonapps.moonmoon.tabatatimer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.moonapps.moonmoon.tabatatimer.R;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TimerConfigurationValues;

import java.util.ArrayList;
import java.util.List;

/***
 * class used to save user configured timers
 */

public class TimerSaver {
    public final static String USER_SAVED_TIMERS = "USER SAVED TABATA TIMERS";

    public final static String SUFFIX_CUSTOM_ROUND = "_CUSTOM_ROUND";
    public final static String SUFFIX_CUSTOM_PAUSE = "_CUSTOM_PAUSE";
    public final static String SUFFIX_CUSTOM_SETS = "_CUSTOM_SETS";
    public final static String PREFIX_TIMER_NAME = "CUSTOM-";

    /*
    max number of user saved tabata timers -
     todo: to be able to edit this via preferences
     */
    public final static int USER_SAVED_TIMERS_MAX_NO = 5;

    /*
    pre-configured timer lists
    */
    private static List<TimerConfigurationValues> defaultTimers = TimerConfigurationsLoader.getDefaultTimers();
    private static List<TimerConfigurationValues> customTimers = TimerConfigurationsLoader.getCustomTimers();

    private static Context context = App.getContext();

    /*
    remembers which index is current
     */
    private static int curentCustomTimersIndex;
    /*
       get the values from the preferences, set to default if not accesible
       todo : it's now final but next step is to alow the user control of the max
        */
    private static int maxNumberOfTimers;
    private static int currentNumberOfTimers;

    /*
        get the configuration timers
         */
    static{

        SharedPreferences prefs = context.getSharedPreferences(USER_SAVED_TIMERS, Context.MODE_PRIVATE);
        /*
        get the values from the preferences, set to default if not accesible
        todo : it's now final but next step is to alow the user control of the max
         */
        maxNumberOfTimers = prefs.getInt(context.getString(R.string.prefs_US_max_no_of_timers), USER_SAVED_TIMERS_MAX_NO);
        currentNumberOfTimers = prefs.getInt(context.getString(R.string.prefs_US_current_no_of_timers), 0);
    }

    /***
     * check if the timer already exists in either the default or the custom list
     * @param timer
     * @return true if timer is found
     */
    private static boolean isInExistingLists(TimerConfigurationValues timer){
        if(timerExistsInList(defaultTimers, timer)
                || timerExistsInList(customTimers,timer)){
            return true;
        }
        return false;
    }

    private static boolean timerExistsInList(List<TimerConfigurationValues> timerList, TimerConfigurationValues timer) {
        for (TimerConfigurationValues compareTimer:
                timerList) {
            if(compareTimer.getRoundDuration().equals(timer.getRoundDuration())
                    && compareTimer.getPauseDuration().equals(timer.getPauseDuration())
                        && compareTimer.getSetsNumber().equals(timer.getSetsNumber()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * add a new timer to the list and to the preferences
     * @param newTimer
     * @return true if successful
     */
    public static boolean addTimer(TimerConfigurationValues newTimer){

        if(isInExistingLists(newTimer)){
            return false;
        }
        /*
        add new timer - the timers in the list by the add.o Oldest are dropped.
         */
        customTimers.add(0, newTimer);
       if(customTimers.size()>maxNumberOfTimers) {
            customTimers = customTimers.subList(0, maxNumberOfTimers);
        }
        /*
        save list to preferences
         */
        saveCustomListToPreferences();
        curentCustomTimersIndex = 0;
        return true;
    }


    /***
     * save the list to preferences using the index as key
     */
    private static void saveCustomListToPreferences() {
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(USER_SAVED_TIMERS, Context.MODE_PRIVATE).edit();

        for (TimerConfigurationValues timer :
                customTimers) {
            prefsEditor.putString(String.valueOf(customTimers.indexOf(timer))+SUFFIX_CUSTOM_ROUND,timer.getRoundDuration());
            prefsEditor.putString(String.valueOf(customTimers.indexOf(timer))+SUFFIX_CUSTOM_PAUSE,timer.getPauseDuration());
            prefsEditor.putString(String.valueOf(customTimers.indexOf(timer))+SUFFIX_CUSTOM_SETS,timer.getSetsNumber());
            prefsEditor.putString(String.valueOf(PREFIX_TIMER_NAME + customTimers.indexOf(timer)),timer.getTimerName());
            /*
            TODO include customized timer name
             */

            /*
            update stored timers number
             */
            currentNumberOfTimers = customTimers.size();
            prefsEditor.putInt(context.getString(R.string.prefs_US_current_no_of_timers), currentNumberOfTimers);

        }
        prefsEditor.apply();

    }

    /***
     * todo: create parent class TimerConfigurationOperations and extend the saver and the loader from it
     */

    /**
     * Returns the next default configuration value set and increments the current index
     *
     * @return
     */
    public static TimerConfigurationValues getDefaultNext() {
        incrementIndex();
        TimerConfigurationValues returnedValueSet = customTimers.get(curentCustomTimersIndex);
        return returnedValueSet;
    }

    /**
     * return the configurations value set previous to the current one in the default list
     *
     * @return
     */
    public static TimerConfigurationValues getDefaultPrevious() {
        decrementIndex();
        TimerConfigurationValues returnedValueSet = customTimers.get(curentCustomTimersIndex);
        return returnedValueSet;
    }

    /**
     * return the configurations value set previous to the current one in the default list
     *
     * @return
     */
    public static TimerConfigurationValues getDefaultCurrent() {
        return customTimers.get(curentCustomTimersIndex);
    }


    /**
     * decrement the index value looping back to top from 0
     */
    private static void decrementIndex() {
        if (curentCustomTimersIndex == 0) {
            curentCustomTimersIndex = currentNumberOfTimers - 1;
        } else {
            curentCustomTimersIndex--;
        }
    }

    /**
     * increment the index looping back to playLongSound on reaching the end
     */
    private static void incrementIndex() {

        curentCustomTimersIndex++;
        curentCustomTimersIndex %= currentNumberOfTimers;
    }

    /***
     * this method is executed on an start
     * @return list of saved timers from preferences file
     */
    public static List<TimerConfigurationValues> loadCustomTimersFromPreferences() {
        /*
        use a new list to make sure we overwrite the old one
         */
        List<TimerConfigurationValues> newList = new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences(USER_SAVED_TIMERS, Context.MODE_PRIVATE);
        for(int i= 0; i<currentNumberOfTimers; i++)
        {
            TimerConfigurationValues timer = new TimerConfigurationValues();
            /*
            set the values of the timers or 0 if problems occured
             */
            timer.setRoundDuration(prefs.getString(String.valueOf(i)+SUFFIX_CUSTOM_ROUND,"0"));
            timer.setPauseDuration(prefs.getString(String.valueOf(i)+SUFFIX_CUSTOM_PAUSE,"0"));
            timer.setSetsNumber(prefs.getString(String.valueOf(i)+SUFFIX_CUSTOM_SETS,"0"));

            timer.setTimerName(PREFIX_TIMER_NAME + prefs.getString(String.valueOf(i),"CUSTOM TIMER"));

            newList.add(timer);
        }
        /*
        store the results also in the class list
         */
        customTimers = newList;
        return customTimers;
    }
}
