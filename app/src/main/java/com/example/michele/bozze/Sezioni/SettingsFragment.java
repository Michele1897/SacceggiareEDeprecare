package com.example.michele.bozze.Sezioni;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;
import com.example.michele.bozze.Sezioni.Controlli.AutomaticFragment;

public class SettingsFragment extends Fragment {

    private static final String TAG = "Impostazioni";
    private Button confirm;
    private Spinner walls;
    private Spinner floor;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);

        floor = (Spinner) view.findViewById(R.id.spinner_floor);
        walls = (Spinner) view.findViewById(R.id.spinner_walls);

        confirm = (Button) view.findViewById(R.id.btn1);
        confirm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                GlobalVariables g = (GlobalVariables)(getActivity().getApplication());
                boolean test = g.getFloor()==0;
                g.selectColors((String)floor.getSelectedItem(),(String)walls.getSelectedItem());
                AutomaticFragment autoControls = (AutomaticFragment)((getActivity().getSupportFragmentManager().getFragments().get(1).getChildFragmentManager().getFragments().get(0)));
                autoControls.setSquareColors();
                if(test){
                    Toast.makeText(getActivity(),"Parametri confermati! Attendere",Toast.LENGTH_SHORT).show();
                    //qua devo spostarmi di fragment in automatico, ma non funziona
                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_content,new ControlsFragment()).commit();
                }
                else{
                    Toast.makeText(getActivity(),"Parametri modificati! Attendere",Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }
}
