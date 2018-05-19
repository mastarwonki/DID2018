package com.example.ronk1.lamp_es;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import com.triggertrap.seekarc.SeekArc;

/**
 * Created by Ronk1 on 19/05/18.
 */

public class Tab2_Rotation extends Fragment {

    //variables
    private int pos, angle;
    private LampManager lm;
    private Lamp activeLamp;
    private String ip;

    private SeekArc seekArc, seekArc2, seekArc3;
    private TextView textView, textView2;
    //default messages
    private final String setIncl = "setInclination";
    private final String setRot = "setRotation";
    //service
    private TcpService myService;
    private boolean mBound = false;

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
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab2_rotation, container, false);
        lm = LampManager.getInstance();
        activeLamp = lm.getLamps().get(pos);

        Drawable mDrawable = context.getResources().getDrawable(R.drawable.seekbar_thumb);
        mDrawable.setColorFilter(new PorterDuffColorFilter(activeLamp.getColor(), PorterDuff.Mode.MULTIPLY));

        seekArc = view.findViewById(R.id.seekArc);
        seekArc.setProgress(activeLamp.getInclination());
        seekArc.setProgressColor(activeLamp.getColor());
        seekArc2 = view.findViewById(R.id.seekArc2);
        seekArc2.setProgress(activeLamp.getInclination());
        seekArc3 = view.findViewById(R.id.seekArc3);
        seekArc3.setProgress(activeLamp.getRotation());
        textView = view.findViewById(R.id.textView);
        textView2 = view.findViewById(R.id.textView2);

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                activeLamp.setInclination(angle);
                myService.setMessage(setIncl + "," + angle);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                angle = progress;
                textView.setText(String.valueOf(progress));
                seekArc2.setProgress(progress);
            }
        });

        seekArc2.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                activeLamp.setInclination(angle);
                myService.setMessage(setIncl + "," + angle);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                angle = progress;
                textView.setText(String.valueOf(progress));
                seekArc.setProgress(progress);
            }
        });

        seekArc3.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                activeLamp.setRotation(angle);
                myService.setMessage(setIncl + "," + angle);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

                if(progress < 164) angle = progress;

                else angle = 164;

                textView2.setText(String.valueOf(angle));
            }
        });

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
