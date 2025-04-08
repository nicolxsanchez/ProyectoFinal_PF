package logico;

import java.io.Serializable;

public class Parada implements Serializable {

    private String nombre;
    private double x;
    private double y;

    public Parada(String nombre) {
        this.nombre = nombre;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public double setX(double x) {
        return this.x = x;
    }
    public double setY(double y) {
        return this.y = y;
    }

    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
