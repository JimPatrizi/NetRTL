package jimpatrizi.com.netrtl;

/**
 * This interface defines methods that handle connection notifications and the data received from the socket connection.
 * The methods are called from the background thread of the AsyncTask.
 *
 * @author Jim Patrizi
**/
public interface ConnectionHandler {

    public void didReceiveData(String data);

    public void didDisconnect(Exception error);

    public void didConnect();
}