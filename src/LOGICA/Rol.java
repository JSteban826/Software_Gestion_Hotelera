
package LOGICA;

public class Rol {
    private int id;
    private String nombre;

    // Constructor con parámetros
    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Este método es importante para que el comboBox muestre el nombre
    @Override
    public String toString() {
        return nombre;
    }
}


