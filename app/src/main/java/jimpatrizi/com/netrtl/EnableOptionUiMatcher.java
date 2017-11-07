package jimpatrizi.com.netrtl;

import android.app.Activity;
import android.widget.Switch;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jamespatrizi on 11/6/17.
 */

class EnableOptionUiMatcher {

    private final Map<String, Switch> mapper;

    EnableOptionUiMatcher()
    {
        mapper = new HashMap<>();
    }

    void add(String key, Switch uiSwitch)
    {
        mapper.put(key, uiSwitch);
    }

    void enableSwitchByString(Activity mainActivity, String value)
    {
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
