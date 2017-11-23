package com.bensherman.rtlsdrdjava.tcpcli;

public class Message
{

    /**
     * This enum specifies the type of message contained in the responseMsg field.
     * "OK" indicates that the response indicated a successful command. "ERROR"
     * indicates that the response contains some form of exception message from
     * rtlsdrd. "UPDATE_AVAILABLE" corresponds to the update string having been sent
     * asynchronously by the server.
     * 
     * @author Bennett Sherman
     *
     */
    public enum ResponseType
    {
        OK, ERROR, UPDATE_AVAILABLE
    }

    /**
     * The message sent to the server
     */
    private final String outboundMsg;

    /**
     * The server's response message. Can only be set once externally.
     */
    private String responseMsg;

    /**
     * Whether or not the responseMsg field has been set.
     */
    private boolean hasResponseBeenSet;

    /**
     * If this message's response represented an OK result or an error. Can only be
     * set once externally.
     */
    private ResponseType responseMsgType;

    /**
     * Whether of not the responseType field has been set.
     */
    private boolean hasResponseTypeBeenSet;

    /**
     * Constructs a new Message given a message to be sent
     *
     * @param outboundMsg
     *            The message to send
     */
    public Message(final String outboundMsg)
    {
        this.outboundMsg = outboundMsg;
        hasResponseBeenSet = false;
        responseMsgType = ResponseType.OK;
        hasResponseTypeBeenSet = false;
    }

    /**
     * @return The outbound/sent message
     */
    public String getOutboundMsg()
    {
        return outboundMsg;
    }

    /**
     * @return The response message
     */
    public String getResponseMsg()
    {
        return responseMsg;
    }

    /**
     * @return The response message's type
     */
    public ResponseType getResponseMsgType()
    {
        return responseMsgType;
    }

    /**
     * Sets the response of this message
     *
     * @param responseMsg
     *            The response to be set
     * @throws IllegalArgumentException
     *             If this method is called once responseMsg has already been set
     */
    public synchronized void setResponseMessage(final String responseMsg) throws IllegalArgumentException
    {
        if (!hasResponseBeenSet)
        {
            this.responseMsg = responseMsg;
            hasResponseBeenSet = true;
        }
        else
        {
            throw new IllegalArgumentException("responseMsg can only be set once!");
        }
    }

    /**
     * Sets the ResponseType of this message.
     *
     * @param responseMsgType
     *            The new value of responseType
     * @throws IllegalArgument
     *             If this method is called once responseType has already been set.
     */
    public synchronized void setResponseType(final ResponseType responseMsgType)
    {
        if (!hasResponseTypeBeenSet)
        {
            this.responseMsgType = responseMsgType;
            hasResponseTypeBeenSet = true;
        }
        else
        {
            throw new IllegalArgumentException("msgType can only be set once");
        }
    }

    /**
     * Basic toString() implementation to describe a Message
     */
    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("OUTBOUND/SEND MESSAGE: ");
        builder.append(outboundMsg);
        builder.append("\n\tRESPONSE MESSAGE:");
        builder.append(responseMsg);
        builder.append("\n\tRESPONSE MESSAGE TYPE: ");
        builder.append(responseMsgType.toString());
        return builder.toString();
    }
}
