package IGU;

import PERSISTENCIA.ConexionBD;

import LOGICA.Cliente1;
import LOGICA.ClienteNoExisteException;
import LOGICA.CodigoError;
import LOGICA.HistorialManager;
import LOGICA.HistorialManagerSingleton;
import LOGICA.ManejadorErrores;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 *
 * @author elian
 */
public class check_in extends javax.swing.JFrame {

    Acompañantes acmp;

    public check_in() {
        initComponents();

        cmb_clientes.setBorder(null);

        setDefaultCloseOperation(check_in.DISPOSE_ON_CLOSE); //  SOLO CIERRA JFrame2

        cargarClientesEnComboBox();
        inicializarListeners();

        int nuevoId = check_in.obtenerSiguienteIdCheck();
        txt_id_check_in.setText(String.valueOf(nuevoId)); // 

        int nuevaLlave = check_in.obtenerSiguienteLlave();
        txt_llave.setText(String.valueOf(nuevaLlave));

        txt_acompañantes.setEnabled(false);
        txt_solicitudes.setEnabled(false);
    }

    // Asegúrate de importar LOGICA.Cliente1
    Map<String, Cliente1> datosClientes = new HashMap<>();

    public void cargarClientesEnComboBox() {
        String sql = "SELECT Cedula, nombre, apellido FROM clientes";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            cmb_clientes.removeAllItems();
            datosClientes.clear();
            txt_id_cliente_chk.setText("");
            txt_id_reserva.setText("");
            txt_habitacion.setText("");

            while (rs.next()) {
                String cedula = rs.getString("Cedula");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");

                Cliente1 cliente = new Cliente1(cedula, nombre, apellido);
                cmb_clientes.addItem(cliente.getNombreCompleto());
                datosClientes.put(cliente.getNombreCompleto(), cliente);
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        // Listener para actualizar campos cuando seleccionas un cliente
        cmb_clientes.addActionListener(e -> {
            String seleccionado = (String) cmb_clientes.getSelectedItem();
            if (seleccionado != null && datosClientes.containsKey(seleccionado)) {
                Cliente1 cliente = datosClientes.get(seleccionado);
                txt_id_cliente_chk.setText(cliente.getCedula());

                // Cargar datos de la reserva y la habitación
                cargarDatosReservaYHabitacion(cliente.getCedula());

                // Cargar dias de estancia
                mostrarDiasEstancia(cliente.getCedula());
            }
        });
    }

    public void buscarClientePorCedula(String cedula) throws ClienteNoExisteException, SQLException {
        String sql = "SELECT Cedula, nombre, apellido FROM clientes WHERE Cedula = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cedula);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");

                    // Crear cliente
                    Cliente1 cliente = new Cliente1(cedula, nombre, apellido);

                    // Mostrar datos
                    cmb_clientes.setSelectedItem(cliente.getNombreCompleto());
                    datosClientes.put(cliente.getNombreCompleto(), cliente);

                    // Cargar datos de la reserva y la habitación
                    cargarDatosReservaYHabitacion(cliente.getCedula());

                    // Cargar dias de estancia
                    mostrarDiasEstancia(cliente.getCedula());
                } else {

                    throw new ClienteNoExisteException("El cliente con cédula " + cedula + " no existe en la base de datos.");

                }

            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

// Método auxiliar para cargar datos de reserva y nombre de habitación
    private void cargarDatosReservaYHabitacion(String cedulaCliente) {
        String sqlReserva = "SELECT id_reserva, id_habitacion FROM reservas WHERE id_cliente = ?";
        String sqlHabitacion = "SELECT nombre_habitacion FROM habitaciones WHERE id_habitacion = ?";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement psReserva = conn.prepareStatement(sqlReserva)) {

            psReserva.setString(1, cedulaCliente);
            try (ResultSet rsReserva = psReserva.executeQuery()) {
                if (rsReserva.next()) {
                    int idReserva = rsReserva.getInt("id_reserva");
                    int idHabitacion = rsReserva.getInt("id_habitacion");

                    txt_id_reserva.setText(String.valueOf(idReserva));

                    // Ahora con el idHabitacion obtenemos el nombre
                    try (PreparedStatement psHabitacion = conn.prepareStatement(sqlHabitacion)) {
                        psHabitacion.setInt(1, idHabitacion);
                        try (ResultSet rsHabitacion = psHabitacion.executeQuery()) {
                            if (rsHabitacion.next()) {
                                String nombreHabitacion = rsHabitacion.getString("nombre_habitacion");
                                txt_habitacion.setText(nombreHabitacion);
                            } else {
                                txt_habitacion.setText("No encontrado");
                            }
                        }
                    }
                } else {
                    txt_id_reserva.setText("No encontrado");
                    txt_habitacion.setText("No encontrado");
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
        chk_acompañantes.addActionListener(e -> HabilitarEscritura());
        chk_solicitudes.addActionListener(e -> HabilitarEscritura());

    }

// Este método actualiza el JTextArea según las casillas marcadas
    public void HabilitarEscritura() {

        if (chk_acompañantes.isSelected()) {
            txt_acompañantes.setEnabled(true);
        } else {
            txt_acompañantes.setEnabled(false);
            txt_acompañantes.setText("");
        }

        if (chk_solicitudes.isSelected()) {
            txt_solicitudes.setEnabled(true);
        } else {
            txt_solicitudes.setEnabled(false);
            txt_solicitudes.setText("");
        }

    }

    public static void insertarCheckIn(int id_check_in, String id_cliente, int id_reserva, int llave, String habitacion, int dias_estancia, int numero_acompanantes, String servicios_adicionales) {
        String sql = "INSERT INTO check_in (id_check_in, id_cliente, id_reserva, llave, habitacion, dias_estancia, numero_acompanantes, servicios_adicionales) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id_check_in);
            statement.setString(2, id_cliente);
            statement.setInt(3, id_reserva);
            statement.setInt(4, llave);
            statement.setString(5, habitacion);
            statement.setInt(6, dias_estancia);
            statement.setInt(7, numero_acompanantes);
            statement.setString(8, servicios_adicionales);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Check-In registrado correctamente.");

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
        txt_id_reserva.setText("");
        txt_dias.setText("");
        txt_acompañantes.setText("");
        txt_solicitudes.setText("");
        chk_acompañantes.setSelected(false);
        chk_solicitudes.setSelected(false);
        txt_habitacion.setText("");
        txt_llave.setText("");

        // Obtener el siguiente ID y mostrarlo en el campo
        int siguienteId = obtenerSiguienteIdCheck(); // este método ya lo tienes
        int siguienteLlave = obtenerSiguienteLlave();
        txt_id_check_in.setText(String.valueOf(siguienteId)); // 
        txt_llave.setText(String.valueOf(siguienteLlave));

    }

    public static int obtenerSiguienteIdCheck() {
        int siguienteId = 1; // Valor por defecto si no hay registros

        String sql = "SELECT MAX(id_check_in) FROM check_in"; // Consulta para obtener el último id_reserva

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                siguienteId = rs.getInt(1) + 1; // Sumamos 1 al último id

            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return siguienteId;
    }

    public static int obtenerSiguienteLlave() {
        int siguienteLlave = 1001; // Valor por defecto si no hay registros

        String sql = "SELECT MAX(llave) FROM check_in"; // Consulta para obtener el último id_reserva

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int maxLlave = rs.getInt(1);
                if (!rs.wasNull()) {
                    siguienteLlave = maxLlave + 1;
                }
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        return siguienteLlave;
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
        btn_adicionar = new javax.swing.JButton();
        txt_id_cliente_chk = new javax.swing.JTextField();
        txt_habitacion = new javax.swing.JTextField();
        chk_acompañantes = new javax.swing.JCheckBox();
        txt_id_reserva = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmb_clientes = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txt_dias = new javax.swing.JTextField();
        btn_refrescar = new javax.swing.JButton();
        chk_solicitudes = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_solicitudes = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        txt_llave = new javax.swing.JTextField();
        txt_acompañantes = new javax.swing.JTextField();
        btn_acompañantes = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txt_id_check_in = new javax.swing.JTextField();
        lb_buscar = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(65, 104, 163));

        jLabel1.setFont(new java.awt.Font("Georgia", 3, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/coral (2).png"))); // NOI18N
        jLabel1.setText("CHECK IN");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel2.setText("Habitación");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 162, 78, -1));

        jLabel3.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel3.setText("Id_Cliente:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 81, -1, -1));

        jLabel4.setFont(new java.awt.Font("Georgia", 3, 14)); // NOI18N
        jLabel4.setText("Cliente:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 39, 70, -1));

        btn_adicionar.setBackground(new java.awt.Color(65, 104, 163));
        btn_adicionar.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_adicionar.setForeground(new java.awt.Color(255, 255, 255));
        btn_adicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/mas-simbolo-negro.png"))); // NOI18N
        btn_adicionar.setText("Adicionar");
        btn_adicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_adicionarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_adicionar, new org.netbeans.lib.awtextra.AbsoluteConstraints(101, 283, 138, 42));

        txt_id_cliente_chk.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_cliente_chk.setPreferredSize(new java.awt.Dimension(65, 20));
        jPanel1.add(txt_id_cliente_chk, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 78, 120, -1));

        txt_habitacion.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_habitacion.setPreferredSize(new java.awt.Dimension(65, 20));
        jPanel1.add(txt_habitacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 159, 120, -1));

        chk_acompañantes.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        chk_acompañantes.setText("Acompañantes:");
        chk_acompañantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_acompañantesActionPerformed(evt);
            }
        });
        jPanel1.add(chk_acompañantes, new org.netbeans.lib.awtextra.AbsoluteConstraints(373, 76, -1, -1));

        txt_id_reserva.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_reserva.setPreferredSize(new java.awt.Dimension(65, 20));
        jPanel1.add(txt_id_reserva, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 120, 120, -1));

        jLabel5.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel5.setText("Id_Reserva:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 123, -1, -1));
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(437, 196, 37, -1));

        cmb_clientes.setBorder(null);
        cmb_clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_clientesActionPerformed(evt);
            }
        });
        jPanel1.add(cmb_clientes, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 37, 140, -1));

        jLabel8.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel8.setText("Dias Estancia:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, -1, -1));

        txt_dias.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_dias.setPreferredSize(new java.awt.Dimension(65, 20));
        txt_dias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_diasActionPerformed(evt);
            }
        });
        jPanel1.add(txt_dias, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 197, 71, -1));

        btn_refrescar.setBackground(new java.awt.Color(65, 104, 163));
        btn_refrescar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_refrescar.setForeground(new java.awt.Color(255, 255, 255));
        btn_refrescar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/actualizar-flecha (2).png"))); // NOI18N
        btn_refrescar.setText("Refrescar");
        btn_refrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refrescarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_refrescar, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 280, 153, 42));

        chk_solicitudes.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        chk_solicitudes.setText("Solicitudes");
        chk_solicitudes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chk_solicitudesActionPerformed(evt);
            }
        });
        jPanel1.add(chk_solicitudes, new org.netbeans.lib.awtextra.AbsoluteConstraints(373, 120, -1, -1));

        txt_solicitudes.setColumns(20);
        txt_solicitudes.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        txt_solicitudes.setRows(5);
        jScrollPane2.setViewportView(txt_solicitudes);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(496, 120, 220, 110));

        jLabel9.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel9.setText("Llave:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 239, -1, -1));

        txt_llave.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_llave.setPreferredSize(new java.awt.Dimension(65, 20));
        txt_llave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_llaveActionPerformed(evt);
            }
        });
        jPanel1.add(txt_llave, new org.netbeans.lib.awtextra.AbsoluteConstraints(161, 235, 71, -1));

        txt_acompañantes.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_acompañantes.setPreferredSize(new java.awt.Dimension(65, 20));
        txt_acompañantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_acompañantesActionPerformed(evt);
            }
        });
        jPanel1.add(txt_acompañantes, new org.netbeans.lib.awtextra.AbsoluteConstraints(496, 79, 71, -1));

        btn_acompañantes.setBackground(new java.awt.Color(65, 104, 163));
        btn_acompañantes.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_acompañantes.setForeground(new java.awt.Color(255, 255, 255));
        btn_acompañantes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/mas-simbolo-negro.png"))); // NOI18N
        btn_acompañantes.setText("Agregar");
        btn_acompañantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_acompañantesActionPerformed(evt);
            }
        });
        jPanel1.add(btn_acompañantes, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 60, -1, 40));

        jLabel10.setFont(new java.awt.Font("Georgia", 3, 12)); // NOI18N
        jLabel10.setText("Id Check In:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(379, 41, -1, -1));

        txt_id_check_in.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_check_in.setPreferredSize(new java.awt.Dimension(65, 20));
        txt_id_check_in.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_id_check_inActionPerformed(evt);
            }
        });
        jPanel1.add(txt_id_check_in, new org.netbeans.lib.awtextra.AbsoluteConstraints(496, 38, 71, -1));

        lb_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/magnifying-glass.png"))); // NOI18N
        lb_buscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_buscarMouseClicked(evt);
            }
        });
        jPanel1.add(lb_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 80, 30, -1));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_acompañantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_acompañantesActionPerformed


    }//GEN-LAST:event_chk_acompañantesActionPerformed

    private void btn_adicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_adicionarActionPerformed
        // TODO add your handling code here:
        try {
            // Validaciones de campos vacíos antes del parseo
            if (txt_id_cliente_chk.getText().trim().isEmpty()
                    || txt_id_reserva.getText().trim().isEmpty()
                    || txt_habitacion.getText().trim().isEmpty()
                    || txt_dias.getText().trim().isEmpty()
                    || txt_llave.getText().trim().isEmpty()
                    || txt_id_check_in.getText().trim().isEmpty()
                    || txt_acompañantes.getText().trim().isEmpty()
                    || txt_solicitudes.getText().trim().isEmpty()) {

                throw new NullPointerException("Todos los campos deben estar completos.");
            }

            // Luego de validar, se puede parsear sin riesgo
            String id_cliente = txt_id_cliente_chk.getText().trim();
            int id_reserva = Integer.parseInt(txt_id_reserva.getText().trim());
            String habitacion = txt_habitacion.getText().trim();
            int dias_estancia = Integer.parseInt(txt_dias.getText().trim());
            int llave = Integer.parseInt(txt_llave.getText().trim());
            int id_check_in = Integer.parseInt(txt_id_check_in.getText().trim());
            int numero_acomp = Integer.parseInt(txt_acompañantes.getText().trim());
            String solicitudes = txt_solicitudes.getText().trim();

            // Reemplazar saltos de línea por comas
            String text_ConComas = solicitudes.replaceAll("\\r?\\n", ", ");

            // Insertar Check-In
            insertarCheckIn(id_check_in, id_cliente, id_reserva, llave, habitacion, dias_estancia, numero_acomp, text_ConComas);

            // Historial de acción
            String nombre = (String) cmb_clientes.getSelectedItem();
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Ingreso del cliente: " + nombre + " al hotel");

        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e); // Manejador de errores personalizado
        } catch (NumberFormatException e) {
            ManejadorErrores.conversion(e);
        }

    }//GEN-LAST:event_btn_adicionarActionPerformed

    private void btn_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refrescarActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_btn_refrescarActionPerformed

    private void cmb_clientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmb_clientesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmb_clientesActionPerformed

    private void txt_diasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_diasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_diasActionPerformed

    private void chk_solicitudesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chk_solicitudesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chk_solicitudesActionPerformed

    private void txt_llaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_llaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_llaveActionPerformed

    private void txt_acompañantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_acompañantesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_acompañantesActionPerformed

    private void txt_id_check_inActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_id_check_inActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_check_inActionPerformed

    private void btn_acompañantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_acompañantesActionPerformed
        // TODO add your handling code here:

        acmp = new Acompañantes();
        acmp.setVisible(true);

        acmp.setResizable(false);
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        // Calcular la posición para centrar el JFrame
        int x = (pantalla.width - acmp.getSize().width) / 2;
        int y = (pantalla.height - acmp.getSize().height) / 2;

        // Posicionar la ventana en el centro de la pantalla
        acmp.setLocation(x, y);
    }//GEN-LAST:event_btn_acompañantesActionPerformed

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
            java.util.logging.Logger.getLogger(check_in.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(check_in.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(check_in.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(check_in.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new check_in().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_acompañantes;
    private javax.swing.JButton btn_adicionar;
    private javax.swing.JButton btn_refrescar;
    private javax.swing.JCheckBox chk_acompañantes;
    private javax.swing.JCheckBox chk_solicitudes;
    private javax.swing.JComboBox<String> cmb_clientes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_buscar;
    private javax.swing.JTextField txt_acompañantes;
    private javax.swing.JTextField txt_dias;
    private javax.swing.JTextField txt_habitacion;
    private javax.swing.JTextField txt_id_check_in;
    private javax.swing.JTextField txt_id_cliente_chk;
    private javax.swing.JTextField txt_id_reserva;
    private javax.swing.JTextField txt_llave;
    private javax.swing.JTextArea txt_solicitudes;
    // End of variables declaration//GEN-END:variables
}
