package jimpatrizi.com.netrtl;

import android.util.Log;
import android.widget.CompoundButton;

import static jimpatrizi.com.netrtl.Parameters.ENABLE_OPTION;

/**
 * Created by Jim Patrizi on 11/4/2017.
 */

public class SwitchOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener
{
    /**
     * For Logcat debugging
     */
    private String TAG = getClass().getName();
    private String type;


    public SwitchOnCheckedChangeListener(final String type)
    {
        this.type = type;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
        {
            Log.v(TAG, type + " option enabled");
            ENABLE_OPTION.append(type);
        }
        else
        {
            ENABLE_OPTION.remove(type);
            Log.v(TAG, type + " option disabled");
        }
    }
}
