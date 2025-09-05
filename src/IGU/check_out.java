package IGU;

import LOGICA.Check_Out;
import LOGICA.CorreoPago;
import PERSISTENCIA.ConexionBD;
import LOGICA.Cliente;
import LOGICA.ClienteNoExisteException;
import LOGICA.HistorialManager;
import LOGICA.HistorialManagerSingleton;
import LOGICA.Reservas;
import static LOGICA.enviarCorreoConAdjunto.enviarCorreoConAdjunto;
import LOGICA.CorreoNoEnviadoException;
import LOGICA.ManejadorErrores;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import java.util.Locale;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.toedter.calendar.JDateChooser;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class check_out extends javax.swing.JFrame {
    
    public check_out() {
        
        initComponents();
        
        cmb_clientes.setBorder(null);
        
        setDefaultCloseOperation(check_out.DISPOSE_ON_CLOSE); //  SOLO CIERRA JFrame2

        cargarClientesEnComboBox();
        inicializarListeners();
        int nuevoId = check_out.obtenerSiguienteIdCheck();
        txt_id_check.setText(String.valueOf(nuevoId)); // 
    }
    
    private String serviciosSeleccionados = "";
    private int valorTotalCalculado = 0;

    // Asegúrate de importar IGU.Cliente
    Map<String, Cliente> datosClientes = new HashMap<>();
    
    private void cargarClientesEnComboBox() {
        String sql = "SELECT Cedula, nombre, apellido, correo_electronico FROM clientes";
        
        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            cmb_clientes.removeAllItems();
            datosClientes.clear();
            txt_id_cliente_chk.setText("");
            txt_correo.setText("");
            
            while (rs.next()) {
                String cedula = rs.getString("Cedula");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                String correo = rs.getString("correo_electronico");
                
                Cliente cliente = new Cliente(cedula, nombre, apellido, correo);
                cmb_clientes.addItem(cliente.getNombreCompleto());
                datosClientes.put(cliente.getNombreCompleto(), cliente);
            }
            
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        // Listener para actualizar campos al seleccionar un cliente
        cmb_clientes.addActionListener(e -> {
            String seleccionado = (String) cmb_clientes.getSelectedItem();
            if (seleccionado != null && datosClientes.containsKey(seleccionado)) {
                Cliente cliente = datosClientes.get(seleccionado);
                txt_id_cliente_chk.setText(cliente.getCedula());
                txt_correo.setText(cliente.getCorreo());

                // NUEVO: Mostrar días de estancia desde tabla reservas
                mostrarDiasEstancia(cliente.getCedula());
            }
        });
    }
    
    public static void insertar_pago(JTextField txt_id_cliente_chk,
            JTextField txt_Valor_total) {
        
        String idCliente = txt_id_cliente_chk.getText();
        int idPago = Reserva.obtenerSiguienteIdPago();
        int valor_total = Integer.parseInt(txt_Valor_total.getText());

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
            Check_Out.insertarPago(idPago, idCliente, valor_total, estado);

            // Registrar acción en el historial
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Registro de Pago (Check_Out): " + idPago + " con estado: " + estado);
        } else {
            JOptionPane.showMessageDialog(null, "No se registró el pago porque no se seleccionó un estado.");
        }
    }
    
    private void buscarClientePorCedula(String cedula) throws ClienteNoExisteException, SQLException  {
        String sql = "SELECT Cedula, nombre, apellido, correo_electronico FROM clientes WHERE Cedula = ?";
        
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cedula);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String correo = rs.getString("correo_electronico");

                    // Crear cliente
                    Cliente cliente = new Cliente(cedula, nombre, apellido, correo);

                    // Mostrar datos
                    cmb_clientes.setSelectedItem(cliente.getNombreCompleto());
                    datosClientes.put(cliente.getNombreCompleto(), cliente);

                    // NUEVO: Mostrar días de estancia desde tabla reservas
                    mostrarDiasEstancia(cliente.getCedula());
                    
                } else {
                    throw new ClienteNoExisteException("El cliente con cédula " + cedula + " no existe en la base de datos.");
                }
            }
            
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }
    
    private void mostrarDiasEstancia(String cedulaCliente) {
        String sql = "SELECT dias_estancia FROM reservas WHERE id_cliente = ?";
        
        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setString(1, cedulaCliente);
            ResultSet rs = statement.executeQuery(); // Solo UNA VEZ

            if (rs.next()) {
                int dias = rs.getInt("dias_estancia");
                txt_dias.setText(String.valueOf(dias));
            } else {
                txt_dias.setText("0");
            }
            
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }
    
    private void inicializarListeners() {
        chk_minibar.addActionListener(e -> actualizarServicios());
        chk_restaurante.addActionListener(e -> actualizarServicios());
        chk_spa.addActionListener(e -> actualizarServicios());
        chk_habitacion.addActionListener(e -> actualizarServicios());
    }

