package logico;

import java.util.*;

public class Algoritmos {

    public static List<Parada> dijkstra(Grafo grafo, Parada origen, Parada destino, String criterio) {
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
    }
}
