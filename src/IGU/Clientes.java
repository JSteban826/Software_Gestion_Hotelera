/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package IGU;

import LOGICA.ClienteNoExisteException;
import LOGICA.GestionReservas;
import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.ManejadorErrores;
import static LOGICA.ManejadorErrores.accesoDenegado;
import PERSISTENCIA.ConexionBD;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;

import java.sql.SQLException;
import javax.swing.JTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Windows
 */
public class Clientes extends javax.swing.JFrame {

    /**
     * Creates new form CLientes
     */
    public Clientes() {
        initComponents();
        CentrarEncabezados(jtable_clientes);
        setDefaultCloseOperation(Clientes.DISPOSE_ON_CLOSE); // <-- AQUÍ
    }

    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("com.images/3.png"));
        return retValue;
    }

    public static void mostrarClientesEnTabla(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar()) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM clientes";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getString("cedula"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellido"),
                        resultSet.getString("correo_electronico"),
                        resultSet.getString("telefono")
                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    private void buscarClientePorCedula(String cedula) throws SQLException, ClienteNoExisteException {
        String sql = "SELECT Cedula, nombre, apellido, correo_electronico, telefono FROM clientes WHERE Cedula = ?";

        // Limpiar la tabla antes de mostrar nuevos resultados
        DefaultTableModel modelo = (DefaultTableModel) jtable_clientes.getModel();
        modelo.setRowCount(0); // Esto elimina las filas existentes

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, cedula);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // Obtener los datos
                    String ced = rs.getString("Cedula");
                    String nombre = rs.getString("nombre");
                    String apellido = rs.getString("apellido");
                    String correo = rs.getString("correo_electronico");
                    String tel = rs.getString("telefono");

                    // Agregar fila a la tabla
                    modelo.addRow(new Object[]{ced, nombre, apellido, correo, tel});
                } // Validar existencia del cliente
                GestionReservas gestion = new GestionReservas();
                if (!gestion.clienteExiste(cedula)) {
                    throw new ClienteNoExisteException("El cliente con cédula " + cedula + " no existe.");
                }

            }

        } catch (ClienteNoExisteException e) {
            ManejadorErrores.clienteNoExiste(e); // Manejador de errores para cliente inexistente

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    public static void modificarcliente(String cedula, String nombre, String apellido, String correo, String telefono) {
        // Actualizar los datos del cliente en la base de datos
        String sql = "UPDATE Clientes SET nombre = ?, apellido = ?, correo_electronico = ?, telefono = ? WHERE cedula = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            // Establecer los valores en la sentencia preparada
            statement.setString(1, nombre);
            statement.setString(2, apellido);
            statement.setString(3, correo);
            statement.setString(4, telefono);
            statement.setString(5, cedula);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Datos del Cliente " + nombre + " modificados correctamente.");
        } catch (SQLException e) {
            ManejadorErrores.errorUpdateSQL(e);
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

                    JMenuItem ModificarCliente = new JMenuItem("Modificar Datos del Cliente");

                    ModificarCliente.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent a) {
                            try {
                                JPasswordField passwordField = new JPasswordField();
                                int option = JOptionPane.showConfirmDialog(null, passwordField, "Ingrese la clave de administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                                
                                if (option == JOptionPane.OK_OPTION) {
                                    String claveIngresada = new String(passwordField.getPassword());
                                    String claveCorrecta = "admin1234";
                                    
                                    if (!claveIngresada.equals(claveCorrecta)) {
                                        throw new SecurityException("Acceso denegado: clave incorrecta.");
                                    }
                                    
                                    String cedula = (String) tabla.getValueAt(filaSeleccionada, 0);
                                    String nombre = (String) tabla.getValueAt(filaSeleccionada, 1);
                                    String apellido = (String) tabla.getValueAt(filaSeleccionada, 2);
                                    String correo = (String) tabla.getValueAt(filaSeleccionada, 3);
                                    String telefono = (String) tabla.getValueAt(filaSeleccionada, 4);
                                    
                                    String nuevoNombre = JOptionPane.showInputDialog("Nuevo nombre del cliente:", nombre);
                                    String nuevoApellido = JOptionPane.showInputDialog("Nuevo apellido del cliente:", apellido);
                                    String nuevoCorreo = JOptionPane.showInputDialog("Nuevo correo del cliente:", correo);
                                    String nuevoTelefono = JOptionPane.showInputDialog("Nuevo teléfono del cliente:", telefono);
                                    
                                    if (nuevoNombre != null && nuevoApellido != null && nuevoCorreo != null && nuevoTelefono != null) {
                                        modificarcliente(cedula, nuevoNombre, nuevoApellido, nuevoCorreo, nuevoTelefono);
                                        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                                        historial_acciones.registrarAccion("Datos del cliente con cédula: " + cedula + " modificados.");
                                    }
                                }
                            } catch (Exception e) {
                                // Llamada al método accesoDenegado para registrar cualquier excepción
                                accesoDenegado(e);
                                
                            }
                        }
                    });

                    menu.add(ModificarCliente);
                    menu.show(tabla, e.getX(), e.getY());
                }
            }
        });
    }

    public void aplicarEstilosTabla(JTable tabla, Font fuente, Color colorTexto, Color colorFondo) {
        // Crear el renderizador personalizado para centrar, cambiar fuente, color de texto y fondo
        DefaultTableCellRenderer renderCentradoConEstilos = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(fuente); // Cambiar la fuente
                setHorizontalAlignment(SwingConstants.CENTER); // Centrar el texto
                c.setForeground(colorTexto); // Cambiar color del texto
                c.setBackground(colorFondo); // Cambiar color de fondo
                return c;
            }
        };

        // Aplicar el renderizador a todas las columnas de la tabla
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderCentradoConEstilos);
        }

    }

    public static void CentrarEncabezados(JTable tabla) {
        // Configurar el renderizador para centrar los encabezados sin cambiar el renderizador predeterminado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Roboto Light", Font.ITALIC, 14)); // Cambiar fuente
        header.setForeground(Color.BLACK); // Color del texto
        header.setBackground(Color.LIGHT_GRAY); // Color de fondo

        // Modificar la alineación de los encabezados sin cambiar su renderizador original
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable_clientes = new javax.swing.JTable();
        BtnActualizar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txt_cedula_cliente = new javax.swing.JTextField();
        lb_buscar = new javax.swing.JLabel();

        setBackground(new java.awt.Color(229, 229, 197));

        jPanel1.setBackground(new java.awt.Color(65, 104, 163));

        jLabel1.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/algas-marinas.png"))); // NOI18N
        jLabel1.setText("CLIENTES INGRESADOS   ");

        jtable_clientes.setAutoCreateRowSorter(true);
        jtable_clientes.setFont(new java.awt.Font("Georgia", 0, 12)); // NOI18N
        jtable_clientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Cedula", "Nombre", "Apellido", "Correo", "Telefono"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jtable_clientes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jtable_clientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtable_clientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtable_clientes);
        if (jtable_clientes.getColumnModel().getColumnCount() > 0) {
            jtable_clientes.getColumnModel().getColumn(3).setPreferredWidth(150);
        }

        BtnActualizar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        BtnActualizar.setForeground(new java.awt.Color(65, 104, 163));
        BtnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/actualizar-flecha.png"))); // NOI18N
        BtnActualizar.setText("Actualizar");
        BtnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnActualizarActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/user.png"))); // NOI18N
        jLabel13.setText("Buscar Cliente:");

        txt_cedula_cliente.setBackground(new java.awt.Color(255, 255, 255));

        lb_buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/loupe (1).png"))); // NOI18N
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
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(288, 288, 288))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(txt_cedula_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lb_buscar)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel13)
                        .addComponent(txt_cedula_cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lb_buscar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BtnActualizar)
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnActualizarActionPerformed
        // TODO add your handling code here:
        mostrarClientesEnTabla(jtable_clientes);
        //centrarTextoTabla(jtable_clientes);
        aplicarEstilosTabla(jtable_clientes, new Font("Roboto Light", Font.ITALIC, 12), Color.BLACK, Color.LIGHT_GRAY);
        //CentrarEncabezados(jtable_clientes);
    }//GEN-LAST:event_BtnActualizarActionPerformed

    private void jtable_clientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtable_clientesMouseClicked
        // TODO add your handling code here:
        agregarMenuContextual(jtable_clientes);
    }//GEN-LAST:event_jtable_clientesMouseClicked

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnActualizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtable_clientes;
    private javax.swing.JLabel lb_buscar;
    private javax.swing.JTextField txt_cedula_cliente;
    // End of variables declaration//GEN-END:variables
}
