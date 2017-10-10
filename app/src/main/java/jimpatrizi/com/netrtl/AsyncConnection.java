package jimpatrizi.com.netrtl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

/**
 * Created by Jim Patrizi 10/8/2017
 *
 * The AsyncConnection class is an AsyncTask that can be used to open a socket connection with a server and to write/read data asynchronously.
 *
 * The socket connection is initiated in the background thread of the AsyncTask which will stay alive reading data in a while loop
 * until disconnect() method is called from outside or the connection has been lost.
 *
 * When the socket reads data it sends it to the ConnectionHandler via didReceiveData() method in the same thread of AsyncTask.
 * To write data to the server call write() method from outside thread. As the input and output streams are separate there will be no problem with synchronisation.
 *
 *  if you wish to avoid connection timeout to happen while the application is inactive try to write some meaningless data periodically as a heartbeat.
 *  if you wish to keep the connection alive for longer that the activity  life cycle than consider using services.
 * @author Jim Patrizi
 *
 */
public class AsyncConnection extends android.os.AsyncTask<Void, String, Exception> {

    /**
     * IP Addr of Server's socket
     */
    private String serverHostname;

    /**
     * Port number of server's socket
     */
    private int portNumber;

    /**
     * Timeout to connect to server
     */
    private int timeout;

    /**
     * Connection Handler to interface with callbacks from server
     */
    private ConnectionHandler connectionHandler;

    /**
     * Server's Response sent through here
     */
    private BufferedReader in;

    /**
     * App generated server commands sent through here
     */
    private BufferedWriter out;

    /**
     * Java socket
     */
    private Socket socket;

    /**
     * Flag for  doInBackground() being interrupted via disconnect()
     */
    private boolean interrupted = false;

    /**
     * For Logcat debugging
     */
    private String TAG = getClass().getName();

    /**
     * Constructor that provides server sockets's IP address and portNumber number
     * @param serverHostname Server's Hostname
     * @param portNumber Server socket's port number
     * @param timeout timeout to connect to server
     * @param connectionHandler connection handler for callbacks
     */
    public AsyncConnection(String serverHostname, int portNumber, int timeout, ConnectionHandler connectionHandler) {
        this.serverHostname = serverHostname;
        this.portNumber = portNumber;
        this.timeout = timeout;
        this.connectionHandler = connectionHandler;
    }

    /**
     * Calls super class onProgressUpdate
     * @param values
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    /**
     * Calls super onPostExecute, tags result in logcat
     * @param result
     */
    @Override
    protected void onPostExecute(Exception result) {
        super.onPostExecute(result);
        Log.d(TAG, "Finished communication with the socket. Result = " + result);
        //If needed move the didDisconnect(error); method call here to implement it on UI thread.
    }

    /**
     * Opens socket and inits Buffered Readers for output and input comms
     * @param params abstract, not using in this case
     * @return returns error message for failures
     */
    @Override
    protected Exception doInBackground(Void... params) {
        Exception error = null;

        try {
            Log.d(TAG, "Opening socket connection.");
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverHostname, portNumber), timeout);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //calls interfaces didConnect method
            connectionHandler.didConnect();

            while(!interrupted) {
                String line = in.readLine();
                //Log.d(TAG, "Received:" + line);
                connectionHandler.didReceiveData(line);
            }
        } catch (UnknownHostException ex) {
            Log.e(TAG, "doInBackground(): " + ex.toString());
            error = interrupted ? null : ex;
        } catch (IOException ex) {
            Log.d(TAG, "doInBackground(): " + ex.toString());
            error = interrupted ? null : ex;
        } catch (Exception ex) {
            Log.e(TAG, "doInBackground(): " + ex.toString());
            error = interrupted ? null : ex;
        } finally {
            //TODO if this fails, reader and writer never close, try using https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html when you do that, close is called automatically
            // FIXME: 10/9/2017
            try {
                socket.close();
                out.close();
                in.close();
            } catch (Exception ex) {}
        }

        //calls interfaces didDisconnect method
        connectionHandler.didDisconnect(error);
        return error;
    }

    /**
     * Writes data to the server
     * @param data Singular command to be written to server
     */
    public void write(final String data) {
        try {
            Log.d(TAG, "writ(): data = " + data);
            out.write(data + "\n");
            out.flush();
        } catch (IOException ex) {
            Log.e(TAG, "write(): " + ex.toString());
        } catch (NullPointerException ex) {
            Log.e(TAG, "write(): " + ex.toString());
        }
    }

    /**
     * Disconnects socket
     */
    public void disconnect() {
        try {
            Log.d(TAG, "Closing the socket connection.");

            interrupted = true;
            if(socket != null) {
                socket.close();
            }
            if(out != null & in != null) {
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            Log.e(TAG, "disconnect(): " + ex.toString());
        }
    }
}
