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
import android.widget.SeekBar;
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
                Log.e(TAG,"ACTION BUTTON PRESS");
            }
            if (event.getAction() == MotionEvent.ACTION_BUTTON_RELEASE) {
                // g.useBluetooth().fermati();
                Log.e(TAG,"ACTION BUTTON RELEASE");
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // g.useBluetooth().fermati();
                Log.e(TAG,"ACTION DOWN");
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // g.useBluetooth().fermati();
                Log.e(TAG,"ACTION UP");
            }
            return false;
        }
    });

        SeekBar barraPinza = rootView.findViewById(R.id.SeekBar_pinza);
        SeekBar barraBraccio = rootView.findViewById(R.id.SeekBar_braccio);

        barraBraccio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true){
                    Log.e(TAG, "utente vuole muovere braccio");
                    try {
                        g.useBluetooth().muoviBraccio(progress);
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }

                }//
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        barraPinza.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true){
                    Log.e(TAG, "utente vuole muovere pinza");
                    try {
                        g.useBluetooth().muoviPinza(progress);
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }

                }//
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return rootView;
    }
}
