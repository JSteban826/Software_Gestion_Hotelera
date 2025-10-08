package LOGICA;

import PERSISTENCIA.ConexionBD;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HabitacionesService {

    /**
     * Muestra todas las habitaciones en la tabla
     */
    public void mostrarHabitacionesEnTabla(JTable tabla) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();

        try (Connection conn = ConexionBD.conectar()) {
            String sql = "SELECT * FROM habitaciones";
            model.setRowCount(0);

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Object[] fila = {
                        rs.getInt("id_habitacion"),
                        rs.getString("tipo_habitacion"),
                        rs.getString("nombre_habitacion"),
                        rs.getDouble("precio_noche"),
                        rs.getString("estado")
                    };
                    model.addRow(fila);
                }
            }
            System.out.println("✅ Habitaciones cargadas correctamente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al cargar habitaciones: " + e.getMessage());
        }
    }

    /**
     * Actualiza el estado de una habitación en la BD
     */
    public boolean actualizarEstadoHabitacion(int idHabitacion, String nuevoEstado) {
        String sql = "UPDATE habitaciones SET estado = ? WHERE id_habitacion = ?";
        try (Connection conn = ConexionBD.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idHabitacion);
            int filas = stmt.executeUpdate();

            if (filas > 0) {
                System.out.println("✅ Estado actualizado correctamente a " + nuevoEstado + " (ID " + idHabitacion + ")");
                return true;
            } else {
                System.out.println("⚠️ No se encontró la habitación con ID " + idHabitacion);
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Agrega un menú contextual (clic derecho) a la tabla con acciones
     */
    public void agregarMenuContextual(JTable tabla) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemDetalles = new JMenuItem("Ver Detalles");
        JMenuItem itemCambiarEstado = new JMenuItem("Cambiar Estado");

        // Acción: Ver Detalles
        itemDetalles.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                String info = String.format(
                        "ID: %s\nTipo: %s\nNombre: %s\nPrecio: %s\nEstado: %s",
                        tabla.getValueAt(fila, 0),
                        tabla.getValueAt(fila, 1),
                        tabla.getValueAt(fila, 2),
                        tabla.getValueAt(fila, 3),
                        tabla.getValueAt(fila, 4)
                );
                JOptionPane.showMessageDialog(null, info, "Detalles de habitación", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Acción: Cambiar Estado
        itemCambiarEstado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila != -1) {
                int id = (int) tabla.getValueAt(fila, 0);
                String estadoActual = (String) tabla.getValueAt(fila, 4);

                String[] opciones = {"Libre", "Ocupada", "Mantenimiento"};
                String nuevoEstado = (String) JOptionPane.showInputDialog(
                        null,
                        "Estado actual: " + estadoActual + "\nSeleccione el nuevo estado:",
                        "Cambiar Estado",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        opciones,
                        estadoActual
                );

                if (nuevoEstado != null && !nuevoEstado.equals(estadoActual)) {
                    boolean exito = actualizarEstadoHabitacion(id, nuevoEstado);
                    if (exito) {
                        tabla.setValueAt(nuevoEstado, fila, 4); // Actualiza visualmente
                        tabla.repaint();
                        JOptionPane.showMessageDialog(null, "Estado cambiado a " + nuevoEstado);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo actualizar en la base de datos.");
                    }
                }
            }
        });

        menu.add(itemDetalles);
        menu.add(itemCambiarEstado);

        // Añade listener para mostrar el menú
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mostrarMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mostrarMenu(e);
            }

            private void mostrarMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int fila = tabla.rowAtPoint(e.getPoint());
                    if (fila != -1) {
                        tabla.setRowSelectionInterval(fila, fila);
                        menu.show(tabla, e.getX(), e.getY());
                    }
                }
            }
        });
    }
}
