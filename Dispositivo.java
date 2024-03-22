package com.example.htechqr;

public class Dispositivo {
    private int idDispositivo;
    private String nombreDispositivo;

    public Dispositivo(int idDispositivo, String nombreDispositivo) {
        this.idDispositivo = idDispositivo;
        this.nombreDispositivo = nombreDispositivo;
    }

    public int getIdDispositivo() {
        return idDispositivo;
    }

    public void setIdDispositivo(int idDispositivo) {
        this.idDispositivo = idDispositivo;
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }
}
