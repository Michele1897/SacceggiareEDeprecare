package com.example.michele.bozze.StrutturaApp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.michele.bozze.R;

import java.util.Iterator;
import java.util.Set;

public class ConnectionActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> apparecchiAppaiati;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter!=null){/*init app appaiati con set di apparecchi coupled via bluetooth*/
            apparecchiAppaiati = mBluetoothAdapter.getBondedDevices();
        }

        /*popolo opzioni spinner tramite lista device appaiati*/
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);

        //adapter.add((CharSequence) "Scegliere robot pirata");/*opzione iniziale*/

        Iterator<BluetoothDevice> iteratore = apparecchiAppaiati.iterator();/*iteratore che scorre il set di device appaiate*/
        BluetoothDevice tmp = null;/* var temporanea per scorrimento*/
        while (iteratore.hasNext())
            {/*scorre set di device appaiate*/
                tmp = iteratore.next();
                adapter.add((CharSequence) tmp.getName()); /*per ognuna, salva il nome in adapter*/
            }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        Button connect = findViewById(R.id.pulsanteConnetti);
        connect.setOnClickListener(v -> {
            startActivity(new Intent("android.intent.action.MainActivity"));
        });

    }
}
