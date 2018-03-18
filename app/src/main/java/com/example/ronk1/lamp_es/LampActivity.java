package com.example.ronk1.lamp_es;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.ronk1.lamp_es.Lamp;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;

/**
 * Created by Ronk1 on 24/11/17.
 */



public class LampActivity extends AppCompatActivity{

    //variables
    private LampView lv;
    private SeekBar sb, seekBar;
    private TcpClient tcpClient;
    private ConnectTask connectTask;

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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void startMyTask(AsyncTask asyncTask) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        else
            asyncTask.execute("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tcpClient != null) {
            tcpClient.stopClient();
            tcpClient = null;
        }
        if(connectTask != null && tcpClient == null){
            connectTask.cancel(true);
            connectTask = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lamp_activity);
        Intent in = getIntent();
        String URL = in.getExtras().getString("URL");
        String url2 = "NOT_DEFINED";
        final Context ref = getApplicationContext();
        LampManager lm = LampManager.getInstance();
        final ArrayList<Lamp> lamps = (ArrayList<Lamp>) lm.getLamps();
        Lamp tmp = new Lamp(url2);

        int i = 0;
        for(; i< lamps.size(); i++) {
            if(lamps.get(i).getURL().equals(URL)) {
                tmp = lamps.get(i);
                break;
            }
        }

        final Lamp activeLamp = tmp;

        if(!activeLamp.getURL().equals("NOT_DEFINED")) {

            final Button b1 = findViewById(R.id.button);
            final Button b2 = findViewById(R.id.button2);
            final Button b3 = findViewById(R.id.button3);
            final Switch switch1 = findViewById(R.id.switch1);

            if(activeLamp.isOn())
                switch1.setChecked(true);
            else
                switch1.setChecked(false);
            final int position = i;

            tcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(final String message) {
                    //this method calls the onProgressUpdate
                    // Get a handler that can be used to post to the main thread
                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {


                            // TODO Switch-method to set Lamp Attributes (board packets)
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                                    if(recv.length > 1) {
                                        Toast.makeText(ref, recv[1], Toast.LENGTH_SHORT).show();
                                        activeLamp.setIntensity(Integer.parseInt(recv[1]));
                                        seekBar.setProgress((activeLamp.getIntensity() * seekMax)/maxLum);
                                    }
                                    break;
                            }

                        } // This is your code
                    };
                    mainHandler.post(myRunnable);

                }
            }, activeLamp);
            connectTask = new ConnectTask(getApplicationContext(), this);
            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, tcpClient);

            TextView tv = findViewById(R.id.lamp_name);
            tv.setText(activeLamp.getName());
            ImageView iv = findViewById(R.id.lampada);
            iv.setImageBitmap(activeLamp.getPicture());

            //LAMP_ROT
            lv=findViewById(R.id.lv);
            sb=findViewById(R.id.sb);
            sb.setProgress((int)lv.getAngle());

            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    lv.setAngle(progress);
                    Log.d("seekBar", ""+progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                        b1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.focus_light_selected));
                        b2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.neutral_light));
                        b3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.relax_light));

                }
            });

            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                    b1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.focus_light));
                    b2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.neutral_light_selected));
                    b3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.relax_light));

                }
            });

            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {

                    b1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.focus_light));
                    b2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.neutral_light));
                    b3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.relax_light_selected));

                   /* ColorPickerDialogBuilder
                            .with(ref)
                            .setTitle("Choose Color")
                            .initialColor(activeLamp.getColor())
                            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                            .density(8)
                            .setOnColorChangedListener(new OnColorChangedListener() {
                                @Override
                                public void onColorChanged(int selectedColor) {
                                    // Handle on color change
                                    Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
                                }
                            })
                            .setOnColorSelectedListener(new OnColorSelectedListener() {
                                @Override
                                public void onColorSelected(int selectedColor) {
                                    Toast.makeText(getApplicationContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("ok", new ColorPickerClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                    activeLamp.setColor(Integer.parseInt("0x" + selectedColor));

                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .showColorEdit(true)
                            .setColorEditTextColor(ContextCompat.getColor(LampActivity.this, android.R.color.holo_blue_bright))
                            .build()
                            .show(); */
                }
            });

            switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(switch1.isChecked()) {
                        if (tcpClient != null) {
                            tcpClient.setMessage("turnOn");
                        }
                        lamps.get(position).turnOn();
                    } else {
                        if (tcpClient != null) {
                            tcpClient.setMessage("turnOff");
                        }
                        lamps.get(position).turnOff();
                    }
                }
            });

            seekBar = findViewById(R.id.seekBar);
            seekBar.setMax(seekMax);
            seekBar.setProgress(3);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

                    currentProgress = progress*lumStep;
                    activeLamp.setIntensity(currentProgress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tcpClient.setMessage(setIntensity + "," + activeLamp.getIntensity());

                }
            });

        }

    }


}
