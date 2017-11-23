/**
 * @author Bennett Sherman
 * Implements a socket reader which is intended to be used with rtlsdrd.
 * TcpSocketReader implements Runnable and should be executed as
 * its own thread
 * The UPDATE_AVAILABLE receipt action must be implemented for the
 * specific application using this code.
 */
package com.bensherman.rtlsdrdjava.tcpcli;

import com.bensherman.rtlsdrdjava.tcpcli.Message.ResponseType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

class TcpSocketReader implements Runnable
{

    // Constants
    private static final String BEGIN_SUCCESSFUL_RESPONSE = "~OK";
    private static final String END_OF_RESPONSE = "~EOR";
    private static final String UPDATE_AVAILABLE_STRING_PREFIX = "~UPDATE_AVAILABLE";

    // Nonconstant members
    /**
     * BufferedReader used to read from the socket
     */
    private final BufferedReader socketBufferedReader;

    /**
     * Queue of messages which are waiting for their associated responses to be
     * received from the server
     */
    private final Queue<Message> msgsWaitingForResponseQueue;

    /**
     * True if this thread should continue, false if it should terminate
     */
    private boolean keepAlive;

    /**
     * The TcpClient which instantiated this TcpSocketReader
     */
    private final TcpClient tcpClient;

    /**
     * Constructs a TcpSocketReader
     *
     * @param socket
     *            A socket which is connected to the server
     * @param tcpClient
     *            The TcpClient which instantiated this TcpSocketReader
     * @throws IOException
     *             If an error occurs when attempting to call getInputStream() on
     *             the socket param
     */
    TcpSocketReader(final Socket socket, final TcpClient tcpClient) throws IOException
    {
        socketBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        msgsWaitingForResponseQueue = new LinkedList<>();
        keepAlive = true;
        this.tcpClient = tcpClient;
    }

    /**
     * Adds the message specified by the parameter to the queue of messages which
     * are waiting for a response. The add() call is synchronized using the
     * msgsWaitingForResponseQueue
     *
     * @param msgNeedingResponse
     *            The message whose response is requested
     */
    void addToWaitingForResponseQueue(final Message msgNeedingResponse)
    {
        synchronized (msgsWaitingForResponseQueue)
        {
            msgsWaitingForResponseQueue.add(msgNeedingResponse);
        }
    }

    /**
     * Executes poll() on msgsWaitingForResponseQueue, which should always contain
     * at least one Message when this function is executed. After the Message is
     * pulled from the queue, and the specified parameters are set.
     *
     * @param response
     *            The responseMsg field's value to be set
     * @param responseMsgType
     *            The responseMsgType field's value to be set
     * @return See description
     */
    Message pollMostRecentlyRespondedToMsgAndSetResponse(final String response,
            final Message.ResponseType responseMsgType)
    {
        Message mostRecentMsgWaitingForResponse;
        synchronized (msgsWaitingForResponseQueue)
        {
            mostRecentMsgWaitingForResponse = msgsWaitingForResponseQueue.poll();
        }
        mostRecentMsgWaitingForResponse.setResponseMessage(response);
        mostRecentMsgWaitingForResponse.setResponseType(responseMsgType);
        return mostRecentMsgWaitingForResponse;
    }

    /**
     * The function called when a new TcpSocketReader thread is executed. This
     * function will listen to the output from the server, and will update Message
     * instances created by the TcpSocketWriter thread with their associated
     * responses. When an response indicating an update is available or an error
     * occurred, the getResponseFromSocket() thread will perform an action WHICH
     * MUST BE DEFINED.
     */
    @Override
    public void run()
    {
        logMsg("run() entered");

        try
        {
            // Thread will return when the loop attempts to reexecute and
            // keepAlive is false.
            while (keepAlive)
            {
                try
                {
                    //TODO Try Catch for NullPointerException, just return and toast a msg
                    final Message completedMsg = getResponseFromSocket();

                    // This message has now been completed (received a response).
                    // Add it to the completed message queue.
                    tcpClient.addToCompletedMsgQueue(completedMsg);

                    if (completedMsg.getResponseMsgType().equals(Message.ResponseType.ERROR))
                    {
                        logMsg("Received ERROR message");
                    }

                    // FOR TESTING
                    System.out.println(completedMsg);
                }
                catch (final IOException exception)
                {
                    logMsg("Exception: " + exception.toString());
                }
            }
        }
        finally
        {
            try
            {
                socketBufferedReader.close();
            }
            catch (final IOException exception)
            {
                logMsg("Error closing socketBufferedReader");
            }
        }
    }

