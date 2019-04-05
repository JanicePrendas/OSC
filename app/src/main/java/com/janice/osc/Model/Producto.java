package com.janice.osc.Model;

public class Producto {

    private String id;
    private String titulo;
    private String descripcion;
    private String img;
    private Long precio;

    public Producto(String id, String titulo, String descripcion, String img, Long precio) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img = img;
        this.precio = precio;
    }

    public Producto() {

    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setId(String id) {
        this.id = id;
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
