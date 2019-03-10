package com.ucll.eventure;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeLaunchedManager {

    private SharedPreferences pref;
    private static final String PREF_NAME = "welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public FirstTimeLaunchedManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, 0);
    }

    /**
     * Method that sets if it is the first time launched or not.
     *
     * @param isFirstTime indicator of first launch or not.
     */
    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    /**
     * Method that checks if the app has been previously launched when the user was verrified.
     *
     * @return boolean to indicate if previously verrified.
     */
    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
