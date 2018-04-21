package com.example.ronk1.lamp_es;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.widget.Toast;

import com.example.ronk1.lamp_es.Lamp;

import static com.example.ronk1.lamp_es.LampDetailsActivity.manager;
import static com.example.ronk1.lamp_es.LampDetailsActivity.manager_running;

public class ListActivity extends AppCompatActivity {

    private Context ref = null;
    private ListView listView;
    private ProgressBar loader;
    LampManager lm;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(listView != null && listView.getAdapter() != null)
            ((LampListAdapter)listView.getAdapter()).notifyDataSetChanged();
        if(LampManager.getInstance().getLamps() != null && LampManager.getInstance().getLamps().isEmpty())
            loader.setVisibility(View.VISIBLE);
        else
            loader.setVisibility(View.GONE);
        super.onResume();
    }

    public Runnable doneMethod() {
        return new Runnable() {
            @Override
            public void run() {

                // Remove expired lamps
                List<Lamp> lamps = LampManager.getInstance().getLamps();
                Iterator<Lamp> iter = lamps.iterator();
                //for (Lamp lamp : lamps) {
                while(iter.hasNext()) {
                    Lamp lamp = iter.next();
                    if ((System.currentTimeMillis() - lamp.getTimestamp()) >= 20000) {
                        iter.remove();
                        Toast.makeText(getApplicationContext(), "Lost connection with " + lamp.getName(), Toast.LENGTH_LONG).show();
                        ((LampListAdapter) listView.getAdapter()).notifyDataSetChanged();
                        //If I'm in lampInfos, stop the activity
                        if(manager_running)
                            manager.finish();

                    }
                }
                //}
                
                if(lamps.isEmpty())
                    loader.setVisibility(View.VISIBLE);
                else
                    loader.setVisibility(View.GONE);


                ((LampListAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        };
    }

    LampAsyncTask lampAsyncTask = new LampAsyncTask(doneMethod());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ref = getApplicationContext();
        loader = findViewById(R.id.loader);
        lm = LampManager.getInstance();
        listView =  findViewById(R.id.list_lamp);
        listView.setAdapter(new LampListAdapter(this, lm.getLamps()));
        lm.discover(lampAsyncTask);
    }

    @Override
    protected void onDestroy() {
        lampAsyncTask.stopRunning();
        super.onDestroy();

    }

}
