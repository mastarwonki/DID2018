package com.example.ronk1.lamp_es;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.ronk1.lamp_es.Tab1_Lamp.preset;

public class LampDetailsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int pos = 0;
    private String url;
    private Lamp activeLamp = null;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static Activity manager;
    public static boolean manager_running = false;

    //Bundle vars
    private boolean isOn = false;
    private int brightness = 15;
    private int color = 0xFF4286f4;
    private int incl = 82;
    private int rot = 82;

    @Override
    protected void onStop() {
        super.onStop();
        manager_running = false;

         editor = sharedPreferences.edit();
       if(activeLamp != null) {
           editor.putBoolean("isOn", activeLamp.isOn());
           editor.putInt("Brightness", activeLamp.getIntensity());
           editor.putInt("Color", activeLamp.getColor());
           editor.putInt("Inclination", activeLamp.getInclination());
           editor.putInt("Rotation", activeLamp.getRotation());
           //editor.putBoolean("Preset", preset);
           editor.commit();
       }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_details);

        manager = this;
        manager_running = true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        final Context context = getApplicationContext();
        Intent in = getIntent();
        //pos = in.getExtras().getInt("POSITION");
        url = in.getExtras().getString("URL");

        sharedPreferences = getSharedPreferences("LampStatus", 0);

        if(sharedPreferences != null) {
            isOn = sharedPreferences.getBoolean("isOn", false);
            brightness = sharedPreferences.getInt("Brightness", 0);
            color = sharedPreferences.getInt("Color", 0x4286f4);
            incl = sharedPreferences.getInt("Inclination", 0);
            rot = sharedPreferences.getInt("Rotation", 0);
            //preset = sharedPreferences.getBoolean("Preset", true);
        }
        LampManager lm = LampManager.getInstance();
        final ArrayList<Lamp> lamps = (ArrayList<Lamp>) lm.getLamps();
        for(int i = 0; i< lamps.size(); i++) {
            if(lamps.get(i).getURL().equals(url)) {
                pos = i;
                activeLamp = lamps.get(pos);
                if(isOn)
                    activeLamp.turnOn();
                else activeLamp.turnOff();
                activeLamp.setIntensity(brightness);
                activeLamp.setColor(color);
                activeLamp.setInclination(incl);
                activeLamp.setRotation(rot);
                break;
            }
        }

    }

   /* @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        if(activeLamp != null) {
            savedInstanceState.putBoolean("isOn", activeLamp.isOn());
            savedInstanceState.putInt("Brightness", activeLamp.getIntensity());
            savedInstanceState.putInt("Color", activeLamp.getColor());
            savedInstanceState.putInt("Inclination", activeLamp.getInclination());
            savedInstanceState.putInt("Rotation", activeLamp.getRotation());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        isOn = savedInstanceState.getBoolean("isOn");
        brightness = savedInstanceState.getInt("Brightness");
        color = savedInstanceState.getInt("Color");
        incl = savedInstanceState.getInt("Inclination");
        rot = savedInstanceState.getInt("Rotation");
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lamp_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("LAMP_POS", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lamp_details, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", pos);
            switch (position)
            {
                case 0:
                    Tab1_Lamp tab1 = new Tab1_Lamp();
                    tab1.setArguments(bundle);
                    return tab1;
                case 1:
                    Tab2_Rotation tab2 = new Tab2_Rotation();
                    tab2.setArguments(bundle);
                    return tab2;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position)
            {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }

    }
}
