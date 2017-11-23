/**
 * @author Bennett Sherman
 * An asynchronous TCP client used to interface with rtlsdrd.
 * This client SPAWNS TWO THREADS: one for reading from the socket, and the
 * other for writing to it. This class itself is intended to be run as its own
 * thread, which largely just sleeps.
 *
 * The intention is that this class obscures access to the socketReader and
 * socketWriter instances. Specifically, users are expected to utilize reading
 * and writing functions through the:
 * 1.) sendToServer() function <- writes the parameter to the server
 * 2.) getOldestCompletedMessage() <- returns received messages in FIFO order
 * 3.) terminate() stops the thread running this TcpClient as well as the
 * 		reader and writer threads.
 * 4. getCompletedMessageQueueLength() <- returns the number of messages which
 * 		are sitting in the completed queue
 */
package com.bensherman.rtlsdrdjava.tcpcli;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jimpatrizi.com.netrtl.MainActivity;

public class TcpClient implements Runnable
{

    // Static constants
    /**
     * Default port number used by rtlsdrd
     */
    public static final int RTLSDRD_DEFAULT_TCP_PORT_NUMBER = 2832;

    /**
     * The name that the user should call this client
     */
    private static final String TCP_CLIENT_DEFAULT_THREAD_NAME = "TcpClient";

    /**
     * When terminate() is called, the TcpSocketWriter and TcpSocketReader threads
     * will have this parameter as their join() timeout.
     */
    private static final int THREAD_JOIN_WAIT_TIME_MS = 500;

    /**
     * Name to be given to the thread associated with the socketWriter instance.
     */
    private static final String SOCKET_WRITER_THREAD_NAME = "TcpSocketWriter";

    /**
     * Name to be given to the thread associated with the socketWriter instance.
     */
    private static final String SOCKET_READER_THREAD_NAME = "TcpSocketReader";

    // Nonstatic members
    /**
     * Socket used to interact w/ the server
     */
    private Socket tcpSocket;

    /**
     * The instance of TcpSocketWriter associated with this client, which is used to
     * handle socket writes.
     */
    private TcpSocketWriter socketWriter;

    /**
     * The instance of TcpSocketReader associated with this client, which is used to
     * handle socket reads.
     */
    private TcpSocketReader socketReader;

    /**
     * The thread which the socketWriter instance is running in.
     */
    private Thread socketWriterThread;

    /**
     * The thread which the socketReader instance is running in
     */
    private Thread socketReaderThread;

    /**
     * When a client writes a message to the server, once the response has been
     * retrieved, this queue is updated with the Message instance describing the
     * pair. Note that this is a blocking queue which will endlessly grow if not
     * tended to. TODO HAVE A LISTENER WHICH PULLS MESSAGES FROM THIS QUEUE
     */
    private final BlockingQueue<Message> completedMsgQueue;

    /**
     * New messages to be sent to the server are placed in this queue. The TcpSocketWriter instance's
     * thread will pend on this queue when empty, until a new message is placed into
     * it. The messages in this queue should only have their outboundMsg field set.
     */
    private final BlockingQueue<Message> sendMsgQueue;

    /**
     * When this is set to false, if the thread containing this client instance is
     * interrupted, the thread will die.
     */
    private boolean keepAlive;

    private final String hostname;
    private final int portNum;

    /**
     * Instantiates a new TcpClient
     *
     * @param hostname
     *            The hostname of the server (can be an IP or textual name to be
     *            resolved w/ DNS)
     * @param portNum
     *            The port number to connect to on the server
     * @throws UnknownHostException
     *             If the host's IP address couldn't be determined
     * @throws IOException
     *             If there was an error creating the socket, which can also occur
     *             during the instantiation of socketWriter and socketReader
     */
    public TcpClient(final String hostname, final int portNum) throws IOException
    {
        this.hostname = hostname;
        this.portNum = portNum;
        completedMsgQueue = new LinkedBlockingQueue<>();
        sendMsgQueue = new LinkedBlockingQueue<>();
        keepAlive = true;
    }

    private void initMembers() throws IOException
    {
        tcpSocket = new Socket(hostname, portNum);
        tcpSocket.setTcpNoDelay(true);
        socketWriter = new TcpSocketWriter(tcpSocket, this);
        socketReader = new TcpSocketReader(tcpSocket, this);
        socketWriterThread = new Thread(socketWriter, SOCKET_WRITER_THREAD_NAME);
        socketReaderThread = new Thread(socketReader, SOCKET_READER_THREAD_NAME);
    }

    /**
     * When this TcpClient is run as a thread, this function will be called. This
     * function then starts the each of the socketWriter and socketReader threads.
     */
    @Override
    public void run()
    {
        try
        {
            initMembers();
        }
        catch (IOException exception)
        {
            return;
//            Log.e("ERROR CONNECTING TO SOCKET");
        }
        logMsg("entering run()");
        socketWriterThread.start();
        socketReaderThread.start();

        while (keepAlive)
        {
            try
            {
                synchronized (this)
                {
                    this.wait();
                }
            }
            catch (final InterruptedException exception)
            {
                logMsg("Exception caught: " + exception.toString());
            }
        }

        logMsg("exiting run()");
    }

