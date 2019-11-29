package com.itq.proyecto.besafe.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ContactoDao {

    @Insert
    void insertarContacto(Contacto contacto);

    @Query("SELECT * FROM contacto")
    List<Contacto> getContactos();

    @Query("SELECT count(*) FROm contacto")
    int getNoContactos();
}
