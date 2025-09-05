package LOGICA;

import javax.swing.JOptionPane;

public class ManejadorErrores {

    public static void camposVacios(Exception e) {
        Logger.registrarError(CodigoError.ERR_CAMPOS_VACIOS, e);
    }

    public static void valorDuplicado(Exception e) {
        Logger.registrarError(CodigoError.ERR_VALOR_DUPLICADO, e);
    }

    public static void errorConexionBD(Exception e) {
        Logger.registrarError(CodigoError.ERR_DB_CONEXION, e);
    }

    public static void bloqueTrigger(Exception e) {
        Logger.registrarError(CodigoError.ERR_TABLAS_BLOQUEADAS_TRIGGER, e);
    }

    public static void bloqueoTimeout(Exception e) {
        Logger.registrarError(CodigoError.ERR_TABLAS_BLOQUEADAS_TIMEOUT, e);
    }

    public static void conversion(Exception e) {
        Logger.registrarError(CodigoError.ERR_CONVERSION_NUMERICA, e);
    }

    public static void errorInsertSQL(Exception e) {
        Logger.registrarError(CodigoError.ERR_SQL_INSERT, e);
    }

    public static void errorSelectSQL(Exception e) {
        Logger.registrarError(CodigoError.ERR_SQL_SELECT, e);
    }
    
    public static void errorDeleteSQL(Exception e) {
        Logger.registrarError(CodigoError.ERR_DELETE_DATOS, e);
    }
    
    public static void errorUpdateSQL(Exception e) {
        Logger.registrarError(CodigoError.ERR_MODIFICAR_DATOS, e);
    }

    public static void tablasBloqueadas(Exception e) {
        Logger.registrarError(CodigoError.ERR_TABLAS_BLOQUEADAS, e);
    }

    public static void conexionInestable(Exception e) {
        Logger.registrarError(CodigoError.ERR_CONEXION_INESTABLE, e);
    }

    public static void clienteNoExiste(Exception e) {
        Logger.registrarError(CodigoError.ERR_CLIENTE_NO_EXISTE, e);
    }

    public static void accesoDenegado(Exception e) {
        Logger.registrarError(CodigoError.ERR_ACCESO_DENEGADO, e);
    }

    public static void fechasInvalidas(FechasInvalidasException e) {
        Logger.registrarError(CodigoError.ERR_FECHAS_INVALIDAS, e);

    }

    public static void errorPago(Exception e) {
        Logger.registrarError(CodigoError.ERR_PAGO_PROCESO, e);
    }

    public static void generarEnlace(Exception e) {
        Logger.registrarError(CodigoError.ERR_GENERAR_ENLACE, e);
    }

    public static void enviarEnlace(Exception e) {
        Logger.registrarError(CodigoError.ERR_ENVIAR_ENLACE, e);
    }

    public static void generarTicket(TicketNoGeneradoException e) {
        // Llamamos a la función de Logger y le pasamos solo el código de error.
        Logger.registrarError(CodigoError.ERR_GENERAR_TICKET, e);
    }

    public static void cancelarOperacion(Exception e) {
        Logger.registrarError(CodigoError.ERR_CANCELAR_OPERACION, e);
    }

    public static void errorLogicaNegocio(Exception e) {
        Logger.registrarError(CodigoError.ERR_LOGICA_NEGOCIO, e);
    }

    public static void errorDesconocido(Exception e) {
        Logger.registrarError(CodigoError.ERR_NO_CONTROLADO, e);
    }
}
