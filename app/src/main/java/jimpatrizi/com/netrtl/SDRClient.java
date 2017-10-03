package jimpatrizi.com.netrtl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jim Patrizi on 10/2/2017.
 */

public class SDRClient {
    /**
     * Port number of the server's socket
     */
    private int portNumber;

    /**
     * Hostname of the server
     */
    private String serverHostname;

    /**
     * TCP socket used to connect to server
     */
    private Socket socket;

    /**
     * User input is read from this reader
     */
    private BufferedReader userInputReader;

    /**
     * Data sent to server through output stream
     */
    private DataOutputStream outToServer = null;

    /*
    /**
     * Constructor that provides server socket's IP address and port #.
     * @param serverHostname Server's hostname
     * @param portNumber Server socket's port number
     */
    public SDRClient(String serverHostname, int portNumber){
        this.serverHostname = serverHostname;
        this.portNumber = portNumber;
    }

    public boolean connectToSocket(){
        boolean success = false;
        try
        {
            socket = new Socket(serverHostname, portNumber);
            success = true;
        }
        catch(UnknownHostException e)
        {
            //post error message Unable to determine the IP addr of the hostname + serverHostname
        }
        catch(IOException e)
        {
            //display error messge unable to reach hostname + portnumber
        }
        return success;
    }

    public static DataOutputStream getSocketDataOutputStream(Socket socket)
    {
        DataOutputStream outStream = null;
        try
        {
            outStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            String othermsg = "Unable to create stream for the specified socket.";
        }
        return outStream;
    }

    public void initializeStream()
    {
        outToServer = getSocketDataOutputStream(socket);
    }

    /**
     * Wrapper to send data to server
     * @param msgToSend The message to send to the server
     */
    public void sendMessageToServer(String msgToSend){
        sendMessageToDataOutputStream(msgToSend, outToServer);
    }

    public static void sendMessageToDataOutputStream(String msgToSend, DataOutputStream stream)
    {
        try
        {
            //Append newline to each of the sent messages
            stream.writeBytes(msgToSend + "\n");
            stream.flush();
        }
        catch (IOException e)
        {
            String othermsg = "Error writing to DataOutputStream";
            e.printStackTrace();
        }
    }

    /**
     * Getter for socket
     * @return Socket reference
     */
    public Socket getSocket(){
        return socket;
    }
}
