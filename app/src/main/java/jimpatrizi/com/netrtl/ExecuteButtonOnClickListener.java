package jimpatrizi.com.netrtl;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Created by Jim Patrizi on 10/2/2017.
 */

public class ExecuteButtonOnClickListener implements View.OnClickListener {
    private Context context;
    int portNumber = 2832;
    Socket socket;


    ExecuteButtonOnClickListener(Context context) throws IOException {
        this.context = context;
        int portNumber = 2832;
        //InetAddress ipADDR = InetAddress.getByName("192.168.0.19");
        socket = new Socket("192.168.0.19", portNumber);
    }

    @Override
    public void onClick(View view) {
        DataOutputStream output = null;
        DataInputStream input = null;
        try {
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
        }
        catch (IOException e){

        }

        if (socket != null && output != null && input != null){
            try{
                output.writeBytes("Hello\n");
            }catch(UnknownHostException e){

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
