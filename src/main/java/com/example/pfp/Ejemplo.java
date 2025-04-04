package com.example.pfp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;

public class Ejemplo extends Application {

    @Override
    public void start(Stage stage) {
        // Crear un grafo simple
        Graph<String, String> graph = new GraphEdgeList<>();
        graph.insertVertex("A");
        graph.insertVertex("B");
        graph.insertEdge("A", "B", "Conexión AB");

        // Configurar la vista del grafo
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(graph, new SmartCircularSortedPlacementStrategy());

        // Crear la escena y mostrarla
        Scene scene = new Scene(graphView, 800, 600);
        stage.setTitle("Ejemplo Simple SmartGraph");
        stage.setScene(scene);
        stage.show();

        // Inicializar la visualización del grafo
        graphView.init();
    }

    public static void main(String[] args) {
        launch(args);
    }
}