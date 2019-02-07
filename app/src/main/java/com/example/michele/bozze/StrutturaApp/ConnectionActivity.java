package com.example.michele.bozze.StrutturaApp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.michele.bozze.Data.GlobalVariables;
import com.example.michele.bozze.R;
import com.example.michele.bozze.StrutturaApp.BluetoothModule.BluetoothModule;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class ConnectionActivity extends AppCompatActivity {
    private final static String TAG = "Connection Activity";

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> apparecchiAppaiati;
    private BluetoothDevice myDevice;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter!=null){/*init app appaiati con set di apparecchi coupled via bluetooth*/
            apparecchiAppaiati = mBluetoothAdapter.getBondedDevices();
        }

        //popolo opzioni spinner tramite lista device appaiati
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);

        BluetoothDevice[] arrDevices = new BluetoothDevice[apparecchiAppaiati.size()];

        Iterator<BluetoothDevice> iteratore = apparecchiAppaiati.iterator();/*iteratore che scorre il set di device appaiate*/
        BluetoothDevice tmp;// var temporanea per scorrimento
        int i = 0;
        while (iteratore.hasNext())
            {//scorre set di device appaiate
                tmp = iteratore.next();
                adapter.add((CharSequence) tmp.getName()); //per ognuna, salva il nome in adapter
                arrDevices[i]=tmp;//device salvata in parallelo nell'array
                i++;
            }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //questo metodo viene chiamato fintanto che qualcosa e selezionato, anche se e il default
                myDevice = mBluetoothAdapter.getRemoteDevice(arrDevices[position].getAddress());
                //Toast.makeText(context,"hai selezionato " + myDevice.getName(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        Button connect = findViewById(R.id.pulsanteConnetti);
        connect.setOnClickListener(v -> {
            //per Seba: piglia la globalVariables e fai setmDevice
            //chiamare metodo per istanziare bluetoothModule
            GlobalVariables gv = (GlobalVariables) getApplication();
            //da device a globalvariables
            //metodo automaticamente istanzia bluetoothmodule e connette
            if(myDevice!=null){
                Log.e(TAG, myDevice.getName());
                gv.setupConnection(myDevice);
                try{
                    BluetoothModule btm = gv.useBluetooth();
                    MainActivityStarterThread thr = new MainActivityStarterThread(btm);
                    thr.start();
                }
                catch (Exception e){
                    Log.e(TAG, "manca la BTM? " + e.getMessage());
                }






                //startActivity(new Intent("android.intent.action.MainActivity"));
            }
            else{
                Log.e(TAG, "myDevice null");
            }

            //startActivity(new Intent("android.intent.action.MainActivity"));



            // bisogna passare myDevice a qualcuno
            // si potrebbe salvarlo in GlobalVariables (oggetti application sono istanziati per prima e
            // ottenibili da qls activity tramite getApplication)
            //si potrebbe rendere il campo mmDevice di bluetoothmodule un campo statico
            //onestamente per qst app ha poco senso avere +d1 oggetto di classe bluetoothmodule
        });

    }

    //THREAD VA IN BUSYLOOP FINCHE LA BTM PASSATA ALL ISTANZIAZIONE NON RICEVE MESS DA 0 A 7
    public class MainActivityStarterThread extends Thread{
        BluetoothModule btm;

        public MainActivityStarterThread(BluetoothModule asd){
            this.btm = asd;
        }
        @Override
        public void run() {
            Log.e(TAG, "MAIN ACTIVITY STARTER THREAD PARTITO");
            while (btm.ricevutoQualcosa()==false)
                {}//busy loop
            Log.e(TAG, "RICEVUTO QUALCOSA, ORA CREO MAIN ACTIVITY...");
            startActivity(new Intent("android.intent.action.MainActivity"));

        }
    }


}


