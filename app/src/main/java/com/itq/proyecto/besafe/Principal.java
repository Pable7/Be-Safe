package com.itq.proyecto.besafe;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class Principal extends AppCompatActivity implements View.OnLongClickListener{
    public final String SENT = "SMS_SENT";
    public final String DELIVERED = "SMS_DELIVERED";
    Button boton;
    BluetoothAdapter adapter;
    UUID uuid = UUID.fromString("560deb50-1230-11ea-8d71-362b9e155667");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted())
                        {
                            Log.i("DeSafe", "Todos los permisos aceptados correctamente." + report.getGrantedPermissionResponses());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

        initComponents();
        setComponents();
    }

    public void initComponents() {
        boton = findViewById(R.id.button_panic);
        listBondedDevices();
    }

    public void listBondedDevices() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bt = adapter.getBondedDevices();
        String[] devices = new String[bt.size()];

        int index = 0;
        if(bt.size() > 0) {
            for(BluetoothDevice device : bt) {
                Log.i("Bluettoth", device.getName() + " " + device.getAddress());
                devices[index++] = device.getName() + " " + device.getAddress();
            }
        }

    }

    public void setConnection() {
        ServerClass serverClass = new ServerClass();
        serverClass.start();
    }

    public void sendData() {

    }

    public String getData() {
        return "";
    }
    public void setComponents() {
        boton.setOnLongClickListener(this);
    }

    private Location getLocation() {
        Location location;
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED)
        {
            LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            for(String provider : locManager.getAllProviders())
            {
                try{ location = locManager.getLastKnownLocation(provider); Log.i("SMS_UBI", location.getLatitude() + ""); }
                catch (Exception e)
                {
                    e.printStackTrace();
                    location = null;
                }
                if(location != null) return location;
            }
        }
        location = new Location("");
        location.setLatitude(-1);
        location.setAltitude(-1);
        return location;
    }

    private void sendSMS(String numero, String mensaje) {
        PendingIntent enviadoPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        PendingIntent entregadoPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS enviado",
                                Toast.LENGTH_SHORT).show();
                        Log.i("SMS_SENT", "SMS Enviado");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Falla Inesperada",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS entregado",
                                Toast.LENGTH_SHORT).show();
                        Log.i("SMS_DELI", "SMS Entregado");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS no entregado",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(numero, null, mensaje, enviadoPI, entregadoPI);
        Log.i("SMS", "Mensaje enviado");
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.button_panic) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && checkSelfPermission(SEND_SMS) == PERMISSION_GRANTED) {
                Location location = getLocation();
                sendSMS("+524428243038", String.format(getString(R.string.sms_body), location.getLatitude(), location.getLongitude()));
            } else {
                Toast.makeText(this, "Sin Permisos ):", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                serverSocket = adapter.listenUsingInsecureRfcommWithServiceRecord("BeSafe", uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            while (socket == null) {
                try {
                    socket = serverSocket.accept();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                if(socket != null) {
                    break;
                }
            }
        }
    }

    private class SendReceive extends Thread{

        private BluetoothSocket bluetoothSocket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    Log.i("Bluetooth", bytes + "");
                    //handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
