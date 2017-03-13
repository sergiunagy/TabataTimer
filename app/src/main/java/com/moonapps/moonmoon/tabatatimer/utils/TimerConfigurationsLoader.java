package com.moonapps.moonmoon.tabatatimer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.moonapps.moonmoon.tabatatimer.R;
import com.moonapps.moonmoon.tabatatimer.timer_configuration.TimerConfigurationValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the configuration value sets to load
 */

public class TimerConfigurationsLoader {
    public final static String DEFAULT_TIMERS = "DEFAULT TABATA TIMERS";

    /*
   * browse through the list of default timer configurations
   */
    public static final int DEFAULT_NEXT = 1;
    public static final int DEFAULT_PREVIOUS = 2;
    /*
    browse through the list of custom(user saved) timer configurations
     */
    private static final int CUSTOM_NEXT = 3;
    private static final int CUSTOM_PREVIOUS = 4;

    /*
    3 default timers are available: Tabata, Boxing and Interval
     */
    private static final int NUMBER_OF_DEFAULTS = 3;
    /*
    application context handler to allow resource access
     */
    private static final Context context = App.getContext();
    /*
    pre-configured timer lists
    */
    private static List<TimerConfigurationValues> defaultTimers;
    private static List<TimerConfigurationValues> customTimers;
    /*
    remembers which index is current
     */
    private static int currentDefaultValuesIndex;

    static {
        /*
        check if the preferences have already been initialized, if not, this is probably the first
         run of the app after installation and they ned to be initialized
         */
        SharedPreferences prefs = App.getContext().getSharedPreferences(DEFAULT_TIMERS, Context.MODE_PRIVATE);
        if (!prefs.contains(context.getString(R.string.prefs_Def_tabata_preferences_initialized))) {
            initializeDefaultPrefs();
        }
    }

    /***
     * method used to initialize default preferences on app installation
     */
    private static void initializeDefaultPrefs() {

        SharedPreferences.Editor editor = context.getSharedPreferences(DEFAULT_TIMERS, Context.MODE_PRIVATE).edit();
        /*
        initialize the preferences
         */
        editor.putString(context.getString(R.string.prefs_Def_tabata_round_key),"20");
        editor.putString(context.getString(R.string.prefs_Def_tabata_pause_key),"10");
        editor.putString(context.getString(R.string.prefs_Def_tabata_sets_key),"8");
        editor.putString(context.getString(R.string.prefs_Def_box_round_key),"180");
        editor.putString(context.getString(R.string.prefs_Def_box_pause_key),"60");
        editor.putString(context.getString(R.string.prefs_Def_box_sets_key),"3");
        editor.putString(context.getString(R.string.prefs_Def_interval_round_key),"30");
        editor.putString(context.getString(R.string.prefs_Def_interval_pause_key),"0");
        editor.putString(context.getString(R.string.prefs_Def_interval_sets_key),"10");
        /*
        set the preferences initialized flag
         */
        editor.putBoolean(context.getString(R.string.prefs_Def_tabata_preferences_initialized),true);
        editor.apply();

        /*
        open the user saves to store initial data
         */
        editor = context.getSharedPreferences(TimerSaver.USER_SAVED_TIMERS, Context.MODE_PRIVATE).edit();
        /*
        get the default number of timers and load it into the Timer Saves preferences
         */
        editor.putInt(context.getString(R.string.prefs_US_max_no_of_timers), TimerSaver.USER_SAVED_TIMERS_MAX_NO );
        loadDefaultTimersFromPreferences();
        editor.apply();
    }

    private static void loadDefaultTimersFromPreferences() {
        defaultTimers = new ArrayList<>();
        currentDefaultValuesIndex = 0;

        /*
        load defaults into the tabata timer
         */
        SharedPreferences prefs = context.getSharedPreferences(DEFAULT_TIMERS, Context.MODE_PRIVATE);
        TimerConfigurationValues tabataDefaults = new TimerConfigurationValues();
        tabataDefaults.setRoundDuration(prefs.getString(context.getString(R.string.prefs_Def_tabata_round_key),"0"));
        tabataDefaults.setPauseDuration(prefs.getString(context.getString(R.string.prefs_Def_tabata_pause_key),"0"));
        tabataDefaults.setSetsNumber(prefs.getString(context.getString(R.string.prefs_Def_tabata_sets_key),"0"));
        tabataDefaults.setTimerName("Tabata timer");

        /*
        load defaults into the boxing timer
         */
        TimerConfigurationValues boxingDefaults = new TimerConfigurationValues();
        boxingDefaults.setRoundDuration(prefs.getString(context.getString(R.string.prefs_Def_box_round_key),"0"));
        boxingDefaults.setPauseDuration(prefs.getString(context.getString(R.string.prefs_Def_box_pause_key),"0"));
        boxingDefaults.setSetsNumber(prefs.getString(context.getString(R.string.prefs_Def_box_sets_key),"0"));
        boxingDefaults.setTimerName("Boxing timer");

        /*
        load defaults into the interval timer
         */
        TimerConfigurationValues intervalDefaults = new TimerConfigurationValues();
        intervalDefaults.setRoundDuration(prefs.getString(context.getString(R.string.prefs_Def_interval_round_key),"0"));
        intervalDefaults.setPauseDuration(prefs.getString(context.getString(R.string.prefs_Def_interval_pause_key),"0"));
        intervalDefaults.setSetsNumber(prefs.getString(context.getString(R.string.prefs_Def_interval_sets_key),"0"));
        intervalDefaults.setTimerName("Interval timer");


        defaultTimers.add(tabataDefaults);
        defaultTimers.add(boxingDefaults);
        defaultTimers.add(intervalDefaults);
    }

    public static void loadDefaults() {
        /*
        load default timers
         */
        loadDefaultTimersFromPreferences();
        /*
        load saved timers from preferences
         */
        customTimers = TimerSaver.loadCustomTimersFromPreferences();
    }

    /**
     * Returns the next default configuration value set and increments the current index
     *
     * @return
     */
    public static TimerConfigurationValues getDefaultNext() {
        incrementIndex();
        TimerConfigurationValues returnedValueSet = defaultTimers.get(currentDefaultValuesIndex);
        return returnedValueSet;
    }

    /**
     * return the configurations value set previous to the current one in the default list
     *
     * @return
     */
    public static TimerConfigurationValues getDefaultPrevious() {
        decrementIndex();
        TimerConfigurationValues returnedValueSet = defaultTimers.get(currentDefaultValuesIndex);
        return returnedValueSet;
    }

    /**
     * return the configurations value set previous to the current one in the default list
     *
     * @return
     */
    public static TimerConfigurationValues getDefaultCurrent() {
        return defaultTimers.get(currentDefaultValuesIndex);
    }


    /**
     * decrement the index value looping back to top from 0
     */
    private static void decrementIndex() {
        if (currentDefaultValuesIndex == 0) {
            currentDefaultValuesIndex = NUMBER_OF_DEFAULTS - 1;
        } else {
            currentDefaultValuesIndex--;
        }
    }

    /**
     * increment the index looping back to playLongSound on reaching the end
     */
    private static void incrementIndex() {

        currentDefaultValuesIndex++;
        currentDefaultValuesIndex %= NUMBER_OF_DEFAULTS;
    }


    public static List<TimerConfigurationValues> getDefaultTimers() {
        return defaultTimers;
    }

    public static List<TimerConfigurationValues> getCustomTimers() {
        return customTimers;
    }
}
