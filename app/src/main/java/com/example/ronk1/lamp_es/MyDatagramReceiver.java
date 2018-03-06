package com.example.ronk1.lamp_es;

import android.os.SystemClock;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.lang.Runnable;
import java.net.SocketTimeoutException;

/**
 * Created by Ronk1 on 18/01/18.
 */

public class MyDatagramReceiver extends Thread {
    private boolean bKeepRunning = true;
    private String lastMessage = "";
    private long timer = 0;
    private long timer_start = 0;

    DatagramSocket socket;

    private MyDatagramReceiver myDatagramReceiver = null;

    protected void onResume() {
        myDatagramReceiver = new MyDatagramReceiver();
        myDatagramReceiver.start();
    }

    protected void onPause() {
        myDatagramReceiver.kill();
    }


    public void run() {
        String message;
        byte[] lmessage = new byte[64000];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            socket = new DatagramSocket(4096);
            socket.setSoTimeout(5000);

            timer_start = SystemClock.elapsedRealtime();
            while (bKeepRunning) {

                socket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                lastMessage = message;
                Log.e("UDP Receive", message);
                //runOnUiThread(updateTextMessage);
                timer = SystemClock.elapsedRealtime() - timer_start;
                if (timer >= 5000) bKeepRunning = false;
            }

        }
        catch(SocketTimeoutException e) {
            e.printStackTrace();
        }

        catch (Throwable e) {
            e.printStackTrace();
        }
        finally {

                if(socket != null) socket.close();
            }
        }

    public void kill() {
        bKeepRunning = false;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    private Runnable updateTextMessage = new Runnable() {
        public void run() {
            if (myDatagramReceiver == null) return;
            //textMessage.setText(myDatagramReceiver.getLastMessage());
        }
    };

}