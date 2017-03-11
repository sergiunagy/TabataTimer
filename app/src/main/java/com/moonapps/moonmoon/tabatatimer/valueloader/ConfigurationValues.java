package com.moonapps.moonmoon.tabatatimer.valueloader;

import android.os.Parcelable;

/***
 * Implementing the Parcelable interface will allow configuration values to be passed between activities as complete, customized objects
 */
public abstract class ConfigurationValues implements Parcelable {

    /*
    loader for a saved value set selected by index
     */
    public abstract void loadValues(int storedValueSetIndex);

    /*
    save the current value set
     */
    public abstract void saveValues();

    /*
    clear the stored value sets - resets all but the default values
     */
    public abstract void clearAllSavedValues();

}
