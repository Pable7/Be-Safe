package com.itq.proyecto.besafe.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Contacto {
    @PrimaryKey(autoGenerate = true)
    int id;
    String nombre;
    String parentesco;
    String telefono;

    public Contacto(String nombre, String parentesco, String telefono) {
        this.nombre = nombre;
        this.parentesco = parentesco;
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Contacto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", parentesco='" + parentesco + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }
}
