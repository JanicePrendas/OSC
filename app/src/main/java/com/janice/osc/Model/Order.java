package com.janice.osc.Model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String estado;
    private List<Producto> productos;

    public Order(String estado, List<Producto> productos) {
        this.estado = estado;
        this.productos = productos;
    }

    public Order(){
        this.productos = new ArrayList<>();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
