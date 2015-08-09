package com.goodstart.minutescheckerlib;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class GsPreferencesActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {        
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // These preferences are automatically read from and saved to
        // the 'default shared preferences' for this app
        // Here is how you read them out:
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// boolean popupBeforeCall = prefs.getBoolean("popupBeforeCall", true);
		// boolean popupAfterCall = prefs.getBoolean("popupAfterCall", true);
    }
}
