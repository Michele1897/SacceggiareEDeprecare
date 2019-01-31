package com.example.michele.bozze.Sezioni;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;

public class ManualFragment extends Fragment {



    private static final String TAG  = "MANUAL FRAGMENT";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manual_controls, container, false);

        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());

        Log.e(TAG, "inizio a creare Button");
        ImageButton frecciaDX = rootView.findViewById(R.id.arrow_right);
        ImageButton frecciaSX = rootView.findViewById(R.id.arrow_left);
        ImageButton frecciaAV = rootView.findViewById(R.id.arrow_up);
        ImageButton frecciaIN = rootView.findViewById(R.id.arrow_down);

        Context me = this.getContext();

        Log.e(TAG, "setup ontouch listener");
        frecciaDX.setOnTouchListener(new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                //g.useBluetooth().muoviAvanti();

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // g.useBluetooth().fermati();
                Toast.makeText(me, "ACTION UP", Toast.LENGTH_LONG);
            }//NON SEMBRA RICONOSCERE ACTION DOWN
            return false;
        }
    });

        return rootView;
    }
}
