package IGU;

import static IGU.Registro.insertarUsuario;
import LOGICA.ClienteNoExisteException;
import LOGICA.CodigoError;
import LOGICA.Logger;
import LOGICA.CorreoNoEnviadoException;
import LOGICA.FechasInvalidasException;
import LOGICA.ReservaManager;
import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.Habitaciones1;
import LOGICA.GestionReservas;
import LOGICA.ManejadorErrores;
import LOGICA.PagoFallidoException;
import static LOGICA.enviarCorreoConAdjunto.enviarCorreoConAdjunto;
import LOGICA.Tablas;
import PERSISTENCIA.ConexionBD;
import LOGICA.Reservas;
import LOGICA.TicketNoGeneradoException;

import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Reserva extends javax.swing.JFrame {

    public Reserva() {
        initComponents();
        setDefaultCloseOperation(Reserva.DISPOSE_ON_CLOSE); // <-- AQUÍ
        setResizable(false);
        Tablas.CentrarEncabezados(jtable_reservas);
        int nuevoId = Reserva.obtenerSiguienteIdReserva();
        txt_id_reserva.setText(String.valueOf(nuevoId)); // txtIdReserva es tu JTextField

        cargarHabitacionesEnComboBox();

    }

    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("com.images/logos.jpg"));
        return retValue;
    }

    public static class ResultadoPrecio {

        public double totalPrecio;
        public long diasEstancia;

        public ResultadoPrecio(double totalPrecio, long diasEstancia) {
            this.totalPrecio = totalPrecio;
            this.diasEstancia = diasEstancia;
        }
    }

    public static void agregarMenuContextual(JTable tabla) {
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    mostrarMenu(e);
                }
            }

            private void mostrarMenu(MouseEvent e) {
                int filaSeleccionada = tabla.rowAtPoint(e.getPoint());
                if (filaSeleccionada != -1) {
                    tabla.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem modificar = new JMenuItem("Modificar Reserva");
                    JMenuItem cancelar = new JMenuItem("Cancelar Reserva");

                    modificar.addActionListener(a -> {
                        int idReserva = (int) tabla.getValueAt(filaSeleccionada, 0);
                        String idCliente = (String) tabla.getValueAt(filaSeleccionada, 1);
                        int idHabitacion = (int) tabla.getValueAt(filaSeleccionada, 2);
                        String fechaEntrada = (String) tabla.getValueAt(filaSeleccionada, 3);
                        String fechaSalida = (String) tabla.getValueAt(filaSeleccionada, 4);

                        try {
                            if (Reservas.verificarAnticipacion(fechaEntrada)) {
                                String nuevaFechaEntradaStr = JOptionPane.showInputDialog("Nueva fecha de entrada (YYYY-MM-DD):", fechaEntrada);
                                String nuevaFechaSalidaStr = JOptionPane.showInputDialog("Nueva fecha de salida (YYYY-MM-DD):", fechaSalida);

                                if (nuevaFechaEntradaStr != null && nuevaFechaSalidaStr != null) {
                                    // Convertir Strings a java.util.Date
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    Date nuevaFechaEntrada = sdf.parse(nuevaFechaEntradaStr);
                                    Date fechaEntradaDate = sdf.parse(fechaEntrada); // convertir la fecha original también

                                    if (nuevaFechaEntrada.after(fechaEntradaDate)) {
                                        Reservas.modificarReserva(idReserva, idCliente, idHabitacion, nuevaFechaEntradaStr, nuevaFechaSalidaStr);
                                    } else {
                                        throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "La nueva fecha de entrada no puede ser anterior a la actual.");
                                    }

                                } else {
                                    throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "Debe ingresar ambas fechas para modificar la reserva.");
                                }

                                // Agregar Acción a Historial
                                HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                                historial_acciones.registrarAccion("Modificación de Reserva: " + idReserva);

                            } else {
                                throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "Por favor seleccione fechas válidas.");
                            }
                        } catch (ParseException ex) { // se cambió "e" por "ex"
                            JOptionPane.showMessageDialog(null, "Formato de fecha incorrecto. Use el formato YYYY-MM-DD.");
                        } catch (FechasInvalidasException ex) {
                            ManejadorErrores.fechasInvalidas(ex);
                        }

                    });
                    cancelar.addActionListener(a -> {
                        int idReserva = (int) tabla.getValueAt(filaSeleccionada, 0);
                        String fechaEntrada = (String) tabla.getValueAt(filaSeleccionada, 3);

                        // Crear un campo de contraseña
                        JPasswordField passwordField = new JPasswordField();
                        int option = JOptionPane.showConfirmDialog(null, passwordField, "Ingrese la clave de administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                        // Si el usuario presionó OK
                        if (option == JOptionPane.OK_OPTION) {
                            String claveIngresada = new String(passwordField.getPassword());
                            String claveCorrecta = "admin1234"; // Cambia esto por tu clave real

                            if (claveIngresada.equals(claveCorrecta)) {
                                int confirm = JOptionPane.showConfirmDialog(null, "¿Seguro que desea cancelar la reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
                                if (confirm == JOptionPane.YES_OPTION) {
                                    Reservas.eliminarReserva(idReserva);
                                } else {
                                    JOptionPane.showMessageDialog(null, "La reserva solo se puede cancelar 2 días antes de la fecha de entrada.");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Clave incorrecta. No se puede cancelar la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            //Agregar Accion a Historial
                            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                            historial_acciones.registrarAccion("Eliminacion de Reserva: " + idReserva);
                        }
                    });

                    menu.add(modificar);
                    menu.add(cancelar);
                    menu.show(tabla, e.getX(), e.getY());
                }
            }

        });
    }

    public static void insertar_reserva(JTextField txt_id_reserva, JTextField txt_id_habitacion, JTextField txt_id_cliente,
            JDateChooser jdate_fecha_entrada, JDateChooser jdate_fecha_salida)
            throws SQLIntegrityConstraintViolationException, SQLTransactionRollbackException, SQLTimeoutException, FechasInvalidasException {

        try {
            // Obtener valores de los campos
            int idReserva = Integer.parseInt(txt_id_reserva.getText().trim());
            int idHabitacion = Integer.parseInt(txt_id_habitacion.getText().trim());
            String idCliente = txt_id_cliente.getText().trim();
            Date fechaEntrada = jdate_fecha_entrada.getDate();
            Date fechaSalida = jdate_fecha_salida.getDate();

            // Verificar si el cliente existe
            if (!Reservas.existeCliente(idCliente)) {
                JOptionPane.showMessageDialog(null, "El cliente con cédula " + idCliente + " no existe en la base de datos.\nPor favor regístrelo antes de continuar.");
                return;
            }

            if (fechaEntrada.equals(fechaSalida)) {
                throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "La fecha de salida debe ser posterior a la de entrada.");

            }

            if (limpiarHora(fechaEntrada).before(limpiarHora(new Date()))) {
                throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "La fecha de entrada no puede ser anterior al día actual.");
            }

            // Calcular el precio y los días de estancia
            ResultadoPrecio resultado = Reservas.calcularPrecioYEstancia(idHabitacion, fechaEntrada, fechaSalida);
            if (resultado == null) {
                return; // Error de cálculo
            }

            // Formatear las fechas
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String strFechaEntrada = sdf.format(fechaEntrada);
            String strFechaSalida = sdf.format(fechaSalida);
            String estadoReserva = "Activa";

            // Insertar la reserva
            Reservas.insertarReserva(idReserva, idCliente, idHabitacion, strFechaEntrada, strFechaSalida, resultado.totalPrecio, resultado.diasEstancia);

            // Insertar en el historial
            int idHistorial = 0; // asumiendo autoincremental en BD
            Reservas.insertarHistorial(idHistorial, idReserva, idCliente, idHabitacion, strFechaEntrada, strFechaSalida, resultado.totalPrecio, estadoReserva);

            // Registrar acción en el historial de acciones
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Registro de Reserva: " + idReserva);

            // Actualizar estado de la habitación
            Reservas.actualizarEstadoHabitacion(idHabitacion, "ocupado");

        } catch (FechasInvalidasException e) {
            ManejadorErrores.fechasInvalidas(e);
        } catch (SQLIntegrityConstraintViolationException e) {
            ManejadorErrores.valorDuplicado(e);
        } catch (SQLTransactionRollbackException e) {
            ManejadorErrores.tablasBloqueadas(e);
        } catch (SQLTimeoutException e) {
            ManejadorErrores.bloqueoTimeout(e);
        } catch (SQLException e) {
            ManejadorErrores.bloqueTrigger(e);
        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
        }
    }

    public static Date limpiarHora(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void insertar_pago(JTextField txt_id_habitacion, JTextField txt_id_cliente,
            JDateChooser jdate_fecha_entrada, JDateChooser jdate_fecha_salida) throws FechasInvalidasException {

        String idCliente = txt_id_cliente.getText();
        int idPago = Reserva.obtenerSiguienteIdPago();
        int idHabitacion = Integer.parseInt(txt_id_habitacion.getText());
        Date fechaEntrada = jdate_fecha_entrada.getDate();
        Date fechaSalida = jdate_fecha_salida.getDate();

        ResultadoPrecio resultado = Reservas.calcularPrecioYEstancia(idHabitacion, fechaEntrada, fechaSalida);
        if (resultado == null) {
            return; // error
        }

        // Opciones para el estado del pago
        String[] opcionesEstado = {"Pagada", "Pendiente", "Cancelado"};
        JComboBox<String> comboBox = new JComboBox<>(opcionesEstado);

        int resultadoDialogo = JOptionPane.showConfirmDialog(
                null,
                comboBox,
                "Seleccione el estado del pago",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (resultadoDialogo == JOptionPane.OK_OPTION) {
            String estado = (String) comboBox.getSelectedItem();

            // Insertar el pago con el estado elegido
            Reservas.insertarPago(idPago, idCliente, resultado.totalPrecio, estado);

            // Registrar acción en el historial
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Registro de Pago (Reserva): " + idPago + " con estado: " + estado);
        } else {
            JOptionPane.showMessageDialog(null, "No se registró el pago porque no se seleccionó un estado.");
        }
    }

    public void limpiarCampos() {
        cmb_habitaciones.setSelectedIndex(0); // Reinicia el ComboBox
        txt_id_cliente.setText("");
        txt_id_habitacion.setText("");
        txt_id_reserva.setText("");
        jdate_fecha_entrada.setDate(null);
        jdate_fecha_salida.setDate(null);
        // Obtener el siguiente ID y mostrarlo en el campo
        int siguienteId = obtenerSiguienteIdReserva(); // este método ya lo tienes
        txt_id_reserva.setText(String.valueOf(siguienteId)); // lo asignas aquí

    }

    public static int obtenerSiguienteIdReserva() {
        int siguienteId = 1; // Valor por defecto si no hay registros

        String sql = "SELECT MAX(id_reserva) FROM reservas"; // Consulta para obtener el último id_reserva

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                siguienteId = rs.getInt(1) + 1; // Sumamos 1 al último id
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return siguienteId;
    }

    public static int obtenerSiguienteIdPago() {
        int siguienteId = 1; // Valor por defecto si no hay registros

        String sql = "SELECT MAX(id_pago) FROM historial_pagos"; // Consulta para obtener el último id_reserva

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                siguienteId = rs.getInt(1) + 1; // Sumamos 1 al último id
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return siguienteId;
    }

    Map<String, Habitaciones1> datosHabitaciones = new HashMap<>();

    private void cargarHabitacionesEnComboBox() {
        String sql = "SELECT id_habitacion, nombre_habitacion FROM habitaciones WHERE estado = 'libre'";
        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            cmb_habitaciones.removeAllItems();
            datosHabitaciones.clear();
            txt_id_habitacion.setText("");

            while (rs.next()) {
                int idHabitacion = rs.getInt("id_habitacion");
                String nombreHabitacion = rs.getString("nombre_habitacion");

                Habitaciones1 habitacion = new Habitaciones1(idHabitacion, nombreHabitacion);
                datosHabitaciones.put(nombreHabitacion, habitacion);
                cmb_habitaciones.addItem(nombreHabitacion);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar las Habitaciones: " + e.getMessage());
        }

        // Listener para actualizar campos al seleccionar una habitación
        cmb_habitaciones.addActionListener(e -> {
            String seleccionado = (String) cmb_habitaciones.getSelectedItem();
            if (seleccionado != null && datosHabitaciones.containsKey(seleccionado)) {
                Habitaciones1 habitacion = datosHabitaciones.get(seleccionado);
                txt_id_habitacion.setText(String.valueOf(habitacion.getIdHabitacion()));
            }
        });
    }

    public String obtenerCorreoCliente(String idCliente) {
        String correo = "";
        String sql = "SELECT correo_electronico FROM clientes WHERE Cedula = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, idCliente);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                correo = rs.getString("correo_electronico");
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return correo;
    }

    public void generarFacturaPDF() throws DocumentException, BadElementException, FileNotFoundException, IOException {
        Document documento = new Document();

        try {
            String idreserva = txt_id_reserva.getText();
            String idCliente = txt_id_cliente.getText();
            String idhabitacion = txt_id_habitacion.getText();
            Date fechaEntrada = jdate_fecha_entrada.getDate();
            Date fechaSalida = jdate_fecha_salida.getDate();

            // Validación previa
            if (idreserva.isEmpty() || idCliente.isEmpty() || idhabitacion.isEmpty() || fechaEntrada == null || fechaSalida == null) {
                throw new TicketNoGeneradoException(CodigoError.ERR_GENERAR_TICKET, "Datos incompletos para generar la factura. Verifica todos los campos.");
            }

            String correo = obtenerCorreoCliente(idCliente);

            if (correo == null || correo.isEmpty() || !correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                throw new TicketNoGeneradoException(CodigoError.ERR_GENERAR_TICKET, "Correo inválido. Verificar datos del cliente.");
            }

            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            String strFechaEntrada = formato.format(fechaEntrada);
            String strFechaSalida = formato.format(fechaSalida);

            String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            // Validar existencia de la ruta antes de escribir el archivo
            File directorio = new File("\"C:\\Users\\elian\\Desktop\\universidad proyectos\\HOTEL2.1\\HOTEL2.0\\Facturas_reservas\"");
            if (!directorio.exists() || !directorio.isDirectory()) {
                throw new TicketNoGeneradoException(CodigoError.ERR_GENERAR_TICKET,
                        "La ruta para guardar la factura no existe: " + directorio.getAbsolutePath());
            }

            String nombreArchivo = "factura_reserva_" + idCliente + "_" + fechaActual + ".pdf";
            String ruta = directorio.getAbsolutePath() + File.separator + "Facturas_" + nombreArchivo;

            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            // Logo
            com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(getClass().getResource("/com/images/coral.png"));
            logo.scaleAbsolute(60, 60);

            // Encabezado
            PdfPTable tablaEncabezado = new PdfPTable(2);
            tablaEncabezado.setWidthPercentage(100);

            PdfPCell celdaNombre = new PdfPCell(new Phrase("Resort Bahía Coral", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK)));
            celdaNombre.setBorder(Rectangle.NO_BORDER);
            celdaNombre.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaNombre.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celdaLogo = new PdfPCell(logo);
            celdaLogo.setBorder(Rectangle.NO_BORDER);
            celdaLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);

            tablaEncabezado.addCell(celdaNombre);
            tablaEncabezado.addCell(celdaLogo);
            documento.add(tablaEncabezado);

            Paragraph subtitulo = new Paragraph("N°Ticket:/" + idCliente + "/" + fechaActual + "/", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_RIGHT);
            documento.add(subtitulo);

            documento.add(Chunk.NEWLINE);

            BaseColor colorPersonalizado = new BaseColor(30, 144, 255);
            Paragraph titulo = new Paragraph("Ticket de Reserva", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, colorPersonalizado));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(Chunk.NEWLINE);

            PdfPTable tablaDatos = new PdfPTable(2);
            tablaDatos.setWidthPercentage(100);
            tablaDatos.setSpacingBefore(10f);

            PdfPCell celdaTitulo = new PdfPCell(new Phrase("Datos de la Reserva", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));
            celdaTitulo.setColspan(2);
            celdaTitulo.setBackgroundColor(colorPersonalizado);
            celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTitulo.setPadding(10);
            tablaDatos.addCell(celdaTitulo);

            PdfPCell celdaDato = new PdfPCell(new Phrase("Dato", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
            celdaDato.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaDato.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaDato.setPadding(8);

            PdfPCell celdaValor = new PdfPCell(new Phrase("Información", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
            celdaValor.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaValor.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaValor.setPadding(8);

            tablaDatos.addCell(celdaDato);
            tablaDatos.addCell(celdaValor);

            tablaDatos.addCell(getCeldaServicioTabla("ID Reserva:"));
            tablaDatos.addCell(getCeldaValorTabla(idreserva));
            tablaDatos.addCell(getCeldaServicioTabla("ID Cliente:"));
            tablaDatos.addCell(getCeldaValorTabla(idCliente));
            tablaDatos.addCell(getCeldaServicioTabla("ID Habitación:"));
            tablaDatos.addCell(getCeldaValorTabla(idhabitacion));
            tablaDatos.addCell(getCeldaServicioTabla("Fecha de Entrada:"));
            tablaDatos.addCell(getCeldaValorTabla(strFechaEntrada));
            tablaDatos.addCell(getCeldaServicioTabla("Fecha de Salida:"));
            tablaDatos.addCell(getCeldaValorTabla(strFechaSalida));

            documento.add(tablaDatos);
            documento.close();

            // Enviar correo
            String asunto = "Ticket Reserva Hotel Bahia Resort Coral";
            String cuerpo = "Adjunto encontrará su ticket correspondiente al pago de su reserva. ¡Gracias por elegirnos!";
            enviarCorreoConAdjunto(correo, asunto, cuerpo, ruta);

            JOptionPane.showMessageDialog(null, "Ticket generado con éxito.");
            JOptionPane.showMessageDialog(null, "Ticket guardado en:\n" + ruta);

        } catch (TicketNoGeneradoException e) {
            Logger.registrarError(e.getCodigoError(), e);
            JOptionPane.showMessageDialog(null, "Error al generar ticket: " + e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            TicketNoGeneradoException errorGeneral = new TicketNoGeneradoException(CodigoError.ERR_GENERAR_TICKET, "Error inesperado al generar la factura: " + e.getMessage());
            Logger.registrarError(errorGeneral.getCodigoError(), errorGeneral);
            //JOptionPane.showMessageDialog(null, "Error al generar ticket: " + errorGeneral.getMessage());
        }
    }

    private PdfPCell getCeldaServicioTabla(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
        celda.setPadding(8);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        return celda;
    }

    private PdfPCell getCeldaValorTabla(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
        celda.setPadding(8);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        return celda;
    }

    public String obtenerNombreCliente(String Cedula) {
        String nombre = " ";
        String sql = "SELECT nombre, apellido FROM clientes WHERE Cedula = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, Cedula);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                nombre = rs.getString("nombre") + " " + rs.getString("apellido");
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return nombre;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel8 = new javax.swing.JLabel();
        btn_adicionar1 = new javax.swing.JButton();
        btn_adicionar2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btn_reservar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txt_id_cliente = new javax.swing.JTextField();
        txt_id_reserva = new javax.swing.JTextField();
        jdate_fecha_entrada = new com.toedter.calendar.JDateChooser();
        jdate_fecha_salida = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        txt_id_habitacion = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtable_reservas = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cmb_habitaciones = new javax.swing.JComboBox<>();
        btn_actualizar = new javax.swing.JButton();
        btn_refrescar1 = new javax.swing.JButton();
        btn_factura = new javax.swing.JButton();
        btn_pago = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();

        jLabel8.setText("jLabel8");

        btn_adicionar1.setBackground(new java.awt.Color(65, 104, 163));
        btn_adicionar1.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_adicionar1.setForeground(new java.awt.Color(255, 255, 255));
        btn_adicionar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/mas-simbolo-negro.png"))); // NOI18N
        btn_adicionar1.setText("Adicionar");
        btn_adicionar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_adicionar1ActionPerformed(evt);
            }
        });

        btn_adicionar2.setBackground(new java.awt.Color(65, 104, 163));
        btn_adicionar2.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_adicionar2.setForeground(new java.awt.Color(255, 255, 255));
        btn_adicionar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/mas-simbolo-negro.png"))); // NOI18N
        btn_adicionar2.setText("Adicionar");
        btn_adicionar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_adicionar2ActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_reservar.setBackground(new java.awt.Color(65, 104, 163));
        btn_reservar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_reservar.setForeground(new java.awt.Color(255, 255, 255));
        btn_reservar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cita.png"))); // NOI18N
        btn_reservar.setText("Reservar");
        btn_reservar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_reservarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_reservar, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 210, -1, -1));

        jLabel2.setBackground(new java.awt.Color(65, 104, 163));
        jLabel2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 104, 163));
        jLabel2.setText("Id Reserva:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 107, -1, -1));

        jLabel4.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(65, 104, 163));
        jLabel4.setText("Documento:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 160, 140, -1));

        txt_id_cliente.setBackground(new java.awt.Color(204, 204, 204));
        txt_id_cliente.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_id_clienteActionPerformed(evt);
            }
        });
        jPanel1.add(txt_id_cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 160, 110, -1));

        txt_id_reserva.setBackground(new java.awt.Color(204, 204, 204));
        txt_id_reserva.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_reserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_id_reservaActionPerformed(evt);
            }
        });
        jPanel1.add(txt_id_reserva, new org.netbeans.lib.awtextra.AbsoluteConstraints(242, 109, 110, -1));

        jdate_fecha_entrada.setDateFormatString("yyyy-MM-dd");
        jPanel1.add(jdate_fecha_entrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(646, 107, 131, -1));

        jdate_fecha_salida.setDateFormatString("yyyy-MM-dd");
        jPanel1.add(jdate_fecha_salida, new org.netbeans.lib.awtextra.AbsoluteConstraints(646, 155, 131, -1));

        jLabel5.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 104, 163));
        jLabel5.setText("Id Habitación:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 203, -1, -1));

        txt_id_habitacion.setBackground(new java.awt.Color(204, 204, 204));
        txt_id_habitacion.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        jPanel1.add(txt_id_habitacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(241, 205, 110, -1));

        jtable_reservas.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jtable_reservas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id Reserva", "Id Cliente", "Id Habitacion", "Fecha Entrada", "Fecha Salida", "Precio Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jtable_reservas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtable_reservasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtable_reservas);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 304, 669, 253));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/licencia-de-conducir.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 40, 20));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/habitacion-disponible.png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, -1, 30));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/calendario (3).png"))); // NOI18N
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 152, 37, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cita (1).png"))); // NOI18N
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 107, -1, 19));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/calendario (3).png"))); // NOI18N
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 109, 37, -1));

        jLabel13.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(65, 104, 163));
        jLabel13.setText("Fecha Salida:");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 160, -1, -1));

        cmb_habitaciones.setBackground(new java.awt.Color(204, 204, 204));
        cmb_habitaciones.setBorder(null);
        cmb_habitaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_habitacionesActionPerformed(evt);
            }
        });
        jPanel1.add(cmb_habitaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(241, 245, 140, -1));

        btn_actualizar.setBackground(new java.awt.Color(65, 104, 163));
        btn_actualizar.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_actualizar.setForeground(new java.awt.Color(255, 255, 255));
        btn_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/mas-simbolo-negro.png"))); // NOI18N
        btn_actualizar.setText("Gestionar");
        btn_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_actualizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 580, 140, 40));

        btn_refrescar1.setBackground(new java.awt.Color(65, 104, 163));
        btn_refrescar1.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_refrescar1.setForeground(new java.awt.Color(255, 255, 255));
        btn_refrescar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/actualizar-flecha (2).png"))); // NOI18N
        btn_refrescar1.setText("Refrescar");
        btn_refrescar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refrescar1ActionPerformed(evt);
            }
        });
        jPanel1.add(btn_refrescar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 580, 130, 40));

        btn_factura.setBackground(new java.awt.Color(65, 104, 163));
        btn_factura.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_factura.setForeground(new java.awt.Color(255, 255, 255));
        btn_factura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/cuenta.png"))); // NOI18N
        btn_factura.setText("Generar Ticket");
        btn_factura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_facturaActionPerformed(evt);
            }
        });
        jPanel1.add(btn_factura, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 580, -1, -1));

        btn_pago.setBackground(new java.awt.Color(65, 104, 163));
        btn_pago.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_pago.setForeground(new java.awt.Color(255, 255, 255));
        btn_pago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/transferencia-movil.png"))); // NOI18N
        btn_pago.setText("Pago");
        btn_pago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_pagoActionPerformed(evt);
            }
        });
        jPanel1.add(btn_pago, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 210, 120, -1));

        jPanel2.setBackground(new java.awt.Color(65, 104, 163));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/logo50.jpg"))); // NOI18N
        jLabel1.setText("RESERVAR     ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 840, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(19, 19, 19))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 850, 90));

        jLabel14.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(65, 104, 163));
        jLabel14.setText("Habitación:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 246, -1, -1));

        jLabel15.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(65, 104, 163));
        jLabel15.setText("Fecha Entrada:");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 110, -1, -1));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/habitacion-disponible.png"))); // NOI18N
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 192, -1, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 837, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 633, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_reservarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_reservarActionPerformed
        // TODO add your handling code here:
        try {
            String cedula = txt_id_cliente.getText().trim();
            String idReserva = txt_id_reserva.getText().trim();
            String habitacion = txt_id_habitacion.getText().trim();

            // Obtener fechas desde los JDateChooser
            Date fechaEntradaUtil = jdate_fecha_entrada.getDate();
            Date fechaSalidaUtil = jdate_fecha_salida.getDate();

            // Validación de campos vacíos
            if (cedula.isEmpty() || idReserva.isEmpty() || habitacion.isEmpty() || fechaEntradaUtil == null || fechaSalidaUtil == null) {
                throw new NullPointerException("Campos vacíos");
            }

            if (fechaEntradaUtil.equals(fechaSalidaUtil)) {
                throw new FechasInvalidasException(CodigoError.ERR_FECHAS_INVALIDAS, "La fecha de entrada no puede ser igual que la de salida.");
            }
            // Validar existencia del cliente
            GestionReservas gestion = new GestionReservas();
            if (!gestion.clienteExiste(cedula)) {
                throw new ClienteNoExisteException("El cliente con cédula " + cedula + " no existe.");
            }

            // Insertar reserva si todo está validado
            insertar_reserva(txt_id_reserva, txt_id_habitacion, txt_id_cliente, jdate_fecha_entrada, jdate_fecha_salida);

        } catch (ClienteNoExisteException e) {
            ManejadorErrores.clienteNoExiste(e); // Manejador de errores para cliente inexistente

        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e); // Manejador de errores para campos vacíos

        } catch (SQLTransactionRollbackException ex) {
            java.util.logging.Logger.getLogger(Reserva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLTimeoutException | FechasInvalidasException ex) {
            java.util.logging.Logger.getLogger(Reserva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Reserva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btn_reservarActionPerformed

    private void txt_id_reservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_id_reservaActionPerformed

    }//GEN-LAST:event_txt_id_reservaActionPerformed

    private void jtable_reservasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtable_reservasMouseClicked
        // TODO add your handling code here:
        agregarMenuContextual(jtable_reservas);

    }//GEN-LAST:event_jtable_reservasMouseClicked

    private void txt_id_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_id_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_clienteActionPerformed

    private void cmb_habitacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_habitacionesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_habitacionesActionPerformed

    private void btn_adicionar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_adicionar1ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_adicionar1ActionPerformed

    private void btn_adicionar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_adicionar2ActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_btn_adicionar2ActionPerformed

    private void btn_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarActionPerformed
        Reservas.mostrarReservasEnTabla(jtable_reservas);
        Tablas.aplicarEstilosTabla(jtable_reservas, new Font("Roboto Light", Font.ITALIC, 12), Color.BLACK, Color.LIGHT_GRAY);
        ReservaManager reservaManager = new ReservaManager();
        reservaManager.gestionarReservasVencidas();
        JOptionPane.showMessageDialog(this, "Reservas gestionadas correctamente.");
        cargarHabitacionesEnComboBox();
    }//GEN-LAST:event_btn_actualizarActionPerformed

    private void btn_refrescar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refrescar1ActionPerformed
        limpiarCampos();
        cargarHabitacionesEnComboBox();

    }//GEN-LAST:event_btn_refrescar1ActionPerformed

    private void btn_facturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_facturaActionPerformed
        // TODO add your handling code here:
        try {
            // Generar la factura en PDF
            generarFacturaPDF();

            // Obtener datos del cliente
            String cedula = txt_id_cliente.getText();
            String nombre = obtenerNombreCliente(cedula);

            // Registrar la acción en el historial
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Ticket del cliente: " + nombre + " generada");

        } catch (Exception ex) {
            ex.printStackTrace(); // o manejar otras excepciones generales
        }

    }//GEN-LAST:event_btn_facturaActionPerformed

    private void btn_pagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_pagoActionPerformed
        // TODO add your handling code here:
        try {
            GestionReservas gestion = new GestionReservas();
            gestion.capturarDatosYRealizarPago(txt_id_cliente, txt_id_reserva);

            String cedula = txt_id_cliente.getText();
            String nombre = obtenerNombreCliente(cedula);

            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Pago del cliente: " + nombre + " enviado al correo");

            insertar_pago(txt_id_habitacion, txt_id_cliente, jdate_fecha_entrada, jdate_fecha_salida);

        } catch (CorreoNoEnviadoException e) {
            ManejadorErrores.enviarEnlace(e);
            JOptionPane.showMessageDialog(null,
                    "No se pudo enviar el correo al cliente: " + e.getMessage(),
                    "Error de Correo", JOptionPane.ERROR_MESSAGE);

        } catch (PagoFallidoException e) {
            ManejadorErrores.errorPago(e);
            JOptionPane.showMessageDialog(null,
                    "Error al procesar el pago: " + e.getMessage(),
                    "Error de Pago", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
            JOptionPane.showMessageDialog(null,
                    "Error inesperado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_pagoActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(check_out.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(check_out.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(check_out.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(check_out.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Reserva().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_actualizar;
    private javax.swing.JButton btn_adicionar1;
    private javax.swing.JButton btn_adicionar2;
    private javax.swing.JButton btn_factura;
    private javax.swing.JButton btn_pago;
    private javax.swing.JButton btn_refrescar1;
    private javax.swing.JButton btn_reservar;
    private javax.swing.JComboBox<String> cmb_habitaciones;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdate_fecha_entrada;
    private com.toedter.calendar.JDateChooser jdate_fecha_salida;
    private javax.swing.JTable jtable_reservas;
    private javax.swing.JTextField txt_id_cliente;
    private javax.swing.JTextField txt_id_habitacion;
    private javax.swing.JTextField txt_id_reserva;
    // End of variables declaration//GEN-END:variables
}
