package logico;

import java.util.*;

public class Algoritmos {

  /*  public static List<Parada> dijkstra(Grafo grafo, Parada origen, Parada destino, String criterio) {
        Map<Parada, Integer> dist = new HashMap<>();
        Map<Parada, Parada> prev = new HashMap<>();
        PriorityQueue<Parada> pq = new PriorityQueue<>(new Comparator<Parada>() {
            public int compare(Parada p1, Parada p2) {
                return Integer.compare(dist.get(p1), dist.get(p2));
            }
        });

        for (Parada p : grafo.getAdyacencias().keySet()) {
            dist.put(p, Integer.MAX_VALUE);
            prev.put(p, null);
        }
        dist.put(origen, 0);
        pq.add(origen);

        while (!pq.isEmpty()) {
            Parada u = pq.poll();
            if (u.equals(destino)) break;

            int du = dist.get(u);
            List<Ruta> adyacentes = grafo.getAdyacencias().get(u);
            if (adyacentes == null) continue;

            for (Ruta r : adyacentes) {
                Parada v = r.getDestino();
                int peso;

                if (criterio.equalsIgnoreCase("distancia")) {
                    peso = r.getDistancia();
                } else if (criterio.equalsIgnoreCase("tiempo")) {
                    peso = r.getTiempo();
                } else {
                    peso = 1;
                }

                int alt = du + peso;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }

        List<Parada> camino = new ArrayList<>();
        Parada paso = destino;
        if (prev.get(paso) != null || paso.equals(origen)) {
            while (paso != null) {
                camino.add(paso);
                paso = prev.get(paso);
            }
            Collections.reverse(camino);
        }

        return camino;
    }*/

    public static List<Parada> dijkstra(Grafo grafo, Parada origen, Parada destino, String criterio) {
        if (origen == null || destino == null || !grafo.getAdyacencias().containsKey(origen)) {
            return new ArrayList<>();
        }

        Map<Parada, Integer> dist = new HashMap<>();
        Map<Parada, Parada> prev = new HashMap<>();
        // No usamos un conjunto "visitados" explícito, sino que usamos el valor de dist para filtrar duplicados.
        PriorityQueue<Parada> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (Parada p : grafo.getAdyacencias().keySet()) {
            dist.put(p, Integer.MAX_VALUE);
            prev.put(p, null);
        }
        dist.put(origen, 0);
        pq.add(origen);

        while (!pq.isEmpty()) {
            Parada u = pq.poll();

            // Si se extrae un nodo cuya distancia no coincide con el valor actual en el mapa, se omite.
            // Esto descarta entradas obsoletas.
            if (dist.get(u) == Integer.MAX_VALUE) continue;

            // Si se llega al destino, se puede romper (ya que su distancia es óptima)
            if (u.equals(destino)) {
                break;
            }

            List<Ruta> adyacentes = grafo.getAdyacencias().get(u);
            if (adyacentes == null) continue;

            for (Ruta r : adyacentes) {
                Parada v = r.getDestino();
                int peso;

                if (criterio.equalsIgnoreCase("distancia")) {
                    peso = r.getDistancia();
                } else if (criterio.equalsIgnoreCase("tiempo")) {
                    peso = r.getTiempo();
                }
                else {

                    continue;
                }
                int alt = dist.get(u) + peso;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    pq.remove(v);
                    pq.add(v);
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

        for (Parada p : grafo.getAdyacencias().keySet()) {
            dist.put(p, Double.POSITIVE_INFINITY);
            prev.put(p, null);
        }

        dist.put(origen, 0.0);

        int V = grafo.getAdyacencias().size();

        for (int i = 0; i < V - 1; i++) {

            for (Map.Entry<Parada, List<Ruta>> entry : grafo.getAdyacencias().entrySet()) {
                Parada u = entry.getKey();
                double du = dist.get(u);

                if (du == Double.POSITIVE_INFINITY) continue;


                for (Ruta r : entry.getValue()) {
                    Parada v = r.getDestino();
                    double costo = r.getCosto();
                    if (du + costo < dist.get(v)) {
                        dist.put(v, du + costo);
                        prev.put(v, u);
                    }
                }
            }
        }

        // Opcional: Verificar la existencia de ciclos negativos (no se espera en este caso)
        // for (Map.Entry<Parada, List<Ruta>> entry : grafo.getAdyacencias().entrySet()) {
        //     Parada u = entry.getKey();
        //     double du = dist.get(u);
        //     if (du == Double.POSITIVE_INFINITY) continue;
        //     for (Ruta r : entry.getValue()) {
        //         Parada v = r.getDestino();
        //         double costo = r.getCosto();
        //         if (du + costo < dist.get(v)) {
        //             System.out.println("El grafo contiene un ciclo negativo.");
        //             return new ArrayList<>();
        //         }
        //     }
        // }

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

