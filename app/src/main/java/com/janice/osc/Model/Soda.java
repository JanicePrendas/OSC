package com.janice.osc.Model;

import android.app.ListActivity;

import java.util.ArrayList;
import java.util.List;

public class Soda extends User {

    private String telefono;
    private double latitud;
    private double longitud;
    private List<Producto> productos;

    public Soda(String id, String nombre, String correo, List<Order> ordenes, String telefono, double latitud, double longitud, List<Producto> productos) {
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

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
