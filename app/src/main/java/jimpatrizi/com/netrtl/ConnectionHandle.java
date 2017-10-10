package jimpatrizi.com.netrtl;

import android.util.Log;

/**
 * Created by Jim Patrizi on 10/9/2017.
 */

public class ConnectionHandle implements ConnectionHandler {

    private String TAG = getClass().getName();

    ConnectionHandle(){
        //empty constructor
    }

    @Override
    public void didReceiveData(String data) {
        Log.d(TAG, data);
    }

    @Override
    public void didDisconnect(Exception error) {
        Log.d(TAG, "Disconnected");
    }

    @Override
    public void didConnect() {
        Log.d(TAG, "Connected to Socket");
    }
}
