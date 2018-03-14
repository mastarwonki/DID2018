package com.example.ronk1.lamp_es;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;


/**
 * Created by Ronk1 on 07/03/18.
 */

public class ConnectTask extends AsyncTask<TcpClient, String, Long> {

    private  Context context;
    private Activity activity;

    public ConnectTask(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        if(aLong == -1){
            Toast.makeText(context, "Connection refused", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    protected Long doInBackground(TcpClient... tcpClients) {
        try{
            tcpClients[0].run();
        }catch (IOException ex){
            ex.printStackTrace();
            return new Long(-1);
        }
        catch (InterruptedException e){
            e.printStackTrace();
            return new Long(-1);
        }

        return new Long(0);
    }
}
