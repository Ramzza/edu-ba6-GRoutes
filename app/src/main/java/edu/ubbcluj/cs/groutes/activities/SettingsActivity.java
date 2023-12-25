package edu.ubbcluj.cs.groutes.activities;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import edu.ubbcluj.cs.groutes.R;
import edu.ubbcluj.cs.groutes.generalhelper.SettingsManager;

public class SettingsActivity extends AppCompatActivity {

    private static int init = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);


    }

    public static class SettingsFragment extends PreferenceFragment {



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.confUseExactUntil_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confTravelMode_list)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.confDirectionTime_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confDistanceTime_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confLang_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confNavHistory_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confPositionInterval_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confZoom_list)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.confZoomInterval_list)));
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            int val = Integer.parseInt(stringValue);

            if(init<8){
                stringValue = SettingsManager.getInstance().initSettings(preference.toString());
                init++;
            }

            SettingsManager.getInstance().updateSetting(preference.toString(), val);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
