package it.optimizeme;

import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .replace(R.id.settingsFrame, new MyPreferenceFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        private EditTextPreference timerTask;
        private EditTextPreference timerShortBreak;
        private EditTextPreference timerLongBreak;
        private SharedPreferences SP;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);

            SP = PreferenceManager.getDefaultSharedPreferences(getActivity());

            timerTask = (EditTextPreference) findPreference("settings_timer_task");
            timerShortBreak = (EditTextPreference) findPreference("settings_timer_short_break");
            timerLongBreak = (EditTextPreference) findPreference("settings_timer_long_break");

            timerTask.setSummary(SP.getString("settings_timer_task", "25"));
            timerShortBreak.setSummary(SP.getString("settings_timer_short_break", "3"));
            timerLongBreak.setSummary(SP.getString("settings_timer_long_break", "25"));

            timerTask.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    timerTask.setSummary(newValue.toString());
                    return true;
                }
            });
            timerShortBreak.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    timerShortBreak.setSummary(newValue.toString());
                    return true;
                }
            });
            timerLongBreak.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    timerLongBreak.setSummary(newValue.toString());
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}