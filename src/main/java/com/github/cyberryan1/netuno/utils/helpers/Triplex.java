package com.github.cyberryan1.netuno.utils.helpers;

public class Triplex<A, B, D> {

    private A a;
    private B b;
    private D d;

    public Triplex() {
        a = null;
        b = null;
        d = null;
    }

    public Triplex( A a, B b, D d ) {
        this.a = a;
        this.b = b;
        this.d = d;
    }

    public void setFirst( A a ) {
        this.a = a;
    }

    public void setSecond( B b ) {
        this.b = b;
    }

    public void setThird( D d ) {
        this.d = d;
    }

    public A getFirst() { return a; }

    public B getSecond() { return b; }

    public D getThird() { return d; }

    @Override
    public String toString() {
        return "Triplex{" + "a=" + a + ", b=" + b + ", d=" + d + '}';
    }
}