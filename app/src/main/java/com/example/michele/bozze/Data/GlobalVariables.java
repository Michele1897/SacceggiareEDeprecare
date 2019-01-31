package com.example.michele.bozze.Data;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.michele.bozze.R;
import com.example.michele.bozze.StrutturaApp.BluetoothModule.BluetoothModule;

    /* PER SEBASTIANO!!!!

        NON ESISTE PIù IL METODO modificaTrovati(int,int)
        NELLA CLASSE BluetoothModule, RIGA 154, L'HO COMMENTATO

       PER SEBASTIANO!!!!
     */

public class GlobalVariables extends Application {

    private static final String TAG = "Global Variables";


    /*
    Ordine dei colori (numero restituito dal robot):
        0: No color
        1: Black
        2: Blue
        3: Green
        4: Yellow
        5: Red
        6: White
        7: Brown
     */


        //variabili e metodi per il bluetooth (per Sebastiano)
    BluetoothModule b1;
    BluetoothDevice mDevice;
    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }
    public void createBTModule(){
        
    }

    //questo array tiene conto del numero di oggetti da raccogliere, inseriti dall'utente
    int [] objectsToCollect = new int [7];

    //questo array tiene conto degli oggetti raccolti dal robot
    int [] collectedObjects = new int [7];

    //oggetti che il robot ignora
    int notCollectedObjects = 0;


    /*
    Dati i numeri degli oggetti inseriti dall'utente, crea l'array e il
    relativo array di oggetti da raccogliere

    Se è zero, non è da raccogliere nessun oggetto di quel colore
    Se è -1, deve raccogliere tutti quelli che trova

    */
    public void setObjectsToCollect(int [] a){
        System.arraycopy(a,0,this.objectsToCollect,0,7);
        collectedObjects = new int [7];
    }


    //ritorna il numeri totale di oggetti raccolti
    public int totalCollectedObjects(){
        int sum = 0;
        for(int a:this.collectedObjects)
            sum+=a;
        return sum;
    }
    //ritorna tutti gli oggetti ignorati
    public int getNotCollectedObjects() {
        return notCollectedObjects;
    }
    //ritorna il numero totale di oggetti rilevati, raccolti o ignorati
    public int totalDetectedObjects(){
        return totalCollectedObjects()+getNotCollectedObjects();
    }

    /*
    Metodi per aggiornare i vari array sulla base dell'input ricevuto dal robot
    (se trova oggetti)
    P è il numero dell'oggetto dato dal robot (vedere il commento a inizio codice,
    dove in base al colore dell'oggetto che rileva passa un numero diverso
    I numeri vanno da 1 a 7, dunque sarà da decrementare di 1
    NB!!: se dovessero sorgere problemi posso fare un mapping tra le stringhe
    es "Rosso" -> 5 per agevolare le cose
    Oppure posso fare un'insieme di costanti es: final static int Rosso = 5
    */
    public void collectObject(int p){
        /*
        L'array di oggetti da collezionare viene decrementato
        L'array di oggetti collezionati si incrementa
         */
        objectsToCollect[p-1]--;
        collectedObjects[p-1]++;
    }

    //AGGIUNGERE QUA LE VARIE FUNZIONI

    //funzione per aggiungere un oggetto ignorato, dipende da come lo passiamo dal robot


    //funzione per ottenere il numero di oggetti raccolti di un solo colore



    /*
        Altri metodi getter e setter autogenerati
     */
    public int[] getObjectsToCollect() {
        return objectsToCollect;
    }

    public int[] getCollectedObjects() {
        return collectedObjects;
    }

    public void setCollectedObjects(int[] collectedObjects) {
        this.collectedObjects = collectedObjects;
    }

    public void setNotCollectedObjects(int notCollectedObjects) {
        this.notCollectedObjects = notCollectedObjects;
    }

    public void setupConnection(BluetoothDevice device){
        mDevice = device;
        Log.e(TAG, "sto per istanziare btm...");
        b1 = new BluetoothModule(device, this);//istanzia btm
        Log.e(TAG, "istanziato btm, sto per lanciare start...");
        // questo e per iniziare a ricevere richieste di connessione

        b1.start();

        //questo e per inviare richiesta di connessione sicura o meno
        //boolean secure = false;
        //boolean secure = true;
        //b1.connect(secure);
        Log.e(TAG, "lanciato start");
    }

    //GETTER DELLA BTM
    public BluetoothModule useBluetooth(){
        return b1;
    }
}