    /**
     * Logs/outputs a message. Replace the System.out.println() with a logger if
     * desired. The message format is "<ThreadName>: msg"
     *
     * @param msg
     *            The msg to log
     */
    private void logMsg(final String msg)
    {
        System.out.println(Thread.currentThread().getName() + ": " + msg);
    }

    /**
     * Writes a message to the server. This happens asynchronously.
     *
     * @param msg
     *            The message to send to the server
     */
    public void sendToServer(final String msg)
    {
        try {
            sendMsgQueue.add(new Message(msg));
        }
        catch(NullPointerException exception)
        {
            MainActivity.printToast("SendToServer() Exception: " + exception);
        }
    }

    /**
     * Requests that the socketWriter thread die. The join() call will wait
     * THREAD_JOIN_WAIT_TIME_MS.
     *
     * @throws InterruptedException
     *             If this thread is interrupted when calling join()
     */
    private void terminateSocketWriterThread() throws InterruptedException
    {
        socketWriter.requestStop();
        socketWriterThread.interrupt();

        logMsg("Waiting at most " + THREAD_JOIN_WAIT_TIME_MS + "ms for socketWriterThread to complete");
        socketWriterThread.join(THREAD_JOIN_WAIT_TIME_MS);
        if (socketWriterThread.isAlive())
        {
            logMsg("socketWriterThread failed to terminate");
        }
        else
        {
            logMsg("socketWriterThread closed successfully");
        }
    }

    /**
     * Requests that the socketReader thread die. The join() call will wait
     * THREAD_JOIN_WAIT_TIME_MS.
     *
     * @throws InterruptedException
     *             If this thread is interrupted when calling join()
     */
    private void terminateSocketReaderThread() throws InterruptedException
    {
        socketReader.requestStop();
        socketReaderThread.interrupt();

        logMsg("Waiting at most " + THREAD_JOIN_WAIT_TIME_MS + "ms for socketReaderThread to complete");
        socketReaderThread.join(THREAD_JOIN_WAIT_TIME_MS);
        if (socketReaderThread.isAlive())
        {
            logMsg("socketReaderThread failed to terminate");
        }
        else
        {
            logMsg("socketReaderThread closed successfully");
        }
    }

    /**
     * Stops the TcpClient thread as well as the TcpSocketWriter and TcpSocketReader
     * threads. The socket is also closed. This TcpClient should NOT be used after
     * executing this function. The socket is closed in order to wake the reader
     * thread.
     *
     * @throws IOException
     *             If an IO error occurs when closing tcpSocket
     * @throws InterruptedException
     *             If this thread is interrupted when terminating either of the
     *             reader or writer threads.
     */
    public void terminate() throws IOException, InterruptedException
    {
        logMsg("Attempting to close socket");
        tcpSocket.close();

        terminateSocketReaderThread();
        terminateSocketWriterThread();

        logMsg("Attempting to kill TcpClient thread");
        synchronized (this)
        {
            keepAlive = false;
            this.notifyAll();
        }
    }

    /**
     * Add a completed Message instance (i.e. its response fields are set) to the
     * completedMsgQueue.
     *
     * @param completedMsg
     *            A completed Message to add to completedMsgQueue
     */
    void addToCompletedMsgQueue(final Message completedMsg)
    {
        completedMsgQueue.add(completedMsg);
    }

    /**
     * THIS CALL BLOCKS on completedMsgQueue. That is, if this function is called
     * when completedMsgQueue is empty, the calling thread will wait until a Message
     * becomes available.
     *
     * @return The head of the completedMsgQueue queue, which is the oldest
     *         completed Message which has not been pulled from the queue.
     * @throws InterruptedException
     *             If the calling thread is interrupted when waiting on
     *             completedMsgQueue.
     */
    public Message getCompletedMessage() throws InterruptedException
    {
        return completedMsgQueue.take();
    }

    /**
     * Returns this TcpClient's socketReader member. Intended to be used only by the
     * socketWriter instance.
     *
     * @return This TcpClient's socketReader member.
     */
    TcpSocketReader getTcpSocketReader()
    {
        return socketReader;
    }

    /**
     * @return The number of completed messages waiting to be received using
     *         getCompletedMessage()
     */
    public int getCompletedMessageQueueLength()
    {
        return completedMsgQueue.size();
    }

    /**
     *
     * @return The default name for the thread executing this class's run() method.
     */
    public static String getDefaultThreadName()
    {
        return TCP_CLIENT_DEFAULT_THREAD_NAME;
    }

    /**
     * Calls take() on the send message queue, causing the calling thread to block.
     * Take() will remove the head of the queue.
     * @return The oldest untaken message in the queue.
     * @throws InterruptedException If the take() operation was interrupted.
     */
    Message takeMsgFromSendMsgQueue() throws InterruptedException
    {
        return sendMsgQueue.take();
    }
}
