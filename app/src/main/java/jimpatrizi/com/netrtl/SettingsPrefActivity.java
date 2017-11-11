package jimpatrizi.com.netrtl;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 *  Handles Networking Parameters, IP and Port # Set by user. Saved as SharedPreferences to be called
 *  anywhere in the code to instantiate socket connection in networking class.
 */

public class SettingsPrefActivity extends AppCompatPreferenceActivity
{
    //for debugging in logcat private static final
    //String TAG = SettingsPrefActivity.class.getSimpleName();

    /**
     * Shared Preference Editor object
     */
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editor = getSharedPreferences("pref_main", MODE_NO_LOCALIZED_COLLATORS).edit();

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);


            // ip EditText change listener, binds preference to the appopriate key defined in
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
                // for each respective preference, get the new value that is inputted
                String stringValue = newValue.toString();
                //if the preference is an edittext preference
                 if (preference instanceof EditTextPreference) {
                     //if this edittextpreference is the ip address
                     if (preference.getKey().equals("key_ip_name")) {
                         // update the changed ip name to summary filed
                         //set the ip address preference summary to the new value inputted
                         preference.setSummary(stringValue);
                         //put the new ip address in the shared preferences and apply the change
                         editor.putString("key_ip_name", stringValue);
                         editor.apply();
                         //if the preference is the port number
                     } else if (preference.getKey().equals("key_port_name")) {
                         //set the preference summary to the new value and apply the new new value to the preference
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
