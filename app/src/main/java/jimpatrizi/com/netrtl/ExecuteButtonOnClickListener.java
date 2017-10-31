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
    Parameters p;
    private ConnectionHandle handler;
    private String TAG = getClass().getName();



    ExecuteButtonOnClickListener(Context context, AsyncConnection connection, ConnectionHandle handler){
        this.context = context;
        this.connection = connection;
        this.handler = handler;
    }


    @Override
    public void onClick(View view) {
        //Parameters.BROADCAST_FM.append("97.9");


        for (Parameters p : Parameters.values())
        {
            for(String s : p.getDameonCallableStrings()){

                MainActivity.getTcpClient().sendToServer(s);
                Log.d(TAG, s);
            }
        }

        //Reset Parameter enums after execute button click.
        Parameters.resetValues();
        //Toast.makeText(context, dameon, Toast.LENGTH_LONG).show();

//        //needs to be done from another thread, because doing a write is blocking to UI exec so it crashes. Looking at the threads in debugger, this should be fine as it uses this same thread every time listener is called
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //connection.write("HELP");
//                connection.write("VOLUME=" + Parameters.VOLUME.getByIndex(0));
//
//                //connection.write("EXECUTE");
//            }
//        }).start();
//        String s = handler.getReply();
//        boolean didFail = handler.getFailureStatus();
//        if(didFail) {
//            connection.disconnect();
//        }
//        if(s.isEmpty())
//        {
//            s = "empty";
//        }
        //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        //Parameters.resetValues();
        //Toast.makeText(context, ip_address, Toast.LENGTH_LONG).show();
    }
}