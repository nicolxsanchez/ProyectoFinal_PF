package logico;

public class Parada {

    private String nombre;
    private double latitud;
    private double longitud;

    public Parada(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;

    }

    public double getLatitud() {
        return latitud;
    }
    public double getLongitud() {
        return longitud;
    }

    public double setLatitud(double latitud) {
        return this.latitud = latitud;
    }
    public double setLongitud(double longitud) {
        return this.longitud = longitud;
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