// Este método actualiza el JTextArea según las casillas marcadas
    private void actualizarServicios() {
        StringBuilder servicios = new StringBuilder();
        
        if (chk_minibar.isSelected()) {
            servicios.append("Minibar activado\n");
        }
        
        if (chk_restaurante.isSelected()) {
            servicios.append("Restaurante activado\n");
        }
        
        if (chk_spa.isSelected()) {
            servicios.append("Spa activado\n");
        }
        
        if (chk_habitacion.isSelected()) {
            servicios.append("Daño a la Habitacion activado\n");
        }
        
        txt_Servicios.setText(servicios.toString());
    }
    
    public static void insertarCheckOut(int idCheck, String idCliente, String correo, String serviciosAdicionales, double valorTotal, int dias) {
        String sql = "INSERT INTO check_out (id_check_out, id_cliente, correo, servicios_adicionales, valor_total, dias_estancia) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            
            statement.setInt(1, idCheck);
            statement.setString(2, idCliente);
            statement.setString(3, correo);
            statement.setString(4, serviciosAdicionales);
            statement.setDouble(5, valorTotal);
            statement.setInt(6, dias);
            
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Check-Out registrado correctamente.");
            
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
    
    public void limpiarCampos() {
        cmb_clientes.setSelectedIndex(0); // Reinicia el ComboBox
        txt_id_cliente_chk.setText("");
        txt_correo.setText("");
        txt_dias.setText("");
        txt_Servicios.setText("");
        chk_minibar.setSelected(false);
        chk_restaurante.setSelected(false);
        chk_spa.setSelected(false);
        chk_habitacion.setSelected(false);
        txt_Valor_total.setText("");
        txt_id_check.setText("");

        // Obtener el siguiente ID y mostrarlo en el campo
        int siguienteId = obtenerSiguienteIdCheck(); // este método ya lo tienes
        txt_id_check.setText(String.valueOf(siguienteId)); // lo asignas aquí

    }
    
    private void calcularServiciosSeleccionados() {
        int total = 0;

        // Precios por servicio
        int precioMinibar = 15000;
        int precioRestaurante = 25000;
        int precioSpa = 40000;
        int precioDaño = 80000;

        // Construir string de servicios seleccionados
        StringBuilder servicios = new StringBuilder();
        
        if (chk_minibar.isSelected()) {
            total += precioMinibar;
            servicios.append("Minibar, ");
        }
        if (chk_restaurante.isSelected()) {
            total += precioRestaurante;
            servicios.append("Restaurante, ");
        }
        if (chk_spa.isSelected()) {
            total += precioSpa;
            servicios.append("Spa, ");
        }
        if (chk_habitacion.isSelected()) {
            total += precioDaño;
            servicios.append("Daño Habitación, ");
        }

        // Mostrar total
        txt_Valor_total.setText(String.valueOf(total));

        // Guardar string de servicios para usarlo al insertar
        serviciosSeleccionados = servicios.toString();
        valorTotalCalculado = total;
    }
    
    public static int obtenerSiguienteIdCheck() {
        int siguienteId = 1; // Valor por defecto si no hay registros

        String sql = "SELECT MAX(id_check_out) FROM check_out"; // Consulta para obtener el último id_reserva

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                siguienteId = rs.getInt(1) + 1; // Sumamos 1 al último id

            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
        
        return siguienteId;
    }
    
    public void generarFacturaPDF() {
        Document documento = new Document();
        
        try {
            String nombreCliente = cmb_clientes.getSelectedItem().toString();
            String idCliente = txt_id_cliente_chk.getText();
            String correo = txt_correo.getText().trim();
            String idCheck = txt_id_check.getText();
            String diasEstancia = txt_dias.getText();
            String valorTotal = txt_Valor_total.getText();
            
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String nombreArchivo = "factura_check_out_" + idCliente + "_" + fechaActual + ".pdf";
            String ruta = "C:\\Users\\elian\\Desktop\\universidad proyectos\\HOTEL2.1\\HOTEL2.0\\Facturas_check_out" + nombreArchivo;
            if (correo.isEmpty() || !correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                JOptionPane.showMessageDialog(this, "Correo inválido. Verifica la dirección.");
                return;
            }
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            // Logo y nombre del hotel
            Image logo = Image.getInstance(getClass().getResource("/com/images/coral.png"));
            logo.scaleAbsolute(60, 60);
            
            PdfPTable tablaEncabezado = new PdfPTable(2);
            tablaEncabezado.setWidthPercentage(100);
            
            PdfPCell celdaNombre = new PdfPCell(new Phrase("Resort Bahía Coral", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK)));
            celdaNombre.setBorder(Rectangle.NO_BORDER);
            celdaNombre.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaNombre.setHorizontalAlignment(Element.ALIGN_LEFT);
            
            PdfPCell celdaLogo = new PdfPCell(logo);
            celdaLogo.setBorder(Rectangle.NO_BORDER);
            celdaLogo.setHorizontalAlignment(Element.ALIGN_RIGHT);
            
            tablaEncabezado.addCell(celdaNombre);
            tablaEncabezado.addCell(celdaLogo);
            
            documento.add(tablaEncabezado);
            
            documento.add(Chunk.NEWLINE);

            // TÍTULO GRANDE (Nombre de la Factura)
            Paragraph subtitulo = new Paragraph("N°Ticket:/" + idCliente + "/" + fechaActual + "/", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_RIGHT);
            documento.add(subtitulo);
            
            documento.add(Chunk.NEWLINE);

            // TÍTULO GRANDE (Nombre de la Factura)
            Paragraph titulo = new Paragraph("Ticket de Check Out", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY));
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            documento.add(Chunk.NEWLINE);

            // Tabla de información del cliente (ahora bonita)
            PdfPTable tablaCliente = new PdfPTable(2);
            tablaCliente.setWidthPercentage(100);
            tablaCliente.setSpacingBefore(10);

            // Fila de título que ocupa las 2 columnas
            PdfPCell celdaTitulo = new PdfPCell(new Phrase("Datos del Cliente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));
            celdaTitulo.setBackgroundColor(new BaseColor(30, 144, 255)); // Un azul bonito (puedes cambiar el color)
            celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTitulo.setPadding(12);
            celdaTitulo.setColspan(2); // MUY IMPORTANTE para que abarque las 2 columnas
            tablaCliente.addCell(celdaTitulo);

            // Encabezado
            PdfPCell celdaDato = new PdfPCell(new Phrase("Dato", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
            celdaDato.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaDato.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaDato.setPadding(10);
            
            PdfPCell celdaInformacion = new PdfPCell(new Phrase("Información", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
            celdaInformacion.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaInformacion.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaInformacion.setPadding(10);
            
            tablaCliente.addCell(celdaDato);
            tablaCliente.addCell(celdaInformacion);

            // Datos
            tablaCliente.addCell(getCeldaServicioTabla("Cliente:"));
            tablaCliente.addCell(getCeldaValorTabla(nombreCliente));
            tablaCliente.addCell(getCeldaServicioTabla("ID Cliente:"));
            tablaCliente.addCell(getCeldaValorTabla(idCliente));
            tablaCliente.addCell(getCeldaServicioTabla("Correo:"));
            tablaCliente.addCell(getCeldaValorTabla(correo));
            tablaCliente.addCell(getCeldaServicioTabla("ID Check:"));
            tablaCliente.addCell(getCeldaValorTabla(idCheck));
            tablaCliente.addCell(getCeldaServicioTabla("Días de estancia:"));
            tablaCliente.addCell(getCeldaValorTabla(diasEstancia));
            
            documento.add(tablaCliente);

            // Tabla de servicios adicionales y total
            PdfPTable tablaServicios = new PdfPTable(2);
            tablaServicios.setWidthPercentage(100);
            tablaServicios.setSpacingBefore(10);

            // Fila de título que ocupa las 2 columnas
            PdfPCell celdaTitulo1 = new PdfPCell(new Phrase("Servicios Adicionales", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE)));
            celdaTitulo1.setBackgroundColor(new BaseColor(30, 144, 255)); // Un azul bonito (puedes cambiar el color)
            celdaTitulo1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaTitulo1.setPadding(12);
            celdaTitulo1.setColspan(2); // MUY IMPORTANTE para que abarque las 2 columnas
            tablaServicios.addCell(celdaTitulo1);

            // Encabezado de tabla con color
            PdfPCell celdaServicio = new PdfPCell(new Phrase("Servicio", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
            celdaServicio.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaServicio.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaServicio.setPadding(10);
            
            PdfPCell celdaPrecio = new PdfPCell(new Phrase("Valor", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
            celdaPrecio.setBackgroundColor(BaseColor.DARK_GRAY);
            celdaPrecio.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaPrecio.setPadding(10);
            
            tablaServicios.addCell(celdaServicio);
            tablaServicios.addCell(celdaPrecio);

            // Agregar servicios seleccionados
            boolean hayServicios = false;
            
            if (chk_minibar.isSelected()) {
                tablaServicios.addCell(getCeldaServicioTabla("Minibar"));
                tablaServicios.addCell(getCeldaValorTabla("$15000")); // Puedes poner el valor real
                hayServicios = true;
            }
            if (chk_restaurante.isSelected()) {
                tablaServicios.addCell(getCeldaServicioTabla("Restaurante"));
                tablaServicios.addCell(getCeldaValorTabla("$25000"));
                hayServicios = true;
            }
            if (chk_spa.isSelected()) {
                tablaServicios.addCell(getCeldaServicioTabla("Spa"));
                tablaServicios.addCell(getCeldaValorTabla("$40000"));
                hayServicios = true;
            }
            if (chk_habitacion.isSelected()) {
                tablaServicios.addCell(getCeldaServicioTabla("Daño en habitación"));
                tablaServicios.addCell(getCeldaValorTabla("$80000"));
                hayServicios = true;
            }
            if (!hayServicios) {
                PdfPCell celdaNoServicios = new PdfPCell(new Phrase("No se utilizaron servicios adicionales."));
                celdaNoServicios.setColspan(2);
                celdaNoServicios.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaServicios.addCell(celdaNoServicios);
            }

            // Agregar total
            PdfPCell celdaTotal = new PdfPCell(new Phrase("TOTAL", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            celdaTotal.setColspan(1);
            celdaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaTotal.setPaddingTop(10);
            celdaTotal.setBorderWidthTop(2f);
            
            PdfPCell celdaValorTotal = new PdfPCell(new Phrase("$" + valorTotal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            celdaValorTotal.setHorizontalAlignment(Element.ALIGN_CENTER);
            celdaValorTotal.setPaddingTop(10);
            celdaValorTotal.setBorderWidthTop(2f);
            
            tablaServicios.addCell(celdaTotal);
            tablaServicios.addCell(celdaValorTotal);
            
            documento.add(tablaServicios);
            
            documento.close();
            String asunto = "Ticket Check-Out Hotel";
            String cuerpo = "Adjunto encontrará su ticket correspondiente a su estadía. ¡Gracias por elegirnos!";
            enviarCorreoConAdjunto(correo, asunto, cuerpo, ruta);
            
            JOptionPane.showMessageDialog(null, "Ticket generada con éxito.");
            JOptionPane.showMessageDialog(null, "Ticket guardada en:\n" + ruta);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        }
        
    }

// Métodos auxiliares
    private PdfPCell getCeldaEtiqueta(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        return celda;
    }
    
    private PdfPCell getCeldaValor(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        return celda;
    }
    
    private PdfPCell getCeldaServicioTabla(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        return celda;
    }
    
    private PdfPCell getCeldaValorTabla(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        return celda;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_pago = new javax.swing.JButton();
        txt_Valor_total = new javax.swing.JTextField();
        txt_id_cliente_chk = new javax.swing.JTextField();
        txt_id_check = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        txt_Servicios = new javax.swing.JTextArea();
        chk_restaurante = new javax.swing.JCheckBox();
        chk_minibar = new javax.swing.JCheckBox();
        txt_correo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        chk_spa = new javax.swing.JCheckBox();
        chk_habitacion = new javax.swing.JCheckBox();
        cmb_clientes = new javax.swing.JComboBox<>();
        btn_calcular = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txt_dias = new javax.swing.JTextField();
        btn_factura = new javax.swing.JButton();
        btn_adicionar1 = new javax.swing.JButton();
        btn_refrescar1 = new javax.swing.JButton();
        lb_buscar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(65, 104, 163));

        jLabel1.setFont(new java.awt.Font("Georgia", 3, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/coral (2).png"))); // NOI18N
        jLabel1.setText("CHECK OUT");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel2.setText("Id_Check:");

        jLabel3.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel3.setText("Id_Cliente:");

        jLabel4.setFont(new java.awt.Font("Georgia", 3, 14)); // NOI18N
        jLabel4.setText("Cliente:");

        jLabel6.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        jLabel6.setText("Valor Total: ");

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

        txt_Valor_total.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_Valor_total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_Valor_totalActionPerformed(evt);
            }
        });

        txt_id_cliente_chk.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_cliente_chk.setPreferredSize(new java.awt.Dimension(65, 20));
        txt_id_cliente_chk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_id_cliente_chkActionPerformed(evt);
            }
        });

        txt_id_check.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_check.setPreferredSize(new java.awt.Dimension(65, 20));

        txt_Servicios.setColumns(20);
        txt_Servicios.setRows(5);
        jScrollPane1.setViewportView(txt_Servicios);

        chk_restaurante.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        chk_restaurante.setText("Restaurante");

        chk_minibar.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        chk_minibar.setText("Minibar");
        chk_minibar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_minibarActionPerformed(evt);
            }
        });

        txt_correo.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_correo.setPreferredSize(new java.awt.Dimension(65, 20));

        jLabel5.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel5.setText("Correo:");

        chk_spa.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        chk_spa.setText("Spa");

        chk_habitacion.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        chk_habitacion.setText("Daño Habitacion");

        cmb_clientes.setBorder(null);
        cmb_clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_clientesActionPerformed(evt);
            }
        });

        btn_calcular.setBackground(new java.awt.Color(65, 104, 163));
        btn_calcular.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_calcular.setForeground(new java.awt.Color(255, 255, 255));
        btn_calcular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/calculadora.png"))); // NOI18N
        btn_calcular.setText("Calcular");
        btn_calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_calcularActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel8.setText("Dias Estancia:");

        txt_dias.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_dias.setPreferredSize(new java.awt.Dimension(65, 20));
        txt_dias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_diasActionPerformed(evt);
            }
        });

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

        lb_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/magnifying-glass.png"))); // NOI18N
        lb_buscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_buscarMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmb_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txt_id_cliente_chk, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(lb_buscar))
                            .addComponent(txt_correo, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_minibar)
                            .addComponent(chk_restaurante)
                            .addComponent(chk_spa)
                            .addComponent(chk_habitacion))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_id_check, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_dias, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(147, 147, 147)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(txt_Valor_total, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(32, 32, 32)
                        .addComponent(btn_calcular))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(btn_adicionar1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btn_refrescar1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addComponent(btn_factura)
                        .addGap(44, 44, 44)
                        .addComponent(btn_pago, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(25, 25, 25)
                        .addComponent(jLabel3)
                        .addGap(28, 28, 28)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmb_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_id_cliente_chk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(lb_buscar)))
                        .addGap(17, 17, 17)
                        .addComponent(txt_correo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(chk_minibar)
                        .addGap(8, 8, 8)
                        .addComponent(chk_restaurante)
                        .addGap(6, 6, 6)
                        .addComponent(chk_spa)
                        .addGap(6, 6, 6)
                        .addComponent(chk_habitacion))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel2)
                        .addGap(24, 24, 24)
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txt_id_check, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txt_dias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(txt_Valor_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(btn_calcular)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(btn_adicionar1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(btn_refrescar1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_factura)
                            .addComponent(btn_pago)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel7)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_Valor_totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_Valor_totalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_Valor_totalActionPerformed

    private void chk_minibarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_minibarActionPerformed
        

    }//GEN-LAST:event_chk_minibarActionPerformed

    private void btn_calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_calcularActionPerformed
        // TODO add your handling code here:
        calcularServiciosSeleccionados();

    }//GEN-LAST:event_btn_calcularActionPerformed

    private void btn_pagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_pagoActionPerformed
        // TODO add your handling code here:
        try {
            String correo = txt_correo.getText();
            int totalCOP = Integer.parseInt(txt_Valor_total.getText());
            
            double tasaCambio = 4300.0;
            double totalUSD = totalCOP / tasaCambio;
            String montoUSD = String.format(Locale.US, "%.2f", totalUSD);

            // Enviar el correo con el enlace
            try {
                CorreoPago.enviarCorreo(correo, montoUSD);
                JOptionPane.showMessageDialog(this,
                        "Correo enviado con el enlace de pago en dólares.",
                        "Correo Enviado", JOptionPane.INFORMATION_MESSAGE);
            } catch (CorreoNoEnviadoException e) {
                ManejadorErrores.enviarEnlace(e);
                JOptionPane.showMessageDialog(this,
                        "No se pudo enviar el correo: " + e.getMessage(),
                        "Error de Correo", JOptionPane.ERROR_MESSAGE);
                return; // Salimos del flujo si no se pudo enviar el correo
            }

            // Registrar en el historial
            String nombre = (String) cmb_clientes.getSelectedItem();
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Pago del cliente: " + nombre + " enviado al correo");

            // Insertar el pago
            insertar_pago(txt_id_cliente_chk, txt_Valor_total);
            
        } catch (NumberFormatException e) {
            ManejadorErrores.conversion(e);
            JOptionPane.showMessageDialog(this,
                    "El valor total debe ser un número válido.",
                    "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            ManejadorErrores.errorDesconocido(e);
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btn_pagoActionPerformed

    private void cmb_clientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_clientesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_clientesActionPerformed

    private void txt_diasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_diasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_diasActionPerformed

    private void btn_facturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_facturaActionPerformed
        // TODO add your handling code here:
        generarFacturaPDF();
        
        String nombre = (String) cmb_clientes.getSelectedItem();

        //Agregar Accion a Historial
        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
        historial_acciones.registrarAccion("Ticket del cliente: " + nombre + " generada");

    }//GEN-LAST:event_btn_facturaActionPerformed

    private void btn_adicionar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_adicionar1ActionPerformed
        // TODO add your handling code here:
        try {
            // Validar primero que los campos no estén vacíos
            if (txt_id_check.getText().trim().isEmpty()
                    || txt_id_cliente_chk.getText().trim().isEmpty()
                    || txt_correo.getText().trim().isEmpty()
                    || serviciosSeleccionados == null || serviciosSeleccionados.trim().isEmpty()
                    || txt_dias.getText().trim().isEmpty()) {
                
                throw new NullPointerException("Campos vacíos");
            }

            // Luego parsear
            int idCheck = Integer.parseInt(txt_id_check.getText().trim());
            String idCliente = txt_id_cliente_chk.getText().trim();
            String correo = txt_correo.getText().trim();
            String servicios = serviciosSeleccionados.trim(); // desde el cálculo
            double total = valorTotalCalculado;
            int dias = Integer.parseInt(txt_dias.getText().trim());
            
            insertarCheckOut(idCheck, idCliente, correo, servicios, total, dias);
            
            String nombre = (String) cmb_clientes.getSelectedItem();

            //Agregar Acción a Historial
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Salida del cliente: " + nombre + " del hotel");
            
        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e); // Manejador de errores para campos vacíos

        } catch (NumberFormatException e) {
            ManejadorErrores.conversion(e);
        }
        

    }//GEN-LAST:event_btn_adicionar1ActionPerformed

    private void btn_refrescar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refrescar1ActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_btn_refrescar1ActionPerformed

    private void txt_id_cliente_chkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_id_cliente_chkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_cliente_chkActionPerformed

    private void lb_buscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_buscarMouseClicked
        // TODO add your handling code here:
        try {
            String cedulaIngresada = txt_id_cliente_chk.getText().trim();

            if (cedulaIngresada.isEmpty()) {
                throw new NullPointerException("Campos vacíos");

            }

            buscarClientePorCedula(cedulaIngresada);

        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e); // Manejador de errores para campos vacíos

        } catch (ClienteNoExisteException ex) {
           ManejadorErrores.clienteNoExiste(ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar el cliente en la base de datos.");
            Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, "Error SQL al buscar cliente", ex);
        }

    }//GEN-LAST:event_lb_buscarMouseClicked

    /**
     * @param args the command line arguments
     */
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
                new check_out().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_adicionar1;
    private javax.swing.JButton btn_calcular;
    private javax.swing.JButton btn_factura;
    private javax.swing.JButton btn_pago;
    private javax.swing.JButton btn_refrescar1;
    private javax.swing.JCheckBox chk_habitacion;
    private javax.swing.JCheckBox chk_minibar;
    private javax.swing.JCheckBox chk_restaurante;
    private javax.swing.JCheckBox chk_spa;
    private javax.swing.JComboBox<String> cmb_clientes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lb_buscar;
    private javax.swing.JTextArea txt_Servicios;
    private javax.swing.JTextField txt_Valor_total;
    private javax.swing.JTextField txt_correo;
    private javax.swing.JTextField txt_dias;
    private javax.swing.JTextField txt_id_check;
    private javax.swing.JTextField txt_id_cliente_chk;
    // End of variables declaration//GEN-END:variables
}
