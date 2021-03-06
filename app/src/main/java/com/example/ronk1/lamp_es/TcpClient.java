package com.example.ronk1.lamp_es;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class TcpClient {

    //public static final String SERVER_IP = "192.168.1.131"; //server IP address
    public String server_ip;
    public static final int SERVER_PORT = 2048;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    //socket
    private Socket socket;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener, String server_ip) {

        mMessageListener = listener;
        this.server_ip = server_ip;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    //mBufferOut.println(message + "\r\n");
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() throws IOException {

        mRun = true;

        //here you must put your computer's IP address.
        InetAddress serverAddr = InetAddress.getByName(server_ip);

        Log.e("TCP Client", "C: Connecting...");

        //create a socket to make the connection with the server
        socket = new Socket(serverAddr, SERVER_PORT);

        //sends the message to the server
        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        //receives the message which the server sends back
        mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //in this while the client listens for the messages sent by the server
        while (mRun) {

            mServerMessage = mBufferIn.readLine();

            if (mServerMessage != null && mMessageListener != null) {
                //call the method messageReceived from MyActivity class
                mMessageListener.messageReceived(mServerMessage);
            }

        }

        Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

        socket.close();

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
