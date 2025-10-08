/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package IGU;

import LOGICA.BackupException;
import LOGICA.CodigoError;
import PERSISTENCIA.ConexionBD;
import LOGICA.ManejadorErrores;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 *
 * @author elian
 */
public class Backups extends javax.swing.JFrame {

    /**
     * Creates new form Backups
     */
    public Backups() {
        initComponents();
        cargarBackupsEnLista();
    }

    private void cargarBackupsEnLista() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        File carpetaBackups = new File("BackupsHotel");
        File[] archivos = carpetaBackups.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));

        if (archivos != null && archivos.length > 0) {
            for (File archivo : archivos) {
                modelo.addElement(archivo.getName());
            }
        } else {
            modelo.addElement("No hay backups disponibles.");
        }

        listaBackups.setModel(modelo);

        // --- Doble clic para abrir archivo ---
        listaBackups.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String nombreArchivo = listaBackups.getSelectedValue();
                    if (nombreArchivo != null && !nombreArchivo.equals("No hay backups disponibles.")) {
                        File archivoSeleccionado = new File("BackupsHotel", nombreArchivo);
                        try {
                            Desktop.getDesktop().open(archivoSeleccionado);
                        } catch (FileNotFoundException fnf) {
                            ManejadorErrores.rutaarchivo(fnf);
                            JOptionPane.showMessageDialog(null, "❌ El archivo no existe o fue movido.");
                        } catch (IOException io) {
                            ManejadorErrores.abrirarchivo(io);
                            JOptionPane.showMessageDialog(null, "❌ No se pudo abrir el archivo.");
                        }
                    }
                }
            }
        });
    }

    public void generarBackup() {

        try {

            // Crear carpeta de backups si no existe
            File carpetaBackups = new File("BackupsHotel");
            if (!carpetaBackups.exists()) {
                carpetaBackups.mkdirs();
            }

            // Nombre del archivo con fecha
            String fecha = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String rutaBackup = carpetaBackups.getAbsolutePath() + File.separator + "hotel_backup_" + fecha + ".sql";

            // Ruta absoluta de mysqldump (ajústala según tu instalación)
            String rutaMysqldump = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";

            ProcessBuilder pb;

            try {
                // --- 1. Intentar ejecutar con mysqldump del PATH ---
                pb = new ProcessBuilder(
                        "mysqldump",
                        "-h", "database-hotel.mysql.database.azure.com",
                        "-u", ConexionBD.USUARIO,
                        "-p" + ConexionBD.CONTRASEÑA,
                        "hotel"
                );
                pb.redirectOutput(new File(rutaBackup));
                pb.redirectErrorStream(true);

                Process proceso = pb.start();
                int resultado = proceso.waitFor();

                if (resultado != 0) {
                    throw new IOException("mysqldump no encontrado en PATH");
                }

                lbl_status.setText("Backup realizado con éxito");

            } catch (IOException e1) {
                // --- 2. Si falla, intentar con ruta absoluta ---
                pb = new ProcessBuilder(
                        rutaMysqldump,
                        "-h", "database-hotel.mysql.database.azure.com",
                        "-u", ConexionBD.USUARIO,
                        "-p" + ConexionBD.CONTRASEÑA,
                        "hotel"
                );
                pb.redirectOutput(new File(rutaBackup));
                pb.redirectErrorStream(true);

                Process proceso = pb.start();
                int resultado = proceso.waitFor();

                if (resultado == 0) {
                    lbl_status.setText("Backup realizado con éxito");
                } else {
                    lbl_status.setText("Error al realizar el backup. Código: " + resultado);
                    throw new BackupException("Error al generar backup con ruta absoluta");
                }
            }

        } catch (BackupException e) {
            ManejadorErrores.generarbackup(e);
            lbl_status.setText("Error de backup: " + e.getMessage());

        } catch (FileNotFoundException fnf) {
            ManejadorErrores.rutaarchivo(fnf);
            lbl_status.setText("Archivo de salida no encontrado: " + fnf.getMessage());

        } catch (IOException ex) {
            ManejadorErrores.abrirarchivo(ex);
            Logger.getLogger(Backups.class.getName()).log(Level.SEVERE, null, ex);
            lbl_status.setText("Error de IO al realizar backup.");

        } catch (InterruptedException ex) {
            Logger.getLogger(Backups.class.getName()).log(Level.SEVERE, null, ex);
            lbl_status.setText("Proceso interrumpido.");
        }
    }

    public void generarBackupAsync() {
        lbl_status.setText("⏳ Realizando backup, por favor espera...");

        // Configuración inicial de la barra
        ProgressBar_backup.setVisible(true);
        ProgressBar_backup.setIndeterminate(false);
        ProgressBar_backup.setMinimum(0);
        ProgressBar_backup.setMaximum(100);
        ProgressBar_backup.setValue(0);

        // Habilitar que Nimbus respete setForeground()
        UIManager.put("ProgressBar[Enabled].foregroundPainter", null);
        UIManager.put("ProgressBar[Enabled+Finished].foregroundPainter", null);
        UIManager.put("ProgressBar[Enabled+Indeterminate].foregroundPainter", null);

        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i <= 90; i += 10) {
                    try {
                        Thread.sleep(5125);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (i < 30) {
                        UIManager.put("nimbusOrange", new Color(135, 206, 250));
                    } else if (i < 70) {
                        UIManager.put("nimbusOrange", new Color(100, 149, 237));
                    } else {
                        UIManager.put("nimbusOrange", new Color(100, 149, 237));
                    }
                    ProgressBar_backup.repaint(); // Forzar repintado

                    publish(i);
                }

                // Metodo de generar backup
                generarBackup();
                publish(100);
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int ultimo = chunks.get(chunks.size() - 1);
                ProgressBar_backup.setValue(ultimo);
            }

            @Override
            protected void done() {
                try {
                    get();
                    lbl_status.setText("Backup finalizado con éxito.");
                } catch (Exception e) {
                    lbl_status.setText("Error durante el backup: " + e.getMessage());
                }
            }
        };

        worker.execute();
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
        btn_generar = new javax.swing.JButton();
        btn_restaurar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaBackups = new javax.swing.JList<>();
        lbl_status = new javax.swing.JLabel();
        btn_refrescar = new javax.swing.JButton();
        ProgressBar_backup = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("BACKUPS");

        btn_generar.setText("Generar");
        btn_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generarActionPerformed(evt);
            }
        });

        btn_restaurar.setText("Restaurar");
        btn_restaurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_restaurarActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(listaBackups);

        lbl_status.setText("Generar backup");

        btn_refrescar.setText("Refrescar");
        btn_refrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refrescarActionPerformed(evt);
            }
        });

        ProgressBar_backup.setForeground(new java.awt.Color(51, 153, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_refrescar)
                        .addGap(18, 18, 18)
                        .addComponent(btn_restaurar))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btn_generar)
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbl_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ProgressBar_backup, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_generar)
                    .addComponent(lbl_status, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProgressBar_backup, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_refrescar)
                    .addComponent(btn_restaurar))
                .addContainerGap(117, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_restaurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_restaurarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_restaurarActionPerformed

    private void btn_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generarActionPerformed
        // TODO add your handling code here:   
        generarBackupAsync();
    }//GEN-LAST:event_btn_generarActionPerformed

    private void btn_refrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refrescarActionPerformed
        // TODO add your handling code here:
        cargarBackupsEnLista();
    }//GEN-LAST:event_btn_refrescarActionPerformed

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
            java.util.logging.Logger.getLogger(Backups.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Backups.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Backups.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Backups.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Backups().setVisible(true);
                System.out.println(UIManager.getLookAndFeel().getName());

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar ProgressBar_backup;
    private javax.swing.JButton btn_generar;
    private javax.swing.JButton btn_refrescar;
    private javax.swing.JButton btn_restaurar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JList<String> listaBackups;
    // End of variables declaration//GEN-END:variables
}
