package com.example.ronk1.lamp_es;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;

/**
 * Created by Ronk1 on 17/03/18.
 */

public class AdaptedDialogFragment extends AppCompatDialogFragment {

    String hexColor = "0xFFFFFF";

    public AdaptedDialogFragment() {


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_adapted_dialog, container, false);

        ColorPickerView colorPickerView = view.findViewById(R.id.color_picker_view);
        colorPickerView.addOnColorChangedListener(new OnColorChangedListener() {
            @Override public void onColorChanged(int selectedColor) {
                // Handle on color change
                //Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));

            }
        });
        colorPickerView.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {

            }
        });

        Button confirm = view.findViewById(R.id.confirm);


        return view;
    }
}
