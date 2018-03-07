package com.example.ronk1.lamp_es;

import android.os.AsyncTask;

/**
 * Created by Ronk1 on 07/03/18.
 */

public class ConnectTask extends AsyncTask<TcpClient, String, Long> {

    public ConnectTask() {
    }

    @Override
    protected Long doInBackground(TcpClient... tcpClients) {
        tcpClients[0].run();
        return null;
    }
}
