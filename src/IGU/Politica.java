/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package IGU;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;
import java.awt.Insets;
import java.awt.*;
import PERSISTENCIA.ConexionBD;

/**
 *
 * @author Windows
 */
public class Politica extends javax.swing.JFrame {

    private JTextField txtDocumentoIdentidad;
    private JComboBox<String> cmbEsClientePrincipal;
    private JComboBox<String> cmbPoliticaAceptada;
    private JComboBox<String> cmbAceptadoPorTercero;
    private JButton btnGuardar;
    private JDateChooser fechaAceptacion;

    /**
     * Creates new form Politica
     */
    public Politica() {
        setTitle("Hotel Resort Bahia Coral - Política de Datos");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Fuente personalizada
        Font fuente = new Font("Georgia", Font.PLAIN, 14);

        // Colores
        Color fondo = new Color(230, 242, 255); // azul claro
        Color texto = new Color(0, 51, 102); // azul oscuro

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(fondo);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTitulo = new JLabel("Hotel Resort Bahía Coral");
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 20));
        lblTitulo.setForeground(texto);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        // Fila 1
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblDoc = new JLabel("Documento de Identidad:");
        lblDoc.setFont(fuente);
        panel.add(lblDoc, gbc);

        gbc.gridx = 1;
        txtDocumentoIdentidad = new JTextField(20);
        txtDocumentoIdentidad.setFont(fuente);
        panel.add(txtDocumentoIdentidad, gbc);

        // Fila 2
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblCliente = new JLabel("¿Es Cliente Principal?");
        lblCliente.setFont(fuente);
        panel.add(lblCliente, gbc);

        gbc.gridx = 1;
        cmbEsClientePrincipal = new JComboBox<>(new String[]{"Sí", "No"});
        cmbEsClientePrincipal.setFont(fuente);
        panel.add(cmbEsClientePrincipal, gbc);

        // Fila 3
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblPolitica = new JLabel("¿Acepta la Política?");
        lblPolitica.setFont(fuente);
        panel.add(lblPolitica, gbc);

        gbc.gridx = 1;
        cmbPoliticaAceptada = new JComboBox<>(new String[]{"Sí", "No"});
        cmbPoliticaAceptada.setFont(fuente);
        panel.add(cmbPoliticaAceptada, gbc);

        // Fila 4
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblTercero = new JLabel("¿Acepta por Tercero?");
        lblTercero.setFont(fuente);
        panel.add(lblTercero, gbc);

        gbc.gridx = 1;
        cmbAceptadoPorTercero = new JComboBox<>(new String[]{"Sí", "No"});
        cmbAceptadoPorTercero.setFont(fuente);
        panel.add(cmbAceptadoPorTercero, gbc);

        // Fila 5
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel lblFecha = new JLabel("Fecha de Aceptación:");
        lblFecha.setFont(fuente);
        panel.add(lblFecha, gbc);

        gbc.gridx = 1;
        
        fechaAceptacion = new JDateChooser();
        fechaAceptacion.setFont(fuente);
        fechaAceptacion.setPreferredSize(new Dimension(150, 25)); // Cambia el ancho a 200 px
        panel.add(fechaAceptacion, gbc);

        // Botón guardar
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Georgia", Font.BOLD, 14));
        btnGuardar.setBackground(texto);
        btnGuardar.setForeground(Color.WHITE);
        panel.add(btnGuardar, gbc);

        add(panel);

        // Acción del botón
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                guardarDatos();
            }
        });
    }

    private void guardarDatos() {
        String documentoIdentidad = txtDocumentoIdentidad.getText();
        String esClientePrincipal = cmbEsClientePrincipal.getSelectedItem().toString();
        String politicaAceptada = cmbPoliticaAceptada.getSelectedItem().toString();
        String aceptadoPorTercero = cmbAceptadoPorTercero.getSelectedItem().toString();
        java.util.Date fecha = fechaAceptacion.getDate();
        java.sql.Date sqlDate = new java.sql.Date(fecha.getTime());

        if (documentoIdentidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el documento de identidad.");
            return;
        }

        // Insertar en la base de datos
        try (Connection con = ConexionBD.conectar()) {  // Usando tu clase ConexionBD
            String sql = "INSERT INTO personas_hospedaje (documento_identidad, es_cliente_principal, politica_aceptada, aceptado_por_tercero, fecha_aceptacion) "
                    + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, documentoIdentidad);
                ps.setString(2, esClientePrincipal);
                ps.setString(3, politicaAceptada);
                ps.setString(4, aceptadoPorTercero);
                ps.setDate(5, sqlDate);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Datos guardados correctamente.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar los datos: " + ex.getMessage());
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Politica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Politica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Politica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Politica.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Politica().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
