package jimpatrizi.com.netrtl;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.bensherman.rtlsdrdjava.tcpcli.Message;
import com.bensherman.rtlsdrdjava.tcpcli.TcpClient;


/**
 * Created by jamespatrizi on 11/1/17.
 *
 * Run from a new thread to always listen to tcpClientReader while not terminated
 */
public class ResponseListener implements Runnable {
    /**
     * tcpClient to listen to
     */
    private TcpClient tcpClient;

    /**
     * MainActivity Reference
     */
    private Activity mainActivity;

    /**
     * Are we supposed to be running this thread?
     */
    private boolean running;

    /**
     * For Logcat debugging
     */
    private String TAG = getClass().getName();


    /**
     * Constructor that constructs, tcpClient, mainActivity
     * @param tcpClient
     * @param mainActivity
     */
    public  ResponseListener(final TcpClient tcpClient, final Activity mainActivity) {
        this.tcpClient = tcpClient;
        this.mainActivity = mainActivity;
        this.running = true;
    }

    @Override
    public void run() {

        /**
         * While terminated() is not called, always run
         */
        while(running) {
            try {
                //get completed message from listener
                final Message msg = tcpClient.getCompletedMessage();
                //if error type
                if (msg.getResponseMsgType() == Message.ResponseType.ERROR) {
                    Log.d(TAG, "ERROR RECEIVED!!!!!: \n" + msg.toString());
                    //parse the error messages
                    parseCmdsInUseResponse(msg.getResponseMsg());
                    //toast the errors
                    mainActivity.runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                           Toast.makeText(mainActivity.getApplicationContext(), msg.getResponseMsg(), Toast.LENGTH_SHORT).show();
                       }
                   }
                );//handle the error msg
                    handleErrorMessage(msg);
                }
                //if the response type is UPDATE_AVAILABLE, another client pushed something to daemon
                else if (msg.getResponseMsgType() == Message.ResponseType.UPDATE_AVAILABLE) {
                    Log.d(TAG, "UPDATE AVAILABLE RECEIVED!!!!!: \n" + msg.toString());
                    //ask the server what is in use, then gets OK msg
                    tcpClient.sendToServer("CMDS_IN_USE");
                }
                else if (msg.getResponseMsgType() == Message.ResponseType.OK)
                {
                    //only do something here if the outbound msg was CMDS_IN_USE
                    if (msg.getOutboundMsg().equals("CMDS_IN_USE"))
                    {
                        Log.d(TAG, "RECEIVED COMMANDS IN USE!!!: \n" + msg.getResponseMsg());
                        //get the response msg for CMDS_IN_USE and parse it
                        parseCmdsInUseResponse(msg.getResponseMsg());
                        //TODO remove, toasted for debugging purposes
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

    /**
     * If Error, update the UI field that had the error to show INVALID, should only happen for edittexts
     * @param msg - message that held an error in it
     */
    private void handleErrorMessage(final Message msg)
    {
        //get the index of the '=' char
        int equalsIndex = msg.getOutboundMsg().indexOf("=");
        //get the command that was an error (from beginning to = char)
        final String command = msg.getOutboundMsg().substring(0, equalsIndex);
        //the value that was wrong
        final String value = msg.getOutboundMsg().substring(equalsIndex + 1);

        //for all parameters values that are equal to the parsed command, set those ui members to "INVALID"
        for (Parameters param : Parameters.values())
        {
            if (param.getFunction().equals(command)) {
                param.updateField(mainActivity, "INVALID");
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mainActivity.getApplicationContext(), "ERR!\n" + command + "=" + value + " not valid", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * Stops thread from running
     */
    public void terminate() {
        running = false;
    }


    /**
     * When CMDS_IN_USE is sent upon an update received, parses the read input from server
     * @param response - response from server to parse
     */
    private void parseCmdsInUseResponse(String response)
    {
        // Each instance doesn't include the newline character
        //all responses
        String[] responseLines = response.split("\n");

        // Reset all enable option strings
        ((EnableOptionUiMatcher) Parameters.ENABLE_OPTION.getUiElement()).uncheckAll(mainActivity);
        Parameters.ENABLE_OPTION.resetValues();

        //for all response lines
        for (String line : responseLines)
        {
            // Skip special svr messages
            if (line.startsWith("~"))
            {
                continue;
            }
            else
            {
                //get '=' char index in return
                int equalsIndex = line.indexOf("=");
                //command is from start to equals index
                String command = line.substring(0, equalsIndex);
                //value is equals index + 1 to the end
                String value = line.substring(equalsIndex + 1);
                //for all parameter values, if that parameter is this current function
                for (Parameters param : Parameters.values())
                {
                    if (param.getFunction().equals(command)) {
                        Log.d(TAG, "ASSOCIATED W/ PARAMETER: " + param.getFunction());

                        //if the parameter is not equal to enable options, reset this params values
                        if (!param.equals(Parameters.ENABLE_OPTION))
                        {
                            param.resetValues();
                        }
                        //append the new value from the server to the parameter values list
                        param.append(value);
                        //update that parameters UI with the parsed value
                        param.updateField(mainActivity, value);
                    }
                }
            }
        }
    }
}
