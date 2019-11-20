package com.itq.proyecto.besafe;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
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

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class Principal extends AppCompatActivity implements View.OnLongClickListener{
    public final String SENT = "SMS_SENT";
    public final String DELIVERED = "SMS_DELIVERED";
    Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);


        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
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
}
