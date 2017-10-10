package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;

import static jimpatrizi.com.netrtl.Parameters.*;

/**
 * Created by jamespatrizi on 9/30/17.
 *
 * Class that makes frequency buttons change listener and makes sure the new frequency is valid from parameters class
 * @author Jim Patrizi
 */

public class FrequencyChangeButtonOnClickListener implements View.OnClickListener {

    private final int changeValue;
    private Context context;

    /**
     * Constructor that gets the new value
     * @param changeValue
     * @param context
     */
    public FrequencyChangeButtonOnClickListener(final int changeValue, Context context){
        this.changeValue = changeValue;
        this.context = context;
    }

    /**
     * Increments/ decrements frequency by changeValue on click, if nothing is present, start the list
     * @param v default
     */
    @Override
    public void onClick(View v) {
        if (FREQUENCY.isIndexValid(0))
        {
            FREQUENCY.replaceIndex(0, "" + (Integer.parseInt(FREQUENCY.getByIndex(0)) + changeValue));
        }
        else
        {
            FREQUENCY.append("" + changeValue);
        }
    }




}
