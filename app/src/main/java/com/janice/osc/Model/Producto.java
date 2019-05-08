package com.janice.osc.Model;

import java.io.Serializable;

public class Producto implements Serializable {

    private String titulo;
    private String descripcion;
    private String img;
    private int estado_cantidad; //Para estado_cantidad --> 1: Activo, 0: Inactivo, Para cantidad --> Numero mayor a 0
    private Long precio;
    private String id;

    public Producto() {

    }

    public Producto(String titulo, String descripcion, String img, int estado_cantidad, Long precio, String id) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img = img;
        this.estado_cantidad = estado_cantidad;
        this.precio = precio;
        this.id = id;
    }

    public Producto(Producto producto_a_copiar, int cantidad) {
        this.titulo = producto_a_copiar.getTitulo();
        this.descripcion = producto_a_copiar.getDescripcion();
        this.img = producto_a_copiar.getImg();
        this.estado_cantidad = cantidad;
        this.precio = producto_a_copiar.getPrecio();
        this.id = producto_a_copiar.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEstado_cantidad() {
        return estado_cantidad;
    }

    public void setEstado_cantidad(int estado_cantidad) {
        this.estado_cantidad = estado_cantidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Long getPrecio() {
        return precio;
    }

    public void setPrecio(Long precio) {
        this.precio = precio;
    }
}
