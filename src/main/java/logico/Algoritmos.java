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
                int peso = criterio.equalsIgnoreCase("distancia") ? r.getDistancia() : r.getTiempo();
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
}
