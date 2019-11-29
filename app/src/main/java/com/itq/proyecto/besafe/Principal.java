package com.itq.proyecto.besafe;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.itq.proyecto.besafe.database.Contacto;
import com.itq.proyecto.besafe.database.DBHelper;
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

public class Principal extends AppCompatActivity implements View.OnClickListener{
    public final String SENT = "SMS_SENT";
    public final String DELIVERED = "SMS_DELIVERED";
    Button boton;
    BluetoothAdapter adapter;
    UUID uuid = UUID.fromString("560deb50-1230-11ea-8d71-362b9e155667");
    ImageView settings;
    DBHelper helper;

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

        helper = Room.databaseBuilder(this,DBHelper.class,"BeSafe")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();

        initComponents();
        setComponents();
    }

    public void initComponents() {
        boton = findViewById(R.id.button_panic);
        settings = findViewById(R.id.ic_settings);
    }
    public void setComponents() {
        boton.setOnClickListener(this);
        settings.setOnClickListener(this);
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
    public void onClick(View v) {
        if (v.getId() == R.id.button_panic) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                    && checkSelfPermission(SEND_SMS) == PERMISSION_GRANTED) {
                Location location = getLocation();
                String mensaje = helper.mensajeDao().getMensaje().getMensaje();
                if(helper.mensajeDao().getMensaje().getUbicacion() == 1) {
                    mensaje += String.format(" \nhttp://maps.google.com?q=%f,%f", location.getLatitude(), location.getLongitude());
                }
                Log.i("Bluetooth", mensaje);


                List<Contacto> contactos = helper.contactoDao().getContactos();
                Log.i("contactos", contactos.toString());

                for (Contacto contacto: contactos) {
                    sendSMS(contacto.getTelefono(), mensaje);
                }
            } else {
                Toast.makeText(this, "Sin Permisos ):", Toast.LENGTH_SHORT).show();
            }
        }
        else if (v.getId() == R.id.ic_settings) {
            startActivity(new Intent(getApplicationContext(), Settings.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
