package com.itq.proyecto.besafe;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itq.proyecto.besafe.database.Contacto;
import com.itq.proyecto.besafe.database.DBHelper;
import com.itq.proyecto.besafe.database.Mensaje;

import java.util.List;

public class Settings extends AppCompatActivity {
    CheckBox ubicacion;
    EditText mensaje;
    DBHelper helper;

    LinearLayout personas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ubicacion = findViewById(R.id.check_ubicacion);
        mensaje = findViewById(R.id.edit_mensaje_ayuda);
        personas = findViewById(R.id.personas);
        helper = Room.databaseBuilder(this,DBHelper.class,"BeSafe")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();

        Mensaje aux = helper.mensajeDao().getMensaje();
        if(aux != null) {
            mensaje.setText(aux.getMensaje());
            if(aux.getUbicacion() == 1) {
                ubicacion.setChecked(true);
            }
            else
                ubicacion.setChecked(false);
        }

        Button botonAgregar = findViewById(R.id.boton_agregar);
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Agregar.class));
            }
        });
    }

    public void setComponents() {
        if(helper == null)
            helper = Room.databaseBuilder(this,DBHelper.class,"BeSafe")
                    .fallbackToDestructiveMigration().allowMainThreadQueries().build();
        List<Contacto> contactos = helper.contactoDao().getContactos();
        for(Contacto contacto: contactos) {
            View view = getLayoutInflater().inflate(R.layout.item_person, null);
            TextView nombre = view.findViewById(R.id.card_nombre);
            TextView parentesco = view.findViewById(R.id.card_parentesco);
            nombre.setText(contacto.getNombre());
            parentesco.setText(contacto.getParentesco());
            personas.addView(view);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            String msj = mensaje.getText().toString();
            int location = 0;
            if (ubicacion.isChecked())
                location = 1;

            Mensaje mensaje = new Mensaje(msj, location);
            helper.mensajeDao().insertarMensaje(mensaje);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
