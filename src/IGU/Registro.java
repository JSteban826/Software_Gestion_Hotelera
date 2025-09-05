package IGU;

import LOGICA.CodigoError;
import LOGICA.HistorialManagerSingleton;
import LOGICA.HistorialManager;
import LOGICA.Logger;
import LOGICA.ManejadorErrores;
import LOGICA.Rol;
import PERSISTENCIA.ConexionBD;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLTimeoutException;
import java.sql.SQLException;
import java.util.logging.Level;

public class Registro extends javax.swing.JFrame {

    int xMouse, yMouse;
    String usuario, contraseña;

    public Registro(String usuario, String contraseña) {
        this.usuario = usuario;
        this.contraseña = contraseña;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    private Login login;

    public Registro(Login login) {
        this.login = login;  // Guardamos la referencia a la ventana de Login
        initComponents();  // Inicialización de los componentes
        cargarRoles();

    }

    public Registro() {

        initComponents();
        setDefaultCloseOperation(Registro.DISPOSE_ON_CLOSE); // <-- AQUÍ
        cargarRoles();
    }

    public static void insertarUsuario(String usuario, String contraseña, int rol_id) throws SQLIntegrityConstraintViolationException, SQLTransactionRollbackException, SQLTimeoutException, SQLException {
        String sql = "INSERT INTO usuarios (usuario, contraseña, rol_id, fecha_registro) VALUES (?, ?, ?, NOW())";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, usuario);
            statement.setString(2, contraseña);
            statement.setInt(3, rol_id);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Usuario insertado correctamente.");
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

    private void cargarRoles() {

        String sql = "SELECT id, nombre FROM roles where id=2";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {
            // Limpia los items previos por si se llama más de una vez
            cmb_roles.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");

                cmb_roles.addItem(new Rol(id, nombre));
            }
        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        header = new javax.swing.JPanel();
        exitBtn = new javax.swing.JPanel();
        exitTxt = new javax.swing.JLabel();
        title = new javax.swing.JLabel();
        userLabel = new javax.swing.JLabel();
        ContraTxt = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        passLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        userTxt = new javax.swing.JTextField();
        Btn_volver = new javax.swing.JButton();
        btn_guardar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Btn_generar = new javax.swing.JButton();
        userLabel1 = new javax.swing.JLabel();
        cmb_roles = new javax.swing.JComboBox<Rol>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(true);
        setResizable(false);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        header.setBackground(new java.awt.Color(255, 255, 255));
        header.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                headerMouseDragged(evt);
            }
        });
        header.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                headerMousePressed(evt);
            }
        });

        exitBtn.setBackground(new java.awt.Color(255, 255, 255));

        exitTxt.setFont(new java.awt.Font("Roboto Light", 0, 24)); // NOI18N
        exitTxt.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exitTxt.setText("X");
        exitTxt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        exitTxt.setPreferredSize(new java.awt.Dimension(40, 40));
        exitTxt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitTxtMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitTxtMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitTxtMouseExited(evt);
            }
        });

        javax.swing.GroupLayout exitBtnLayout = new javax.swing.GroupLayout(exitBtn);
        exitBtn.setLayout(exitBtnLayout);
        exitBtnLayout.setHorizontalGroup(
            exitBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        exitBtnLayout.setVerticalGroup(
            exitBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitTxt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addComponent(exitBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 260, Short.MAX_VALUE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(exitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bg.add(header, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 300, 40));

        title.setFont(new java.awt.Font("Roboto Black", 1, 24)); // NOI18N
        title.setText("REGISTRO");
        bg.add(title, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 90, -1, -1));

        userLabel.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel.setText("ROL");
        bg.add(userLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));

        ContraTxt.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        ContraTxt.setBorder(null);
        ContraTxt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ContraTxtMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ContraTxtMousePressed(evt);
            }
        });
        ContraTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContraTxtActionPerformed(evt);
            }
        });
        bg.add(ContraTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 260, 30));

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));
        bg.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 290, 240, 20));

        passLabel.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        passLabel.setText("SU CONTRASEÑA:");
        bg.add(passLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, -1, -1));

        jSeparator2.setForeground(new java.awt.Color(0, 0, 0));
        bg.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 370, 220, 20));

        userTxt.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        userTxt.setText("Ingrese su nombre de usuario");
        userTxt.setBorder(null);
        userTxt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userTxtMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTxtMousePressed(evt);
            }
        });
        userTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userTxtActionPerformed(evt);
            }
        });
        bg.add(userTxt, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, 260, 30));

        Btn_volver.setBackground(new java.awt.Color(65, 104, 163));
        Btn_volver.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Btn_volver.setForeground(new java.awt.Color(255, 255, 255));
        Btn_volver.setText("VOLVER");
        Btn_volver.setPreferredSize(new java.awt.Dimension(75, 22));
        Btn_volver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_volverActionPerformed(evt);
            }
        });
        bg.add(Btn_volver, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, 80, 25));

        btn_guardar.setBackground(new java.awt.Color(65, 104, 163));
        btn_guardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btn_guardar.setForeground(new java.awt.Color(255, 255, 255));
        btn_guardar.setText("GUARDAR");
        btn_guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_guardarActionPerformed(evt);
            }
        });
        bg.add(btn_guardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 400, 90, 25));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/logo2025.png"))); // NOI18N
        bg.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 0, -1, 510));

        Btn_generar.setBackground(new java.awt.Color(65, 104, 163));
        Btn_generar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        Btn_generar.setForeground(new java.awt.Color(255, 255, 255));
        Btn_generar.setText("GENERAR");
        Btn_generar.setMaximumSize(new java.awt.Dimension(75, 22));
        Btn_generar.setMinimumSize(new java.awt.Dimension(75, 22));
        Btn_generar.setPreferredSize(new java.awt.Dimension(75, 22));
        Btn_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_generarActionPerformed(evt);
            }
        });
        bg.add(Btn_generar, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 400, 90, 25));

        userLabel1.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        userLabel1.setText("USUARIO");
        bg.add(userLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, -1, -1));

        cmb_roles.setModel(new javax.swing.DefaultComboBoxModel<Rol>()
        );
        bg.add(cmb_roles, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 130, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void headerMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headerMousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_headerMousePressed

    private void headerMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headerMouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();
        this.setLocation(x - xMouse, y - yMouse);
    }//GEN-LAST:event_headerMouseDragged

    private void exitTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitTxtMouseClicked
        System.exit(0);
    }//GEN-LAST:event_exitTxtMouseClicked

    private void exitTxtMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitTxtMouseEntered
        exitBtn.setBackground(Color.red);
        exitTxt.setForeground(Color.white);
    }//GEN-LAST:event_exitTxtMouseEntered

    private void exitTxtMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitTxtMouseExited
        exitBtn.setBackground(Color.white);
        exitTxt.setForeground(Color.black);
    }//GEN-LAST:event_exitTxtMouseExited

    private void ContraTxtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContraTxtMousePressed


    }//GEN-LAST:event_ContraTxtMousePressed

    private void ContraTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContraTxtMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_ContraTxtMouseClicked

    private void userTxtMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxtMouseClicked
        // TODO add your handling code here:
        userTxt.setText("");
    }//GEN-LAST:event_userTxtMouseClicked

    private void userTxtMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTxtMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxtMousePressed

    private void userTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTxtActionPerformed

    private void Btn_volverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_volverActionPerformed
        // TODO add your handling code here:

        login = new Login(); // Asegúrate de inicializarlo
        login.setVisible(true);
        this.dispose(); // si deseas cerrar la ventana actual
        login.setVisible(true);  // Muestra la ventana de registro
        login.setResizable(false);
        Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

        // Calcular la posición para centrar el JFrame
        int x = (pantalla.width - login.getSize().width) / 2;
        int y = (pantalla.height - login.getSize().height) / 2;

        // Posicionar la ventana en el centro de la pantalla
        login.setLocation(x, y);

    }//GEN-LAST:event_Btn_volverActionPerformed

    private void ContraTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContraTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ContraTxtActionPerformed

    private void btn_guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_guardarActionPerformed

        String usuario = userTxt.getText();
        String contraseña = ContraTxt.getText();
        Rol rolSeleccionado = (Rol) cmb_roles.getSelectedItem();

        try {
            if (usuario.isEmpty() || contraseña.isEmpty() || rolSeleccionado == null) {
                throw new NullPointerException("Campos vacíos");

            }

            insertarUsuario(usuario, contraseña, rolSeleccionado.getId());

            // Agregar acción a historial
            HistorialManager historial_acciones = HistorialManagerSingleton.getInstancia();
            historial_acciones.registrarAccion("Registro de Usuario: " + usuario);

        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Registro.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btn_guardarActionPerformed

    private void Btn_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_generarActionPerformed
        // TODO add your handling code here:

        usuario = String.valueOf(userTxt.getText());

        String texto = userTxt.getText();
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            int ascii = (int) caracter;
            char ascii2 = (char) (caracter + 1);
            resultado.append(ascii2);
        }

        ContraTxt.setText(resultado.toString());
        contraseña = String.valueOf(ContraTxt.getText());
    }//GEN-LAST:event_Btn_generarActionPerformed

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
            java.util.logging.Logger.getLogger(Registro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Registro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Registro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Registro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                centrar();
            }

            public void centrar() {
                Registro rg = new Registro();
                rg.setVisible(true);
                rg.setResizable(false);
                Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();

                // Calcular la posición para centrar el JFrame
                int x = (pantalla.width - rg.getSize().width) / 2;
                int y = (pantalla.height - rg.getSize().height) / 2;

                // Posicionar la ventana en el centro de la pantalla
                rg.setLocation(x, y);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_generar;
    private javax.swing.JButton Btn_volver;
    private javax.swing.JTextField ContraTxt;
    private javax.swing.JPanel bg;
    private javax.swing.JButton btn_guardar;
    private javax.swing.JComboBox<Rol> cmb_roles;
    private javax.swing.JPanel exitBtn;
    private javax.swing.JLabel exitTxt;
    private javax.swing.JPanel header;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel passLabel;
    private javax.swing.JLabel title;
    private javax.swing.JLabel userLabel;
    private javax.swing.JLabel userLabel1;
    private javax.swing.JTextField userTxt;
    // End of variables declaration//GEN-END:variables
}
