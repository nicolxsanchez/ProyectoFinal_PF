package logico;

import java.util.*;
public class Grafo {

    private Map<Parada, List<Ruta>> adyacencias;

    public Grafo() {
        adyacencias = new HashMap<>();
    }

    public List<Parada> getParadas() {
        return new ArrayList<>(adyacencias.keySet());
    }

    public List<Ruta> getRutas() {
        List<Ruta> rutas = new ArrayList<>();
        for (List<Ruta> listaRutas : adyacencias.values()) {
            rutas.addAll(listaRutas);
        }
        return rutas;
    }

    //agrega una nueva parada al grafo
    public boolean agregarParada(Parada parada) {
        if (adyacencias.containsKey(parada)) {
            System.out.println("La parada '" + parada.getNombre() + "' ya existe.");
            return false;
        }
        adyacencias.put(parada, new ArrayList<>());
        System.out.println("Parada agregada: " + parada.getNombre());
        return true;
    }

    //agrega una nueva ruta entre dos paradas en el grafo
    public boolean agregarRuta(Parada origen, Parada destino, int tiempo, int distancia, double costo) {
        if (!adyacencias.containsKey(origen) || !adyacencias.containsKey(destino)) {
            System.out.println("Origen o destino no existen.");
            return false;
        }

        for (Ruta ruta : adyacencias.get(origen)) {
            if (ruta.getDestino().equals(destino)) {
                System.out.println("Ya existe una ruta de " + origen.getNombre() + " a " + destino.getNombre());
                return false;
            }
        }
        Ruta nuevaRuta = new Ruta(origen, destino, tiempo, distancia, costo);
        adyacencias.get(origen).add(nuevaRuta);
        System.out.println("Ruta agregada de " + origen.getNombre() + " a " + destino.getNombre());
        return true;
    }

    //modifica los datos de una parada existente
    public boolean modificarParada(Parada parada, String nuevoNombre, double nuevoLatitud, double nuevoLongitud) {
        if (!adyacencias.containsKey(parada)) {
            System.out.println("La parada no existe.");
            return false;
        }
        parada.setNombre(nuevoNombre);
        parada.setLatitud(nuevoLatitud);
        parada.setLongitud(nuevoLongitud);
        System.out.println("Parada modificada: " + parada.getNombre());
        return true;
    }

    //elimina una parada del grafo y sus rutas asociadas
    public boolean eliminarParada(Parada parada) {
        if (!adyacencias.containsKey(parada)) {
            System.out.println("La parada no existe.");
            return false;
        }
        adyacencias.remove(parada);
        adyacencias.forEach((key, rutas) -> rutas.removeIf(ruta -> ruta.getDestino().equals(parada)));
        System.out.println("Parada eliminada: " + parada.getNombre());
        return true;
    }

    //elimina una ruta del grafo
    public boolean eliminarRuta(Ruta ruta) {
        if (!adyacencias.containsKey(ruta.getOrigen())) {
            System.out.println("La parada de origen no existe.");
            return false;
        }

        boolean eliminado = adyacencias.get(ruta.getOrigen()).removeIf(rutas -> ruta.getDestino().equals(ruta.getDestino()));
        if (eliminado) {
            System.out.println("Ruta eliminada de " + ruta.getOrigen().getNombre() + " a " + ruta.getDestino().getNombre());
        } else {
            System.out.println("No existe la ruta para eliminar.");
        }
        return eliminado;
    }

    //devuelve el mapa de adyacencias
    public Map<Parada, List<Ruta>> getAdyacencias() {
        return adyacencias;
    }
}
