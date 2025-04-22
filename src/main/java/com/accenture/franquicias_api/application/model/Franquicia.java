package com.accenture.franquicias_api.application.model;

import java.util.List;

public class Franquicia {

    private String nombre;
    private List<Sucursal> sucursales;

    public Franquicia() {}

    public Franquicia(String nombre, List<Sucursal> sucursales) {
        this.nombre = nombre;
        this.sucursales = sucursales;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public List<Sucursal> getSucursales() {
        return sucursales;
    }
    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales;
    }
}
