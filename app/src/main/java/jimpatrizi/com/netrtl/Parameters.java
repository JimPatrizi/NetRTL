package jimpatrizi.com.netrtl;

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
    TUNER_GAIN("TUNER_GAIN"),
    MODULATION_MODE("MODULATION_MODE"),
//    SCANNABLE_FREQUENCY("SCANNABLE_FREQUENCY"), OVERSAMPLING("OVERSAMPLING"), PPM_ERROR("PPM_ERROR"),
    SAMPLE_RATE("SAMPLE_RATE"), //SQUELCH_DELAY("SQUELCH_DELAY"), ,
    RESAMPLE_RATE("RESAMPLE_RATE"), //ATAN_MATH("ATAN_MATH"),
    VOLUME("VOLUME");
    //STOP("STOP"), CMDS_IN_USE("CMDS_IN_USE"), MY_STORED_CMDS("MY_STORED_CMDS"),
    //EXECUTE("EXECUTE"), CLEAR("CLEAR");//BROADCAST_AM("BROADCAST_AM"), BROADCAST_FM("BROADCAST_FM");




    private final String FUNCTION;
    private List<String> values = new ArrayList<>();

    Parameters(final String function)
    {
        this.FUNCTION = function;
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
