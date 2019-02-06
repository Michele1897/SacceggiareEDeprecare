package com.example.michele.bozze.StrutturaApp.BluetoothModule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;

import com.example.michele.bozze.Data.GlobalVariables;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothModule {

    private GlobalVariables mGlobalVariables;
    private BluetoothServerSocket mBtServerSocket;

    //costanti di stato
    private final int STATE_CONNECTED =1;
    private final int STATE_LISTEN =2;
    private final int STATE_CONNECTING =3;
    private final int STATE_NONE=4;

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

    //costanti colore
    private final int NO_COLORE = 0;
    private final int COLORE_NERO = 1;
    private final int COLORE_BLU = 2;
    private final int COLORE_VERDE = 3;
    private final int COLORE_GIALLO = 4;
    private final int COLORE_ROSSO = 5;
    private final int COLORE_BIANCO = 6;
    private final int COLORE_MARRONE = 7;

    private BluetoothDevice myDevice;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean secure = true;

    //THREAD
    private ConnectThread mConnectThread;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectedThread mConnectedThread;

    //PUNTATORE A SE STESSO
    public BluetoothModule myself;

    //STATO
    private int mState;

    //UUID OBSOLETO
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //
    //
    //

    //metodi per settare connessioni (COME CLIENT) sicura o insicura
    //NB: come server automaticamente ascolta sia connessioni sicure che insicure
    public void setSecure(){this.secure=true;}
    public void setInsecure(){this.secure=false;}

    //unico costruttore di bluetoothmodule
    //tgt = bluetoothdevice appaiata (selezionata dallo spinner di connectionactivity)
    //gbl = globalvariables preistanziata a cui si daranno dati ricevuti via bluetooth
    public BluetoothModule(BluetoothDevice tgt, GlobalVariables gbl){
        myDevice = tgt;
        mGlobalVariables = gbl;
        myself = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //THREAD VARI//
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            try {
                if (secure) {
                    tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("connessione non sic",
                            MY_UUID_SECURE);
                    loggingFun("listen di connessione non sicura");
                } else {
                    tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            "connessione sicura", MY_UUID_INSECURE);
                    loggingFun("listen di connessione sicura");
                }
            } catch (IOException e) {
                loggingFun("ERRORE LISTEN RFCOMM \n");
                loggingFun("" + e.getMessage());
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            loggingFun("PARTE ACCEPTTHREAD \n");
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    //Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    loggingFun("ACCPET SOCKET FALLITO \n" + e.getMessage() + " \n");
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    loggingFun("SIAM PRIMA DEL HELPER ADESSO \n");
                    loggingFun("STATE = " + mState);

                    //DA TESTARE SYNCH
                    synchronized (BluetoothModule.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    //Log.e(TAG, "Could not close unwanted socket", e);
                                    loggingFun("FALLIMENTO CHIUSURA SOCKET INUTILE \n" + e.getMessage()+ " \n");
                                }
                                break;
                        }
                    }
                }
            }
            //Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
            loggingFun("FINE mAcceptThread \n");

        }

        public void cancel() {
            //Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            loggingFun("ACCEPTTHREAD CANCEL SOCKET \n");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
                loggingFun("FALLIMENTO CHIUSURA SOCKET \n" + e.getMessage() + " \n");
            }
        }
    }

    private class ConnectThread extends Thread {

        private BluetoothSocket muhSocket;
        private BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            //mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                //Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
                loggingFun("CREAZ SOCKET FALLITA \n" + e.getMessage());
            }
            muhSocket = tmp;
            if(muhSocket == null){
                loggingFun("SOCKET NULL \n");
            }
            mState = STATE_CONNECTING;
        }

        public void run() {
            loggingFun("INIZIO CONNECT THREAD \n");
            //setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();
            loggingFun("STO PER FARE .CONNECT...  \n");
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                this.muhSocket.connect();
            } catch (IOException e) {
                loggingFun("FAIL DELLA .CONNECT \n");
                // Close the socket
                try {
                    muhSocket.close();
                } catch (IOException e2) {
                    //Log.e(TAG, "unable to close() " + mSocketType +
                    //        " socket during connection failure", e2);
                    loggingFun("FALLIMENTO CHIUSUR SOCKET \n" + e.getMessage() + " \n");
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothModule.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(muhSocket, mmDevice, mSocketType);

        }

        public void cancel() {
            try {
                muhSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
                loggingFun("FALLITO CHIUSURA SOCKET \n" + e.getMessage() + " \n");
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            loggingFun("CREO CONNECTEDTHREAD \n");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();
            } catch (IOException e) {
                loggingFun("SOCKET TEMPORANEI NON CREATI \n" + e.getMessage());
            }

            //mmInStream = tmpIn;
            //mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            //Log.i(TAG, "BEGIN mConnectedThread");
            loggingFun("INIZIO CONNECTEDTHREAD \n");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    //bytes = numero bytes letti
                    //buffer = copia bytes letti
                    bytes = mmInStream.read(buffer);
                    //ho fatto test, non sembra bloccare thread
                    //si posson inviare messaggi prima di aver ricevuto alcun messaggio

                    //INSERIRE QUI METODO PER UTILIZZARE DATI
                    receiveMessage(buffer, bytes);

                } catch (IOException e) {
                    loggingFun("DISCONNESSO " + e.getMessage());
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param message The bytes to write
         */
        public void write(byte[] message) {
            try {
                //SCRIVE MESSAGGIO IN OUTPUTSTREAM
                mmOutStream.write(message);
            } catch (IOException e) {
                //Log.e(TAG, "Exception during write", e);
                loggingFun("ERRORE DURANTE WRITE \n");
                loggingFun("" + e.getMessage() + " \n");
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "close() of connect socket failed", e);
                loggingFun("FALLIMENTO CHIUSURA SOCKET \n");
                loggingFun("" + e.getMessage() + " \n");
            }
        }
    }

    //FINE THREAD




    //
    //GESTIONE MESSAGGI
    //

    //MESSAGGI IN USCITA

    //metodo interno
    //interpretatore messaggi input, lancia metodi del globalvariables
    //appropriati secondo dati appropriati interpretati dai byte ricevuti via bluetooth

    public void richiediOggetto(int colore, int quantit)
        {
            int i;//crea messaggio
            byte[] msg = new byte[quantit];
            for (i=0; i<quantit; i++){
                msg[i] = (byte) colore;
            }//messaggio e un numero di byte colore lungo quanti sono gli oggetti di quel colore desiderati
            try {
                sendData(msg);
            }catch (IOException e){
                loggingFun("ERRORE IN SEND :" + e.getMessage());
                loggingFun("MESSAGGIO: " + msg);

            }
        }

    public void annullaRichieste(){
        byte[] msg = {8};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO AZZERAMENTO RICHIESTE: " + e.getMessage());
        }
    }

    //FUNZIONE CAMBIO MODALITA
    public void vaiManuale(){
        byte[] msg = {9};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MODALITA MANUALE: " + e.getMessage());
        }
    }
    public void vaiAutomatico(){
        byte[] msg = {99};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MODALITA AUTOMATICA: " + e.getMessage());
        }
    }


    //FUNZIONI BRACCIO/PINZA
    
    //(byte) int funziona solo per int <257
    //per pinza/braccio bisogna inviare 2 byte.
    public void muoviBraccio(int position){
        //position va da 0 a 100
        
        byte[] msg = {(byte)55, (byte)position};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO MODIFICA BRACCIO: " + e.getMessage());
        }
    }
    public void muoviPinza(int position){
        //position va da 0 a 100
        int tru = position +2000;

        byte[] msg = {(byte)56, (byte)position};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO MODIFICA PINZA: " + e.getMessage());
        }
    }
    
    //funzioni alt
        public void muoviPinzaAlt(int position){
        //position va da 0 a 100
            int content;
        if (position < 25)
            {
            content = 30;
            }
        else if (position < 50)
            {
            content = 31;
            }
        else if (position < 75)
            {
            content = 32;
            }
        else if (position == 100)
            {
            content = 33;
            }
        byte[] msg = {(byte)content};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO MODIFICA PINZA: " + e.getMessage());
        }
    }
    public void muoviBraccioAlt(int position){
        //position va da 0 a 100
            int content;
        if (position < 25)
            {
            content = 40;
            }
        else if (position < 50)
            {
            content = 41;
            }
        else if (position < 75)
            {
            content = 42;
            }
        else if (position == 100)
            {
            content = 43;
            }
        byte[] msg = {(byte)content};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO MODIFICA BRACCIO: " + e.getMessage());
        }
    }

    //FUNZIONI MOVIMENTO
    //    18
    // 14 15 16
    //    12

    public void muoviAvanti(){
        byte[] msg = {18};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MOVIMENTO: " + e.getMessage());
        }
    }
    public void muoviDietro(){
        byte[] msg = {12};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MOVIMENTO: " + e.getMessage());
        }
    }
    public void muoviDestra(){
        byte[] msg = {16};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MOVIMENTO: " + e.getMessage());
        }
    }
    public void muoviSinistra(){
        byte[] msg = {14};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MOVIMENTO: " + e.getMessage());
        }
    }

    public void fermati(){
        byte[] msg = {15};
        try {
            sendData(msg);
        }catch(IOException e){
            loggingFun("ERRORE INVIO COMANDO DI MOVIMENTO: " + e.getMessage());
        }
    }



    //metodo universale per inviare dati
    //usa metodo write di connectedthread
    private void sendData(byte[] message) throws IOException
    {//documentazione JAVA mi dice che OutputStream.write(byte[]) puo lanciare IOException, eg se loutputstream e chiuso
        if (mConnectedThread==null) {
            throw new IOException(" NO CONNECTED THREAD ATTIVO");
        }
        else {
            mConnectedThread.write(message);
            loggingFun("INVIATO MESSAGGIO "+ message);
        }

    }

    //MESSAGGI IN ENTRATA

    //METODO PER RICEVUTA NOTIFICA RACCOLTA OGGETTI
    //NB: INTERPRETA OGNI BYTE COME COMUNICAZIONE CHE UN OGGETTO DEL COLORE
    //    CONTENUTO NEL BYTE RAPPRESENTATO TRAMITE NUMERO, SIA STATO RACCOLTO
    private void receiveMessage(byte[] message, int length){
        //int length = message.length;
        int i;
        for (i=0;i<length;i++){//SCORRE DATI RICEVUTI
            loggingFun("ANALIZZANDO POSIZIONE " + i + " di " + length);
            switch(message[i]) {//INTERPRETA BYTE PER BYTE
                case COLORE_BIANCO: {
                    //mGlobalVariables.daiBianco();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO BIANCO ");
                    break;
                }
                case COLORE_BLU :{
                    //mGlobalVariables.daiBlu();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO BLU");
                    break;
                }
                case COLORE_GIALLO :{
                    //mGlobalVariables.daiGiallo();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO GIALLO");
                    break;
                }
                case COLORE_MARRONE :{
                    //mGlobalVariables.daiMarrone();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO MARRONE");
                    break;
                }
                case COLORE_NERO :{
                    //mGlobalVariables.daiNero();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO NERO");
                    break;
                }
                case COLORE_ROSSO :{
                    //mGlobalVariables.daiRosso();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO ROSSO");
                    break;
                }
                case COLORE_VERDE :{
                    //mGlobalVariables.daiVerde();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO VERDE");
                    break;
                }
                case NO_COLORE :{
                    //mGlobalVariables.daiNoColor();
                    mGlobalVariables.collectObject(message[i]);
                    loggingFun("RILEVATO OGGETTO NO COLOR");
                    break;
                }
            //SWITCH/CASE PER IGNORARE DATI IMPROPRI

            }
        }
    }

    //FINE GESTIONE MESSAGGI

    //
    // FUNZIONI DI GESTIONE THREAD
    //


    //METODO CHE SPEGNE LA CONNESSIONE
    //RENDE THREAD ORFANI
    //GARBAGE COLLECTOR SI ARRANGERA A ELIMINARLI
    public synchronized void stop() {
        loggingFun("STOP \n");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        mState = STATE_NONE;
    }

    //METODO INTERNO LANCIATO IN CASO DI FALLIMENTO DI CONNESSIONE
    private void connectionFailed() {
        loggingFun("CONNESSIONE FALLITA \n");
        //
    }

    //METODO INTERNO LANCIATO IN CASO DI CONNESSIONE PERSA
    private void connectionLost() {
        // Send a failure message back to the Activity
        loggingFun("CONNESSIONE PERSA \n");

        mState = STATE_NONE;

        // Start the service over to restart listening mode
        BluetoothModule.this.start();
    }

    //METODO PER ASCOLTARE TENTATIVI DI CONNESSIONE DA PARTE DI CLIENT
    public synchronized void start() {
        loggingFun("START");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
        // Update UI title
        //updateUserInterfaceTitle();
    }

    //METODO CHE GESTISCE CONNESSIONE ATTIVA, A PRESCINDERE DA STATUS DI CLIENT/SERVER
    private synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        //Log.d(TAG, "connected, Socket Type:" + socketType);

        loggingFun("connected()");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            loggingFun("ce un connect thread, lo cancello");
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            loggingFun("ce un connectedthread, lo cancello");
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            loggingFun("ce un acceptthread sicuro lo cancello");
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            loggingFun("ce un acceptthread non sicuro lo cancello");
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        loggingFun("creo connectedthread nuovo");
        mConnectedThread = new ConnectedThread(socket, socketType);
        loggingFun("start di connectedthread");
        mConnectedThread.start();

        loggingFun("CONNESSO A " + myDevice.getName() + " \n");
        // Send the name of the connected device back to the UI Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        //Bundle bundle = new Bundle();
        //bundle.putString(Constants.DEVICE_NAME, device.getName());
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);
        // Update UI title
        //updateUserInterfaceTitle();
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        // Log.d(TAG, "connect to: " + device);
        loggingFun("CONNECT");
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
                loggingFun("STAVA GIA CONNETTENDO");
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
            loggingFun("RIMOZIONE CONNECTED THREAD");
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        // Update UI title
        loggingFun("FINE CONNECT");
    }

    //FINE GESTORI THREAD



    //METODI AUSILIARI TECNICI

    //METODO PER LOGGING
    public void loggingFun (String asd){
        Log.e("BLUETOOTHMODULE", asd);
    }

    //METODO CHE RITORNA STATO BTM
    public String getState(){
        String result;
        result = "INVALID STATE";
        switch (mState){
            case STATE_CONNECTED :{
                result = "CONNECTED";
            }
            case STATE_LISTEN :{
                result = "LISTENING";
            }
            case STATE_CONNECTING :{
                result = "CONNECTING";
            }
            case STATE_NONE :{
                result = "IDLE";
            }
        }
        return result;
    }


}

