/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

/**
 *
 * @author elian
 */
public class NodoHistorial {
    String accion;
    String fecha; // Guardamos la fecha como cadena (puedes cambiarla por un objeto Date si lo prefieres)
    NodoHistorial anterior;
    NodoHistorial siguiente;

    public NodoHistorial(String accion, String fecha) {
        this.accion = accion;
        this.fecha = fecha;
        this.anterior = null;
        this.siguiente = null;
    }
    
}
