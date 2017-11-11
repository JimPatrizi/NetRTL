package jimpatrizi.com.netrtl;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jim Patrizi on 10/2/2017.
 *
 * OnClick Listener for Execute button, handles kick off to daemon
 */
public class ExecuteButtonOnClickListener implements View.OnClickListener {

    /**
     * Application Context
     */
    private Context context;

    /**
     * Logcat Debugging
     */
    private String TAG = getClass().getName();

    /**
     * Constructor for listener, gets the current application context for toast msgs
     * @param context
     */
    ExecuteButtonOnClickListener(Context context){
        this.context = context;
    }

    /**
     * When execute button is clicked, this function is called, sends parameters to daemon
     * @param view - not used
     */
    @Override
    public void onClick(View view) {
        //checks if the thread/client are connected
        if(MainActivity.isConnected()) {
            //for all parameter values, get their respective daemoncallbackstrings and send thosse individually
            //to the server, server handles \n between each string
            for (Parameters p : Parameters.values()) {
                for (String s : p.getDameonCallableStrings()) {

                    MainActivity.getTcpClient().sendToServer(s);
                    Log.d(TAG, s);
                }
            }
            //Once all parameters have been sent, send EXECUTE to kickoff the server with those settings
            MainActivity.getTcpClient().sendToServer("EXECUTE");
        }
        else
        {
            Toast.makeText(context, "Not connected to daemon", Toast.LENGTH_SHORT).show();
        }
    }
}