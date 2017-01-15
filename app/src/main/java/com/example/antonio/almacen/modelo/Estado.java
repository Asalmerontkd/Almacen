package com.example.antonio.almacen.modelo;

/**
 * Created by antonio on 14/01/17.
 */

public class Estado {
    private String state;

    public Estado() {

    }

    @Override
    public String toString() {
        return "Estado{" +
                "state='" + state + '\'' +
                '}';
    }

    public Estado(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
