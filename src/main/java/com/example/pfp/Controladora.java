package com.example.pfp;
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
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logico.Grafo;
import logico.Parada;
import logico.Ruta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controladora {

    private Grafo grafo = new Grafo();

    @FXML
    private Button btnAgregarParada;
    @FXML
    private Button btnAgregarRuta;
    @FXML
    private StackPane grafoContenedor;

    private int nodoContador = 1;
    private  int nodosSeleccionados = 0;

    private boolean moverNodo = false;
    private boolean modoAgregarNodo = false;
    private boolean modoAgregarRuta = false;
    private boolean clicDerechoEnNodo = false;

    private Group nodoOrigen = null;
    private Group nodoDestino = null;

    private Map<Group, List<Line>> lineasPorNodo = new HashMap<>();
    private Map<Line, Text> textoPorLinea = new HashMap<>();

    @FXML
    public void initialize() {

        grafoContenedor.setOnMouseClicked(this::manejarClick);


        btnAgregarParada.setOnAction(e -> {
            modoAgregarNodo = true;
            modoAgregarRuta = false;
            btnAgregarParada.getStyleClass().add("button-selected");
            btnAgregarRuta.getStyleClass().remove("button-selected");
        });

        btnAgregarRuta.setOnAction(e -> {
            modoAgregarNodo = false;
            modoAgregarRuta = true;
            nodosSeleccionados = 0;
            btnAgregarRuta.getStyleClass().add("button-selected");
            btnAgregarParada.getStyleClass().remove("button-selected");

        });


    }




    private boolean existeRutaEntre(Group nodo1, Group nodo2) {
        List<Line> lineasNodo1 = lineasPorNodo.get(nodo1);
        if (lineasNodo1 == null) return false;

        for (Line linea : lineasNodo1) {
            Group origen = (Group) linea.getUserData();

            // Revisamos si la línea también está asociada al nodo2
            List<Line> lineasNodo2 = lineasPorNodo.get(nodo2);
            if (lineasNodo2 != null && lineasNodo2.contains(linea)) {
                return true;
            }
        }

        return false;
    }


   private void manejarClick(MouseEvent event) {



        if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
            clicDerechoEnNodo = true;
        } else {
            clicDerechoEnNodo = false;
        }


       if (modoAgregarRuta) {

           Group nodoSeleccionado = getCursorPosicionNodo(event);

           if (nodoSeleccionado == null) return;

           if (nodoOrigen == null) {
               nodoOrigen = nodoSeleccionado;
           } else {
                nodoDestino = nodoSeleccionado;

               if (nodoOrigen == nodoDestino) {
                   mostrarMensajeError("No se puede crear una ruta entre la misma parada.");
                   nodoOrigen = null;
                   return;
               }

               if (existeRutaEntre(nodoOrigen, nodoDestino)) {
                   mostrarMensajeError("Ya existe una ruta entre estas dos paradas.");
                   nodoOrigen = null;
                   return;
               }

               mostrarVentanaRuta(nodoOrigen, nodoDestino, null, null, null, null);
               nodoOrigen = null;
           }
           return;
       }

            if (!modoAgregarNodo || clicDerechoEnNodo) return;

            if (moverNodo) {
                moverNodo = false;
                return;
            }

            Group nodoSeleccionado = getCursorPosicionNodo(event);

            if (nodoSeleccionado != null) {
                System.out.println("Se hizo click en un nodo existente en modo agregar nodo.");
                return;
            }

            addNuevoNodo(event);
        }


    private void posicionarTextoLinea(Text texto, double startX, double startY, double endX, double endY) {
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;
        texto.setLayoutX(midX - texto.getBoundsInLocal().getWidth() / 2);
        texto.setLayoutY(midY - 5);
        texto.setManaged(false);
    }

    private void dibujarLineaEntreNodos(Group nodo1, Group nodo2, String distancia, String tiempo, String costo) {
        double centroContenedorX = grafoContenedor.getWidth() / 2;
        double centroContenedorY = grafoContenedor.getHeight() / 2;

        double startX = centroContenedorX + nodo1.getTranslateX();
        double startY = centroContenedorY + nodo1.getTranslateY();
        double endX = centroContenedorX + nodo2.getTranslateX();
        double endY = centroContenedorY + nodo2.getTranslateY();

        Line linea = new Line(startX, startY, endX, endY);
        linea.getStyleClass().add("linea-ruta");

        linea.setManaged(false);

        linea.setUserData(nodo1);

        mostrarVentanaRuta(nodoOrigen, nodoDestino, null, null, null, null);

        // Texto con info de la ruta
        Text texto = new Text(distancia + " km, " + tiempo + " min, RD$" + costo);
        texto.setFont(Font.font(12));
        texto.setFill(Color.DARKBLUE);
        posicionarTextoLinea(texto, startX, startY, endX, endY);
        grafoContenedor.getChildren().add(texto);

        textoPorLinea.put(linea, texto);


        lineasPorNodo.computeIfAbsent(nodo1, k -> new ArrayList<>()).add(linea);
        lineasPorNodo.computeIfAbsent(nodo2, k -> new ArrayList<>()).add(linea);

        grafoContenedor.getChildren().add(0, linea);

    }


    private void actualizarLineasConNodo(Group nodo) {

        List<Line> lineas = lineasPorNodo.get(nodo);
        if (lineas != null) {
            double centroContenedorX = grafoContenedor.getWidth() / 2;
            double centroContenedorY = grafoContenedor.getHeight() / 2;

            double posX = centroContenedorX + nodo.getTranslateX();
            double posY = centroContenedorY + nodo.getTranslateY();

            for (Line linea : lineas) {
                Group nodoOrigen = (Group) linea.getUserData();
                if (nodoOrigen == nodo) {

                    linea.setStartX(posX);
                    linea.setStartY(posY);
                } else {

                    linea.setEndX(posX);
                    linea.setEndY(posY);
                }

                linea.setOnMouseClicked(event -> {
                    if (event.isSecondaryButtonDown()) {
                        Group origen = (Group) linea.getUserData();
                        Group destino = null;

                        for (Map.Entry<Group, List<Line>> entry : lineasPorNodo.entrySet()) {
                            if (entry.getValue().contains(linea) && entry.getKey() != origen) {
                                destino = entry.getKey();
                                break;
                            }
                        }

                        // Aquí podrías hacer algo con origen y destino si lo deseas.
                        // Por ejemplo, mostrar datos o eliminar la ruta.

                        event.consume();
                    }
                });

                Text texto = textoPorLinea.get(linea);
                if (texto != null) {
                    double newStartX = linea.getStartX();
                    double newStartY = linea.getStartY();
                    double newEndX = linea.getEndX();
                    double newEndY = linea.getEndY();
                    posicionarTextoLinea(texto, newStartX, newStartY, newEndX, newEndY);
                }
            }
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

            actualizarLineasConNodo(nodo);

            event.consume();
        });





        nodo.setOnMouseReleased(event -> {
            if (!modoAgregarNodo) return;
            event.consume();
        });

        nodo.setOnContextMenuRequested(event -> {
            if (!modoAgregarNodo) return;
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

            nuevaParada.setX(adjustadoX);
            nuevaParada.setY(adjustadoY);

        }
    }

    private Group getCursorPosicionNodo(MouseEvent evento) {
        for (Node nodo : grafoContenedor.getChildren()) {
            if (nodo instanceof Group) {
                if (nodo.getBoundsInParent().contains(evento.getX(), evento.getY())) {
                    return (Group) nodo;
                }
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



    private void mostrarVentanaRuta(Group nodoOrigen, Group nodoDestino, String distanciaInicial, String tiempoInicial, String costoInicial, Line lineaExistente) {
        Stage ventanaRuta = new Stage();
        ventanaRuta.setTitle("Detalles de la Ruta");

        Label labelDistancia = new Label("Distancia:");
        TextField campoDistancia = new TextField(distanciaInicial != null ? distanciaInicial : "");
        Label labelTiempo = new Label("Tiempo:");
        TextField campoTiempo = new TextField(tiempoInicial != null ? tiempoInicial : "");
        Label labelCosto = new Label("Costo:");
        TextField campoCosto = new TextField(costoInicial != null ? costoInicial : "");

        Button botonGuardar = new Button("Guardar");
        botonGuardar.setOnAction(e -> {
            String distancia = campoDistancia.getText();
            String tiempo = campoTiempo.getText();
            String costo = campoCosto.getText();

            if (validarDatos(distancia, tiempo, costo)) {
                try {
                    int distanciaInt = Integer.parseInt(distancia);
                    int tiempoInt = Integer.parseInt(tiempo);
                    double costoDouble = Double.parseDouble(costo);

                    Parada paradaOrigen = (Parada) nodoOrigen.getUserData();
                    Parada paradaDestino = (Parada) nodoDestino.getUserData();

                    if (lineaExistente == null) {
                        // NUEVA RUTA
                        dibujarLineaEntreNodos(nodoOrigen, nodoDestino, distancia, tiempo, costo);
                        grafo.agregarRuta(paradaOrigen, paradaDestino, tiempoInt, distanciaInt, costoDouble);
                    } else {
                        // MODIFICACIÓN
                        Text texto = textoPorLinea.get(lineaExistente);
                        texto.setText(distancia + " km, " + tiempo + " min, RD$" + costo);
                        grafo.modificarRuta(paradaOrigen, paradaDestino, tiempoInt, distanciaInt, costoDouble); // Asegúrate que este método exista
                    }

                    ventanaRuta.close();
                } catch (NumberFormatException ex) {
                    mostrarMensajeError("Los valores deben ser numéricos válidos.");
                }
            } else {
                mostrarMensajeError("Por favor, complete todos los campos.");
            }
        });

        VBox layout = new VBox(10, labelDistancia, campoDistancia, labelTiempo, campoTiempo, labelCosto, campoCosto, botonGuardar);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout);
        ventanaRuta.setScene(scene);
        ventanaRuta.initModality(Modality.APPLICATION_MODAL);
        ventanaRuta.initOwner(grafoContenedor.getScene().getWindow());
        ventanaRuta.showAndWait();
    }



    private boolean validarDatos(String distancia, String tiempo, String costo) {

        return !distancia.isEmpty() && !tiempo.isEmpty() && !costo.isEmpty();
    }

    private void mostrarMensajeError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}




