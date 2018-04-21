package com.example.ronk1.lamp_es;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class TcpService extends Service {

    private final IBinder tcpBinder = new MyLocalBinder();

    //variables
    private String server_ip;
    InetAddress serverAddr = null;
    public static final int SERVER_PORT = 2048;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private TcpService.OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    //socket
    private Socket socket;
    //message
    private static final String DEFAULT= "?";
    private String message = DEFAULT;

    public TcpService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Create socket - L'intent deve contenere l'IP del server
        mRun = true;
        server_ip = intent.getStringExtra("IP");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverAddr = InetAddress.getByName(server_ip);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                //in this while the client listens for the messages sent by the server
                while (mRun) {

                    Log.e("TCP Client", "C: Connected");

                    //create a socket to make the connection with the server
                    try {
                        socket = new Socket(serverAddr, SERVER_PORT);
                        socket.setSoTimeout(5000);

                        //sends the message to the server
                        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                        //receives the message which the server sends back
                        mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        String sent = sendMessage(message);

                        mServerMessage = mBufferIn.readLine();

                        if (mServerMessage != null && mMessageListener != null) {
                            //call the method messageReceived from MyActivity class
                            mMessageListener.messageReceived(mServerMessage);
                            Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");
                        }

                        socket.close();
                        if (sent.equals(message))
                            setMessage(DEFAULT);

                        Log.e("TCP Client", "C: Disconnected");

                        sleep(1000);
                    }

                    catch(IOException e){
                        if(mMessageListener != null)
                        mMessageListener.messageReceived("Connection Refused");
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        if(mMessageListener != null)
                        mMessageListener.messageReceived("Connection Stopped");
                        e.printStackTrace();
                    }
                }

            }
        });


        t.start();

        return tcpBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Close socket

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;

        return super.onUnbind(intent);
    }

    public class MyLocalBinder extends Binder {
        TcpService getService() {
            return TcpService.this;
        }

    }

    private String sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    //mBufferOut.println(message + "\r\n");
                    mBufferOut.print(message + "\n");
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

    public void setMessageListener (OnMessageReceived listener) {

        mMessageListener = listener;
    }
}
