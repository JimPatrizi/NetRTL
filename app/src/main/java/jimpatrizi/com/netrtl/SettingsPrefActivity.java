package jimpatrizi.com.netrtl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsPrefActivity extends AppCompatPreferenceActivity
{
    private static final String TAG = SettingsPrefActivity.class.getSimpleName();
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editor = getSharedPreferences("pref_main", MODE_PRIVATE).edit();

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);


            // ip EditText change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_ip_name)));

            // port edittext preference change listener
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_port_name)));
        }


        private static void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();

                 if (preference instanceof EditTextPreference) {
                     if (preference.getKey().equals("key_ip_name")) {
                         // update the changed ip name to summary filed
                         preference.setSummary(stringValue);
                         editor.putString("key_ip_name", stringValue);
                         editor.apply();
                     } else if (preference.getKey().equals("key_port_name")) {
                         preference.setSummary(stringValue);
                         editor.putInt("key_port_name", Integer.parseInt(stringValue));
                         editor.apply();
                     }
                 }
                return true;
            }
        };
    }
}