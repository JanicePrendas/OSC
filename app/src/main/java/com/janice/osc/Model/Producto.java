package com.janice.osc.Model;

import java.io.Serializable;

public class Producto implements Serializable {

    private String titulo;
    private String descripcion;
    private String img;
    private int estado; //1: Activo, 0: Inactivo
    private Long precio;
    private String id;
/*
    public Producto(String titulo, String descripcion, String img, int estado, Long precio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img = img;
        this.estado = estado;
        this.precio = precio;
    }
*/
    public Producto() {

    }

    public Producto(String titulo, String descripcion, String img, int estado, Long precio, String id) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img = img;
        this.estado = estado;
        this.precio = precio;
        this.id = id;
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
