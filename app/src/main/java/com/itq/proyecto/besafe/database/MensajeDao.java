package com.itq.proyecto.besafe.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface MensajeDao {

    @Insert
    void insertarMensaje(Mensaje mensaje);

    @Query("SELECT * FROM Mensaje ORDER BY id DESC LIMIT 1")
    Mensaje getMensaje();
}
