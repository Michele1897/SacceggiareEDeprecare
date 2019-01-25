package com.example.michele.bozze.Sezioni.Controlli;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;

public class ManualFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manual_controls, container, false);

        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());

        return rootView;
    }
}
