package LOGICA;

public class ClienteNoExisteException extends Exception {

    public ClienteNoExisteException(String mensaje) {
        super(mensaje);
    }

    public ClienteNoExisteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
