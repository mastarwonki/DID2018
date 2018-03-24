package com.example.ronk1.lamp_es;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import java.util.ArrayList;

/**
 * Created by irene on 30/12/2017.
 */

public class Tab1_Lamp extends Fragment {

    //lamp infos
    private int pos;
    private String ip;

    //variables
    private LampView lv;
    private SeekBar sb, seekBar;
    //private TcpClient tcpClient;
    //private ConnectTask connectTask;

    //default messages
    private final String turnOn = "turnOn";
    private final String turnOff = "turnOff";
    private final String setIntensity = "setIntensity";
    private final String setColor = "setColor";

    //seekbar and view controls
    private int maxLum = 255;
    private int lumStep = 5;
    private int currentProgress;
    private int seekMax = maxLum/lumStep;

    //service
    private TcpService myService;
    boolean mBound = false;

    @Override
    public void onStart() {
        super.onStart();
        pos = this.getArguments().getInt("position");
        LampManager lm = LampManager.getInstance();
        ip = lm.getLamps().get(pos).getURL();
        // Bind to LocalService
        Intent intent = new Intent(getActivity().getApplicationContext(), TcpService.class);
        intent.putExtra("IP", ip);
        getActivity().bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(myConnection);
        mBound = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //context
        final Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(context, TcpService.class);
        intent.putExtra("IP", ip);
        View view = getLayoutInflater().inflate(R.layout.tab1_lamp, container, false);

        LampManager lm = LampManager.getInstance();
        final ArrayList<Lamp> lamps = (ArrayList<Lamp>) lm.getLamps();
        final Lamp activeLamp = lamps.get(pos);

        final Button b1 = view.findViewById(R.id.button);
        final Button b2 = view.findViewById(R.id.button2);
        final Button b3 = view.findViewById(R.id.button3);
        final Switch switch1 = view.findViewById(R.id.switch1);

        if(activeLamp.isOn())
            switch1.setChecked(true);
        else
            switch1.setChecked(false);

        if(mBound) {

            myService.setMessageListener(new TcpService.OnMessageReceived() {
                @Override
                public void messageReceived(final String message) {
                    //this method calls the onProgressUpdate
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {


                            // TODO Switch-method to set Lamp Attributes (board packets)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Log.e("message: ", message);

                            String[] recv = message.split(",");
                            switch (recv[0]) {

                                case turnOn:
                                    activeLamp.turnOn();
                                    switch1.setChecked(true);
                                    break;

                                case turnOff:
                                    activeLamp.turnOff();
                                    switch1.setChecked(false);
                                    break;

                                case setIntensity:
                                    if (recv.length > 1) {
                                        Toast.makeText(context, recv[1], Toast.LENGTH_SHORT).show();
                                        activeLamp.setIntensity(Integer.parseInt(recv[1]));
                                        seekBar.setProgress((activeLamp.getIntensity() * seekMax) / maxLum);
                                    }
                                    break;
                            }

                        }
                    };
                    mainHandler.post(myRunnable);

                }
            });
            
            TextView tv = view.findViewById(R.id.lamp_name);
            tv.setText(activeLamp.getName());
            ImageView iv = view.findViewById(R.id.lampada);
            iv.setImageBitmap(activeLamp.getPicture());

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    b1.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_light_selected));
                    b2.setBackground(ContextCompat.getDrawable(context, R.drawable.neutral_light));
                    b3.setBackground(ContextCompat.getDrawable(context, R.drawable.relax_light));

                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    b1.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_light));
                    b2.setBackground(ContextCompat.getDrawable(context, R.drawable.neutral_light_selected));
                    b3.setBackground(ContextCompat.getDrawable(context, R.drawable.relax_light));

                }
            });

            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    b1.setBackground(ContextCompat.getDrawable(context, R.drawable.focus_light));
                    b2.setBackground(ContextCompat.getDrawable(context, R.drawable.neutral_light));
                    b3.setBackground(ContextCompat.getDrawable(context, R.drawable.relax_light_selected));
                }
            });

            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (switch1.isChecked()) {
                        //if (tcpClient != null) {
                        myService.setMessage("turnOn");
                        //}
                        activeLamp.turnOn();
                    } else {
                        //if (tcpClient != null) {
                        myService.setMessage("turnOff");
                        //}
                        activeLamp.turnOff();
                    }
                }
            });

            seekBar = view.findViewById(R.id.seekBar);
            seekBar.setMax(seekMax);
            if (activeLamp.getIntensity() != 0)
                seekBar.setProgress(activeLamp.getIntensity() / lumStep);
            else seekBar.setProgress(0);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                    currentProgress = progress * lumStep;
                    activeLamp.setIntensity(currentProgress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    myService.setMessage(setIntensity + "," + activeLamp.getIntensity());

                }
            });
        }


        return view;
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            TcpService.MyLocalBinder binder = (TcpService.MyLocalBinder) service;
            myService = binder.getService();
            mBound = true;
        }
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            myService = null;
        }
    };


}