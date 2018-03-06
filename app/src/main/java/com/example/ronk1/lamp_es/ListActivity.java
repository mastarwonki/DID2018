package com.example.ronk1.lamp_es;

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
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;

import com.example.ronk1.lamp_es.Lamp;

public class ListActivity extends AppCompatActivity {

    private Context ref = null;
    private ListView listView;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if(listView != null && listView.getAdapter() != null)
            ((LampListAdapter)listView.getAdapter()).notifyDataSetChanged();
        super.onResume();
    }

    public Runnable doneMethod() {
        return new Runnable() {
            @Override
            public void run() {
                ((LampListAdapter)listView.getAdapter()).notifyDataSetChanged();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ref = getApplicationContext();
        LampManager lm = LampManager.getInstance();
        listView =  findViewById(R.id.list_lamp);
        listView.setAdapter(new LampListAdapter(this, lm.getLamps()));
        lm.discover(doneMethod());
    }

}
