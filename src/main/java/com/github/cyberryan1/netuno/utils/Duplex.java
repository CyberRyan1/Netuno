package com.github.cyberryan1.netuno.utils;

public class Duplex<T, H> {

    private T t;
    private H h;

    public Duplex() {
        t = null;
        h = null;
    }

    public Duplex( T t, H h ) {
        this.t = t;
        this.h = h;
    }

    public void setFirst( T t ) {
        this.t = t;
    }

    public void setSecond( H h ) {
        this.h = h;
    }

    public T getFirst() { return t; }

    public H getSecond() { return h; }
}