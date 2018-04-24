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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Created by irene on 30/12/2017.
 */

public class Tab1_Lamp extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Switch.OnCheckedChangeListener, EditText.OnFocusChangeListener, EditText.OnEditorActionListener{

    //lamp infos
    private int pos;
    private String ip;
    private Lamp activeLamp = null;

    //variables
    //GUI
    private Switch switch1;
    private Button b1;
    private Button b2;
    private Button b3;
    private LampView_inclination lv;
    private SeekBar sb, seekBar, inclAngle;
    private EditText timer;

    //default messages
    private final String turnOn = "turnOn";
    private final String turnOff = "turnOff";
    private final String setIntensity = "setIntensity";
    private final String setColor = "setColor";
    private final String setIncl = "setInclination";
    private final String setRot = "setRotation";
    private final String setTimer = "setTimer";

    //seekbar and view controls
    private int maxLum = 255;
    private int lumStep = 5;
    private int currentProgress;
    private int seekMax = maxLum/lumStep;

    //service
    private TcpService myService;
    private boolean mBound = false;
    private TcpService.OnMessageReceived messageReceived = null;

    //color preset constants
    public static final long FOCUS = Long.decode("0x87d0ff");
    public static final long NEUTRAL = Long.decode("0xfffec9");
    public static final long RELAX = Long.decode("0xfff296");
    public static boolean preset = false;

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
        if(activeLamp != null) {
        switch1.setChecked(activeLamp.isOn());
        seekBar.setProgress(activeLamp.getIntensity()/lumStep);
        }
    }

   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
            savedInstanceState.putBoolean("Sent", sent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        if(savedInstanceState != null)
        sent = savedInstanceState.getBoolean("Sent");
    }
*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //context
        final Context context = getActivity().getApplicationContext();
        Intent intent = new Intent(context, TcpService.class);
        intent.putExtra("IP", ip);
        View view = getLayoutInflater().inflate(R.layout.tab1_lamp, container, false);
        View view1 = getLayoutInflater().inflate(R.layout.tab3_rotation, container, false);

        LampManager lm = LampManager.getInstance();
        final ArrayList<Lamp> lamps = (ArrayList<Lamp>) lm.getLamps();
        activeLamp = lamps.get(pos);

        b1 = view.findViewById(R.id.button);
        b2 = view.findViewById(R.id.button2);
        b3 = view.findViewById(R.id.button3);
        switch1 = view.findViewById(R.id.switch1);
        seekBar = view.findViewById(R.id.seekBar);
        inclAngle = view1.findViewById(R.id.seekBar2);
        timer = view.findViewById(R.id.timer);

        switch1.setChecked(activeLamp.isOn());

        seekBar.setProgress(activeLamp.getIntensity());

        int color = activeLamp.getColor();

        if(color == (int)FOCUS) {
            b1.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.focus_light_selected));
            preset = true;
        }
        if(color == (int)NEUTRAL) {
            b2.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.neutral_light_selected));
            preset = true;
        }
        if(color == (int)RELAX) {
            b3.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.relax_light_selected));
            preset = true;
        }
        //if(mBound) {

           messageReceived = new TcpService.OnMessageReceived() {
                @Override
                public void messageReceived(final String message) {
                    //this method calls the onProgressUpdate
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(context.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {

                            //Switch-case method to set Lamp Attributes (board packets)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Log.e("message: ", message);

                            String[] recv = message.split(",");
                            switch (recv[0]) {

                                case turnOn:
                                    activeLamp.turnOn();
                                    switch1.setChecked(true);
                                    if (recv.length > 1) {
                                        activeLamp.setInclination(Integer.parseInt(recv[1]));
                                        inclAngle.setProgress(activeLamp.getInclination());
                                    }
                                    break;

                                case turnOff:
                                    activeLamp.turnOff();
                                    switch1.setChecked(false);
                                    if (recv.length > 1)
                                        inclAngle.setProgress(Integer.parseInt(recv[1]));
                                    break;

                                case setIntensity:
                                    if (recv.length > 1) {
                                        //Toast.makeText(context, recv[1], Toast.LENGTH_SHORT).show();
                                        activeLamp.setIntensity(Integer.parseInt(recv[1]));
                                        seekBar.setProgress((activeLamp.getIntensity() * seekMax) / maxLum);
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
            };

            TextView tv = view.findViewById(R.id.lamp_name);
            tv.setText(activeLamp.getName());
            ImageView iv = view.findViewById(R.id.lampada);
            iv.setImageDrawable(activeLamp.getPicture());

            b1.setOnClickListener(this);
            b2.setOnClickListener(this);
            b3.setOnClickListener(this);

            switch1.setOnCheckedChangeListener(this);

            seekBar.setMax(seekMax);
            if (activeLamp.getIntensity() != 0)
                seekBar.setProgress(activeLamp.getIntensity() / lumStep);
            else seekBar.setProgress(0);
            seekBar.setOnSeekBarChangeListener(this);

            timer.setOnFocusChangeListener(this);
            timer.setOnEditorActionListener(this);


       // }


        return view;
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            TcpService.MyLocalBinder binder = (TcpService.MyLocalBinder) service;
            myService = binder.getService();
            myService.setMessageListener(messageReceived);
            if(activeLamp != null) {
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                        myService.setMessage(setIntensity + "," + activeLamp.getIntensity());
                        sleep(1000);
                        myService.setMessage(setColor + "," + activeLamp.getColor());
                        sleep(1000);
                        myService.setMessage(setIncl + "," + activeLamp.getInclination());
                        sleep(1000);
                        myService.setMessage(setRot + "," + activeLamp.getRotation());
                        sleep(1000);

                        if(activeLamp.isOn())
                            myService.setMessage(turnOn);
                        else myService.setMessage(turnOff);
                    }

                    catch(InterruptedException e){
                            Runnable r = new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(), "Unable to init parameters", Toast.LENGTH_SHORT).show();
                                }
                            };

                            getActivity().runOnUiThread(r);
                        }
                    }
                });

                t.start();
            }
            mBound = true;
        }
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            myService = null;
        }
    };


    @Override
    public void onClick(View view) {

        switch(view.getId()) {

            case R.id.button:
                b1.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.focus_light_selected));
                b2.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.neutral_light));
                b3.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.relax_light));
                myService.setMessage(setColor + "," + (int)FOCUS);
                activeLamp.setColor((int)FOCUS);
                preset = true;
                break;

            case R.id.button2:
                b1.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.focus_light));
                b2.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.neutral_light_selected));
                b3.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.relax_light));
                myService.setMessage(setColor + "," + (int)NEUTRAL);
                activeLamp.setColor((int)NEUTRAL);
                preset = true;
                break;

            case R.id.button3:
                b1.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.focus_light));
                b2.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.neutral_light));
                b3.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.relax_light_selected));
                myService.setMessage(setColor + "," + (int)RELAX);
                activeLamp.setColor((int)RELAX);
                preset = true;
                break;

            default:
                break;
        }

    }

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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        if (switch1.isChecked()) {
            activeLamp.turnOn();
            if(myService != null)
            myService.setMessage(turnOn);
        } else {
            activeLamp.turnOff();
            if(myService != null)
            myService.setMessage(turnOff);
            timer.setText("");
        }

    }

    @Override
    public void onFocusChange(View view, boolean b) {

        if(!timer.hasFocus()){
            InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            //Clear focus here from edittext
            if(!timer.getText().equals("") && myService!= null)
                myService.setMessage(setTimer + "," + timer.getText().toString());
            timer.clearFocus();
        }
        return false;
    }
}