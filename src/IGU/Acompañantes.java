/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package IGU;

import LOGICA.Cliente1;
import LOGICA.ClienteNoExisteException;
import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.ManejadorErrores;
import PERSISTENCIA.ConexionBD;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Acompañantes extends javax.swing.JFrame {

    Clientes cl1;
    Habitaciones hab1;
    Politica pa;
    Reserva rsv1;
    String documento, nombre, apellido, telefono, parentesco;
    int id_check_in, edad;

    /**
     * Creates new form Principal
     */
    public Acompañantes() {

        initComponents();
        setDefaultCloseOperation(Acompañantes.DISPOSE_ON_CLOSE); // ✅ SOLO CIERRA JFrame2

        cargarClientesEnComboBox();
        pa = new Politica();

        // Ahora puedes llamar al método setDefaultCloseOperation de manera segura
        pa.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra la ventana Politica

        // Resto de la configuración
        pa.setVisible(true);  // Muestra la ventana Politica
        pa.setResizable(false);

        // Centrar la ventana
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (pantalla.width - pa.getSize().width) / 2;
        int y = (pantalla.height - pa.getSize().height) / 2;
        pa.setLocation(x, y);

        // Agregar acción al historial
        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
        historial_acciones.registrarAccion("Ingreso a Politica");
    }

    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("/images/coral45.png"));
        return retValue;
    }

    // Método para insertar un acompañante en la tabla Acompañantes
    public static void insertarAcompañante(String documento, int id_check_in, String nombre, String apellido, String telefono, String parentesco, int edad) {
        String sql = "INSERT INTO acompanantes (Documento, id_check_in, nombre, apellido, telefono, parentesco, edad) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, documento);
            statement.setInt(2, id_check_in);
            statement.setString(3, nombre);
            statement.setString(4, apellido);
            statement.setString(5, telefono);
            statement.setString(6, parentesco);
            statement.setInt(7, edad);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Acompañante insertado correctamente.");
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

    // Asegúrate de importar LOGICA.Cliente1
    Map<String, Cliente1> datosClientes = new HashMap<>();

    public void cargarClientesEnComboBox() {
        String sql = "SELECT Cedula, nombre, apellido FROM clientes";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            cmb_clientes.removeAllItems();
            datosClientes.clear();
            txt_cedula_cliente.setText("");
            txt_id_check_in.setText("");

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
                txt_cedula_cliente.setText(cliente.getCedula());

                // Cargar datos del Check In
                cargarDatosCheckIn(cliente.getCedula());
            }
        });
    }

    // Método auxiliar para cargar datos de reserva y nombre de habitación
    private void cargarDatosCheckIn(String cedulaCliente) {
        String sqlReserva = "SELECT id_check_in FROM check_in WHERE id_cliente = ?";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement psReserva = conn.prepareStatement(sqlReserva)) {

            psReserva.setString(1, cedulaCliente);
            try (ResultSet rsReserva = psReserva.executeQuery()) {
                if (rsReserva.next()) {
                    int id_check = rsReserva.getInt("id_check_in");

                    txt_id_check_in.setText(String.valueOf(id_check));

                } else {
                    txt_id_check_in.setText("No encontrado");
                }
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    private void buscarClientePorCedula(String cedula) throws ClienteNoExisteException, SQLException {
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
                    txt_id_check_in.setText(""); // Limpiar antes de buscar

                    // Cargar datos del Check In
                    cargarDatosCheckIn(cliente.getCedula());

                } else {
                    throw new ClienteNoExisteException("El cliente con cédula " + cedula + " no existe en la base de datos.");
                }
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_cedula = new javax.swing.JTextField();
        txt_nombre = new javax.swing.JTextField();
        txt_apellido = new javax.swing.JTextField();
        txt_telefono = new javax.swing.JTextField();
        txt_parentesco = new javax.swing.JTextField();
        btn_ingresar = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txt_edad = new javax.swing.JTextField();
        cmb_clientes = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txt_cedula_cliente = new javax.swing.JTextField();
        txt_id_check_in = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lb_buscar = new javax.swing.JLabel();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenu1.setText("jMenu1");

        jMenu4.setText("jMenu4");

        jMenuItem3.setText("jMenuItem3");

        jScrollPane1.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Registro Clientes");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(65, 104, 163));
        jLabel1.setText("Nombre:");

        jLabel2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 104, 163));
        jLabel2.setText("Documento:");

        jLabel3.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(65, 104, 163));
        jLabel3.setText("Telefono:");

        jLabel4.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(65, 104, 163));
        jLabel4.setText("Parentesco:");

        jLabel5.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 104, 163));
        jLabel5.setText("Apellido");

        txt_cedula.setBackground(new java.awt.Color(204, 204, 204));
        txt_cedula.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_cedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cedulaActionPerformed(evt);
            }
        });

        txt_nombre.setBackground(new java.awt.Color(204, 204, 204));
        txt_nombre.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_nombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nombreActionPerformed(evt);
            }
        });

        txt_apellido.setBackground(new java.awt.Color(204, 204, 204));
        txt_apellido.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_apellido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_apellidoActionPerformed(evt);
            }
        });

        txt_telefono.setBackground(new java.awt.Color(204, 204, 204));
        txt_telefono.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        txt_parentesco.setBackground(new java.awt.Color(204, 204, 204));
        txt_parentesco.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        btn_ingresar.setBackground(new java.awt.Color(65, 104, 163));
        btn_ingresar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_ingresar.setForeground(new java.awt.Color(255, 255, 255));
        btn_ingresar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/grupo (1).png"))); // NOI18N
        btn_ingresar.setText("REGISTRAR");
        btn_ingresar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_ingresar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ingresarActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(225, 225, 191));
        jLabel9.setFont(new java.awt.Font("Georgia", 3, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(65, 104, 163));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/logo50.jpg"))); // NOI18N
        jLabel9.setText("Registro  Acompañantes");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/usuario_1.png"))); // NOI18N

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/licencia-de-conducir.png"))); // NOI18N

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/llamada-telefonica_1.png"))); // NOI18N

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/usuario_1.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(65, 104, 163));
        jLabel12.setText("Edad:");

        txt_edad.setBackground(new java.awt.Color(204, 204, 204));
        txt_edad.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));

        cmb_clientes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel13.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(65, 104, 163));
        jLabel13.setText("Cliente:");

        jLabel14.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(65, 104, 163));
        jLabel14.setText("Cedula:");

        jLabel15.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(65, 104, 163));
        jLabel15.setText("Id Check In:");

        txt_cedula_cliente.setBackground(new java.awt.Color(204, 204, 204));
        txt_cedula_cliente.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_cedula_cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_cedula_clienteActionPerformed(evt);
            }
        });

        txt_id_check_in.setBackground(new java.awt.Color(204, 204, 204));
        txt_id_check_in.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        txt_id_check_in.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_id_check_inActionPerformed(evt);
            }
        });

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/alt-de-edad (1).png"))); // NOI18N

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/control-parental.png"))); // NOI18N

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/usuario_1.png"))); // NOI18N

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/licencia-de-conducir.png"))); // NOI18N

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/hotel-bell.png"))); // NOI18N

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
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 626, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(txt_id_check_in, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel14))
                                .addGap(51, 51, 51)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmb_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txt_cedula_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lb_buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(btn_ingresar, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txt_parentesco)
                            .addComponent(txt_apellido)
                            .addComponent(txt_nombre)
                            .addComponent(txt_cedula)
                            .addComponent(txt_telefono)
                            .addComponent(txt_edad, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addGap(28, 28, 28)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txt_cedula, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cmb_clientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel14)
                                .addComponent(txt_cedula_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(txt_id_check_in, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(lb_buscar)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_telefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_parentesco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ingresar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_edad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ingresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ingresarActionPerformed
        // TODO add your handling code here:
        try {
            // Validación previa de campos vacíos antes del parseo
            if (txt_cedula.getText().trim().isEmpty()
                    || txt_id_check_in.getText().trim().isEmpty()
                    || txt_nombre.getText().trim().isEmpty()
                    || txt_apellido.getText().trim().isEmpty()
                    || txt_telefono.getText().trim().isEmpty()
                    || txt_parentesco.getText().trim().isEmpty()
                    || txt_edad.getText().trim().isEmpty()) {

                throw new NullPointerException("Todos los campos deben estar completos.");
            }

            // Parseo seguro después de validaciones
            String documento = txt_cedula.getText().trim();
            int id_check_in = Integer.parseInt(txt_id_check_in.getText().trim());
            String nombre = txt_nombre.getText().trim();
            String apellido = txt_apellido.getText().trim();
            String telefono = txt_telefono.getText().trim();
            String parentesco = txt_parentesco.getText().trim();
            int edad = Integer.parseInt(txt_edad.getText().trim());

            // Registro del acompañante
            insertarAcompañante(documento, id_check_in, nombre, apellido, telefono, parentesco, edad);

            // Agregar acción al historial
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Registro de Acompañante: " + nombre + " " + apellido);

        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e); // Maneja campos vacíos
        } catch (NumberFormatException e) {
            ManejadorErrores.conversion(e);
        }


    }//GEN-LAST:event_btn_ingresarActionPerformed

    private void txt_apellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_apellidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_apellidoActionPerformed

    private void txt_nombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nombreActionPerformed

    private void txt_cedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cedulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cedulaActionPerformed

    private void txt_cedula_clienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_cedula_clienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_cedula_clienteActionPerformed

    private void txt_id_check_inActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_id_check_inActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_id_check_inActionPerformed

    private void lb_buscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_buscarMouseClicked
        // TODO add your handling code here:
        try {
            String cedulaIngresada = txt_cedula_cliente.getText().trim();

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
            java.util.logging.Logger.getLogger(Acompañantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Acompañantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Acompañantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Acompañantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Acompañantes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_ingresar;
    private javax.swing.JComboBox<String> cmb_clientes;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lb_buscar;
    private javax.swing.JTextField txt_apellido;
    private javax.swing.JTextField txt_cedula;
    private javax.swing.JTextField txt_cedula_cliente;
    private javax.swing.JTextField txt_edad;
    private javax.swing.JTextField txt_id_check_in;
    private javax.swing.JTextField txt_nombre;
    private javax.swing.JTextField txt_parentesco;
    private javax.swing.JTextField txt_telefono;
    // End of variables declaration//GEN-END:variables
}
