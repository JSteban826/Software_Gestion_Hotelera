/*package LOGICA;

public class NewClass {
    // Campos vacíos o datos faltantes

    catch (NullPointerException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_CAMPOS_VACIOS, e);
    }

// Error de formato numérico (por ejemplo, Integer.parseInt fallido)
    catch (NumberFormatException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_CONVERSION_NUMERICA, e);
    }

// Cliente no encontrado en la base de datos
    catch (ClienteNoEncontradoException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_CLIENTE_NO_EXISTE, e);
    }

// Error por valor duplicado (clave primaria repetida)
    catch (SQLIntegrityConstraintViolationException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_VALOR_DUPLICADO, e);
    }

// Error general de SQL
    catch (SQLException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_SQL_INSERT, e);
    }

// Error si las tablas están bloqueadas (puede extenderse)
    catch (SQLTransactionRollbackException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_TABLAS_BLOQUEADAS, e);
    }

// Error por conexión inestable o perdida
    catch (SQLNonTransientConnectionException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_CONEXION_INESTABLE, e);
    }

// Error general de conexión a la base de datos
    catch (SQLRecoverableException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_DB_CONEXION, e);
    }

// Error al modificar datos
    catch (ModificarDatosException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_MODIFICAR_DATOS, e);
    }

// Error al cancelar una reserva u operación
    catch (OperacionCanceladaException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_CANCELAR_OPERACION, e);
    }

// Error de acceso denegado (sin permisos)
    catch (AccessControlException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_ACCESO_DENEGADO, e);
    }

// Error al procesar un pago
    catch (PagoFallidoException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_PAGO_PROCESO, e);
    }

// Error al generar el enlace de pago
    catch (GeneracionEnlaceException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_GENERAR_ENLACE, e);
    }

// Error al enviar el enlace de pago (correo, SMS)
    catch (EnvioEnlaceException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_ENVIAR_ENLACE, e);
    }

// Error al generar ticket (factura, comprobante)
    catch (GenerarTicketException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_GENERAR_TICKET, e);
    }

// Fechas inválidas (entrada >= salida)
    catch (FechasInvalidasException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_FECHAS_INVALIDAS, e);
    }

// Lógica de negocio fallida
    catch (ReglaNegocioException e

    
        ) {
    Logger.registrarError(CodigoError.ERR_LOGICA_NEGOCIO, e);
    }

// Catch general para errores no controlados
    catch (Exception e

    
        ) {
    Logger.registrarError(CodigoError.ERR_NO_CONTROLADO, e);
    }

}
*/