    /**
     * Each call to this function is intended to build a response for the oldest
     * message which hasn't been responded to. This function will build the response
     * message as well as determine its type. Once the END_OF_RESPONSE specifier has
     * been received, the last not-responded-to message is retrieved (and removed
     * from the queue), it's responseMsg and responseMsgType fields are set,and it's
     * returned to the caller.
     *
     * The server can return an OK or ERROR response. Also, it can return an
     * UPDATE_AVAILABLE specifier asynchronously, in the middle of the current
     * response being generated. In this case, the response string isn't updated
     * with the UPDATE_AVAILABLE specifier. Instead, the user of this library MUST
     * define the appropriate action for when such a message is received. The
     * location where this application-specific change is to be made is in the
     * comments below. For sake of example, a basic handler for the UPDATE_AVAILABLE
     * mesasge has been implemented (addUpdateAvailableMsgToCompletedQueue()). When
     * UPDATE_AVAILABLE is received, a Message associated with it will be added to
     * the completed queue, but this function will not return; instead, it'll keep
     * processing the current response being build.
     *
     * This function may be terminated due to an IOException being thrown when the
     * underlying socket is closed.
     *
     * @return The oldest not-responded-to message with set response and response
     *         type fields.
     *
     * @throws IOException
     *             When an error has occurred when reading the socket.
     * @throws IllegalStateException
     *             If the new response specifier is sent before the response being
     *             built is complete.
     */
    Message getResponseFromSocket() throws IOException
    {
        Message.ResponseType responseMsgType = Message.ResponseType.ERROR;
        final StringBuilder responseBuilder = new StringBuilder();
        String readFromSocket;

        while ((readFromSocket = socketBufferedReader.readLine()) != null)
        {
            // Indicates the start of a new server response; corresponds
            // to a new request
            if (readFromSocket.startsWith(BEGIN_SUCCESSFUL_RESPONSE))
            {
                if (responseBuilder.length() != 0)
                {
                    logMsg("Got " + BEGIN_SUCCESSFUL_RESPONSE + " when prev. msg. hadn't been completed");
                    logMsg("Line is: " + readFromSocket);
                    logMsg("Rest of buffer is: " + responseBuilder.toString());
                    throw new IllegalStateException(
                            "Received indicator for a" + " new response before previous response completed!");
                }
                else
                {
                    responseMsgType = Message.ResponseType.OK;
                    logMsg("Starting to build a NEW response string");
                    responseBuilder.append(readFromSocket + "\n");
                }
            }
            // In this case, the current message has been completed.
            // Stop reading the socket by exiting the loop.
            else if (readFromSocket.startsWith(END_OF_RESPONSE))
            {
                logMsg("End of response received. Breaking");
                responseBuilder.append(readFromSocket + "\n");
                break;
            }

            // TODO IMPLEMENT UI-FACING UPDATE HANDLER HERE
            // The update ready string has been received, so alert the hander
            // to deal with receiving new params.
            else if (readFromSocket.startsWith(UPDATE_AVAILABLE_STRING_PREFIX))
            {
                logMsg("UPDATE_AVAILABLE RECEIVED: " + readFromSocket);

                // Example handler approach
                addUpdateAvailableMsgToCompletedQueue(readFromSocket);
            }

            // If no messages have been received for the current request,
            // and the new line isn't an EOR specifier, successful response specifier,
            // or update ready specifier, it means that this new response represents an
            // exception. The END_OF_RESPONSE string should be the next string received
            // by the scket.
            else if (readFromSocket.length() == 0)
            {
                responseMsgType = Message.ResponseType.ERROR;
                responseBuilder.append(readFromSocket + "\n");
            }

            // Last but not least, in this case, a simple newline in the
            // response is being received.
            else
            {
                responseBuilder.append(readFromSocket + "\n");
            }
        }

        // Remove the oldest not-responded-to message from the queue,
        // update its response fields, and return it.
        return pollMostRecentlyRespondedToMsgAndSetResponse(responseBuilder.toString(), responseMsgType);
    }

    /**
     * Handler for UPDATE_AVAILABLE being received. This function creates a new
     * Message with a blank "outboundMsg" field, response type of UPDATE_AVAILABLE,
     * and responseMessage of the msg parameter (which is expected to be the
     * UPDATE_AVAILABLE + timestamp string from rtlsdrd). The listener to the
     * completed message queue can handle this message type appropriately.
     * 
     * @param msg
     *            The UPDATE_AVAILABLE + timestamp string from rtlsdrd
     */
    private void addUpdateAvailableMsgToCompletedQueue(final String msg)
    {
        final Message updateAvailableMsg = new Message("");
        updateAvailableMsg.setResponseMessage(msg);
        updateAvailableMsg.setResponseType(ResponseType.UPDATE_AVAILABLE);
        tcpClient.addToCompletedMsgQueue(updateAvailableMsg);
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
     * Sets the keepAlive boolean to false, so that if this thread is interrupted or
     * the socket is closed, the thread will die.
     */
    synchronized void requestStop()
    {
        keepAlive = false;
        logMsg("TcpSocketWriter keepAlive set to false");
    }
}
