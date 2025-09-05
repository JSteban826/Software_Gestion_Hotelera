/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package IGU;

import LOGICA.ManejadorErrores;
import LOGICA.Tablas;
import PERSISTENCIA.ConexionBD;
import java.awt.Color;
import java.awt.Font;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class HISTORIAL_ACCION extends javax.swing.JFrame {

    /**
     * Creates new form HISTORIAL
     */
    public HISTORIAL_ACCION() {
        initComponents();
        Tablas.CentrarEncabezados(jtable_historial);
        setDefaultCloseOperation(HISTORIAL_ACCION.DISPOSE_ON_CLOSE); // ✅ SOLO CIERRA JFrame2
        cargarUltimaAccion();
    }

    Connection conn = ConexionBD.conectar();

    public static void mostrarHitorial(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            // Crear la consulta SQL
            String consulta = "SELECT * FROM historial_acciones";

            // Limpiar la tabla antes de cargar nuevos datos
            model.setRowCount(0);

            // Crear el statement
            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {

                // Procesar los resultados y agregar filas a la tabla
                while (resultSet.next()) {
                    Object[] fila = {
                        resultSet.getInt("id"),
                        resultSet.getString("accion"),
                        resultSet.getString("fecha")

                    };
                    model.addRow(fila);
                }
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);

        }
    }

    // Método para mostrar la acción anterior en el JTextField
    private int currentActionId = -1; // Inicializa en -1 para indicar que no hay acción mostrada

// Método para cargar la última acción al iniciar
    public void cargarUltimaAccion() {
        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            String consulta = "SELECT * FROM historial_acciones ORDER BY id DESC LIMIT 1"; // Última acción

            try (Statement statement = conn.createStatement(); ResultSet resultSet = statement.executeQuery(consulta)) {
                if (resultSet.next()) {
                    String accion = resultSet.getString("accion");
                    currentActionId = resultSet.getInt("id"); // Guarda el ID de la acción actual
                    txt_anterior.setText(accion); // Mostrar la acción en el JTextField
                } else {
                    txt_anterior.setText("No hay acciones registradas");
                }
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);

            txt_anterior.setText("Error al cargar la última acción");
        }
    }

// Método para mostrar la acción anterior
    public void mostrarAccionAnterior() {
        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            String consulta;

            // Verificar si se ha mostrado alguna acción
            if (currentActionId == -1) {
                // Si no se ha mostrado ninguna acción, no se puede mostrar anterior
                txt_anterior.setText("No hay acciones anteriores");
                return;
            } else {
                // Obtener la acción anterior al ID actual
                consulta = "SELECT * FROM historial_acciones WHERE id < ? ORDER BY id DESC LIMIT 1"; // Acción anterior
            }

            try (PreparedStatement preparedStatement = conn.prepareStatement(consulta)) {
                preparedStatement.setInt(1, currentActionId); // Establecer el ID actual

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String accionAnterior = resultSet.getString("accion");
                        currentActionId = resultSet.getInt("id"); // Actualiza el ID de la acción actual
                        txt_anterior.setText(accionAnterior); // Mostrar la acción anterior en el JTextField
                    } else {
                        txt_anterior.setText("No hay acciones anteriores"); // Mensaje si no hay más acciones
                    }
                }
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);

            txt_anterior.setText("Error al cargar la acción anterior"); // Mensaje de error
        }
    }

