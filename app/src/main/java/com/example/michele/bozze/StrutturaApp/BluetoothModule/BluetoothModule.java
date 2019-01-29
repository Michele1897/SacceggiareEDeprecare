package com.example.michele.bozze.StrutturaApp.BluetoothModule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
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

    //BluetoothDevice target;
    //TextView myLabel;
    //EditText myTextbox;
    public final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    //int counter;
    volatile boolean stopWorker;
    GlobalVariables variabili;
    BluetoothServerSocket mBtServerSocket;
    public final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    final byte delimiter = 10;/*This is the ASCII code for a newline character
            BYTE USATO PER SEPARARE MESSAGGI DIVERSI
            POSTO IN CODA A MESSAGGIOi*/


    //unico costruttore di bluetoothmodule
    //tgt = bluetoothdevice appaiata selezionata dallo spinner di connectionactivity (n qualche modo)
    //gbl = globalvariables preistanziata a cui si daranno dati ricevuti via bluetooth
    public BluetoothModule(BluetoothDevice tgt, GlobalVariables gbl){
        mmDevice = tgt;
        variabili = gbl;

    }

    //thread per connettersi con client, richiede server chiami listenUsingRfcommWithServiceRecord("titolo arbitrario", UUID = MY_UUID)
    void openBT() throws IOException
    {
        //UUID uuid =  //Standard SerialPortService ID
        //mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        //mmSocket.connect();
        //mmOutputStream = mmSocket.getOutputStream();
        //mmInputStream = mmSocket.getInputStream();
        //beginListenForData();

        Thread accepterThread = new ConnectThread(mmDevice);
        accepterThread.start();

        //myLabel.setText("Bluetooth Opened");
    }

    //connette a server, lancia manageMyConnectedSocket
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                //Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    //Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    //metodo interno
    //interpretatore messaggi input, lancia metodi del globalvariables
    //appropriati secondo dati appropriati interpretati dai byte ricevuti via bluetooth
    private void sendToGV (GlobalVariables gv, byte[] msg)
        {
            //int i = 0;
            /*byte[] msg = message.getBytes();*/

            int length = msg.length;
            switch (msg[1]) {

                case 0 :
                    {// e' un messaggio di colore trovato
                        gv.modificaTrovati((int) msg[2], 1);

                    break;
                    }
            }
        }

    void richiediOggetto(int colore, int quantit)
        {
            byte[] msg = new byte[4];//crea messaggio
            msg[0] = 1;
            msg[1] = (byte) colore;
            msg[2] = (byte) quantit;
            msg[3] = delimiter;
            try {
                sendData(msg);
            }
            catch (IOException e)
                {

                }

        }
    private void muoviAvanti(){
        muovi(8);
    }
    private void muoviDietro(){
        muovi(2);
    }
    private void muoviDestra(){
        muovi(6);
    }
    private void muoviSinistra(){
        muovi(4);
    }

    private void muovi(int dir){
        byte[] msg = new byte[3];
        msg[0] = 2;
        msg[1] = (byte) dir;
        msg[3] = delimiter;
        try{
            sendData(msg);
        }
        catch (IOException e)
            {

            }
    }



    //metodo pubblico per iniziare ad accettare connesssione da un client
    //client deve lanciare createRfcommSocketToServiceRecord(UUID = MY_UUID)
    public void openAsServer(){
        Thread acceptThrd = new AcceptThread();
        acceptThrd.run();
    }


    //thread per ACCETTARE connessione
    //lancia manageMyConnectedSocket(socket); dopo aver stabilito connessione

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("jolly roger app", MY_UUID);
            } catch (IOException e) {
                //Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    //Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    }
                    catch(IOException e){

                    }
                    finally {
                        break;//mi stupisco di essermene ricordato
                    }
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket){
        //mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        mmSocket = socket;
        connected = true;

        try {
            mmSocket.connect();

        }
        catch (IOException e){
            connected = false;
        }
        try{
            mmOutputStream = mmSocket.getOutputStream();

        }catch (IOException e) {

        }

        try{
            mmInputStream = mmSocket.getInputStream();
        }catch (IOException e) {

        }


    }


    //metodo interno, lancia
    private void beginListenForData()
    {
        //final Handler handler = new Handler();
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new AcceptThread();
        workerThread.start();
    }

    void sendData(byte[] message) throws IOException
    {
        /*String msg = myTextbox.getText().toString();
        msg += "\n";*/
        mmOutputStream.write(message);
        /*myLabel.setText("Data Sent");*/
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        /*myLabel.setText("Bluetooth Closed");*/
    }
}
