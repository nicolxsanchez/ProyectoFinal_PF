package com.example.pfp;

import com.brunomnsilva.smartgraph.graph.*;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logico.Grafo;
import logico.Parada;


public class Controladora {

    private Grafo grafo = new Grafo();


    @FXML
    private Button btnAgregarParada;
    @FXML
    private Button btnAgregarRuta;
    @FXML
    private StackPane grafoContenedor;

    private int nodoContador = 1;


    private boolean moverNodo = false;
    private boolean modoAgregarNodo = false;
    private boolean clicDerechoEnNodo = false;

    @FXML
    public void initialize() {

        grafoContenedor.setOnMouseClicked(this::manejarClick);

        btnAgregarParada.setOnAction(e -> {
            modoAgregarNodo = true;
            btnAgregarParada.getStyleClass().add("button-selected");
            btnAgregarRuta.getStyleClass().remove("button-selected");
        });

        btnAgregarRuta.setOnAction(e -> {
            modoAgregarNodo = false;
            btnAgregarRuta.getStyleClass().add("button-selected");
            btnAgregarParada.getStyleClass().remove("button-selected");

        });

    }


    private void manejarClick(MouseEvent event) {

        if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
            // Si se hace clic derecho, evitamos que se agregue un nodo
            clicDerechoEnNodo = true;
        } else {
            clicDerechoEnNodo = false; // Clic izquierdo
        }

        if (!modoAgregarNodo || clicDerechoEnNodo) return;

        if (moverNodo) {
            moverNodo = false;
            return;
        }

        Group nodoSeleccionado = getCursorPosicionNodo(event);

        if (nodoSeleccionado != null) {
            makeNodoMovible(nodoSeleccionado);
        } else {
            addNuevoNodo(event);
        }
    }

    private void makeNodoMovible(Group nodo) {
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];

        nodo.setOnMousePressed(event -> {
            if (!modoAgregarNodo) return;
            moverNodo = false;
            offsetX[0] = event.getSceneX() - nodo.getTranslateX();
            offsetY[0] = event.getSceneY() - nodo.getTranslateY();
            event.consume();
        });

        nodo.setOnMouseDragged(event -> {
            if (!modoAgregarNodo) return;
            moverNodo = true;
            nodo.setTranslateX(event.getSceneX() - offsetX[0]);
            nodo.setTranslateY(event.getSceneY() - offsetY[0]);
            event.consume();
        });

        nodo.setOnMouseReleased(event -> {
            if (!modoAgregarNodo) return;
            event.consume();
        });

        nodo.setOnContextMenuRequested(event -> {
            ContextMenu contextMenu = new ContextMenu();


            MenuItem modificarItem = new MenuItem("Modificar");
            modificarItem.setOnAction(e -> {

                Parada parada = (Parada) nodo.getUserData();
                abrirVentanaModificar(parada, nodo);
            });


            MenuItem eliminarItem = new MenuItem("Eliminar");
            eliminarItem.setOnAction(e -> {

                Parada parada = (Parada) nodo.getUserData();
                boolean eliminado = grafo.eliminarParada(parada);

                if (eliminado) {
                    grafoContenedor.getChildren().remove(nodo);
                }
            });

            contextMenu.getItems().addAll(modificarItem, eliminarItem);
            contextMenu.show(nodo, event.getScreenX(), event.getScreenY());
        });
    }

    private void addNuevoNodo(MouseEvent event) {

        String nombreParada = "Parada " + nodoContador;
        Parada nuevaParada = new Parada(nombreParada);

        boolean agregado = grafo.agregarParada(nuevaParada);

        if (agregado) {
            Circle circulo = new Circle(15);
            circulo.getStyleClass().add("nodo-circulo");

            Text nombre = new Text(nombreParada);
            nombre.getStyleClass().add("nodo-texto");

            Group nodo = new Group(circulo, nombre);
            nombre.setTranslateY(circulo.getRadius() + 15);


            double adjustadoX = event.getX() - (grafoContenedor.getWidth() / 2);
            double adjustadoY = event.getY() - (grafoContenedor.getHeight() / 2);

            nombre.setTranslateX(-nombre.getBoundsInLocal().getWidth() / 2);

            nodo.setTranslateX(adjustadoX);
            nodo.setTranslateY(adjustadoY);

           nodo.setUserData(nuevaParada);

            nodoContador++;

            grafoContenedor.getChildren().add(nodo);

            makeNodoMovible(nodo);

        }
    }

    private Group getCursorPosicionNodo(MouseEvent evento) {

        for (Node nodo : grafoContenedor.getChildren()) {
            if (nodo instanceof Group && nodo.contains(evento.getX(), evento.getY())) {
                return (Group) nodo;
            }
        }
        return null;
    }


    private void abrirVentanaModificar(Parada parada, Group nodo) {
        Stage ventanaModificar = new Stage();
        ventanaModificar.setTitle("Modificar Parada");

        Label label = new Label("Nombre:");
        TextField campoNombre = new TextField(parada.getNombre());
        Button botonConfirmar = new Button("Confirmar");

        botonConfirmar.setOnAction(e -> {

            String nuevoNombre = campoNombre.getText();
            parada.setNombre(nuevoNombre);
            //System.out.println("Nuevo nombre: " + parada.getNombre());

            Text nombreTexto = (Text) nodo.getChildren().get(1);
            nombreTexto.setText(nuevoNombre);

            ventanaModificar.close();
        });

        VBox layout = new VBox(10, label, campoNombre, botonConfirmar);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout);
        ventanaModificar.setScene(scene);
        ventanaModificar.show();
    }
}




