package IGU;

import LOGICA.HistorialManager;
import LOGICA.HistorialManagerSingleton;
import LOGICA.ManejadorErrores;
import PERSISTENCIA.ConexionBD;
import LOGICA.Tablas;

import java.awt.Color;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;

public class Historial_Pagos extends javax.swing.JFrame {

    public Historial_Pagos() {
        initComponents();
        Tablas.CentrarEncabezados(jtable_Historial);
        setDefaultCloseOperation(Historial_Pagos.DISPOSE_ON_CLOSE); // ✅ SOLO CIERRA JFrame2

    }

    public static void mostrarHistorialEnTabla(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar()) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM historial_pagos";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getInt("id_pago"),
                        resultSet.getString("id_cliente"),
                        resultSet.getDouble("valor_pago"),
                        resultSet.getString("estado_pago"),
                        resultSet.getString("fecha_pago")
                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);

        }
    }

    public static void marcarPagocomoPagada(int id_pago) {
        String sql = "UPDATE historial_pagos SET estado_pago = 'Pagada' WHERE id_pago = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_pago);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "El pago ha sido marcado como 'Pagada'.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el pago con ID: " + id_pago);
            }

        } catch (SQLException e) {
            ManejadorErrores.errorUpdateSQL(e);
        }
    }

    public static void marcarPagocomoPendiente(int id_pago) {
        String sql = "UPDATE historial_pagos SET estado_pago = 'Pendiente' WHERE id_pago = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_pago);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "El pago ha sido marcado como 'Pendiente'.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el pago con ID: " + id_pago);
            }

        } catch (SQLException e) {
            ManejadorErrores.errorUpdateSQL(e);
        }
    }

    public static void marcarPagocomoCancelada(int id_pago) {
        String sql = "UPDATE historial_pagos SET estado_pago = 'Cancelado' WHERE id_pago = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_pago);
            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(null, "El pago ha sido marcado como 'Cancelado'.");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el pago con ID: " + id_pago);
            }

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
                    JMenuItem marcarPagada = new JMenuItem("Marcar como Pagada");
                    JMenuItem marcarPendiente = new JMenuItem("Marcar como Pendiente");
                    JMenuItem marcarCancelada = new JMenuItem("Marcar como Cancelada");

                    marcarPagada.addActionListener(a -> {
                        int id_pago = (int) tabla.getValueAt(filaSeleccionada, 0);

                        marcarPagocomoPagada(id_pago); // método que ya tienes

                        // Registrar en historial
                        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                        historial_acciones.registrarAccion("El pago ha sido marcado como 'Pagada'." + id_pago);
                    });

                    marcarPendiente.addActionListener(a -> {
                        int id_pago = (int) tabla.getValueAt(filaSeleccionada, 0);

                        marcarPagocomoPendiente(id_pago); // método que ya tienes

                        // Registrar en historial
                        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                        historial_acciones.registrarAccion("El pago ha sido marcado como 'Pendiente'." + id_pago);
                    });
                    
                    marcarCancelada.addActionListener(a -> {
                        int id_pago = (int) tabla.getValueAt(filaSeleccionada, 0);

                        marcarPagocomoCancelada(id_pago); // método que ya tienes

                        // Registrar en historial
                        HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
                        historial_acciones.registrarAccion("El pago ha sido marcado como 'Cancelada'." + id_pago);
                    });

                    menu.add(marcarPagada);
                    menu.add(marcarPendiente);
                    menu.add(marcarCancelada);
                    menu.show(tabla, e.getX(), e.getY());
                }
            }
        });
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
        jScrollPane2 = new javax.swing.JScrollPane();
        jtable_Historial = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        BtnActualizar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(65, 104, 163));

        jtable_Historial.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        jtable_Historial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id Pago", "Id Cliente", "Valor Pago", "Estado", "Fecha Pago"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jtable_Historial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtable_HistorialMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtable_Historial);

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Georgia", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/arrecife-de-coral (1).png"))); // NOI18N
        jLabel1.setText("HISTORIAL PAGOS");

        BtnActualizar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        BtnActualizar.setForeground(new java.awt.Color(65, 104, 163));
        BtnActualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/actualizar-flecha.png"))); // NOI18N
        BtnActualizar.setText("Actualizar");
        BtnActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnActualizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(276, 276, 276))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtable_HistorialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtable_HistorialMouseClicked
        agregarMenuContextual(jtable_Historial);        // TODO add your handling code here:

    }//GEN-LAST:event_jtable_HistorialMouseClicked

    private void BtnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnActualizarActionPerformed
        // TODO add your handling code here:
        mostrarHistorialEnTabla(jtable_Historial);
        Tablas.aplicarEstilosTabla(jtable_Historial, new Font("Georgia", Font.ITALIC, 12), Color.BLACK, Color.LIGHT_GRAY);
    }//GEN-LAST:event_BtnActualizarActionPerformed

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
            java.util.logging.Logger.getLogger(Historial_Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Historial_Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Historial_Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Historial_Pagos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Historial_Pagos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnActualizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jtable_Historial;
    // End of variables declaration//GEN-END:variables
}
