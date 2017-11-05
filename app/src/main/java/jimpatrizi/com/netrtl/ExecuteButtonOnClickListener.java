package jimpatrizi.com.netrtl;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jim Patrizi on 10/2/2017.
 */



public class ExecuteButtonOnClickListener implements View.OnClickListener {
    private Context context;

    private String frequency;
    private String modulationMode;
    private String overSampling;
    private String sampleRate;
    private String squelch;
    private String resampleRate;
    private String gain;
    private String volume;

    String dameon = "";

    private String TAG = getClass().getName();



    ExecuteButtonOnClickListener(Context context){
        this.context = context;
    }

    //TODO Bug for Enable Options, after sending them with execute once, only the last indexed param remains
    @Override
    public void onClick(View view) {
        if(MainActivity.isConnected()) {
            for (Parameters p : Parameters.values()) {
                for (String s : p.getDameonCallableStrings()) {

                    MainActivity.getTcpClient().sendToServer(s);
                    Log.d(TAG, s);
                }
            }
            MainActivity.getTcpClient().sendToServer("EXECUTE");
        }
        else
        {
            Toast.makeText(context, "Not connected to daemon", Toast.LENGTH_SHORT).show();
        }
    }
}