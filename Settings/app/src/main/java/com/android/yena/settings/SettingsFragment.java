package com.android.yena.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import com.android.yena.common.util.Log;

/**
 * Created by Yena on 2016-11-01.
 */
public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = "SettingsFragment";

    Preference.OnPreferenceClickListener clickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            Log.d(TAG, "onPreferenceClick) key=" +key);
            if (TextUtils.isEmpty(key)) {
                return false;
            }

            if (preference instanceof PreferenceScreen) {
                switch (key) {
                    case "simple_use": {
                        getFragmentManager().beginTransaction()
                                .replace(android.R.id.content, new SimplePayUseFragment())
                                .addToBackStack(null).commit();
                        break;
                    }
                }
            }
            return true;
        }
    };

    Preference.OnPreferenceChangeListener changedListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            Log.d(TAG, "onPreferenceChange) key=" +key + " newValue=" + newValue);
            if (TextUtils.isEmpty(key)) {
                return false;
            }

            if (preference instanceof PreferenceScreen) {

            } else if (preference instanceof SwitchPreference) {
                switch (key) {
                    case "payplanner_use": {
//                        Boolean value = (Boolean) newValue;
//                        PreferenceScreen plannerGroup = (PreferenceScreen) getPreferenceManager().findPreference("planner");
//                        Preference plannerDependencyPref = getPreferenceManager().findPreference("payplanner_sms_use");
//                        if (!value) {
//                            plannerGroup.removePreference(plannerDependencyPref);
//                        } else {
//                            plannerGroup.addPreference(plannerDependencyPref);
//                        }
                        break;
                    }
                }
            }
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings_list);
        setHasOptionsMenu(true);


//        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
//        int categoryCount = this.getPreferenceScreen().getPreferenceCount();
//        for (int i = 0; i < categoryCount; i++) {
//            PreferenceCategory category = (PreferenceCategory) this.getPreferenceScreen().getPreference(i);
//            int count = category.getPreferenceCount();
//            for (int j = 0; j < count; j++) {
//                Preference preference = category.getPreference(j);
//                String key = preference.getKey();
//                String value = sharedPreferences.getString(key, "");
//                Log.d(TAG, "[" + i + "/" + j + "] key=" + key + ", value=" + value);
//                if (key != null) {
//                    preference.setOnPreferenceClickListener(clickListener);
//                    SettingsUtil.bindPreferenceChangedListener(preference, changedListener);
//                }
//            }
//        }
        setListenerAllPreference(getPreferenceScreen());
    }

    void setListenerAllPreference(Preference preference) {
        if (preference instanceof PreferenceCategory) {
            PreferenceCategory category = (PreferenceCategory) preference;
            int count = category.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                setListenerAllPreference(category.getPreference(i));
            }
        } else if (preference instanceof PreferenceScreen) {
            PreferenceScreen screen = (PreferenceScreen) preference;
            int count = screen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                setListenerAllPreference(screen.getPreference(i));
            }
            screen.setOnPreferenceClickListener(clickListener);
        } else {
            String key = preference.getKey();
            Object value;
            if (preference instanceof SwitchPreference) {
                value = preference.getSharedPreferences().getBoolean(key, false);
            } else {
                value = preference.getSharedPreferences().getString(key, "");
            }
            Log.d(TAG, "key=" + key + ", value=" + value);

//            Log.d(TAG, "[" + i + "/" + j + "] key=" + key + ", value=" + value);
            if (key != null) {
                preference.setOnPreferenceClickListener(clickListener);
                preference.setOnPreferenceChangeListener(changedListener);
                changedListener.onPreferenceChange(preference, value);
            }
        }
    }

}
