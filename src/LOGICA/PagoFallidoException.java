package LOGICA;

public class PagoFallidoException extends Exception {
    private int idCliente;
    private int idReserva;

    public PagoFallidoException(String message) {
        super(message);
    }

    public PagoFallidoException(String message, Throwable cause) {
        super(message, cause);
    }

    public PagoFallidoException(String message, int idCliente, int idReserva) {
        super(message);
        this.idCliente = idCliente;
        this.idReserva = idReserva;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdReserva() {
        return idReserva;
    }
}
