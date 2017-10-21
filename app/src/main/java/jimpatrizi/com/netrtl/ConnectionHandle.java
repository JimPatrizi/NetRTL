package jimpatrizi.com.netrtl;

import android.util.Log;

/**
 * Creates a class to debug asynctask callbacks in logcat
 * Created by Jim Patrizi on 10/9/2017.
 * @author Jim Patrizi
 */

public class ConnectionHandle implements ConnectionHandler {

    private String TAG = getClass().getName();
    private String reply = "No Reply";

    ConnectionHandle(){
        //empty constructor
    }

    @Override
    public void didReceiveData(String data) {
        Log.d(TAG, data);
        //TODO Notice Logcat has seperate entries, this gets called per line of logcat. Put into a list like Parameters? Need to fix for first reply for OK and for returning all reply lengths
        reply = data;
    }

    @Override
    public void didDisconnect(Exception error) {
        Log.d(TAG, "Disconnected");
    }

    @Override
    public void didConnect() {
        Log.d(TAG, "Connected to Socket");
    }

    public String getReply()
    {
        return reply;
    }
}
