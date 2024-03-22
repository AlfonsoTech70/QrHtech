package com.example.htechqr;

public class Sistema {
    private String idSistema;
    private String nombre;
    private String logo;
    private String estado;

    public Sistema(String idSistema, String nombre, String logo, String estado) {
        this.idSistema = idSistema;
        this.nombre = nombre;
        this.logo = logo;
        this.estado = estado;
    }

    public String getIdSistema() {
        return idSistema;
    }

    public void setIdSistema(String idSistema) {
        this.idSistema = idSistema;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre; // Mostrar solo el nombre en el spinner
    }
}


