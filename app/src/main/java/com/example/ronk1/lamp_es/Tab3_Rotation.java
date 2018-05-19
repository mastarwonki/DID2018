package com.example.ronk1.lamp_es;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

/**
 * Created by irene on 30/12/2017.
 */

public class Tab3_Rotation extends Fragment implements SeekBar.OnSeekBarChangeListener{

    //variables
    int pos, angle;
    LampManager lm;
    Lamp activeLamp;
    String ip;
    private LampView_inclination inclination_view;
    private LampView_rotation rot_view;
    private SeekBar inclAngle, rotAngle;
    //default messages
    private final String turnOn = "turnOn";
    private final String turnOff = "turnOff";
    private final String setIntensity = "setIntensity";
    private final String setIncl = "setInclination";
    private final String setRot = "setRotation";
    //service
    private TcpService myService;
    private boolean mBound = false;
    private TcpService.OnMessageReceived messageReceived = null;

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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final Context context = getActivity().getApplicationContext();
        pos = this.getArguments().getInt("position");
        View view = getLayoutInflater().inflate(R.layout.tab3_rotation, container, false);
        lm = LampManager.getInstance();
        activeLamp = lm.getLamps().get(pos);

        /*messageReceived = new TcpService.OnMessageReceived() {
            @Override
            public void messageReceived(final String message) {
                //this method calls the onProgressUpdate
                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {

                        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        Log.e("message: ", message);

                        String[] recv = message.split(",");
                        switch (recv[0]) {

                            case turnOn:
                                activeLamp.turnOn();
                                if (recv.length > 1)
                                    activeLamp.setInclination(Integer.parseInt(recv[1]));
                                    inclAngle.setProgress(activeLamp.getInclination());
                                break;

                            case turnOff:
                                activeLamp.turnOff();
                                break;

                            case setIntensity:
                                if (recv.length > 1) {
                                    Toast.makeText(context, recv[1], Toast.LENGTH_SHORT).show();
                                    activeLamp.setIntensity(Integer.parseInt(recv[1]));
                                }
                                break;

                            case "Connection Refused":
                                //getActivity().finish();
                                break;

                            case "Connection Stopped":
                                //getActivity().finish();
                                break;

                            default:
                                break;
                        }

                    }
                };
                mainHandler.post(myRunnable);

            }
        }; */

        inclination_view=view.findViewById(R.id.lv);
        inclination_view.setAngle((float)activeLamp.getInclination());
        inclAngle=view.findViewById(R.id.seekBar2);
        inclAngle.setMax(164);
        inclAngle.setProgress(activeLamp.getInclination());
        inclAngle.setOnSeekBarChangeListener(this);

        rot_view=view.findViewById(R.id.lv2);
        rot_view.setAngle((float)activeLamp.getRotation());
        rotAngle=view.findViewById(R.id.seekBar3);
        rotAngle.setMax(164);
        rotAngle.setProgress(activeLamp.getRotation());
        rotAngle.setOnSeekBarChangeListener(this);

        return view;
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            TcpService.MyLocalBinder binder = (TcpService.MyLocalBinder) service;
            myService = binder.getService();
            //myService.setMessageListener(messageReceived);
            mBound = true;
        }
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            myService = null;
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()){

            case R.id.seekBar2:
                angle = progress;
                inclination_view.setAngle(progress);
                break;

            case R.id.seekBar3:
                angle = progress;
                rot_view.setAngle(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){

            case R.id.seekBar2:
                activeLamp.setInclination(angle);
               myService.setMessage(setIncl + "," + angle);
                break;

            case R.id.seekBar3:
                activeLamp.setRotation(angle);
                myService.setMessage(setRot + "," + angle);
                break;
        }

    }
}