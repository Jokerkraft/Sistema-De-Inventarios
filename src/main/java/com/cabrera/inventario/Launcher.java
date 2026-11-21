package com.cabrera.inventario;

import javafx.application.Application;

// Inicia JavaFX desde una clase sencilla compatible con empaquetadores y lanzadores.
public class Launcher {

    // Delega el arranque a la clase Application principal.
    public static void main(String[] args) {
        Application.launch(SistemaInventarios.class, args);
    }
}
