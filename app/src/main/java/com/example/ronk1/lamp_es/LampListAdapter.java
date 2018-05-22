package com.example.ronk1.lamp_es;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
        //Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.lamp1);
        ImageView iv = view.findViewById(R.id.lamp_img);
        Picasso.get()
                .load(lamp.getPicture())
                .placeholder(R.drawable.lamp1) //http://i.imgur.com/DvpvklR.png "https://i.imgur.com/J4FPH26.png"
                .resize(100, 100)
                .centerCrop()
                .transform(new CircleTransform())
                .into(iv);
        /*Picasso.get()
                .load("https://i.imgur.com/J4FPH26.png")
                .placeholder(R.drawable.lamp1) //http://i.imgur.com/DvpvklR.png
                .resize(100, 100)
                .centerCrop()
                .transform(new CircleTransform())
                .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                lamp.setPicture(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        // Populate Switch On-Off depending on lamp state
        /*final Switch switchStatus = view.findViewById(R.id.lamp_switch);
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
        }); */
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent in = new Intent(context, LampActivity.class);
                in.putExtra("URL", lamp.getURL());
                context.startActivity(in); */
                Intent in = new Intent(context, LampDetailsActivity.class);
                //in.putExtra("POSITION", position);
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
