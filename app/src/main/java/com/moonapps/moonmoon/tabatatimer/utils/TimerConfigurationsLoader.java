package com.moonapps.moonmoon.tabatatimer.utils;

import com.moonapps.moonmoon.tabatatimer.TabataTimer.TimerConfigurationValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the configuration value sets to load
 */

public class TimerConfigurationsLoader {

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
    private static final int NUMBER_OF_DEFAULTS = 3 ;

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
        defaultTimers = new ArrayList<>();
        currentDefaultValuesIndex = 0;
        /*
        load defaults into the tabata timer
         */
        TimerConfigurationValues tabataDefaults = new TimerConfigurationValues();
        tabataDefaults.setSetsNumber("8");
        tabataDefaults.setRoundDuration("20");
        tabataDefaults.setPauseDuration("10");
        tabataDefaults.setTimerName("Tabata timer");

        /*
        load defaults into the boxing timer
         */
        TimerConfigurationValues boxingDefaults = new TimerConfigurationValues();
        boxingDefaults.setSetsNumber("3");
        boxingDefaults.setRoundDuration("180");
        boxingDefaults.setPauseDuration("60");
        boxingDefaults.setTimerName("Boxing timer");

        /*
        load defaults into the interval timer
         */
        TimerConfigurationValues intervalDefaults = new TimerConfigurationValues();
        intervalDefaults.setSetsNumber("5");
        intervalDefaults.setRoundDuration("30");
        intervalDefaults.setPauseDuration("0");
        intervalDefaults.setTimerName("Interval timer");


        defaultTimers.add(tabataDefaults);
        defaultTimers.add(boxingDefaults);
        defaultTimers.add(intervalDefaults);

    }

    /**
     * Returns the next default configuration value set and increments the current index
     * @return
     */
    public static TimerConfigurationValues getDefaultNext() {
        incrementIndex();
        TimerConfigurationValues returnedValueSet = defaultTimers.get(currentDefaultValuesIndex);
        return returnedValueSet;
    }

    /**
     * return the configurations value set previous to the current one in the default list
     * @return
     */
   public static TimerConfigurationValues getDefaultPrevious() {
       decrementIndex();
       TimerConfigurationValues returnedValueSet = defaultTimers.get(currentDefaultValuesIndex);
        return returnedValueSet;
    }

    /**
     * return the configurations value set previous to the current one in the default list
     * @return
     */
   public static TimerConfigurationValues getDefaultCurrent() {

        return  defaultTimers.get(currentDefaultValuesIndex);
    }


    /**
     * decrement the index value looping back to top from 0
     */
    private static void decrementIndex() {
        if (currentDefaultValuesIndex == 0){
            currentDefaultValuesIndex = NUMBER_OF_DEFAULTS-1;
        }else {
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



}
