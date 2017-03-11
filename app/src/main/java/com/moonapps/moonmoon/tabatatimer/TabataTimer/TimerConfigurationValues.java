package com.moonapps.moonmoon.tabatatimer.TabataTimer;

import android.os.Parcel;
import android.os.Parcelable;

import com.moonapps.moonmoon.tabatatimer.R;
import com.moonapps.moonmoon.tabatatimer.utils.App;
import com.moonapps.moonmoon.tabatatimer.valueloader.ConfigurationValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Moon Moon on 2/17/2017.
 */

public class TimerConfigurationValues extends ConfigurationValues {

    public static final Parcelable.Creator<TimerConfigurationValues> CREATOR = new Parcelable.Creator<TimerConfigurationValues>() {

        @Override
        public TimerConfigurationValues createFromParcel(Parcel source) {
            return new TimerConfigurationValues(source);
        }

        @Override
        public TimerConfigurationValues[] newArray(int size) {
            return new TimerConfigurationValues[size];
        }
    };
    private String roundDuration;
    private String pauseDuration;
    private String setsNumber;
    private String timerName;
/*******************************************/
    /**Parcelable methods - allow the passing of objects instantiating this class between activities*/
    /***
     * extract the parameters in the order they were entered in the parcel
     *
     * @param source
     */
    public TimerConfigurationValues(Parcel source) {

        List<String> paramList = new ArrayList<>();
        source.readStringList(paramList);
        roundDuration = paramList.get(0);
        pauseDuration = paramList.get(1);
        setsNumber = paramList.get(2);
        timerName = paramList.get(3);
    }

    public TimerConfigurationValues() {
        super();

    }

    /*****************************************/
    @Override
    public int describeContents() {
        return 0;
    }


/*****************************************************/
    /*End of Parcelable methods section*/

    /***
     * Store class fields in a Parcel object so they can be passed completely to other Activities
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        List<String> paramList = new ArrayList<String>();
        paramList.add(roundDuration);
        paramList.add(pauseDuration);
        paramList.add(setsNumber);
        paramList.add(timerName);
        dest.writeStringList(paramList);
    }

    /********************************************************/

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public String getRoundDuration() {
        return roundDuration;
    }

    public void setRoundDuration(String roundDuration) {
        this.roundDuration = roundDuration;
    }

    public String getPauseDuration() {
        return pauseDuration;
    }

    public void setPauseDuration(String pauseDuration) {
        this.pauseDuration = pauseDuration;
    }

    public String getSetsNumber() {
        return setsNumber;
    }

    public void setSetsNumber(String setsNumber) {
        this.setsNumber = setsNumber;
    }

    /**
     * load default set of values
     */
    private void loadDefaults() {

        roundDuration = App.getContext().getResources().getString(R.string.tabataDefaultRound);
        pauseDuration = App.getContext().getResources().getString(R.string.tabataDefaultPause);
        setsNumber = App.getContext().getResources().getString(R.string.tabataDefaultSetsNo);
    }

    @Override
    public void loadValues(int storedValueSetIndex) {

    }

    @Override
    public void saveValues() {

    }

    @Override
    public void clearAllSavedValues() {

    }


}
