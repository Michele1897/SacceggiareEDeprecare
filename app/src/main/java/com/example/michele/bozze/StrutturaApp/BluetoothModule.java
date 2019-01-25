package com.example.michele.bozze.StrutturaApp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/*import static android.support.v4.app.ActivityCompat.startActivityForResult;*/

public class BluetoothModule
        extends Activity
{
    /*
    A Bluetooth profile is a wireless interface specification for Bluetooth-based communication between devices.
    An example is the Hands-Free profile. For a mobile phone to connect to a wireless headset, both devices must
    support the Hands-Free profile.
    */
    List<String> outputStream;
    List<String> inputStream;
    BluetoothSocket connection;
    private BluetoothAdapter myBtAdapter;
    private boolean connected;
    private boolean bluetoothsupported;
    private final static int REQUEST_ENABLE_BT = 1;
    IntentFilter filter;
    /*private final String codiceUUID = "a60f35f0-b93a-11de-8a39-08002009c666";*/
    private final UUID codiceUUID = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");/*uuid di connessione, da stabilire con bluetooth module robot*/
    /*final UUID = new UUID();*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.connected = false;
        myBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBtAdapter==null)
        {
                /*
                device non supporta bluetooth
                */
            bluetoothsupported = false;
        }
        else    {
            bluetoothsupported = true;
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    /*
        To create a listening BluetoothServerSocket that's ready for incoming connections, use
        BluetoothAdapter.listenUsingRfcommWithServiceRecord(). Then call accept() to listen for
        incoming connection requests. This call will block until a connection is established, at
        which point, it will return a BluetoothSocket to manage the connection. Once the
        BluetoothSocket is acquired, it's a good idea to call close() on the BluetoothServerSocket
        when it's no longer needed for accepting connections. Closing the BluetoothServerSocket
        will not close the returned BluetoothSocket.

        BluetoothServerSocket is thread safe. In particular, close() will always immediately
        abort ongoing operations and close the server socket.
        */
    public void connect(){
        if (this.bluetoothsupported)
        {
            while (!myBtAdapter.isEnabled())/*se bt non è acceso, finchè non viene acceso*/
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    /*richiede utente di accendere bt,
                    metodo blocca questo thread finchè intent non è risolto*/
            }
            try {
                myBtAdapter.listenUsingRfcommWithServiceRecord("BT_SERVER", codiceUUID);
                //connection = BluetoothServerSocket.accept();
                this.connected = true;
                //BluetoothServerSocket.close();
            }
            catch (IOException e)
            {/*notifica se connessione fallita*/
                Toast.makeText(this,"ERRORE CONNESSIONE", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {   /*se bt non supportato da device utente, avvisa*/
            Toast.makeText(this,"DEVICE INCOMPATIBILE", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean isConnected()
    {
        return this.connected;
    }


}
