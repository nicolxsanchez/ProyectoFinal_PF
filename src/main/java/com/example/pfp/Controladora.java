package com.example.pfp;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
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

import javax.swing.*;
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
    private Button btnCalculoRutas;
    @FXML
    private StackPane grafoContenedor;
    @FXML
    private Pane paneRutas;
    @FXML
    private Button btnCalcular;
    @FXML
    private ComboBox<String> cbCriterio;

    private int nodoContador = 1;
    private boolean moverNodo = false;
    private boolean modoAgregarNodo = false;
    private boolean modoAgregarRuta = false;
    private boolean modoCalculoRutas = false;
    private boolean clicDerechoEnNodo = false;
    private boolean nodosSeleccionadosCompletos = false;


    private Group nodoOrigen = null;
    private Group nodoDestino = null;
    private Map<Group, List<Line>> lineasPorNodo = new HashMap<>();
    private Map<Line, Text> textoPorLinea = new HashMap<>();

    @FXML
    public void initialize() {

        cbCriterio.getItems().addAll("Distancia", "Tiempo", "Costo", "Transbordo");
        grafoContenedor.setOnMouseClicked(this::manejarClick);

        btnAgregarParada.setOnAction(e -> {
            resetCalculoRutas();
            modoAgregarRuta = false;
            modoCalculoRutas = false;
            modoAgregarNodo = true;
            btnAgregarRuta.getStyleClass().removeAll("button-selected");
            btnCalculoRutas.getStyleClass().removeAll("button-selected");
            btnAgregarParada.getStyleClass().add("button-selected");
        });

        btnAgregarRuta.setOnAction(e -> {
            resetCalculoRutas();
            modoAgregarNodo = false;
            modoCalculoRutas = false;
            modoAgregarRuta = true;
            btnAgregarParada.getStyleClass().removeAll("button-selected");
            btnCalculoRutas.getStyleClass().removeAll("button-selected");
            btnAgregarRuta.getStyleClass().add("button-selected");
        });

        btnCalculoRutas.setOnAction(e -> {

            if (!existenRutasEnGrafo()) {
                mostrarMensajeError("No existen rutas creadas en el grafo.");
                btnCalculoRutas.getStyleClass().removeAll("button-selected");
                modoCalculoRutas = false;
                return;
            }

            resetCalculoRutas();
            modoAgregarNodo = false;
            modoAgregarRuta = false;
            modoCalculoRutas = true;
            btnAgregarRuta.getStyleClass().removeAll("button-selected");
            btnAgregarParada.getStyleClass().removeAll("button-selected");
            btnCalculoRutas.getStyleClass().add("button-selected");
        });

        btnCalcular.setOnAction(e -> {
            String criterio = cbCriterio.getSelectionModel().getSelectedItem();
            if (criterio == null) {
                mostrarMensajeError("Debes seleccionar un criterio antes de calcular.");
                return;
            }

            if(criterio.equals("Distancia")){

            } else if (criterio.equals("Tiempo")){

            }else if (criterio.equals("Costo")){

            }else if(criterio.equals("Transbordo")){

            }
        });
    }

    private boolean existenRutasEnGrafo() {

        for (List<Ruta> listaRutas : grafo.getAdyacencias().values()) {
            if (listaRutas != null && !listaRutas.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    private boolean existeRutaEntre(Group nodo1, Group nodo2) {
        List<Line> lineasNodo1 = lineasPorNodo.get(nodo1);
        if (lineasNodo1 == null) return false;

        for (Line linea : lineasNodo1) {
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

       if(modoCalculoRutas) {
           if (nodosSeleccionadosCompletos) return;
           Group nodoSeleccionado = getCursorPosicionNodo(event);



           if (nodoSeleccionado == null) return;

           Circle circulo = (Circle) nodoSeleccionado.getChildren().get(0);

           if (nodoOrigen == null) {
               nodoOrigen = nodoSeleccionado;
               circulo.getStyleClass().add("parada-selected");
           } else {
               nodoDestino = nodoSeleccionado;

               if (nodoOrigen == nodoDestino) {
                   mostrarMensajeError("No puedes seleccionar la misma parada.");
                   circulo.getStyleClass().remove("parada-selected");
                   nodoOrigen = null;
                   return;
               }
               circulo.getStyleClass().add("parada-selected");
               nodosSeleccionadosCompletos = true;
               paneRutas.setVisible(true);
           }
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


    //muy largo
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
        linea.setPickOnBounds(true);

        linea.setOnContextMenuRequested(event -> {
            event.consume();
            ContextMenu menu = new ContextMenu();

            MenuItem modificarItem = new MenuItem("Modificar");
            MenuItem eliminarItem = new MenuItem("Eliminar");

            modificarItem.setOnAction(e -> {
                Group origen = (Group) linea.getUserData();
                Group destino = null;

                for (Map.Entry<Group, List<Line>> entry : lineasPorNodo.entrySet()) {
                    if (entry.getValue().contains(linea) && entry.getKey() != origen) {
                        destino = entry.getKey();
                        break;
                    }
                }

                if (origen != null && destino != null) {
                    Parada paradaOrigen = (Parada) origen.getUserData();
                    Parada paradaDestino = (Parada) destino.getUserData();

                    Ruta ruta = grafo.obtenerRuta(paradaOrigen, paradaDestino);
                    if (ruta != null) {
                        String distanciaRuta = String.valueOf(ruta.getDistancia());
                        String tiempoRuta = String.valueOf(ruta.getTiempo());
                        String costoRuta = String.valueOf(ruta.getCosto());
                        mostrarVentanaRuta(origen, destino, distanciaRuta, tiempoRuta, costoRuta, linea);
                    }
                }
            });

            eliminarItem.setOnAction(e -> {
                Group origen = (Group) linea.getUserData();
                Group destino = null;

                for (Map.Entry<Group, List<Line>> entry : lineasPorNodo.entrySet()) {
                    if (entry.getValue().contains(linea) && entry.getKey() != origen) {
                        destino = entry.getKey();
                        break;
                    }
                }

                if (origen != null && destino != null) {
                    Parada paradaOrigen = (Parada) origen.getUserData();
                    Parada paradaDestino = (Parada) destino.getUserData();

                    Ruta rutaAEliminar = null;
                    List<Ruta> rutasDesdeOrigen = grafo.getAdyacencias().get(paradaOrigen);

                    if (rutasDesdeOrigen != null) {
                        for (Ruta r : rutasDesdeOrigen) {
                            if (r.getDestino().equals(paradaDestino)) {
                                rutaAEliminar = r;
                                break;
                            }
                        }
                    }

                    if (rutaAEliminar != null) {
                        boolean eliminado = grafo.eliminarRuta(rutaAEliminar);
                        if (eliminado) {
                            grafoContenedor.getChildren().remove(linea);
                            grafoContenedor.getChildren().remove(textoPorLinea.get(linea));
                            lineasPorNodo.get(origen).remove(linea);
                            lineasPorNodo.get(destino).remove(linea);
                            textoPorLinea.remove(linea);
                        }
                    }
                }
            });
            menu.getItems().addAll(modificarItem, eliminarItem);
            menu.show(linea, event.getScreenX(), event.getScreenY());
        });

        Text texto = new Text(distancia + " km, " + tiempo + " min, RD$" + costo);
        texto.setFont(Font.font(12));
        texto.setFill(Color.DARKBLUE);
        posicionarTextoLinea(texto, startX, startY, endX, endY);
        texto.setMouseTransparent(true);
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


    //muy largo
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
                List<Line> lineasAsociadas = lineasPorNodo.get(nodo);
                if (lineasAsociadas != null) {
                    for (Line linea : new ArrayList<>(lineasAsociadas)) {
                        Group otroNodo = null;
                        for (Map.Entry<Group, List<Line>> entry : lineasPorNodo.entrySet()) {
                            if (!entry.getKey().equals(nodo) && entry.getValue().contains(linea)) {
                                otroNodo = entry.getKey();
                                break;
                            }
                        }
                        grafoContenedor.getChildren().remove(linea);
                        Text txt = textoPorLinea.remove(linea);
                        if (txt != null) {
                            grafoContenedor.getChildren().remove(txt);
                        }
                        if (otroNodo != null) {
                            List<Line> listaOtro = lineasPorNodo.get(otroNodo);
                            if (listaOtro != null) {
                                listaOtro.remove(linea);
                            }
                        }
                    }
                    lineasPorNodo.remove(nodo);
                }
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
                        grafo.modificarRuta(paradaOrigen, paradaDestino, tiempoInt, distanciaInt, costoDouble);
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
        grafoContenedor.requestFocus();
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

    private void resetCalculoRutas() {

        if (nodoOrigen != null) {
            ((Circle) nodoOrigen.getChildren().get(0))
                    .getStyleClass().removeAll("parada-selected");
        }
        if (nodoDestino != null) {
            ((Circle) nodoDestino.getChildren().get(0))
                    .getStyleClass().removeAll("parada-selected");
        }

        cbCriterio.getSelectionModel().selectFirst();
        nodoOrigen = null;
        nodoDestino = null;
        nodosSeleccionadosCompletos = false;
        paneRutas.setVisible(false);
    }
}
