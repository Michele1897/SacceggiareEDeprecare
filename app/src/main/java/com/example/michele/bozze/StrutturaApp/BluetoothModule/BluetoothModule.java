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
    //int counter;
    //Thread workerThread;
    //BluetoothDevice target;
    //TextView myLabel;
    //EditText myTextbox;

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mmDevice;

    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    private byte[] readBuffer;
    private int readBufferPosition;
    private Thread gestoreIO;

    private volatile boolean stopWorker;
    private GlobalVariables variabili;
    private BluetoothServerSocket mBtServerSocket;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean gestore;

    private final byte delimiter = 10;//This is the ASCII code for a newline character
           // BYTE USATO PER SEPARARE MESSAGGI DIVERSI
           // POSTO IN CODA A MESSAGGIO
    private boolean connected;




    //unico costruttore di bluetoothmodule
    //tgt = bluetoothdevice appaiata selezionata dallo spinner di connectionactivity (n qualche modo)
    //gbl = globalvariables preistanziata a cui si daranno dati ricevuti via bluetooth
    public BluetoothModule(BluetoothDevice tgt, GlobalVariables gbl){
        mmDevice = tgt;
        variabili = gbl;
        gestore = false;

    }

    //thread per connettersi con client, richiede server chiami listenUsingRfcommWithServiceRecord("titolo arbitrario", UUID = MY_UUID)
    //void openBT() throws IOException obsoleto
    void openBT()
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

        ConnectThread(BluetoothDevice device) {
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
                e.printStackTrace();
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
                        //gv.modificaTrovati((int) msg[2], 1);

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
                    e.printStackTrace();
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

    //crea messaggio per operazione di movimento
    // e lo invia a senddata
    private void muovi(int dir){
        byte[] msg = new byte[3];
        msg[0] = 2;
        msg[1] = (byte) dir;
        msg[2] = delimiter;
        try{
            sendData(msg);
        }
        catch (IOException e)
            {
                e.printStackTrace();
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

        AcceptThread() {
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
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    //Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                        break;
                    }
                    catch(IOException e){
                        e.printStackTrace();
                        break;
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
                e.printStackTrace();
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket){
        //mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        mmSocket = socket;

        try{
            mmOutputStream = mmSocket.getOutputStream();

        }catch (IOException e) {
            e.printStackTrace();
        }

        try{
            mmInputStream = mmSocket.getInputStream();
        }catch (IOException e) {
            e.printStackTrace();
        }

        //crea thread di gestione input/output e lo avvia
        gestoreIO = new ConnectedThread(mmSocket);
        gestoreIO.start();
        gestore = true;
    }

    //thread che gestisce stream
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    //read salva contenuti inputstream in mmbuffer e restituisce
                    //numero celle occupate
                    //NB: read e BLOCCANTE finche non arriva qualcosa in inputstream

                    numBytes = mmInStream.read(mmBuffer);


                    // Send the obtained bytes to the UI activity.
                    //Message readMsg = mHandler.obtainMessage(
                     //       MessageConstants.MESSAGE_READ, numBytes, -1,
                     //       mmBuffer);
                    //readMsg.sendToTarget();
                } catch (IOException e) {
                    //Log.d(TAG, "Input stream was disconnected", e);
                    e.printStackTrace();
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                //Message writtenMsg = mHandler.obtainMessage(
                //        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                //writtenMsg.sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                //Message writeErrorMsg =
                //        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                //Bundle bundle = new Bundle();
                //bundle.putString("toast",
                //        "Couldn't send data to the other device");
                //writeErrorMsg.setData(bundle);
                //mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }


    //metodo interno obsoleto
    private void beginListenForData()
    {
        //final Handler handler = new Handler();
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        //workerThread = new AcceptThread();
        //workerThread.start();
    }

    //metodo universale per inviare dati
    //esempio usa metodo write di connectedthread
    private void sendData(byte[] message) throws IOException
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



//segue handler usato dall esempio per passare messaggi dall'oggetto
// bluetoothchatservic (per noi bluetoothmodule) a activity
/*private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }*/
