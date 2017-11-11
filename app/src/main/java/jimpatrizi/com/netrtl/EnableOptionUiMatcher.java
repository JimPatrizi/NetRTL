package jimpatrizi.com.netrtl;

import android.app.Activity;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamespatrizi on 11/6/17.
 *
 * Package protected class that associates all enable option UI elements to the respective keys to be appended
 * to the enable options paraeter
 */

class EnableOptionUiMatcher {

    /**
     * Map that holds Enable Option and UI Switch
     */
    private final Map<String, Switch> mapper;

    EnableOptionUiMatcher()
    {
        mapper = new HashMap<>();
    }

    /**
     * Adds key and switch to map
     * @param key - enable option key
     * @param uiSwitch - ui switch element
     */
    void add(String key, Switch uiSwitch)
    {
        mapper.put(key, uiSwitch);
    }

    /**
     * Sets the appopriate UI element to true in UI thread, used in response listener thread
     * @param mainActivity - the main activity reference
     * @param value - Enable Option value to check true
     */
    void enableSwitchByString(Activity mainActivity, String value)
    {
        /**
         * For all keys in the map, if the key equals the provided value, set that ui element to true
         */
        for (final String key : mapper.keySet())
        {
            if (key.equals(value))
            {
                mainActivity.runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                       mapper.get(key).setChecked(true);
                   }
                                           }
                );

            }
        }
    }

    /**
     * Unchecks all switches in the map
     * @param mainActivity - main activity reference
     */
    void uncheckAll(Activity mainActivity)
    {
        for (final String key : mapper.keySet())
        {
            mainActivity.runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       mapper.get(key).setChecked(false);
                   }
               }
            );
        }
    }


}
