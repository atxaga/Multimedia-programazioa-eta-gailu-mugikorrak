package com.example.ariketa2;

import androidx.annotation.NonNull;

public class Plato {
    private String titulo;
    private String descripcion;
    private double precio;

    public Plato(String titulo, String descripcion, double precio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    @NonNull
    @Override
    public String toString() {
        return titulo;
    }
}
