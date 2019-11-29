package com.itq.proyecto.besafe.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Mensaje {
    @PrimaryKey(autoGenerate = true)
    int id;
    String mensaje;
    int ubicacion;

    public Mensaje(String mensaje, int ubicacion) {
        this.mensaje = mensaje;
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "Mensaje{" +
                "id=" + id +
                ", mensaje='" + mensaje + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(int ubicacion) {
        this.ubicacion = ubicacion;
    }
}
