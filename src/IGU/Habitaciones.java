/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package IGU;

import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.ManejadorErrores;
import LOGICA.Tablas;
import PERSISTENCIA.ConexionBD;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 *
 * @author Windows
 */
public class Habitaciones extends javax.swing.JFrame {

    private int idHabitacion;

    // Otros componentes...
    private javax.swing.JTextField txt_id_habitacion; // Solo si es necesario en este frame

    public Habitaciones(int idHabitacion) {
        this.idHabitacion = idHabitacion;
        initComponents();
        setResizable(false);
        setDefaultCloseOperation(Habitaciones.DISPOSE_ON_CLOSE); // <-- AQUÍ
        // Puedes usar idHabitacion aquí para cualquier propósito necesario
    }

    /**
     * Creates new form Habitaciones
     */
    public Habitaciones() {
        initComponents();
        setResizable(false);
        Tablas.CentrarEncabezados(jtable_habitaciones);

    }

    public Image getIconImage() {
        Image retValue = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("com.images/logos.jpg"));
        return retValue;
    }

    public static void mostrarHabitacionesEnTabla(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM habitaciones";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getInt("id_habitacion"),
                        resultSet.getString("tipo_habitacion"),
                        resultSet.getString("nombre_habitacion"),
                        resultSet.getDouble("precio_noche"),
                        resultSet.getString("estado")

                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void marcarHabitacionEnMantenimiento(int idHabitacion) {
        String sql = "UPDATE habitaciones SET estado = 'Mantenimiento' WHERE id_habitacion = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idHabitacion);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "La habitación ha sido marcada como 'mantenimiento'.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la habitación con ID: " + idHabitacion);
            }

        } catch (SQLException e) {
            ManejadorErrores.errorUpdateSQL(e);
        }
    }
    
    public static void marcarHabitacionLibre(int idHabitacion) {
        String sql = "UPDATE habitaciones SET estado = 'Libre' WHERE id_habitacion = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idHabitacion);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "La habitación ha sido marcada como 'Libre'.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la habitación con ID: " + idHabitacion);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el estado de la habitación: " + e.getMessage());
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
                    JMenuItem marcarMantenimiento = new JMenuItem("Marcar como Mantenimiento");
                    JMenuItem marcarLibre = new JMenuItem("Marcar como Libre");

                    marcarMantenimiento.addActionListener(a -> {
                        int idHabitacion = (int) tabla.getValueAt(filaSeleccionada, 0);

                        marcarHabitacionEnMantenimiento(idHabitacion); // método que ya tienes

                        // Registrar en historial
                        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                        historial_acciones.registrarAccion("Habitación marcada en mantenimiento: " + idHabitacion);
                    });
                    
                    marcarLibre.addActionListener(a -> {
                        int idHabitacion = (int) tabla.getValueAt(filaSeleccionada, 0);

                        marcarHabitacionLibre(idHabitacion); // método que ya tienes

                        // Registrar en historial
                        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                        historial_acciones.registrarAccion("Habitación marcada como Libre: " + idHabitacion);
                    });
                    
                    

                    menu.add(marcarMantenimiento);
                    menu.add(marcarLibre);
                    menu.show(tabla, e.getX(), e.getY());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable_habitaciones = new javax.swing.JTable();
        btn_actualizar = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(65, 104, 163));
        jPanel1.setForeground(new java.awt.Color(254, 255, 239));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/arrecife.png"))); // NOI18N
        jLabel1.setText("HABITACIONES");

        jtable_habitaciones.setFont(new java.awt.Font("Microsoft JhengHei UI", 1, 14)); // NOI18N
        jtable_habitaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id Habitacion", "Tipo Habitacion", "Nombre Habitacion", "Precio", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jtable_habitaciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtable_habitacionesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtable_habitaciones);
        if (jtable_habitaciones.getColumnModel().getColumnCount() > 0) {
            jtable_habitaciones.getColumnModel().getColumn(2).setPreferredWidth(125);
            jtable_habitaciones.getColumnModel().getColumn(3).setPreferredWidth(60);
            jtable_habitaciones.getColumnModel().getColumn(4).setPreferredWidth(60);
        }

        btn_actualizar.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_actualizar.setForeground(new java.awt.Color(65, 104, 163));
        btn_actualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/actualizar-flecha.png"))); // NOI18N
        btn_actualizar.setText("Actualizar");
        btn_actualizar.setBorder(new javax.swing.border.MatteBorder(null));
        btn_actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_actualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 25, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(282, 282, 282))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
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
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_actualizarActionPerformed
        // TODO add your handling code here:
        mostrarHabitacionesEnTabla(jtable_habitaciones);
        Tablas.aplicarEstilosTabla(jtable_habitaciones, new Font("Georgia", Font.PLAIN, 12), Color.BLACK, Color.LIGHT_GRAY);

    }//GEN-LAST:event_btn_actualizarActionPerformed

    private void jtable_habitacionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtable_habitacionesMouseClicked
        // TODO add your handling code here:
        agregarMenuContextual(jtable_habitaciones);
    }//GEN-LAST:event_jtable_habitacionesMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_actualizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtable_habitaciones;
    // End of variables declaration//GEN-END:variables
}
