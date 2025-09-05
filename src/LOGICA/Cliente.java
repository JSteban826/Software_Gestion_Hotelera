package LOGICA;

public class Cliente {
    private String cedula;
    private String nombreCompleto;
    private String correo; // Nuevo atributo

    public Cliente(String cedula, String nombre, String apellido, String correo) {
        this.cedula = cedula;
        this.nombreCompleto = nombre + " " + apellido;
        this.correo = correo;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return nombreCompleto; // Para que JComboBox muestre solo el nombre
    }
}
