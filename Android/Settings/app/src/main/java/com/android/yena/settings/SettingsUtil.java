package com.android.yena.settings;

import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by Yena on 2016-11-01.
 */
class SettingsUtil {
    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
//     * @see #sBindPreferenceSummaryToValueListener
     */
    static void bindPreferenceChangedListener(Preference preference, Preference.OnPreferenceChangeListener listener) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(listener);

        // Trigger the listener immediately with the preference's
        // current value.
        listener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    static void bindPreferenceChangedListener(Preference preference, Preference.OnPreferenceClickListener listener) {
        preference.setOnPreferenceClickListener(listener);
    }
}
