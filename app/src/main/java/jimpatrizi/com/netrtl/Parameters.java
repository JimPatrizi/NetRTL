package jimpatrizi.com.netrtl;

import android.app.Activity;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates enums for all rtlsdrd dameon parameters
 * Created by jamespatrizi on 9/30/17.
 * @author Jim Patrizi
 */

public enum Parameters {
    FREQUENCY("FREQUENCY"),
    SQUELCH_LEVEL("SQUELCH_LEVEL"),
    SQUELCH_DELAY("SQUELCH_DELAY"),
    TUNER_GAIN("TUNER_GAIN"),
    MODULATION_MODE("MODULATION_MODE"),
    OVERSAMPLING("OVERSAMPLING"),
    ENABLE_OPTION("ENABLE_OPTION"),
    SCANNABLE_FREQUENCY("SCANNABLE_FREQUENCY"),
    PPM_ERROR("PPM_ERROR"),
    SAMPLE_RATE("SAMPLE_RATE"),
    RESAMPLE_RATE("RESAMPLE_RATE"),
    ATAN_MATH("ATAN_MATH"),
    FIR_SIZE("FIR_SIZE"),
    VOLUME("VOLUME");

    private final String FUNCTION;
    private List<String> values = new ArrayList<>();
    private Object uiElement;
    private Class uiElementSpecificType;

    Parameters(final String function)
    {
        this.FUNCTION = function;
    }

    public void append(String val)
    {
        values.add(val);
    }

    public void setUiMembers(Object uiElement, Class uiElementSpecificType)
    {
        this.uiElement = uiElement;
        this.uiElementSpecificType = uiElementSpecificType;
    }

    //private method of your class
    protected int getIndex(Spinner spinner, String string)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(string)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void updateField(Activity mainActivity, final String newVal)
    {
        if (uiElementSpecificType.equals(android.support.v7.widget.AppCompatEditText.class))
        {
            mainActivity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           ((android.widget.EditText) uiElement).setText(newVal);
                       }
                   }
            );
        }
        if (uiElementSpecificType.equals(EnableOptionUiMatcher.class))
        {
            ((EnableOptionUiMatcher) uiElement).enableSwitchByString(mainActivity, newVal);
        }

        if (uiElementSpecificType.equals(android.support.v7.widget.AppCompatSeekBar.class))
        {
            final int newValInt = Integer.parseInt(newVal);
            mainActivity.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               ((android.widget.SeekBar) uiElement).setProgress(newValInt);
                                           }
                                       }
            );
        }

        if (uiElementSpecificType.equals(android.support.v7.widget.AppCompatSpinner.class))
        {
            mainActivity.runOnUiThread(new Runnable() {
                                           @Override
                                           public void run() {
                                               ((android.widget.Spinner) uiElement).setSelection(getIndex((android.widget.Spinner) uiElement, newVal));
                                           }
                                       }
            );
        }
    }

    public Object getUiElement()
    {
        return uiElement;
    }

    public boolean remove(String val)
    {
        return values.remove(val);
    }

    public void resetValues()
    {
        values.clear();
    }

    public String getByIndex(int idx)
    {
        return values.get(idx);
    }

    public boolean isIndexValid(int idx)
    {
        if (idx < 0)
        {
            return false;
        }

        return idx < values.size();
    }

    //if indx doesnt exist, return false
    public boolean replaceIndex(int idx, String val)
    {
        if (!isIndexValid(idx))
        {
            return false;
        }
        else
        {
            values.set(idx, val);
            return true;
        }
    }

    public List<String> getValues()
    {
        return new ArrayList<>(values);
    }

    public List<String> getDameonCallableStrings(){
        List<String> dameonStrings = new ArrayList<>();
        for(String s : values){
            dameonStrings.add(FUNCTION + "=" + s);
        }
        return dameonStrings;
    }

    public String getFunction()
    {
        return FUNCTION;
    }
}
