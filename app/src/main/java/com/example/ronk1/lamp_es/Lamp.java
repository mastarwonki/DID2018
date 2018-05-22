package com.example.ronk1.lamp_es;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Ronk1 on 23/11/17.
 */

public class Lamp {

    private String URL;
    private int intensity = 15;
    private int hex = 0xFF4286f4;
    private String img;
    //private int img_res;
    private boolean state;
    private long timestamp;
    private String name;
    private int inclination = 0;
    private int rotation = 0;

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

    public void setColor(int hex) {

        this.hex = hex;

    }

    public int getColor() {
        return hex;
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

    public String getPicture() {
        return img;
    }

    public void setPicture(String img) {

        this.img = img;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public int getInclination() {
        return inclination;
    }

    public void setInclination(int inclination) {
        this.inclination = inclination;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

}

