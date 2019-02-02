package com.example.michele.bozze.Sezioni;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;


//C'è molto codice "verboso" poichè non ho trovato un modo di gestire gli id degli elementi grafici
//come un array. Se vuoi posso fare una versione migliore
public class AutomaticFragment extends Fragment {

    private static final String TAG = "Automatic Fragment";

    //tutti gli elementi grafici
    Button start,stop,reset;
    TextView squares [];
    Button infinite [];
    EditText number_squares [];
    TextView t1,t2;

    GifImageView robot;

    View rootView;

    //variabile booleana per far partire l'esecuzione e nascondere gli oggetti/mostrare gif
    boolean started = false;

    //variabile booleana per permettere di fare diversi start & stop senza ri-settare i numeri
    //degli oggetti da cercare
    boolean initializedResearch = false;

    public AutomaticFragment(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_automatic_controls, container, false);

        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());

            //collega gli elementi grafici agli oggetti presenti in questo codice
            initializeElements();

            //azione di inserire infinito per ogni bottone
            setInfiniteAction();

            //fa partire l'esecuzione
            start = rootView.findViewById(R.id.start);
            start.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!started) {
                        started = !started;
                        setHidden();
                        robot.setVisibility(View.VISIBLE);
                        if (!initializedResearch) {
                            initializedResearch = !initializedResearch;
                            int a[] = new int[7];
                            for (int i = 0; i < a.length; i++) {
                                if (number_squares[i].getText().toString().equals("∞")) a[i] = -1;
                                else if (number_squares[i].getText().toString().equals(""))
                                    a[i] = 0;
                                else
                                    a[i] = Integer.parseInt(number_squares[i].getText().toString());
                            }
                            g.setObjectsToCollect(a);
                            //mandare l'array al robot e dirgli di partire

                            //azzera oggetti richiesti
                            try {
                                g.useBluetooth().annullaRichieste();
                            }catch(Exception e){
                                Log.e(TAG, e.getMessage());
                            }

                            //invia numero oggetti da trovare al bot
                            int i;
                            for(i=0;i<a.length;i++) {
                                try {
                                    g.useBluetooth().richiediOggetto(i + 1, a[i]);
                                }catch(Exception e){
                                    Log.e(TAG, e.getMessage());
                                }
                            }//
                        }
                    }
                    //dice al bot di andare in automatico
                    try {
                        g.useBluetooth().vaiAutomatico();
                    }
                    catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }



                }
            });

            //bottone stop nella schermata di controllo automatico
            stop = rootView.findViewById(R.id.stop);
            stop.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //bisogna stoppare il robot
                    if (started) {
                        setVisible();
                        robot.setVisibility(View.INVISIBLE);
                        started = !started;
                    }
                    //di al bot di andare in manuale
                    try {
                        g.useBluetooth().vaiManuale();
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                    }
                }
            });

            //bottone di reset per azzerare i numeri degli oggetti da cercare
            reset = rootView.findViewById(R.id.reset);
            reset.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((EditText) (rootView.findViewById(R.id.number_square_1))).setText("");
                    ((EditText) (rootView.findViewById(R.id.number_square_2))).setText("");
                    ((EditText) (rootView.findViewById(R.id.number_square_3))).setText("");
                    ((EditText) (rootView.findViewById(R.id.number_square_4))).setText("");
                    ((EditText) (rootView.findViewById(R.id.number_square_5))).setText("");
                    ((EditText) (rootView.findViewById(R.id.number_square_6))).setText("");
                    ((EditText) (rootView.findViewById(R.id.number_square_7))).setText("");
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
        rootView.findViewById(R.id.infinite7).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((EditText)rootView.findViewById(R.id.number_square_7)).setText("∞");
            }
        });
    }

    //associa gli elementi grafici della pagina ad oggetti
    public void initializeElements(){
        GlobalVariables g = (GlobalVariables)(getActivity().getApplication());
        t1 = rootView.findViewById(R.id.textView5);
        t2 = rootView.findViewById(R.id.textView6);
        reset = rootView.findViewById(R.id.reset);
        robot = rootView.findViewById(R.id.gif);


        squares = new TextView[7];
        infinite = new Button[7];
        number_squares = new EditText[7];

        squares[0] = rootView.findViewById(R.id.square1);
        squares[1] = rootView.findViewById(R.id.square2);
        squares[2] = rootView.findViewById(R.id.square3);
        squares[3] = rootView.findViewById(R.id.square4);
        squares[4] = rootView.findViewById(R.id.square5);
        squares[5] = rootView.findViewById(R.id.square6);
        squares[6] = rootView.findViewById(R.id.square7);

        infinite[0] = rootView.findViewById(R.id.infinite1);
        infinite[1] = rootView.findViewById(R.id.infinite2);
        infinite[2] = rootView.findViewById(R.id.infinite3);
        infinite[3] = rootView.findViewById(R.id.infinite4);
        infinite[4] = rootView.findViewById(R.id.infinite5);
        infinite[5] = rootView.findViewById(R.id.infinite6);
        infinite[6] = rootView.findViewById(R.id.infinite7);

        number_squares[0] = rootView.findViewById(R.id.number_square_1);
        number_squares[1] = rootView.findViewById(R.id.number_square_2);
        number_squares[2] = rootView.findViewById(R.id.number_square_3);
        number_squares[3] = rootView.findViewById(R.id.number_square_4);
        number_squares[4] = rootView.findViewById(R.id.number_square_5);
        number_squares[5] = rootView.findViewById(R.id.number_square_6);
        number_squares[6] = rootView.findViewById(R.id.number_square_7);
    }

    //mostra gli elementi
    public void setVisible(){
        t1.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        reset.setVisibility(View.VISIBLE);

        for (int i = 0; i < 7; i++) {
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

        for (int i = 0; i < 7; i++) {
            number_squares[i].setVisibility(View.INVISIBLE);
            squares[i].setVisibility(View.INVISIBLE);
            infinite[i].setVisibility(View.INVISIBLE);
        }
    }
}
