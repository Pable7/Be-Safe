package com.itq.proyecto.besafe.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Contacto.class, Mensaje.class}, version = 5, exportSchema = false)
public abstract class DBHelper extends RoomDatabase {
    public abstract MensajeDao mensajeDao();
    public abstract ContactoDao contactoDao();
}
