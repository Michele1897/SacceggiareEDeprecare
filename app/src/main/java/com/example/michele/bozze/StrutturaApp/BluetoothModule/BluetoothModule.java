package com.example.michele.bozze.StrutturaApp.BluetoothModule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
    //BluetoothAdapter mBluetoothAdapter;
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

    /*void findBT(Activity requester)
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            requester.startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0)
        {
            for(BluetoothDevice device : pairedDevices)
            {
                if(device.getName().equals(target.getName()))
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        myLabel.setText("Bluetooth Device Found");
    }*/

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();

        //myLabel.setText("Bluetooth Opened");
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

    //metodo interno
    private void beginListenForData()
    {
        //final Handler handler = new Handler();


        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter) /*se b = limiter, abbiamo appena finito di prendere un messaggio*/
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    //copia
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    /*final String data = new String(encodedBytes, "US-ASCII");*/

                                    readBufferPosition = 0;//messaggio preso, buffer azzerato

                                    //manda messaggi a interpretatore messaggi
                                    sendToGV(variabili,encodedBytes);

                                    //handler.post(new Runnable()
                                    /*{
                                        public void run()
                                        {*/

                                        /*}
                                    });*/
                                }
                                else
                                {   //altrimenti prende b e lo mette nel buffer
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

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
