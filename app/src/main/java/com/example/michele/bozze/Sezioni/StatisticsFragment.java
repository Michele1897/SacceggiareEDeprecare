package com.example.michele.bozze.Sezioni;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;

public class StatisticsFragment extends Fragment {
    TextView trovatiRossi, trovatiGialli, trovatiVerdi, trovatiBlu, trovatiMarroni, trovatiNeri, trovatiTotale, rilevati, nonRaccolti;
    GlobalVariables myVariables;
    public StatisticsFragment() {

    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics,container,false);

        //init textview varie, forse sta roba va in oncreate? boh
        trovatiBlu = (TextView) view.findViewById(R.id.number_trovati_blu);
        trovatiRossi = (TextView) view.findViewById(R.id.number_trovati_rossi);
        trovatiVerdi = (TextView) view.findViewById(R.id.number_trovati_verdi);
        trovatiMarroni = (TextView) view.findViewById(R.id.number_trovati_marroni);
        trovatiGialli = (TextView) view.findViewById(R.id.number_trovati_gialli);
        trovatiNeri = (TextView) view.findViewById(R.id.number_trovati_neri);
        trovatiTotale = (TextView) view.findViewById(R.id.number_raccolti);
        rilevati = (TextView) view.findViewById(R.id.number_rilevati);
        nonRaccolti = (TextView) view.findViewById(R.id.number_non_raccolti);

        //linka a globalvariables per semplicita
        try {
            myVariables = (GlobalVariables) getActivity().getApplication();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        //ye gods, that casting

        //inizializza contenuti
        updateTexts();



        return view;
    }

    //metodo a parte perche va rimandato a ogni cambiamento di valori
    //altrimenti rimangono i vecchi lol
    public void updateTexts()
        {

            //"hei globalvariables, dimmi dimmi quanti oggetti abbiam arruffato"
            //"dimmelo come sai fare tu, con un array"
            // i numeri son messi in quest ordine:
            //{trovatiRossi, trovatiGialli, trovatiVerdi, trovatiBlu, trovatiMarroni, trovatiNeri}
            //coupling level: BLACK HOLE
            String warningskiller;
            int[] trovati = myVariables.giveTrovati();
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
        }
}
