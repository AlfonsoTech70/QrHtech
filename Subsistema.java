package com.example.htechqr;

public class Subsistema {
    private String idSubsistema;
    private String nombre;
    private String tipo;

    public Subsistema(String idSubsistema, String nombre, String tipo) {
        this.idSubsistema = idSubsistema;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getIdSubsistema() {
        return idSubsistema;
    }

    public void setIdSubsistema(String idSubsistema) {
        this.idSubsistema = idSubsistema;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Subsistema{" +
                "idSubsistema='" + idSubsistema + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
