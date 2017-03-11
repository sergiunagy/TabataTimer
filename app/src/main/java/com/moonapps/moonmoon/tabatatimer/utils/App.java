package com.moonapps.moonmoon.tabatatimer.utils;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Class used to get access to string.xml resource from any Java class
 */

public class App extends Application {

    private static WeakReference<Context> myContext;

    @Override
    public void onCreate() {
        super.onCreate();
        myContext = new WeakReference<Context>(this);
    }

    public static Context getContext(){
        return myContext.get();
    }
}
