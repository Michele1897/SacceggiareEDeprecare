package com.example.michele.bozze.Sezioni.Controlli;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Set;


//C'è molto codice "verboso" poichè non ho trovato un modo di gestire gli id degli elementi grafici
//come un array. Se vuoi posso fare una versione migliore
public class AutomaticFragment extends Fragment {

    Button settings,start,reset;
    TextView squares [];
    Button infinite [];
    EditText number_squares [];
    TextView t1,t2;
    View rootView;
    boolean visible = true;

    public AutomaticFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_automatic_controls, container, false);

        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());

        //collega gli elementi grafici agli oggetti presenti in questo codice
        initializeElements();

        //azione di inserire infinito per ogni bottone
        setInfiniteAction();

        //bottone Impostazioni nella schermata di controllo automatico
        settings = rootView.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(visible)
                    setHidden();
                else
                    setVisible();
                visible = !visible;
            }
        });

        //bottone di reset per azzerare i numeri degli oggetti da cercare
        reset = rootView.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)(rootView.findViewById(R.id.number_square_1))).setText("");
                ((EditText)(rootView.findViewById(R.id.number_square_2))).setText("");
                ((EditText)(rootView.findViewById(R.id.number_square_3))).setText("");
                ((EditText)(rootView.findViewById(R.id.number_square_4))).setText("");
                ((EditText)(rootView.findViewById(R.id.number_square_5))).setText("");
                ((EditText)(rootView.findViewById(R.id.number_square_6))).setText("");
            }
        });

        //fa partire l'esecuzione
        start = rootView.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (start.getText().equals("Start")) {
                    if (g.getFloor() == 0)
                        Toast.makeText(getActivity(), "Confermare prima il colore di muri e pavimenti!", Toast.LENGTH_SHORT).show();
                    else {
                        start.setText("Stop");
                        setHidden();
                        int a [] = new int [g.isSixthColorSearched()?6:5];
                        for(int i=0;i<a.length;i++){
                            if(number_squares[i].getText().toString().equals("∞")) a[i] = -1;
                            else if(number_squares[i].getText().toString().equals("")) a[i] = 0;
                            else a[i] = Integer.parseInt(number_squares[i].getText().toString());
                        }
                        g.setObjectsToCollect(a);
                        //bisogna far apparire l'oggetto mappa
                    }
                } else {
                    start.setText("Start");
                }
            }
        });
        return rootView;
    }

        //Per ognugno dei sei bottoni con l'infinito disegnato, inserisce l'azione (consiste nello
        //scrivere nella casella di testo accanto il valore infinito)
    public void setInfiniteAction(){
        rootView.findViewById(R.id.infinite1).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_1)).setText("∞");
            }
        });
        rootView.findViewById(R.id.infinite2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_2)).setText("∞");
            }
        });
        rootView.findViewById(R.id.infinite3).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_3)).setText("∞");
            }
        });
        rootView.findViewById(R.id.infinite4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_4)).setText("∞");
            }
        });
        rootView.findViewById(R.id.infinite5).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_5)).setText("∞");
            }
        });
        rootView.findViewById(R.id.infinite6).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_6)).setText("∞");
            }
        });
    }

        //associa gli elementi grafici della pagina ad oggetti
    public void initializeElements(){
        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());
        t1 = rootView.findViewById(R.id.textView5);
        t2 = rootView.findViewById(R.id.textView6);
        reset = rootView.findViewById(R.id.reset);
        if(g.isSixthColorSearched()){
            squares = new TextView[6];
            infinite = new Button[6];
            number_squares = new EditText[6];
        }

        squares[0] = rootView.findViewById(R.id.square1);
        squares[1] = rootView.findViewById(R.id.square2);
        squares[2] = rootView.findViewById(R.id.square3);
        squares[3] = rootView.findViewById(R.id.square4);
        squares[4] = rootView.findViewById(R.id.square5);
        squares[5] = rootView.findViewById(R.id.square6);

        infinite[0] = rootView.findViewById(R.id.infinite1);
        infinite[1] = rootView.findViewById(R.id.infinite2);
        infinite[2] = rootView.findViewById(R.id.infinite3);
        infinite[3] = rootView.findViewById(R.id.infinite4);
        infinite[4] = rootView.findViewById(R.id.infinite5);
        infinite[5] = rootView.findViewById(R.id.infinite6);

        number_squares[0] = rootView.findViewById(R.id.number_square_1);
        number_squares[1] = rootView.findViewById(R.id.number_square_2);
        number_squares[2] = rootView.findViewById(R.id.number_square_3);
        number_squares[3] = rootView.findViewById(R.id.number_square_4);
        number_squares[4] = rootView.findViewById(R.id.number_square_5);
        number_squares[5] = rootView.findViewById(R.id.number_square_6);

        if(!g.isSixthColorSearched()) {
            rootView.findViewById(R.id.square6).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.infinite6).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.number_square_6).setVisibility(View.INVISIBLE);
        }
    }

    //mostra gli elementi
    public void setVisible(){
        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());
        t1.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);
        int loop;
        if(g.isSixthColorSearched()) loop = 6;
        else loop = 5;
            for (int i = 0; i < loop; i++) {
                number_squares[i].setVisibility(View.VISIBLE);
                squares[i].setVisibility(View.VISIBLE);
                infinite[i].setVisibility(View.VISIBLE);
            }
    }

    //nasconde gli elementi
    public void setHidden(){
        t1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        reset.setVisibility(View.INVISIBLE);
        for (int i = 0; i < squares.length; i++) {
            number_squares[i].setVisibility(View.INVISIBLE);
            squares[i].setVisibility(View.INVISIBLE);
            infinite[i].setVisibility(View.INVISIBLE);
        }
    }

    public void setSquareColors(){
        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());
        HashMap<String,Integer> h = g.getColors();
        Object s [] = h.keySet().toArray();
        ((TextView)(rootView.findViewById(R.id.square1))).setBackgroundColor(h.get(s[0].toString()));
        ((TextView)(rootView.findViewById(R.id.square2))).setBackgroundColor(h.get(s[1].toString()));
        ((TextView)(rootView.findViewById(R.id.square3))).setBackgroundColor(h.get(s[2].toString()));
        ((TextView)(rootView.findViewById(R.id.square4))).setBackgroundColor(h.get(s[3].toString()));
        ((TextView)(rootView.findViewById(R.id.square5))).setBackgroundColor(h.get(s[4].toString()));
        if(g.isSixthColorSearched())
            ((TextView)(rootView.findViewById(R.id.square6))).setBackgroundColor(h.get(s[5].toString()));
        else {
            rootView.findViewById(R.id.square6).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.infinite6).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.number_square_6).setVisibility(View.INVISIBLE);
        }
    }
}
