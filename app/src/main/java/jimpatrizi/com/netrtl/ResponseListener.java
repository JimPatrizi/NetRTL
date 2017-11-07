package jimpatrizi.com.netrtl;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.bensherman.rtlsdrdjava.tcpcli.Message;
import com.bensherman.rtlsdrdjava.tcpcli.TcpClient;


/**
 * Created by jamespatrizi on 11/1/17.
 */

public class ResponseListener implements Runnable {
    private TcpClient tcpClient;
    private Activity mainActivity;

    /**
     * For Logcat debugging
     */
    private String TAG = getClass().getName();


    public  ResponseListener(final TcpClient tcpClient, final Activity mainActivity) {
        this.tcpClient = tcpClient;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {

        while(true) {
            try {
                final Message msg = tcpClient.getCompletedMessage();
                if (msg.getResponseMsgType() == Message.ResponseType.ERROR) {
                    Log.d(TAG, "ERROR RECEIVED!!!!!: \n" + msg.toString());
                } else if (msg.getResponseMsgType() == Message.ResponseType.UPDATE_AVAILABLE) {
                    Log.d(TAG, "UPDATE AVAILABLE RECEIVED!!!!!: \n" + msg.toString());
                    tcpClient.sendToServer("CMDS_IN_USE");
                }
                else if (msg.getResponseMsgType() == Message.ResponseType.OK)
                {
                    if (msg.getOutboundMsg().equals("CMDS_IN_USE"))
                    {
                        Log.d(TAG, "RECEIVED COMMANDS IN USE!!!: \n" + msg.getResponseMsg());
                        parseCmdsInUseResponse(msg.getResponseMsg());
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mainActivity.getApplicationContext(), msg.getResponseMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        );
                    }
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void parseCmdsInUseResponse(String response)
    {
        // Each instance doesn't include the newline character
        String[] responseLines = response.split("\n");
        boolean hasEnableOptionParamBeenReset = false;

        // Reset all enable option strings
        ((EnableOptionUiMatcher) Parameters.ENABLE_OPTION.getUiElement()).uncheckAll(mainActivity);

        for (String line : responseLines)
        {
            // Skip special svr messages
            if (line.startsWith("~"))
            {
                continue;
            }
            else
            {
                int equalsIndex = line.indexOf("=");
                String command = line.substring(0, equalsIndex);
                String value = line.substring(equalsIndex + 1);
                for (Parameters param : Parameters.values())
                {
                    if (param.getFunction().equals(command)) {
                        Log.d(TAG, "ASSOCIATED W/ PARAMETER: " + param.getFunction());

                        if (param.equals(Parameters.ENABLE_OPTION) && !hasEnableOptionParamBeenReset)
                        {
                            param.resetValues();
                            hasEnableOptionParamBeenReset = true;
                        }
                        else if (!param.equals(Parameters.ENABLE_OPTION))
                        {
                            param.resetValues();
                        }

                        param.append(value);

                        if (param.equals(Parameters.FREQUENCY))
                        {
                            param.updateField(mainActivity, value);
                        }

                        else if (param.equals(Parameters.SAMPLE_RATE))
                        {
                            param.updateField(mainActivity, value);
                        }

                        else if (param.equals(Parameters.RESAMPLE_RATE))
                        {
                            param.updateField(mainActivity, value);
                        }

                        else if (param.equals(Parameters.ENABLE_OPTION))
                        {
                            param.updateField(mainActivity, value);
                        }
                    }
                }
            }
        }
    }
}
