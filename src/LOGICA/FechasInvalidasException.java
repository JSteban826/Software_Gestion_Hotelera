package LOGICA;

public class FechasInvalidasException extends Exception {
    private final String codigoError;

    public FechasInvalidasException(String codigoError, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
    }


    public String getCodigoError() {
        return codigoError;
    }
}
