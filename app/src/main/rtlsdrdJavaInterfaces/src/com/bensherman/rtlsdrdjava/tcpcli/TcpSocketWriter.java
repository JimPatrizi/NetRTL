/**
 * @author Bennett Sherman
 * Implements a tcp socket writer to be used with rtlsdrd.
 * TcpSocketWriter implements Runnable and is intended
 * to be run as its own thread.
 */
package com.bensherman.rtlsdrdjava.tcpcli;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class TcpSocketWriter implements Runnable
{
    /**
     * When set to false, if this thread is interrupted, it will die.
     */
    private boolean keepAlive;

    /**
     * PrintWriter instance which is tied to the socket's output stream.
     */
    private final PrintWriter tcpSocketWriter;

    /**
     * The TcpClient which instantiated this class.
     */
    private final TcpClient tcpClient;

    /**
     * Constructs a new TcpSocketWriter.
     *
     * @param socketToWriteTo
     *            The socket which is connected to the server, which this thread
     *            will write to.
     * @param tcpClient
     *            The TcpClient which instantiated this class
     * @throws IOException
     *             In the event that there was an error retrieving the output stream
     *             of the socket param
     */
    TcpSocketWriter(final Socket socketToWriteTo, final TcpClient tcpClient) throws IOException
    {
        tcpSocketWriter = new PrintWriter(socketToWriteTo.getOutputStream(), true);
        keepAlive = true;
        this.tcpClient = tcpClient;
    }

    /**
     * The function called when this class is executed as a thread. this thread will
     * pend on the sendMsgQueue, waiting for new messages to be submitted. When a
     * new message has been submitted, this thread will retrieve it, remove it from
     * the queue, and send it to the server. Then, the message will be added to the
     * msgsWaitingForResponseQueue owned by the TcpSocketReader class/thread.
     */
    @Override
    public void run()
    {
        logMsg("entering run()");

        try
        {
            while (keepAlive)
            {
                try
                {
                    // Wait for a new message to be submitted, and a new msg
                    // has been submitted, send it.
                    write(tcpClient.takeMsgFromSendMsgQueue());
                }
                catch (final InterruptedException exception)
                {
                    logMsg("Exception caught: " + exception.toString());
                }
            }
        }
        finally
        {
            tcpSocketWriter.close();
        }

        logMsg("exiting run()");
    }

    /**
     * Writes the message specified by the parameter, followed by a newline to the
     * server. The message is also added to the msgsWaitingForResponseQueue owned by
     * the TcpSocketReader instance obtained from the TcpClient
     */
    private void write(final Message msg)
    {
        logMsg("Writing msg: \"" + msg.getOutboundMsg() + "\"");
        tcpClient.getTcpSocketReader().addToWaitingForResponseQueue(msg);
        tcpSocketWriter.println(msg.getOutboundMsg());
        tcpSocketWriter.flush();
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
     * Sets the keepAlive flag to false. If the thread executing run() is
     * interrupted, thread will die.
     */
    synchronized void requestStop()
    {
        keepAlive = false;
        logMsg("TcpSocketWriter keepAlive set to false");
    }
}
