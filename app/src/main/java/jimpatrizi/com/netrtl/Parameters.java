package jimpatrizi.com.netrtl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamespatrizi on 9/30/17.
 */

public enum Parameters {
    FREQUENCY("FREQUENCY"), BROADCAST_FM("BROADCAST_FM"), SAMPLE_RATE("SAMPLE_RATE"), ATAN_MATH("ATAN_MATH");

    private final String FUNCTION;
    private final List<String> values;

    private Parameters(final String function)
    {
        this.FUNCTION = function;
        values = new ArrayList<>();
    }

    public void append(String val)
    {
        values.add(val);
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

        if (idx < values.size())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //if indx doesnt exist, write the val and return true
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
}
