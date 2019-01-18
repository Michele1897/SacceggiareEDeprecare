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

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    private Button btn1;

    public Tab1Fragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);
        btn1 = (Button) view.findViewById(R.id.btn1);

        btn1.setOnClickListener((View) -> {
            Toast.makeText(getActivity(),"TEST1", Toast.LENGTH_SHORT).show();/*solo esempio, lancia notifica in basso contenente testo*/
        });
        return view;
    }
}
