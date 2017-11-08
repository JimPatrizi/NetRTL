package jimpatrizi.com.netrtl;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Jim Patrizi on 11/4/2017.
 */

public class InputTextWatcher implements TextWatcher
{
    private EditText input;
    private String type;

    public InputTextWatcher(EditText input, String type)
    {
        this.input = input;
        this.type = type;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        String inputString;

        if(type.equals(MainActivity.HZFIELD))
        {
            inputString = input.getText().toString();

            // Entering a scannable freq in the form MIN:MAX:INCREMENT
            if(inputString.contains(":"))
            {
                Parameters.SCANNABLE_FREQUENCY.resetValues();
                Parameters.FREQUENCY.resetValues();
                Parameters.SCANNABLE_FREQUENCY.append(inputString);
            }

            // Standard frequency input
            else
            {
                Parameters.SCANNABLE_FREQUENCY.resetValues();
                Parameters.FREQUENCY.resetValues();
                Parameters.FREQUENCY.append(inputString);
            }
        }

        else if(type.equals(MainActivity.SAMPLE))
        {
            inputString = input.getText().toString();
            Parameters.SAMPLE_RATE.replaceIndex(0, inputString);
        }

        else if(type.equals(MainActivity.RESAMPLE))
        {
            inputString = input.getText().toString();
            Parameters.RESAMPLE_RATE.replaceIndex(0, inputString);
        }

        else if(type.equals(MainActivity.PPM))
        {
            inputString = input.getText().toString();
            Parameters.PPM_ERROR.replaceIndex(0, inputString);
        }
        else if(type.equals(MainActivity.DELAY))
        {
            inputString = input.getText().toString();
            Parameters.SQUELCH_DELAY.replaceIndex(0, inputString);
        }

    }
}


