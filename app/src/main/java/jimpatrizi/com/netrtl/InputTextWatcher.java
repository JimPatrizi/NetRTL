package jimpatrizi.com.netrtl;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Jim Patrizi on 11/4/2017.
 *
 * Class that watches all EditText Field for after text changes to adjust corresponding Parameters
 */

public class InputTextWatcher implements TextWatcher
{
    /**
     * The EditText member
     */
    private EditText input;

    /**
     * The Edit Text type we are adjusting
     */
    private String type;

    /**
     * Constructor that takes the current EditText and its type to be watched
     * @param input - this Edit Text Field
     * @param type - current type
     */
    public InputTextWatcher(EditText input, String type)
    {
        this.input = input;
        this.type = type;
    }

    /**
     * Not used
     * @param charSequence
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    /**
     * Not used
     * @param charSequence
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    /**
     * After the user is done editing the text, the value inside each respective edit text is then
     * used to adjust the respective Parameter
     * @param editable -  not used
     */
    @Override
    public void afterTextChanged(Editable editable)
    {
        /**
         * The current input
         */
        String inputString;

        /**
         * HZ Field Edit Text
         */
        if(type.equals(MainActivity.HZFIELD))
        {
            inputString = input.getText().toString();

            // Entering a scannable freq in the form MIN:MAX:INCREMENT
            //if the inputstring contains a :, we know the user wants scannable frequency parameter,
            // not frequency
            if(inputString.contains(":"))
            {
                Parameters.SCANNABLE_FREQUENCY.resetValues();
                //clear FREQUENCY so we do not used it
                Parameters.FREQUENCY.resetValues();
                Parameters.SCANNABLE_FREQUENCY.append(inputString);
            }

            // Nothing specified
            else if (inputString.isEmpty())
            {
                Parameters.FREQUENCY.resetValues();
                Parameters.SCANNABLE_FREQUENCY.resetValues();
                Parameters.FREQUENCY.append(Parameters.DEFAULT_SPECIFIER);
            }

            // Standard frequency input
            else
            {
                Parameters.SCANNABLE_FREQUENCY.resetValues();
                Parameters.FREQUENCY.resetValues();
                Parameters.FREQUENCY.append(inputString);
            }
        }

        /**
         * Sampling Rate Edit Text
         */
        else if(type.equals(MainActivity.SAMPLE))
        {
            inputString = input.getText().toString();

            if (inputString.isEmpty())
            {
                Parameters.SAMPLE_RATE.replaceIndex(0, Parameters.DEFAULT_SPECIFIER);
            }
            else
            {
                Parameters.SAMPLE_RATE.replaceIndex(0, inputString);
            }
        }

        /**
         * Resampling Rate Edit Text
         */
        else if(type.equals(MainActivity.RESAMPLE))
        {
            inputString = input.getText().toString();

            if (inputString.isEmpty())
            {
                Parameters.RESAMPLE_RATE.replaceIndex(0, Parameters.DEFAULT_SPECIFIER);
            }
            else
            {
                Parameters.RESAMPLE_RATE.replaceIndex(0, inputString);
            }
        }

        /**
         * PPM Error Edit text
         */
        else if(type.equals(MainActivity.PPM))
        {
            inputString = input.getText().toString();

            if (inputString.isEmpty())
            {
                Parameters.PPM_ERROR.replaceIndex(0, Parameters.DEFAULT_SPECIFIER);
            }
            else
            {
                Parameters.PPM_ERROR.replaceIndex(0, inputString);
            }
        }

        /**
         * Squelch Delay Edit Text
         */
        else if(type.equals(MainActivity.DELAY))
        {
            inputString = input.getText().toString();

            if (inputString.isEmpty())
            {
                Parameters.SQUELCH_DELAY.replaceIndex(0, Parameters.DEFAULT_SPECIFIER);
            }
            else
            {
                Parameters.SQUELCH_DELAY.replaceIndex(0, inputString);
            }
        }
    }
}


