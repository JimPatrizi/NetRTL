package jimpatrizi.com.netrtl;

import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * Created by Jim Patrizi on 10/2/2017.
 */

public class ExecuteButtonOnClickListener implements View.OnClickListener {
    private Context context;
    public AsyncConnection connection;
    String dameon = "";
    private ConnectionHandle handler;
    private String TAG = getClass().getName();



    ExecuteButtonOnClickListener(Context context, AsyncConnection connection, ConnectionHandle handler){
        this.context = context;
        this.connection = connection;
        this.handler = handler;
    }


    @Override
    public void onClick(View view) {
        for (Parameters p : Parameters.values())
        {
            for(String s : p.getDameonCallableStrings()){

                MainActivity.getTcpClient().sendToServer(s);
                Log.d(TAG, s);
            }
           // MainActivity.getTcpClient().sendToServer("EXECUTE");
            p.resetValues();
        }
    }
}