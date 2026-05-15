/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package IGU;

import LOGICA.BackupException;
import PERSISTENCIA.ConexionBD;
import LOGICA.ManejadorErrores;
import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
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
                            JOptionPane.showMessageDialog(null, "El archivo no existe o fue movido.");
                        } catch (IOException io) {
                            ManejadorErrores.abrirarchivo(io);
                            JOptionPane.showMessageDialog(null, " No se pudo abrir el archivo.");
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

                pb.redirectOutput(new File(rutaBackup)); // salida al archivo .sql
                pb.redirectError(ProcessBuilder.Redirect.INHERIT); // errores a la consola

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
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

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
        lbl_status.setText("Realizando backup, por favor espera...");

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

    private void restaurarBackup(String nombreBackup) {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                publish("⏳ Restaurando copia de seguridad, por favor espera...");

                try {
                    File carpetaBackups = new File("BackupsHotel");
                    File archivoBackup = new File(carpetaBackups, nombreBackup);

                    if (!archivoBackup.exists()) {
                        publish("⚠️ No se encontró el archivo: " + archivoBackup.getAbsolutePath());
                        return null;
                    }

                    String rutaMysql = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe";

                    ProcessBuilder pb = new ProcessBuilder(
                            rutaMysql,
                            "-h", "database-hotel.mysql.database.azure.com",
                            "-u", ConexionBD.USUARIO,
                            "-p" + ConexionBD.CONTRASEÑA,
                            "hotel"
                    );

                    // ✅ Ejecutar el backup como entrada
                    pb.redirectInput(archivoBackup);
                    pb.redirectErrorStream(true);

                    Process proceso = pb.start();

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
                        String linea;
                        while ((linea = br.readLine()) != null) {
                            System.out.println(linea);
                        }
                    }

                    int exitCode = proceso.waitFor();

                    if (exitCode == 0) {
                        publish("✅ Restauración completada correctamente.");
                        // 🔄 Refrescar vista o datos
                        SwingUtilities.invokeLater(() -> {
                            // Aquí llamas tu método para recargar datos desde la BD
                            // Ejemplo:
                            // cargarDatosTabla();
                        });
                    } else {
                        publish("❌ Error al restaurar la base de datos. Código: " + exitCode);
                    }

                } catch (Exception e) {
                    publish("⚠️ Error en la restauración: " + e.getMessage());
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                lbl_status2.setText(chunks.get(chunks.size() - 1));
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
        jScrollPane1 = new javax.swing.JScrollPane();
        listaBackups = new javax.swing.JList<>();
        lbl_status = new javax.swing.JLabel();
        ProgressBar_backup = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        lbl_status2 = new javax.swing.JLabel();
        btn_generar = new javax.swing.JButton();
        btn_restaurar = new javax.swing.JButton();
        btn_refrescar = new javax.swing.JButton();
        lbl_info = new javax.swing.JLabel();
        lbl_progress = new javax.swing.JLabel();
        lbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        listaBackups.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(listaBackups);

        lbl_status.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        lbl_status.setForeground(new java.awt.Color(65, 104, 163));
        lbl_status.setText("Presione para Generar Backup");

        ProgressBar_backup.setBackground(new java.awt.Color(204, 204, 204));
        ProgressBar_backup.setForeground(new java.awt.Color(51, 153, 255));

        jLabel9.setBackground(new java.awt.Color(225, 225, 191));
        jLabel9.setFont(new java.awt.Font("Georgia", 3, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(65, 104, 163));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/images/logo50.jpg"))); // NOI18N
        jLabel9.setText("BACKUPS");

        lbl_status2.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        lbl_status2.setForeground(new java.awt.Color(65, 104, 163));
        lbl_status2.setText("Presione para Restaurar la copia de seguridad");

        btn_generar.setBackground(new java.awt.Color(65, 104, 163));
        btn_generar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_generar.setForeground(new java.awt.Color(255, 255, 255));
        btn_generar.setText("Generar");
        btn_generar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_generar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_generarActionPerformed(evt);
            }
        });

        btn_restaurar.setBackground(new java.awt.Color(65, 104, 163));
        btn_restaurar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_restaurar.setForeground(new java.awt.Color(255, 255, 255));
        btn_restaurar.setText("Restaurar");
        btn_restaurar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_restaurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_restaurarActionPerformed(evt);
            }
        });

        btn_refrescar.setBackground(new java.awt.Color(65, 104, 163));
        btn_refrescar.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        btn_refrescar.setForeground(new java.awt.Color(255, 255, 255));
        btn_refrescar.setText("Refrescar");
        btn_refrescar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_refrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refrescarActionPerformed(evt);
            }
        });

        lbl_info.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        lbl_info.setForeground(new java.awt.Color(65, 104, 163));
        lbl_info.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_info.setText("Lista de Backups");

        lbl_progress.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        lbl_progress.setForeground(new java.awt.Color(65, 104, 163));
        lbl_progress.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_progress.setText("Progreso:");

        lbl.setFont(new java.awt.Font("Georgia", 1, 14)); // NOI18N
        lbl.setForeground(new java.awt.Color(65, 104, 163));
        lbl.setText("Presione para Refrescar la lista de Backups");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_info, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 92, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_refrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btn_generar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_progress, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btn_restaurar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_status2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_status, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ProgressBar_backup, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(75, 75, 75))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(lbl_info)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_generar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_status))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_progress)
                    .addComponent(ProgressBar_backup, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_restaurar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_status2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_refrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl))
                .addContainerGap(92, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void btn_generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_generarActionPerformed
        // TODO add your handling code here:
        generarBackupAsync();
    }//GEN-LAST:event_btn_generarActionPerformed

    private void btn_restaurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_restaurarActionPerformed
        // TODO add your handling code here:
        String selectedBackup = listaBackups.getSelectedValue();

        if (selectedBackup == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Selecciona un backup de la lista.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de restaurar el backup?\n⚠️ Esto sobrescribirá la base de datos actual.",
                "Confirmar Restauración",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            restaurarBackup(selectedBackup);
        }
    }//GEN-LAST:event_btn_restaurarActionPerformed

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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl;
    private javax.swing.JLabel lbl_info;
    private javax.swing.JLabel lbl_progress;
    private javax.swing.JLabel lbl_status;
    private javax.swing.JLabel lbl_status2;
    private javax.swing.JList<String> listaBackups;
    // End of variables declaration//GEN-END:variables
}
