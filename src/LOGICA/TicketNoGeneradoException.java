package LOGICA;

public class TicketNoGeneradoException extends Exception {
    private final String codigoError;

    public TicketNoGeneradoException(String codigoError, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
    }

    public String getCodigoError() {
        return codigoError;
    }
}
