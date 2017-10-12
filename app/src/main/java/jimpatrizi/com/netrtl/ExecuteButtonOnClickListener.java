package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jim Patrizi on 10/2/2017.
 */

public class ExecuteButtonOnClickListener implements View.OnClickListener {
    private Context context;
    public AsyncConnection connection;
    String dameon = "";
    private ConnectionHandle handler;


    ExecuteButtonOnClickListener(Context context, AsyncConnection connection, ConnectionHandle handler){
        this.context = context;
        this.connection = connection;
        this.handler = handler;
    }


    @Override
    public void onClick(View view) {
        Parameters.BROADCAST_FM.append("91.1");

        for (Parameters p : Parameters.values())
        {
            //if i want to mess with sockets, later, do socket shit here
            for(String s : p.getDameonCallableStrings()){
                dameon += s;
                dameon += "\n";
            }
            p.resetValues();
        }
        //Toast.makeText(context, dameon, Toast.LENGTH_LONG).show();

        //TODO make class for thread that does all of this with asyncconnection
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.write("HELP");
                //connection.write(dameon);
                //connection.write("EXECUTE");
            }
        }).start();

        Toast.makeText(context, handler.getReply(), Toast.LENGTH_LONG).show();
    }
}