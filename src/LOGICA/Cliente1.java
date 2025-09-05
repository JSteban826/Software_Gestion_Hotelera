package LOGICA;

public class Cliente1 {
    private String cedula;
    private String nombreCompleto;
    private String correo; // Nuevo atributo

    public Cliente1(String cedula, String nombre, String apellido) {
        this.cedula = cedula;
        this.nombreCompleto = nombre + " " + apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    @Override
    public String toString() {
        return nombreCompleto; // Para que JComboBox muestre solo el nombre
    }
}
