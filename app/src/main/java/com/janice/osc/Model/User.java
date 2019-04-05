package com.janice.osc.Model;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String nombre;
    private String correo;
    private List<Order> ordenes;

    public User(String id, String nombre, String correo, List<Order> orders) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.ordenes = orders;
    }

    public User(){
        this.ordenes = new ArrayList<>();
    }

    public List<Order> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<Order> ordenes) {
        this.ordenes = ordenes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
