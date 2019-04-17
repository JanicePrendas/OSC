package com.janice.osc.Model;

import android.app.ListActivity;

import java.util.ArrayList;
import java.util.List;

public class Soda extends User {

    private String telefono;
    private String latitud;
    private String longitud;
    private List<Producto> productos;

    public Soda(String id, String nombre, String correo, List<Order> ordenes, String telefono, String latitud, String longitud, List<Producto> productos) {
        super(id, nombre,correo,ordenes);
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.productos = productos;
    }

    public Soda() {
        super();
        productos = new ArrayList<>();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
