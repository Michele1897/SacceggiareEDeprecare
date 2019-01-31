package com.example.michele.bozze.Sezioni;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;

public class StatisticsFragment extends Fragment {

    TextView t [];

    public StatisticsFragment() {

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics,container,false);
        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());

            //inizializzazione elementi grafici
        t =  new TextView[10];
        t[0] = view.findViewById(R.id.number_rilevati);
        t[1] = view.findViewById(R.id.number_raccolti);
        t[2] = view.findViewById(R.id.number_trovati_neri);
        t[3] = view.findViewById(R.id.number_trovati_blu);
        t[4] = view.findViewById(R.id.number_trovati_rossi);
        t[5] = view.findViewById(R.id.number_trovati_verdi);
        t[6] = view.findViewById(R.id.number_trovati_gialli);
        t[7] = view.findViewById(R.id.number_trovati_bianchi);
        t[8] = view.findViewById(R.id.number_trovati_marroni);
        t[9] = view.findViewById(R.id.number_non_raccolti);

        setNumbers();



        return view;
    }
        //prende da GlobalVariables i valori e li mette nelle celle
    public void setNumbers(){
        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());
        t[0].setText(Integer.toString(g.totalDetectedObjects()));
        t[1].setText(Integer.toString(g.totalCollectedObjects()));

        int temp [] = g.getCollectedObjects();
        t[2].setText(Integer.toString(temp[0]));
        t[3].setText(Integer.toString(temp[1]));
        t[4].setText(Integer.toString(temp[2]));
        t[5].setText(Integer.toString(temp[3]));
        t[6].setText(Integer.toString(temp[4]));
        t[7].setText(Integer.toString(temp[5]));
        t[8].setText(Integer.toString(temp[6]));
        t[9].setText(Integer.toString(g.getNotCollectedObjects()));

    }

    //metodo a parte perche va rimandato a ogni cambiamento di valori
    public void updateTexts()
        {

            //"hei globalvariables, dimmi dimmi quanti oggetti abbiam arruffato"
            //"dimmelo come sai fare tu, con un array"
            // i numeri son messi in quest ordine:
            //{trovatiRossi, trovatiGialli, trovatiVerdi, trovatiBlu, trovatiMarroni, trovatiNeri}
            //coupling level: BLACK HOLE
            String warningskiller;
            /*int[] trovati = myVariables.giveTrovati();
            int totale = trovati[0] + trovati[1] + trovati[2] + trovati[3] + trovati[4] + trovati[5];
            warningskiller = ""+ totale;
            trovatiTotale.setText(warningskiller);
            warningskiller =("" + trovati[0]);
            trovatiRossi.setText(warningskiller);
            warningskiller =(""+ trovati[1]);
            trovatiGialli.setText(warningskiller);
            warningskiller =(""+ trovati[2]);
            trovatiVerdi.setText(warningskiller);
            warningskiller =(""+ trovati[3]);
            trovatiBlu.setText(warningskiller);
            warningskiller =(""+ trovati[4]);
            trovatiMarroni.setText(warningskiller);
            warningskiller =(""+ trovati[5]);
            trovatiNeri.setText(warningskiller);


            //manca non raccolti
            //nonRaccolti.setText(""+);
            //manca rilevati
            //rilevati.setText(""+);
            */
        }
}
