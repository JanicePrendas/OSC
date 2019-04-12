package com.janice.osc.Model;

import android.app.ListActivity;

import java.util.ArrayList;
import java.util.List;

public class Soda extends User {

    private String telefono;
    private String direccion;
    private List<Producto> productos;

    public Soda(String nombre, String correo, List<Order> ordenes, String telefono, String direccion, List<Producto> productos) {
        super(nombre,correo,ordenes);
        this.telefono = telefono;
        this.direccion = direccion;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
