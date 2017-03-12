package com.moonapps.moonmoon.tabatatimer.user_preferences;

import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.moonapps.moonmoon.tabatatimer.R;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.activity_preferences, new SettingsFragment())
                .commit();
    }


    /***
     * use a fragment to prevent options from reseting if activity is destroyed
     */
    public static class SettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            /*
            load preferences from the xml resource
             */
            addPreferencesFromResource(R.xml.settings);
        }
    }
}
