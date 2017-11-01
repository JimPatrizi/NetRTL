package jimpatrizi.com.netrtl;

import android.content.Context;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static jimpatrizi.com.netrtl.Parameters.SQUELCH_LEVEL;
import static jimpatrizi.com.netrtl.Parameters.TUNER_GAIN;
import static jimpatrizi.com.netrtl.Parameters.VOLUME;

/**
 * Created by jamespatrizi on 10/21/17.
 */

public class SeekBarChangeOnClickListener implements SeekBar.OnSeekBarChangeListener
{
    private Context context;
    private int progressChangedValue = 0;
    private String gainChange = "0";
    private  String type;
    private TextView textView;
    private  int AGC = -100;

    /**
     * Constructor that gets the new value
     * @param context
     */
    public SeekBarChangeOnClickListener(Context context, TextView textView, String type){
        this.context = context;
        this.type = type;
        this.textView = textView;
    }


    /**
     * TODO Ask Ben about Tuner Gain defaults
     * For when i forget later and I need to fix this gain.
     * How many values there are between two integers? -> 13

     How many integers are needed? 8 - 2 = 6

     How many values in overall? -> 13 * 6 = 78 (max value of Seekbar)

     How to show the value?

     (progress / 13) + "." + (progress % 13)

     * @param seekBar
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progressChangedValue = progress;
//        if(type.equals("gain"))
//        {
//            gainChange = (progress / 13) + "." + (progress % 13);
//        }
//        else
//        {
//            progressChangedValue = progress;
//        }

    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub, do i need this?
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if(type.equalsIgnoreCase("volume"))
        {
            if (VOLUME.isIndexValid(0)) {
                VOLUME.replaceIndex(0, "" + progressChangedValue);
            } else {
                VOLUME.append("" + progressChangedValue);
            }
            Toast.makeText(context, "System Volume =  " + progressChangedValue + "%",
                    Toast.LENGTH_SHORT).show();
            //textView.append(Integer.toString(progressChangedValue), 7, 10);
        }
        else if(type.equalsIgnoreCase("gain"))
        {
            if(progressChangedValue != 0)
            {
                if (TUNER_GAIN.isIndexValid(0)) {
                    TUNER_GAIN.replaceIndex(0, "" + progressChangedValue);
                } else {
                    TUNER_GAIN.append("" + progressChangedValue);
                }
                Toast.makeText(context, "Gain (dB) =  " + progressChangedValue + "dB",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (TUNER_GAIN.isIndexValid(0)) {
                    TUNER_GAIN.replaceIndex(0, "" + AGC);
                } else {
                    TUNER_GAIN.append("" + AGC);
                }
                Toast.makeText(context, "Automatic Gain Control Enabled", Toast.LENGTH_SHORT).show();
            }
        }
        else //if(type.equalsIgnoreCase("squelch")
        {
            if (SQUELCH_LEVEL.isIndexValid(0)) {
                SQUELCH_LEVEL.replaceIndex(0, "" + progressChangedValue);
            } else {
                SQUELCH_LEVEL.append("" + progressChangedValue);
            }
            Toast.makeText(context, "Squelch Level =  " + progressChangedValue + "%",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
