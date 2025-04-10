package com.example.pfp;
import javafx.application.Platform;
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
import logico.Algoritmos;
import logico.Grafo;
import logico.Parada;
import logico.Ruta;

import java.util.*;

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
    @FXML
    private Pane paneResultado;
    @FXML
    private Label lblTiempo;
    @FXML
    private Label lblCosto;
    @FXML
    private Label lblDistancia;

    private static final String archivo = "grafo10.dat";
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
    private Map<Parada, Group> mapaVisualNodos = new HashMap<>();

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
            resetColores();
            String criterio = cbCriterio.getSelectionModel().getSelectedItem();
            if (criterio == null) {
                mostrarMensajeError("Debes seleccionar un criterio antes de calcular.");
                return;
            }

            if (nodoOrigen == null || nodoDestino == null) {
                mostrarMensajeError("Debes seleccionar dos paradas antes de calcular.");
                return;
            }
            Parada pOrigen  = (Parada) nodoOrigen.getUserData();
            Parada pDestino = (Parada) nodoDestino.getUserData();

            System.out.println("Parada Origen: " + pOrigen.getNombre());
            System.out.println("Parada Destino: " + pDestino.getNombre());

            if(criterio.equals("Distancia")) {
                List<Parada> ruta = Algoritmos.dijkstra(grafo, pOrigen, pDestino, criterio );

                System.out.println("Ruta encontrada:");
                for (Parada p : ruta) {
                    System.out.println("- " + p.getNombre());
                }

                if (ruta.isEmpty()) {
                    mostrarMensajeError("No existe camino entre esas paradas.");
                    return;
                }

                resaltarRuta(ruta);
                mostrarResultadosRuta(ruta);
            }

            if(criterio.equals("Tiempo")) {
                List<Parada> ruta = Algoritmos.dijkstra(grafo, pOrigen, pDestino, criterio );

                System.out.println("Ruta encontrada:");
                for (Parada parada : ruta) {
                    System.out.println("- " + parada.getNombre());
                }

                if (ruta.isEmpty()) {
                    mostrarMensajeError("No existe camino entre esas paradas.");
                    return;
                }

                resaltarRuta(ruta);
                mostrarResultadosRuta(ruta);
            }

            if(criterio.equals("Costo")) {

                List<Parada> ruta = Algoritmos.bellmanFord(grafo, pOrigen, pDestino);

                        System.out.println("Ruta encontrada:");
                for (Parada parada : ruta) {
                    System.out.println("- " + parada.getNombre());
                }

                if (ruta.isEmpty()) {
                    mostrarMensajeError("No existe camino entre esas paradas.");
                    return;
                }
                resaltarRuta(ruta);
                mostrarResultadosRuta(ruta);
            }

            if(criterio.equals("Transbordo")) {

                List<Parada> ruta = Algoritmos.bfs(grafo, pOrigen, pDestino);

                System.out.println("Ruta encontrada:");
                for (Parada parada : ruta) {
                    System.out.println("- " + parada.getNombre());
                }

                if (ruta.isEmpty()) {
                    mostrarMensajeError("No existe camino entre esas paradas.");
                    return;
                }
                resaltarRuta(ruta);
                mostrarResultadosRuta(ruta);
            }

        });

        cargarGrafoVisual(archivo);
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
               ventanaRuta(nodoOrigen, nodoDestino, null, null, null, null);
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
                        ventanaRuta(origen, destino, distanciaRuta, tiempoRuta, costoRuta, linea);
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
                            Grafo.guardarGrafo(grafo, archivo);
                        }
                    }
                }
            });
            menu.getItems().addAll(modificarItem, eliminarItem);
            menu.show(linea, event.getScreenX(), event.getScreenY());
        });

        Text texto = new Text(distancia + " km\n" + tiempo + " min\n RD$" + costo);
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
                ventanaModificarParada(parada, nodo);
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
                    mapaVisualNodos.remove(parada);
                    grafoContenedor.getChildren().remove(nodo);
                    Grafo.guardarGrafo(grafo, archivo);
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

            Grafo.guardarGrafo(grafo, archivo);
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


    private void ventanaModificarParada(Parada parada, Group nodo) {
        Stage ventanaModificar = new Stage();
        ventanaModificar.setTitle("Modificar Parada");

        Label label = new Label("Nombre:");
        TextField campoNombre = new TextField(parada.getNombre());
        Button botonConfirmar = new Button("Confirmar");

        botonConfirmar.setOnAction(e -> {

            String nuevoNombre = campoNombre.getText();
            parada.setNombre(nuevoNombre);
            Grafo.guardarGrafo(grafo, archivo);

            Text nombreTexto = (Text) nodo.getChildren().get(1);
            nombreTexto.setText(nuevoNombre);
            ventanaModificar.close();
        });

        VBox layout = new VBox(10, label, campoNombre, botonConfirmar);
        layout.setStyle("-fx-padding: 10;");
        layout.setMinWidth(280);

        Scene scene = new Scene(layout);
        ventanaModificar.setScene(scene);
        ventanaModificar.show();
    }


    private void ventanaRuta(Group pOrigen, Group pDestino, String distanciaInicial, String tiempoInicial, String costoInicial, Line lineaExiste) {
        Stage ventanaRuta = new Stage();
        ventanaRuta.setTitle("Detalles de la Ruta");

        Label labelDistancia = new Label("Distancia(km):");
        TextField campoDistancia = new TextField(distanciaInicial != null ? distanciaInicial : "");
        Label labelTiempo = new Label("Tiempo(min):");
        TextField campoTiempo = new TextField(tiempoInicial != null ? tiempoInicial : "");
        Label labelCosto = new Label("Costo(RD$):");
        TextField campoCosto = new TextField(costoInicial != null ? costoInicial : "");

        Button botonGuardar = new Button("Guardar");
        botonGuardar.setOnAction(e -> {
            String distancia = campoDistancia.getText();
            String tiempo = campoTiempo.getText();
            String costo = campoCosto.getText();

            if (validarDatos(distancia, tiempo, costo)) {
                try {
                    int distanciaModificada = Integer.parseInt(distancia);
                    int tiempoModificado = Integer.parseInt(tiempo);
                    double costoModificado = Double.parseDouble(costo);

                    Parada paradaOrigen = (Parada) pOrigen.getUserData();
                    Parada paradaDestino = (Parada) pDestino.getUserData();

                    if (lineaExiste == null) {
                        dibujarLineaEntreNodos(pOrigen, pDestino, distancia, tiempo, costo);
                        grafo.agregarRuta(paradaOrigen, paradaDestino, tiempoModificado, distanciaModificada, costoModificado);
                        Grafo.guardarGrafo(grafo, archivo);
                    } else {
                        Text texto = textoPorLinea.get(lineaExiste);
                        texto.setText(distancia + " km, " + tiempo + " min, RD$" + costo);
                        grafo.modificarRuta(paradaOrigen, paradaDestino, tiempoModificado, distanciaModificada, costoModificado);
                        Grafo.guardarGrafo(grafo, archivo);
                    }
                    ventanaRuta.close();

                } catch (NumberFormatException ex) {
                    mostrarMensajeError("Los valores deben ser numéricos válidos.");
                }
            } else {
                mostrarMensajeError("Error, revisar los datos de la ruta.");
            }
        });

        VBox layout = new VBox(10, labelDistancia, campoDistancia, labelTiempo, campoTiempo, labelCosto, campoCosto, botonGuardar);
        layout.setStyle("-fx-padding: 10;");
        layout.setMinWidth(300);

        Scene scene = new Scene(layout);
        ventanaRuta.setScene(scene);
        ventanaRuta.initModality(Modality.APPLICATION_MODAL);
        ventanaRuta.initOwner(grafoContenedor.getScene().getWindow());
        grafoContenedor.requestFocus();
        ventanaRuta.showAndWait();
    }


    private boolean validarDatos(String distancia, String tiempo, String costo) {
        if (distancia.isEmpty() || tiempo.isEmpty() || costo.isEmpty()) {
            return false;
        }
        try {
            int distance = Integer.parseInt(distancia);
            int time = Integer.parseInt(tiempo);
            double cost = Double.parseDouble(costo);
            if (distance < 0 || time < 0 || cost < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }


    private void mostrarMensajeError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    private void resetCalculoRutas() {

        resetColores();
        nodoOrigen = null;
        nodoDestino = null;
        nodosSeleccionadosCompletos = false;
        paneRutas.setVisible(false);
        paneResultado.setVisible(false);
    }


    private void resaltarRuta(List<Parada> ruta) {

        for (Node nodo : grafoContenedor.getChildren()) {
            if (nodo instanceof Group grupo) {
                Parada parada = (Parada) grupo.getUserData();
                if (ruta.contains(parada)) {
                    Circle circulo = (Circle) grupo.getChildren().get(0);
                    circulo.getStyleClass().add("parada-preferida");
                }
            }
        }

        for (int i = 0; i < ruta.size() - 1; i++) {
            Parada p1 = ruta.get(i);
            Parada p2 = ruta.get(i + 1);

            Group grupo1 = getGrupoPorParada(p1);
            Group grupo2 = getGrupoPorParada(p2);
            if (grupo1 != null && grupo2 != null) {
                List<Line> lineasGrupo1 = lineasPorNodo.get(grupo1);
                if (lineasGrupo1 != null) {
                    for (Line linea : lineasGrupo1) {
                        List<Line> lineasGrupo2 = lineasPorNodo.get(grupo2);
                        if (lineasGrupo2 != null && lineasGrupo2.contains(linea)) {
                            linea.getStyleClass().removeAll("linea-ruta");
                            linea.getStyleClass().add("linea-preferida");
                        }
                    }
                }
            }
        }
    }


    private Group getGrupoPorParada(Parada paradaBuscada) {
        for (Node node : grafoContenedor.getChildren()) {
            if (node instanceof Group grupo) {
                Parada parada = (Parada) grupo.getUserData();
                if (parada.equals(paradaBuscada)) {
                    return grupo;
                }
            }
        }
        return null;
    }


    private void mostrarResultadosRuta(List<Parada> rutas) {
        double totalTiempo = 0;
        double totalCosto = 0;
        double totalDistancia = 0;

        for (int i = 0; i < rutas.size() - 1; i++) {
            Parada actual = rutas.get(i);
            Parada siguiente = rutas.get(i + 1);
            List<Ruta> adyacentes = grafo.getAdyacencias().get(actual);

            for (Ruta ruta : adyacentes) {
                if (ruta.getDestino().equals(siguiente)) {
                    totalTiempo += ruta.getTiempo();
                    totalCosto += ruta.getCosto();
                    totalDistancia += ruta.getDistancia();
                    break;
                }
            }
        }

        lblTiempo.setText(String.format("%.0f min", totalTiempo));
        lblCosto.setText(String.format("%.2f DOP", totalCosto));
        lblDistancia.setText(String.format("%.0f km", totalDistancia));
        paneResultado.setVisible(true);
    }


    private void resetColores() {
        for (Node node : grafoContenedor.getChildren()) {
            if (node instanceof Group) {
                Group grupo = (Group) node;
                Circle circulo = (Circle) grupo.getChildren().get(0);
                circulo.getStyleClass().removeAll("parada-selected", "parada-preferida");
                if (!circulo.getStyleClass().contains("nodo-circulo")) {
                    circulo.getStyleClass().add("nodo-circulo");
                }
            }
        }

        Set<Line> todasLineas = new HashSet<>();
        for (List<Line> lista : lineasPorNodo.values()) {
            todasLineas.addAll(lista);
        }
        for (Line linea : todasLineas) {
            linea.getStyleClass().removeAll("linea-preferida");
            if (!linea.getStyleClass().contains("linea-ruta")) {
                linea.getStyleClass().add("linea-ruta");
            }
        }
    }


    private void cargarGrafoVisual(String archivos) {

        grafo = Grafo.cargarGrafo(archivos);
        if (grafo == null) {
            System.out.println("No se pudo cargar el grafo desde " + archivos + ", se creará uno nuevo.");
            grafo = new Grafo();
        } else {
            System.out.println("Grafo cargado");
        }

        for (Parada parada : grafo.getParadas()) {
            Group nodoVisual = crearNodoVisual(parada);
            mapaVisualNodos.put(parada, nodoVisual);
            grafoContenedor.getChildren().add(nodoVisual);

            makeNodoMovible(nodoVisual);
        }

        Platform.runLater(() -> {
            for (Map.Entry<Parada, List<Ruta>> entry : grafo.getAdyacencias().entrySet()) {
                Parada origen = entry.getKey();
                List<Ruta> rutas = entry.getValue();
                Group nodoOrigenVisual = mapaVisualNodos.get(origen);
                if (nodoOrigenVisual == null) continue;
                for (Ruta ruta : rutas) {
                    Parada destino = ruta.getDestino();
                    Group nodoDestinoVisual = mapaVisualNodos.get(destino);
                    if (nodoDestinoVisual == null) continue;
                    dibujarLineaEntreNodos(nodoOrigenVisual, nodoDestinoVisual,
                            String.valueOf(ruta.getDistancia()),
                            String.valueOf(ruta.getTiempo()),
                            String.valueOf(ruta.getCosto()));
                }
            }
        });
    }


    private Group crearNodoVisual(Parada parada) {
        Circle circulo = new Circle(15);
        circulo.getStyleClass().add("nodo-circulo");

        Text nombre = new Text(parada.getNombre());
        nombre.getStyleClass().add("nodo-texto");
        nombre.setTranslateY(circulo.getRadius() + 15);
        nombre.setTranslateX(-nombre.getBoundsInLocal().getWidth() / 2);

        Group nodo = new Group(circulo, nombre);
        nodo.setTranslateX(parada.getX());
        nodo.setTranslateY(parada.getY());

        nodo.setUserData(parada);

        return nodo;
    }
}