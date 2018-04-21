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
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

import java.util.ArrayList;

/**
 * Created by irene on 30/12/2017.
 */

public class Tab2_Color extends Fragment implements View.OnClickListener{

    //variables
    int pos;
    LampManager lm;
    Lamp activeLamp;
    String ip;
    int color;
    String hexcolor;
    ColorPickerView colorPickerView;
    //default messages
    private final String turnOn = "turnOn";
    private final String turnOff = "turnOff";
    private final String setIntensity = "setIntensity";
    private final String setColor = "setColor";
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
        colorPickerView.setInitialColor(activeLamp.getColor(), true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final Context context = getActivity().getApplicationContext();
        pos = this.getArguments().getInt("position");
        View view = getLayoutInflater().inflate(R.layout.tab2_color, container, false);
        TextView txt = view.findViewById(R.id.txt);
        txt.setText(String.valueOf(pos));
        lm = LampManager.getInstance();
        activeLamp = lm.getLamps().get(pos);

        /* messageReceived = new TcpService.OnMessageReceived() {
            @Override
            public void messageReceived(final String message) {
                //this method calls the onProgressUpdate
                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {

                        // TODO Switch-method to set Lamp Attributes (board packets)
                        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        Log.e("message: ", message);

                        String[] recv = message.split(",");
                        switch (recv[0]) {

                            case turnOn:
                                activeLamp.turnOn();
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

        colorPickerView = view.findViewById(R.id.color_picker_view);
        colorPickerView.setInitialColor(activeLamp.getColor(), true);
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override public void onColorChanged(int selectedColor) {
                // Handle on color change
                Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
            }
        });

        colorPickerView.addOnColorSelectedListener((new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                Toast.makeText(
                        getContext(),
                        "selectedColor: " + Integer.toHexString(selectedColor).toUpperCase(),
                        Toast.LENGTH_SHORT).show();
                        //hexcolor = Integer.toHexString(selectedColor).toUpperCase();
                        //color = Long.decode("0x" + hexcolor);
                        color = selectedColor;

            }
        }));

        Button colorSetter = view.findViewById(R.id.colorSetter);
        colorSetter.setOnClickListener(this);

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
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.colorSetter:
                activeLamp.setColor(color);
                myService.setMessage(setColor + "," + color);
        }

    }
}