/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author elian
 */
public class ListaDobleHistorial {

    NodoHistorial inicio;
    NodoHistorial fin;
    NodoHistorial cursor;

    public ListaDobleHistorial() {
        this.inicio = null;
        this.fin = null;
    }

    public void agregarAccion(String accion) {
        // Obtenemos la fecha actual
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        NodoHistorial nuevoNodo = new NodoHistorial(accion, fechaActual);

        if (inicio == null) {
            inicio = nuevoNodo;
            fin = nuevoNodo;
        } else {
            fin.siguiente = nuevoNodo;
            nuevoNodo.anterior = fin;
            fin = nuevoNodo;
        }

        System.out.println("Acción agregada a la lista: " + accion + " - " + fechaActual);
    }

    public void avanzar() {
        if (cursor != null && cursor.siguiente != null) {
            cursor = cursor.siguiente;
            System.out.println("Acción actual: " + cursor.accion + " - " + cursor.fecha);
        } else {
            System.out.println("No hay más acciones hacia adelante.");
        }
    }

    public void retroceder() {
        if (cursor != null && cursor.anterior != null) {
            cursor = cursor.anterior;
            System.out.println("Acción actual: " + cursor.accion + " - " + cursor.fecha);
        } else {
            System.out.println("No hay más acciones hacia atrás.");
        }
    }

    public void imprimirHistorial() {
        NodoHistorial actual = inicio;
        while (actual != null) {
            System.out.println(actual.accion + " - " + actual.fecha);
            actual = actual.siguiente;
        }
    }

}
