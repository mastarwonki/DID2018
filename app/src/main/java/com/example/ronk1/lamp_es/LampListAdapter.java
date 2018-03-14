package com.example.ronk1.lamp_es;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Ronk1 on 06/03/18.
 */

public class LampListAdapter extends BaseAdapter {

    private Context context;
    private List<Lamp> lamps;

    public LampListAdapter(Context context, List<Lamp> lamps) {
        this.context = context;
        this.lamps = lamps;
    }

    @Override
    public int getCount() {
        return lamps.size();
    }

    @Override
    public Object getItem(int i) {
        return lamps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if(view == null)
            view = layoutInflater.inflate(R.layout.list_member, viewGroup, false);
        final Lamp lamp = (Lamp) this.getItem(i);
        // Populate TextView with Lamp Name
        TextView tv = view.findViewById(R.id.lamp_name);
        tv.setText(lamp.getName());
        // Populate ImageView with Lamp Icon
        //TODO Picasso HTTP
        Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.lamp1);
        lamp.setPicture(img);
        ImageView iv = view.findViewById(R.id.lamp_img);
        iv.setImageBitmap(lamp.getPicture());
        // Populate Switch On-Off depending on lamp state
        final Switch switchStatus = view.findViewById(R.id.lamp_switch);
        if(lamp.isOn())
            switchStatus.setChecked(true);
        else
            switchStatus.setChecked(false);
        switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switchStatus.isChecked()) {
                    lamp.turnOn();
                }
                else {
                    lamp.turnOff();
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, LampActivity.class);
                in.putExtra("URL", lamp.getURL());
                context.startActivity(in);
            }
        });
        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
