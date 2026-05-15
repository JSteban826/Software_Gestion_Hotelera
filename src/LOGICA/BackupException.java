package LOGICA;

public class BackupException extends RuntimeException {

    private final String codigoError;

    public BackupException(String codigoError, Throwable cause) {
        super(cause);
        this.codigoError = codigoError;
    }

    // Nuevo constructor para usar solo un String
    public BackupException(String codigoError) {
        super(codigoError);
        this.codigoError = codigoError;
    }

    public String getCodigoError() {
        return codigoError;
    }
}
