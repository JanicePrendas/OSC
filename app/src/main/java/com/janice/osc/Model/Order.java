package com.janice.osc.Model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String id;
    private String sodaId;
    private String customerId;
    private int estado;
    private List<Producto> productos;
    private long total;

    public Order(String id, String sodaId, String customerId, int estado, List<Producto> productos, long total) {
        this.id = id;
        this.sodaId = sodaId;
        this.customerId = customerId;
        this.estado = estado;
        this.productos = productos;
        this.total = total;
    }

    public Order(){
        this.productos = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public String getSodaId() {
        return sodaId;
    }

    public void setSodaId(String sodaId) {
        this.sodaId = sodaId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
