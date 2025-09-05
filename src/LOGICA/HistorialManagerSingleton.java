/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

import LOGICA.HistorialManager;

/**
 *
 * @author elian
 */
public class HistorialManagerSingleton {
      private static HistorialManager instancia = null;

    private HistorialManagerSingleton() {
        // Constructor privado para evitar instancias adicionales
    }

    public static HistorialManager getInstancia() {
        if (instancia == null) {
            instancia = new HistorialManager();
        }
        return instancia;
    }
}
