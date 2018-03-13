package com.example.ronk1.lamp_es;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Ronk1 on 06/03/18.
 */

public class LampAsyncTask extends AsyncTask<String,Integer, Long> {

    private Runnable done;

    public LampAsyncTask(Runnable done) {
        this.done = done;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        done.run();
    }

    @Override
    protected Long doInBackground(String... params) {
        Log.e("thread_udp", "partito");
        String message;
        byte[] lmessage = new byte[64000];
        DatagramSocket socket = null;
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

        try {
            socket = new DatagramSocket(4096);
        } catch (SocketException e) {
            e.printStackTrace();
        }


        while (true) {

            try {

                socket.setSoTimeout(5000);
                socket.receive(packet);
                message = new String(lmessage, 0, packet.getLength());
                Log.e("UDP Receive", message);
                // TODO ADD IMG TO LAMP
                //lamp.setPicture(img);
                //retrieve IP from message
                if(message.contains(",")) {
                    String[] str = message.split(",");
                    Lamp lamp = new Lamp(str[0]);
                    lamp.setName(str[1]);
                    LampManager.getInstance().addLamp(lamp, lamp.getURL());
                }
                else {
                    Lamp lamp = new Lamp(message);
                    lamp.setName(message);
                    LampManager.getInstance().addLamp(lamp, message);
                }
                publishProgress(100);

            } catch (SocketException e) {
                // TODO Handle Exception
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                publishProgress(100);
                continue;
            } catch (IOException e) {
                // TODO Handle Exception
            }finally {

//                if (socket != null)
//                    socket.close();

                // TODO Handle Closure

            }

        }


    }
}
