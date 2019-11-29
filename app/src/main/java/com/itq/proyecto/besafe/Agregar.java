package com.itq.proyecto.besafe;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.itq.proyecto.besafe.database.Contacto;
import com.itq.proyecto.besafe.database.DBHelper;

public class Agregar extends AppCompatActivity {
    DBHelper helper;
    EditText nombre, par, tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        helper = Room.databaseBuilder(this, DBHelper.class,"BeSafe")
                .fallbackToDestructiveMigration().allowMainThreadQueries().build();

        nombre = findViewById(R.id.contacto_nombre);
        par = findViewById(R.id.contacto_parentesco);
        tel = findViewById(R.id.contacto_numero);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.agregar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.agregar) {
            Contacto contacto = new Contacto(nombre.getText().toString(), par.getText().toString(), tel.getText().toString());
            helper.contactoDao().insertarContacto(contacto);
            finish();
        }
        else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
