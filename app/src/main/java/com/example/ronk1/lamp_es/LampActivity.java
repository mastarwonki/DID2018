package com.example.ronk1.lamp_es;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import com.example.ronk1.lamp_es.Lamp;

import java.util.ArrayList;

/**
 * Created by Ronk1 on 24/11/17.
 */

public class LampActivity extends AppCompatActivity {

    private LampView lv;
    private SeekBar sb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lamp_activity);
        Intent in = getIntent();
        String URL = in.getExtras().getString("URL");
        Context ref = getApplicationContext();
        LampManager lm = LampManager.getInstance();
        final ArrayList<Lamp> lamps = (ArrayList<Lamp>) lm.getLamps();
        Lamp activeLamp = new Lamp("NOT_DEFINED");
        int i = 0;
        for(; i< lamps.size(); i++) {
            if(lamps.get(i).getURL().equals(URL)) {
                activeLamp = lamps.get(i);
                break;
            }
        }

        if(!activeLamp.getURL().equals("NOT_DEFINED")) {

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


            final Button b1 = findViewById(R.id.button);
            final Button b2 = findViewById(R.id.button2);
            final Button b3 = findViewById(R.id.button3);

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

                }
            });


        }

        final Switch switch1 = findViewById(R.id.switch1);
        final int position = i;
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switch1.isChecked())
                    lamps.get(position).turnOn();
                else
                    lamps.get(position).turnOff();
            }
        });
    }
}
