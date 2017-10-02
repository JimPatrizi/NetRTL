package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;

import static jimpatrizi.com.netrtl.Parameters.*;

/**
 * Created by jamespatrizi on 9/30/17.
 */

public class FrequencyChangeButtonOnClickListener implements View.OnClickListener {

    private final int changeValue;
    private Context context;

    public FrequencyChangeButtonOnClickListener(final int changeValue, Context context){
        this.changeValue = changeValue;
        this.context = context;
    }

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
