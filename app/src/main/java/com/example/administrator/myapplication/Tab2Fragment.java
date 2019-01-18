package com.example.administrator.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    private Button btn2;

    public Tab2Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);
        btn2 = (Button) view.findViewById(R.id.btn2);

        btn2.setOnClickListener((View) -> {
            Toast.makeText(getActivity(),"TEST2", Toast.LENGTH_SHORT).show();/*solo esempio, lancia notifica in basso contenente testo*/
        });
        return view;
    }
}
