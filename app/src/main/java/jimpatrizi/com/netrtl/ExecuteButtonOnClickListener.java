package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jim Patrizi on 10/2/2017.
 * This class makes the executebutton listener and creates a thread that writes to daemon
 * @author Jim Patrizi
 */

public class ExecuteButtonOnClickListener implements View.OnClickListener {
    private Context context;
    public AsyncConnection connection;


    /**
     * Constructor that gets the context and passes the asyncconnection class to write data to server
     * @param context app context
     * @param connection asyncconnect object passed from main activity
     */
    ExecuteButtonOnClickListener(Context context, AsyncConnection connection){
        this.context = context;
        this.connection = connection;
    }


    /**
     * When execute is clicked, runs the below code
     * @param view default
     */
    @Override
    public void onClick(View view) {
        Parameters.SAMPLE_RATE.append("Ben Sucks at 100MS/s");
        Parameters.ATAN_MATH.append("Ben Sucks 69Hz");
        Parameters.BROADCAST_FM.append("91.1");
        String dameon = "";
        for (Parameters p : Parameters.values())
        {
            //if i want to mess with sockets, later, do socket shit here
            for(String s : p.getDameonCallableStrings()){
                dameon += s;
                dameon += "\n";
            }
            p.resetValues();
        }
        Toast.makeText(context, dameon, Toast.LENGTH_LONG).show();

        //TODO make class for thread that does all of this with asyncconnection
        new Thread(new Runnable() {
            @Override
            public void run() {
            connection.write("HELP");
            }
        }).start();
    }
}
