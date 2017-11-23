/**
 * @author Bennett Sherman
 * An example showing how to use the read and write functions of the TcpClient
 */
package com.bensherman.rtlsdrdjava.tcpcli;

public class ExampleUsage
{

    private static final String[] TEST_MESSAGES = {
            // Messages which will be successful
            "FREQUENCY=98500000", "ENABLE_OPTION=direct", "VOLUME=10",

            // Junk messages
            "IAMABADMESSAGE",

            // Try to execute - You should see a print statement
            // noting that UPDATE_AVAILABLE has been received
            "EXECUTE",

            // Getter
            "CMDS_IN_USE" };

//    public static void main(final String[] args) throws Exception
//    {
//
//        // Instantiate the client
//        final TcpClient tcpClient = new TcpClient("localhost", TcpClient.RTLSDRD_DEFAULT_TCP_PORT_NUMBER);
//
//        // Create a new thread of the client
//        final Thread tcpClientThread = new Thread(tcpClient, TcpClient.getDefaultThreadName());
//
//        // Start the TcpClient, reader, and writer threads
//        tcpClientThread.start();
//
//        // Write some strings to rtlsdrd
//        for (final String testMsg : TEST_MESSAGES)
//        {
//            tcpClient.sendToServer(testMsg);
//
//            // Sleep due to a daemon bug
//            Thread.sleep(100);
//        }
//
//        // Wait for 1 second to allow responses to be returned.
//        Thread.sleep(1000);
//
//        // See how many responses have been received at this point
//        System.out.println("Number of completed submissions: " + tcpClient.getCompletedMessageQueueLength());
//
//        // Print out received messages
//        for (int responseNum = 0; responseNum < TEST_MESSAGES.length; ++responseNum)
//        {
//            System.out.println(tcpClient.getCompletedMessage() + "\n");
//        }
//
//        // Kill the threads
//        tcpClient.terminate();
//
//        System.out.println("PROGRAM TERMINATING!");
//    }
}
