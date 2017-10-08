package jimpatrizi.com.netrtl;

/**
 * Created by Jim Patrizi on 10/8/2017.
 */

public class AsyncRunnable implements Runnable {
    public Thread thread;
    public AsyncConnection connection;

    AsyncRunnable(AsyncConnection connection)
    {
        this.connection = connection;
    }

    @Override
    public void run() {
        connection.doInBackground();
    }
}