// Método para mostrar la siguiente acción
    public void mostrarAccionSiguiente() {
        // Conectar a la base de datos
        try (Connection conn = ConexionBD.conectar();) {
            String consulta;

            // Verificar si se ha mostrado alguna acción
            if (currentActionId == -1) {
                // Si no se ha mostrado ninguna acción, cargar la primera acción registrada
                consulta = "SELECT * FROM historial_acciones ORDER BY id ASC LIMIT 1";
            } else {
                // Obtener la acción siguiente al ID actual
                consulta = "SELECT * FROM historial_acciones WHERE id > ? ORDER BY id ASC LIMIT 1"; // Acción siguiente
            }

            try (PreparedStatement preparedStatement = conn.prepareStatement(consulta)) {
                if (currentActionId != -1) {
                    preparedStatement.setInt(1, currentActionId); // Establecer el ID actual si hay una acción mostrada
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String accionSiguiente = resultSet.getString("accion");
                        currentActionId = resultSet.getInt("id"); // Actualiza el ID de la acción actual
                        txt_siguiente.setText(accionSiguiente); // Mostrar la acción siguiente en el JTextField
                    } else {
                        txt_siguiente.setText("No hay acciones siguientes"); // Mensaje si no hay más acciones
                    }
                }
            } catch (SQLException ex) {
                ManejadorErrores.errorSelectSQL(ex);
            }
        } catch (SQLException ex) {
            ManejadorErrores.errorSelectSQL(ex);

            txt_siguiente.setText("Error al cargar la acción siguiente"); // Mensaje de error
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

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        BTN_ANTERIOR = new javax.swing.JButton();
        SIGUIENTE = new javax.swing.JButton();
        btn_historial = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtable_historial = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_siguiente = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txt_anterior = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 104, 163));
        jLabel2.setText("Anterior:");
        jLabel2.setPreferredSize(new java.awt.Dimension(57, 30));
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 97, 50));

        BTN_ANTERIOR.setBackground(new java.awt.Color(65, 104, 163));
        BTN_ANTERIOR.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        BTN_ANTERIOR.setForeground(new java.awt.Color(255, 255, 255));
        BTN_ANTERIOR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/flecha-hacia-arriba.png"))); // NOI18N
        BTN_ANTERIOR.setText("Anterior");
        BTN_ANTERIOR.setToolTipText("");
        BTN_ANTERIOR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_ANTERIORActionPerformed(evt);
            }
        });
        jPanel1.add(BTN_ANTERIOR, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 125, 140, -1));

        SIGUIENTE.setBackground(new java.awt.Color(65, 104, 163));
        SIGUIENTE.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        SIGUIENTE.setForeground(new java.awt.Color(255, 255, 255));
        SIGUIENTE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/flecha-hacia-abajo.png"))); // NOI18N
        SIGUIENTE.setText("Siguiente");
        SIGUIENTE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SIGUIENTEActionPerformed(evt);
            }
        });
        jPanel1.add(SIGUIENTE, new org.netbeans.lib.awtextra.AbsoluteConstraints(495, 182, 140, -1));

        btn_historial.setBackground(new java.awt.Color(65, 104, 163));
        btn_historial.setFont(new java.awt.Font("Georgia", 1, 12)); // NOI18N
        btn_historial.setForeground(new java.awt.Color(255, 255, 255));
        btn_historial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/mas-simbolo-negro.png"))); // NOI18N
        btn_historial.setText("MOSTRAR TODO");
        btn_historial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_historialActionPerformed(evt);
            }
        });
        jPanel1.add(btn_historial, new org.netbeans.lib.awtextra.AbsoluteConstraints(232, 611, 196, -1));

        jtable_historial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id Accion", "Accion", "Fecha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jtable_historial.setShowGrid(false);
        jScrollPane1.setViewportView(jtable_historial);
        if (jtable_historial.getColumnModel().getColumnCount() > 0) {
            jtable_historial.getColumnModel().getColumn(0).setPreferredWidth(20);
            jtable_historial.getColumnModel().getColumn(1).setPreferredWidth(275);
            jtable_historial.getColumnModel().getColumn(2).setPreferredWidth(35);
        }

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 273, 615, 305));

        jLabel5.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 104, 163));
        jLabel5.setText("Todo El Historial");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 239, 183, 23));

        jLabel6.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 104, 163));
        jLabel6.setText("Siguiente:");
        jLabel6.setPreferredSize(new java.awt.Dimension(61, 30));
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 81, 50));

        jPanel2.setBackground(new java.awt.Color(65, 104, 163));

        jLabel7.setFont(new java.awt.Font("Georgia", 1, 36)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/algas-marinas.png"))); // NOI18N
        jLabel7.setText("Historial De Acciones");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel7)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 677, -1));

        txt_siguiente.setColumns(20);
        txt_siguiente.setRows(5);
        jScrollPane2.setViewportView(txt_siguiente);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 180, 310, 50));

        txt_anterior.setColumns(20);
        txt_anterior.setRows(5);
        jScrollPane4.setViewportView(txt_anterior);

        jPanel1.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, 310, 50));

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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_historialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_historialActionPerformed
        // TODO add your handling code here:
        mostrarHitorial(jtable_historial);
        Tablas.aplicarEstilosTabla(jtable_historial, new Font("Roboto Light", Font.ITALIC, 12), Color.BLACK, Color.LIGHT_GRAY);
    }//GEN-LAST:event_btn_historialActionPerformed

    private void BTN_ANTERIORActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_ANTERIORActionPerformed
        // TODO add your handling code here:
        mostrarAccionAnterior();
    }//GEN-LAST:event_BTN_ANTERIORActionPerformed

    private void SIGUIENTEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SIGUIENTEActionPerformed
        // TODO add your handling code here:
        mostrarAccionSiguiente();
    }//GEN-LAST:event_SIGUIENTEActionPerformed

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
            java.util.logging.Logger.getLogger(HISTORIAL_ACCION.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HISTORIAL_ACCION.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HISTORIAL_ACCION.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HISTORIAL_ACCION.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HISTORIAL_ACCION().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTN_ANTERIOR;
    private javax.swing.JButton SIGUIENTE;
    private javax.swing.JButton btn_historial;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jtable_historial;
    private javax.swing.JTextArea txt_anterior;
    private javax.swing.JTextArea txt_siguiente;
    // End of variables declaration//GEN-END:variables
}
