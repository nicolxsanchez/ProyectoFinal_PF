package logico;

import java.util.*;

public class Algoritmos {

    public static List<Parada> dijkstra(Grafo grafo, Parada origen, Parada destino, String criterio) {
        if (origen == null || destino == null || !grafo.getAdyacencias().containsKey(origen)) {
            return new ArrayList<>();
        }

        Map<Parada, Integer> dist = new HashMap<>();
        Map<Parada, Parada> prev = new HashMap<>();
        PriorityQueue<Parada> prioridadCola = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (Parada parada : grafo.getAdyacencias().keySet()) {
            dist.put(parada, Integer.MAX_VALUE);
            prev.put(parada, null);
        }
        dist.put(origen, 0);
        prioridadCola.add(origen);

        while (!prioridadCola.isEmpty()) {
            Parada paradaActual = prioridadCola.poll();

            if (dist.get(paradaActual) == Integer.MAX_VALUE) continue;

            if (paradaActual.equals(destino)) {
                break;
            }

            List<Ruta> adyacentes = grafo.getAdyacencias().get(paradaActual);
            if (adyacentes == null) continue;

            for (Ruta ruta : adyacentes) {
                Parada paradaDestino = ruta.getDestino();
                int peso;

                if (criterio.equalsIgnoreCase("distancia")) {
                    peso = ruta.getDistancia();
                } else if (criterio.equalsIgnoreCase("tiempo")) {
                    peso = ruta.getTiempo();
                }
                else {

                    continue;
                }
                int nuevoCosto = dist.get(paradaActual) + peso;
                if (nuevoCosto < dist.get(paradaDestino)) {
                    dist.put(paradaDestino, nuevoCosto);
                    prev.put(paradaDestino, paradaActual);
                    prioridadCola.remove(paradaDestino);
                    prioridadCola.add(paradaDestino);
                }
            }
        }

        List<Parada> camino = new ArrayList<>();
        Parada actual = destino;
        while (actual != null && prev.containsKey(actual)) {
            camino.add(0, actual);
            actual = prev.get(actual);
        }
        if (!camino.isEmpty() && !camino.get(0).equals(origen)) {
            return new ArrayList<>();
        }
        return camino;
    }

    public static List<Parada> bellmanFord(Grafo grafo, Parada origen, Parada destino) {

        if (origen == null || destino == null || !grafo.getAdyacencias().containsKey(origen)) {
            return new ArrayList<>();
        }

        Map<Parada, Double> dist = new HashMap<>();
        Map<Parada, Parada> prev = new HashMap<>();

        for (Parada paradaActual : grafo.getAdyacencias().keySet()) {
            dist.put(paradaActual, Double.POSITIVE_INFINITY);
            prev.put(paradaActual, null);
        }

        dist.put(origen, 0.0);

        int cantParadas = grafo.getAdyacencias().size();

        for (int i = 0; i < cantParadas - 1; i++) {

            for (Map.Entry<Parada, List<Ruta>> entry : grafo.getAdyacencias().entrySet()) {
                Parada paradaOrigen = entry.getKey();
                double distanciaOrigenActual = dist.get(paradaOrigen);

                if (distanciaOrigenActual == Double.POSITIVE_INFINITY) continue;


                for (Ruta rutaActual : entry.getValue()) {
                    Parada paradaDestino = rutaActual.getDestino();
                    double costo = rutaActual.getCosto();
                    if (distanciaOrigenActual + costo < dist.get(paradaDestino)) {
                        dist.put(paradaDestino, distanciaOrigenActual + costo);
                        prev.put(paradaDestino, paradaOrigen);
                    }
                }
            }
        }
        List<Parada> camino = new ArrayList<>();
        Parada actual = destino;
        while (actual != null) {
            camino.add(0, actual);
            actual = prev.get(actual);
        }

        if (camino.isEmpty() || !camino.get(0).equals(origen)) {
            return new ArrayList<>();
        }

        return camino;
    }

    public static List<Parada> bfs(Grafo grafo, Parada origen, Parada destino) {
        Queue<Parada> cola = new LinkedList<>();
        Map<Parada, Parada> antecesor = new HashMap<>();
        Set<Parada> visitados = new HashSet<>();

        cola.add(origen);
        visitados.add(origen);
        antecesor.put(origen, null);

        while (!cola.isEmpty()) {
            Parada actual = cola.poll();

            if (actual.equals(destino)) {
                return reconstruirCamino(antecesor, destino);
            }

            List<Ruta> rutas = grafo.getAdyacencias().get(actual);
            if (rutas != null) {
                for (Ruta ruta : rutas) {
                    Parada vecino = ruta.getDestino();
                    if (!visitados.contains(vecino)) {
                        cola.add(vecino);
                        visitados.add(vecino);
                        antecesor.put(vecino, actual);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private static List<Parada> reconstruirCamino(Map<Parada, Parada> antecesor, Parada destino) {
        List<Parada> camino = new LinkedList<>();
        for (Parada actual = destino; actual != null; actual = antecesor.get(actual)) {
            camino.add(0, actual);
        }
        return camino;
    }
}

