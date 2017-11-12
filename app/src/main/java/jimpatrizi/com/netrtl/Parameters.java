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

    public static final String DEFAULT_SPECIFIER = "default";

    /**
     * Each enum
     */
    private final String FUNCTION;

    /**
     * Array of Strings for each enum to hold their params
     */
    private List<String> values = new ArrayList<>();

    /**
     * Associated ui element with parameter
     */
    private Object uiElement;

    /**
     * The ui elements type
     */
    private Class uiElementSpecificType;

    /**
     * Constructor, for the enums allowed sets each to FUNCTION
     * @param function - the current enum
     */
    Parameters(final String function)
    {
        this.FUNCTION = function;
    }

    /**
     * Appends the value to that enums list
     * @param val
     */
    public void append(String val)
    {
        values.add(val);
    }

    /**
     * Associates ui member with this parameter
     * @param uiElement
     * @param uiElementSpecificType
     */
    public void setUiMembers(Object uiElement, Class uiElementSpecificType)
    {
        this.uiElement = uiElement;
        this.uiElementSpecificType = uiElementSpecificType;
    }

    /**
     * Gets the index of the string in the spinner if it exists
     *
     * @param spinner - the spinner to check
     * @param toLookUp - the string to look for in the spinner
     * @return - returns index of string in spinner if string exists
     */
    protected int getIndex(Spinner spinner, String toLookUp)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(toLookUp)){
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Updates the associated parameter uo field on the UI thread, used in ResponseListener.
     * For each type of ui element, calls respective setters to change their values programmatically
     * One thing to note, must set ui elements from ui thread only!! Crashes happen otherwise
     * @param mainActivity - main activity
     * @param newVal - the new value of the ui thread to be set to.
     */
    public void updateField(Activity mainActivity, final String newVal)
    {
        //if EditText
        if (uiElementSpecificType.equals(android.support.v7.widget.AppCompatEditText.class))
        {
            mainActivity.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           //setText on Edit Text objects to newval
                           ((android.widget.EditText) uiElement).setText(newVal);
                       }
                   }
            );
        }
        //if enableoptionmatcherclass, call the enableswitchbystring method for that enable option
        if (uiElementSpecificType.equals(EnableOptionUiMatcher.class))
        {
            ((EnableOptionUiMatcher) uiElement).enableSwitchByString(mainActivity, newVal);
        }

        //if seekbar, set progress of seek bar to new val
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

        //if Spinner, set the spinner to the index of the new val
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

    /**
     * Get uiElement for Parameter
     * @return returns UI element for this parameter
     */
    public Object getUiElement()
    {
        return uiElement;
    }

    /**
     * Removes val from list of values for Parameter
     * @param val - value to be removed
     * @return - returns true if value is successfully removed
     */
    public boolean remove(String val)
    {
        return values.remove(val);
    }

    /**
     * Clear all values for this parameter
     */
    public void resetValues()
    {
        values.clear();
    }

    /**
     * Return the value at this index
     * @param idx - index to lookup value at
     * @return - returns string value at the idx
     */
    public String getByIndex(int idx)
    {
        return values.get(idx);
    }

    /**
     * Checks if the index is valid
     * @param idx - index to check
     * @return - returns boolean of condition idx < values.size()
     */
    public boolean isIndexValid(int idx)
    {
        if (idx < 0)
        {
            return false;
        }

        return idx < values.size();
    }

    /**
     * Replaces current index with val
     * @param idx - index to replace
     * @param val - value to replace in index
     * @return - if index doesn't exist, return false
     */
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

    /**
     * Get all values of arraylist
     * @return - array list of values
     */
    public List<String> getValues()
    {
        return new ArrayList<>(values);
    }

    /**
     * Makes a new list of daemon formatted strings to send to the server
     * @return - list of daemon formatted strings
     */
    public List<String> getDameonCallableStrings(){
        List<String> dameonStrings = new ArrayList<>();
        for(String s : values){
            dameonStrings.add(FUNCTION + "=" + s);
        }
        return dameonStrings;
    }

    /**
     * Returns the current parameter
     * @return - this parameter
     */
    public String getFunction()
    {
        return FUNCTION;
    }
}
