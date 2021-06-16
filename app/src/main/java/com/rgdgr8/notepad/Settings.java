package com.rgdgr8.notepad;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends PreferenceFragmentCompat {

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference preference=findPreference(SettingsActivity.ring);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        assert preference != null;

        if (Build.VERSION.SDK_INT>=26){
            preferenceScreen.removePreference(preference);
        }else {
            preferenceScreen.addPreference(preference);
        }

    }
}
