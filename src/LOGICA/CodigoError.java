package LOGICA;

public class CodigoError {

    // ⚙️ ERRORES GENERALES
    public static final String ERR_NO_CONTROLADO             = "ERR000"; // Error no controlado
    public static final String ERR_CAMPOS_VACIOS             = "ERR001"; // Campos vacíos
    public static final String ERR_CONVERSION_NUMERICA       = "ERR002"; // Error de formato numérico
    public static final String ERR_LOGICA_NEGOCIO            = "ERR003"; // Regla de negocio fallida

    // 🗄️ BASE DE DATOS
    public static final String ERR_DB_CONEXION               = "ERR100"; // Error al conectar a la BD
    public static final String ERR_SQL_INSERT                = "ERR101"; // Error al insertar datos
    public static final String ERR_SQL_SELECT                = "ERR102"; // Error al obtener datos
    public static final String ERR_VALOR_DUPLICADO           = "ERR103"; // Valor duplicado en la BD
    public static final String ERR_MODIFICAR_DATOS           = "ERR104"; // Error al modificar datos
    public static final String ERR_TABLAS_BLOQUEADAS         = "ERR105"; // Tablas bloqueadas
    public static final String ERR_CONEXION_INESTABLE        = "ERR106"; // Conexión inestable con la BD
    public static final String ERR_TABLAS_BLOQUEADAS_TRIGGER = "ERR107"; // Tablas bloqueadas Trigger
    public static final String ERR_TABLAS_BLOQUEADAS_TIMEOUT = "ERR108"; // Tablas bloqueadas Timeout
    public static final String ERR_DELETE_DATOS              = "ERR109"; // Error al eliminar datos

    // 👤 USUARIOS Y ACCESO
    public static final String ERR_CLIENTE_NO_EXISTE         = "ERR200"; // Cliente no encontrado
    public static final String ERR_ACCESO_DENEGADO           = "ERR201"; // Acceso denegado

    // 📅 FECHAS Y RESERVAS
    public static final String ERR_FECHAS_INVALIDAS          = "ERR300"; // Rango de fechas inválido

    // 💳 PAGOS Y FACTURACIÓN
    public static final String ERR_PAGO_PROCESO              = "ERR400"; // Error al procesar pago
    public static final String ERR_GENERAR_ENLACE            = "ERR401"; // Error al generar enlace de pago
    public static final String ERR_ENVIAR_ENLACE             = "ERR402"; // Error al enviar enlace de pago
    public static final String ERR_GENERAR_TICKET            = "ERR403"; // Error al generar ticket
    public static final String ERR_CANCELAR_OPERACION        = "ERR404"; // Error al cancelar una operación

}

