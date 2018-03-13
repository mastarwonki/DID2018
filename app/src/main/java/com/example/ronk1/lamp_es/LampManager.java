package com.example.ronk1.lamp_es;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.SystemClock;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.lang.Runnable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ronk1 on 30/11/17.
 */

public class LampManager {

//    private Map<String, Lamp> lampMap;
    private List<Lamp> lamps;
    private static LampManager instance = null;

    //private constructor to avoid client applications to use constructor
    private LampManager() {
        lamps = new ArrayList<>();
    }

    public static LampManager getInstance() {
        if (instance == null)
            instance = new LampManager();
        return instance;
    }

    public void addLamp(Lamp lamp, String url) {

        for (Lamp l : lamps) {
            if (l.getURL().equalsIgnoreCase(url)) {
                l.setTimestamp(System.currentTimeMillis());
                return;
            }
        }

        lamp.setTimestamp(System.currentTimeMillis());
        lamps.add(lamp);
    }

    public List<Lamp> getLamps() {
        return lamps;
    }

    public void discover(final LampAsyncTask lampAsyncTask) {
        lampAsyncTask.execute();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                Log.e("thread_udp", "partito");
//                String message;
//                byte[] lmessage = new byte[64000];
//                DatagramSocket socket = null;
//                DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
//
//                try {
//                    socket = new DatagramSocket(4096);
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
//
//                while (true) {
//
//                    try {
//
//                        socket.setSoTimeout(5000);
//                        socket.receive(packet);
//                        message = new String(lmessage, 0, packet.getLength());
//                        Log.e("UDP Receive", message);
//                        // TODO ADD IMG TO LAMP
//                        //lamp.setPicture(img);
//                        Lamp lamp = new Lamp(message);
//                        lamp.setName("Lampada " + message);
//                        addLamp(lamp, message);
//                        done.run();
//                        break;
//
//                    } catch (SocketException e) {
//                        // TODO Handle Exception
//                    } catch (SocketTimeoutException e) {
//                        continue;
//                    } catch (IOException e) {
//                        // TODO Handle Exception
//                    }finally {
//
////                if (socket != null)
////                    socket.close();
//
//                        // TODO Handle Closure
//
//                    }
//
//                }
//            }
//        };
//        runnable.run();

    }

    public void stopDiscover(LampAsyncTask lampAsyncTask) {
        lampAsyncTask.cancel(true);
        if(lampAsyncTask != null)
        lampAsyncTask = null;
    }
}