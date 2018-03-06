package com.example.ronk1.lamp_es;

import android.graphics.Bitmap;
import android.media.Image;

/**
 * Created by Ronk1 on 23/11/17.
 */

public class Lamp {

    private String URL;
    private int intensity;
    private int rgb;
    private Bitmap img;
    //private int img_res;
    private boolean state;
    String name;

    /*public Lamp (int brightness, int color, int img_res, boolean state, String name) {

        this.brightness = brightness; //invece di settare la luminosità iniziale,
        this.color = color;          //la andrò a prendere dall'ultima impostata
        this.img_res = img_res;             //tramite SharedPreferences
        this.state = state;
        this.name = name;
    } */

    public Lamp(String URL) {
        this.URL = URL;
    }

    public String getURL() {

        return URL;

    }

    public void setIntensity(int intensity) {

        this.intensity = intensity;

    }

    public int getIntensity() {

        return intensity;

    }

    public void setColor(int rgb) {

        this.rgb = rgb;

    }

    public int getColor() {
        return rgb;
    }


    public void setName(String name) {

        this.name = name;

    }

    public String getName() {
        return name;
    }

    public boolean isOn() {
        return state;
    }

    public void turnOn() {

        this.state = true;

    }

    public void turnOff() {

        this.state = false;

    }

    public Bitmap getPicture() {
        return img;
    }

    public void setPicture(Bitmap img) {

        this.img = img;
    }

}


