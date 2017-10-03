package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Jim Patrizi on 10/2/2017.
 */

public class ExecuteButtonOnClickListener implements View.OnClickListener {
    private Context context;
    private SDRClient client;


    ExecuteButtonOnClickListener(Context context, SDRClient client){
        this.context = context;
        this.client = client;
    }

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
    }
}
