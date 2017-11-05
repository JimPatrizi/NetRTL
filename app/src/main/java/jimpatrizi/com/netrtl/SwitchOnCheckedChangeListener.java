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

    private String direct = "direct";
    private String edge = "edge";
    private String dc = "dc";
    private String deemp = "deemp";
    private String offset = "offset";

    public SwitchOnCheckedChangeListener(String type)
    {
        this.type = type;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
        {
            if(type.equals(direct)) {
                Log.v(TAG, "direct option enabled");
                ENABLE_OPTION.append(direct);
            }

            else if(type.equals(edge))
            {
                Log.v(TAG, "edge option enabled");
                ENABLE_OPTION.append(edge);
            }

            else if(type.equals(dc))
            {
                Log.v(TAG, "dc option enabled");
                ENABLE_OPTION.append(dc);
            }

            else if(type.equals(deemp))
            {
                Log.v(TAG, "deemp option enabled");
                ENABLE_OPTION.append(deemp);
            }

            else if(type.equals(offset))
            {
                Log.v(TAG, "offset option enabled");
                ENABLE_OPTION.append(offset);
            }
        }

        else if(!isChecked)
        {
            if(type.equals(direct))
            {
                ENABLE_OPTION.remove(direct);
                Log.v(TAG, "direct option disabled");
            }
            else if(type.equals(edge))
            {
                ENABLE_OPTION.remove(edge);
                Log.v(TAG, "edge option disabled");
            }

            else if(type.equals(dc))
            {
                ENABLE_OPTION.remove(dc);
                Log.v(TAG, "dc option disabled");
            }

            else if(type.equals(deemp))
            {
                ENABLE_OPTION.remove(deemp);
                Log.v(TAG, "deemp option disabled");
            }

            else if(type.equals(offset))
            {
                ENABLE_OPTION.remove(offset);
                Log.v(TAG, "offset option disabled");
            }
        }
    }

}
