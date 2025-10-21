/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LOGICA;

import IGU.Clientes;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import PERSISTENCIA.ConexionBD;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import LOGICA.Productos_MBar;
import java.sql.Connection;
import java.util.Locale;
import java.util.logging.Level;

public class Consumo_Minibar {

    // Método que agrega una fila a la JTable usando los JTextField
    public static void agregarConsumo(JTextField txt_id_prod, JComboBox cmb_productos, JTextField txt_valor, JTextField txt_cantidad, JTable jtable_productos) {
        try {
            // Obtiene el modelo de la tabla
            DefaultTableModel modelo = (DefaultTableModel) jtable_productos.getModel();

            // Valida que no haya campos vacíos
            if (txt_id_prod.getText().isEmpty() || cmb_productos.getSelectedItem().equals("") || txt_valor.getText().isEmpty() || txt_cantidad.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos antes de agregar el consumo.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int valor = Integer.parseInt(txt_valor.getText().trim());
            int cant = Integer.parseInt(txt_cantidad.getText().trim());
            int total = valor * cant;

            // Crea un arreglo con los datos a insertar
            Object[] fila = new Object[5];
            fila[0] = txt_id_prod.getText();
            fila[1] = cmb_productos.getSelectedItem();
            fila[2] = txt_valor.getText();
            fila[3] = txt_cantidad.getText();
            fila[4] = total;

            // Agrega la fila a la tabla
            modelo.addRow(fila);

            // Limpia los campos
            txt_id_prod.setText("");
            cmb_productos.setSelectedItem("");
            txt_valor.setText("");
            txt_cantidad.setText("");

            JOptionPane.showMessageDialog(null, "Consumo agregado correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar el consumo: " + e.getMessage());
        }
    }
    ;
    
    Map<String, Productos_MBar> datosProductos = new HashMap<>();

    public void buscarClientePorCedula(String cedula, JComboBox cmb_clientes, JTextField txt_id_check) throws ClienteNoExisteException, SQLException {
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
                    txt_id_check.setText(""); // Limpiar antes de buscar

                    // Cargar datos del Check In
                    cargarDatosCheckIn(cliente.getCedula(), txt_id_check);

                } else {
                    throw new ClienteNoExisteException("El cliente con cédula " + cedula + " no existe en la base de datos.");
                }
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    Map<String, Cliente1> datosClientes = new HashMap<>();

    public void cargarClientesEnComboBox(JTextField txt_id_cliente, JComboBox cmb_clientes, JTextField txt_id_check) {
        String sql = "SELECT Cedula, nombre, apellido FROM clientes";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            cmb_clientes.removeAllItems();
            datosClientes.clear();
            txt_id_cliente.setText("");
            txt_id_check.setText("");

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
                txt_id_cliente.setText(cliente.getCedula());

                // Cargar datos del Check In
                cargarDatosCheckIn(cliente.getCedula(), txt_id_check);
            }
        });
    }

    // Método auxiliar para cargar datos de reserva y nombre de habitación
    public void cargarDatosCheckIn(String cedulaCliente, JTextField txt_id_check) {
        String sqlReserva = "SELECT id_check_in FROM check_in WHERE id_cliente = ?";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement psReserva = conn.prepareStatement(sqlReserva)) {

            psReserva.setString(1, cedulaCliente);
            try (ResultSet rsReserva = psReserva.executeQuery()) {
                if (rsReserva.next()) {
                    int id_check = rsReserva.getInt("id_check_in");

                    txt_id_check.setText(String.valueOf(id_check));

                } else {
                    txt_id_check.setText("No encontrado");
                }
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    public void cargarProductosEnComboBox(JTextField txt_id_prod, JComboBox cmb_productos, JTextField txt_valor) {
        String sql = "SELECT id_producto, nombre_producto, valor_unitario FROM productos_minibar";

        try (java.sql.Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            cmb_productos.removeAllItems();
            datosProductos.clear();
            txt_id_prod.setText("");
            txt_valor.setText("");

            while (rs.next()) {
                String id_prod = rs.getString("id_producto");
                String nom_prod = rs.getString("nombre_producto");
                int valor = rs.getInt("valor_unitario");

                Productos_MBar prod = new Productos_MBar(id_prod, nom_prod, valor);

                cmb_productos.addItem(prod);
                datosProductos.put(nom_prod, prod);

            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }

        // Listener para actualizar campos cuando seleccionas un cliente
        cmb_productos.addActionListener(e -> {
            Productos_MBar seleccionado = (Productos_MBar) cmb_productos.getSelectedItem(); // ✅ ahora sí es un Producto
            if (seleccionado != null) {
                txt_id_prod.setText(seleccionado.getId_prod());
                txt_valor.setText(String.valueOf(seleccionado.getValor()));
            }
        });

    }

    public void buscarProductosPorId(String idBuscado, JTextField txt_id_prod, JComboBox<Productos_MBar> cmb_productos, JTextField txt_valor) {
        String sql = "SELECT id_producto, nombre_producto, valor_unitario FROM productos_minibar WHERE id_producto = ?";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, idBuscado);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String idProd = rs.getString("id_producto");
                    String nombre = rs.getString("nombre_producto");
                    int valor = rs.getInt("valor_unitario");

                    // Crear objeto producto
                    Productos_MBar producto = new Productos_MBar(idProd, nombre, valor);

                    // Mostrar en los componentes
                    txt_id_prod.setText(idProd);
                    for (int i = 0; i < cmb_productos.getItemCount(); i++) {
                        Productos_MBar p = cmb_productos.getItemAt(i);
                        if (p.getId_prod().equals(idProd)) {
                            cmb_productos.setSelectedIndex(i);
                            break;
                        }
                    }
                    txt_valor.setText(String.valueOf(valor));

                } else {
                    JOptionPane.showMessageDialog(null, "No se encontró ningún producto con ese ID.");
                    txt_id_prod.setText("");
                    txt_valor.setText("");
                }
            }

        } catch (SQLException e) {
            ManejadorErrores.errorSelectSQL(e);
        }
    }

    public void buscarcliente(String cedula, JComboBox cmb_clientes, JTextField txt_id_cliente) {
        try {
            String cedulaIngresada = txt_id_cliente.getText().trim();

            if (cedulaIngresada.isEmpty()) {
                throw new NullPointerException("Campos vacíos");

            }

            buscarClientePorCedula(cedulaIngresada, cmb_clientes, txt_id_cliente);

        } catch (NullPointerException e) {
            ManejadorErrores.camposVacios(e); // Manejador de errores para campos vacíos

        } catch (ClienteNoExisteException ex) {
            ManejadorErrores.clienteNoExiste(ex);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar el cliente en la base de datos.");
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(Level.SEVERE, "Error SQL al buscar cliente", ex);
        }

    }

    public static void calcularTotalCuenta(JTable jtable_productos, JTextField txt_total_cuenta) {
        DefaultTableModel modelo = (DefaultTableModel) jtable_productos.getModel();
        double sumaTotal = 0;

        int columnaTotal = 4;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            Object valor = modelo.getValueAt(i, columnaTotal);
            if (valor != null) {
                try {
                    sumaTotal += Double.parseDouble(valor.toString());
                } catch (NumberFormatException e) {

                }
            }
        }

        txt_total_cuenta.setText(String.format(Locale.US, "%.2f", sumaTotal));
    }

    public static void registrarConsumoDesdeTabla(JTable jtable_productos, JTextField txt_id_cliente, JTextField txt_id_check,
            Timestamp fechaConsum, JTextField txt_total_cuenta) {

        if (jtable_productos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "La tabla de consumo está vacía.");
            return;
        }

        String sqlInsertConsumo = "INSERT INTO consumo_minibar (id_cliente, id_check, total_cuenta, fecha_consumo) VALUES (?, ?, ?, ?)";
        String sqlSelectIdConsumo = "SELECT id_consumo FROM consumo_minibar WHERE id_cliente = ? ORDER BY fecha_consumo DESC LIMIT 1";
        String sqlInsertDetalle = "INSERT INTO detalle_consumo_minibar (id_consumo, id_producto, cantidad, valor_unitario) VALUES (?, ?, ?, ?)";
        String sqlUpdateTotal = "UPDATE consumo_minibar SET total_cuenta = ? WHERE id_consumo = ?";

        DefaultTableModel model = (DefaultTableModel) jtable_productos.getModel();

        // 🔹 Nueva forma: conexión con try-with-resources
        try (Connection conn = ConexionBD.conectar()) {

            conn.setAutoCommit(false); // inicio de transacción

            double totalCuenta = Double.parseDouble(txt_total_cuenta.getText());
            String idCliente = txt_id_cliente.getText();
            String idCheck = txt_id_check.getText();

            Timestamp fechaParaInsert = (fechaConsum != null) ? fechaConsum : new Timestamp(System.currentTimeMillis());

            // 1️⃣ Insertar en consumo_minibar
            try (PreparedStatement psConsumo = conn.prepareStatement(sqlInsertConsumo)) {
                psConsumo.setString(1, idCliente);
                psConsumo.setString(2, idCheck);
                psConsumo.setDouble(3, totalCuenta);
                psConsumo.setTimestamp(4, fechaParaInsert);
                psConsumo.executeUpdate();
            }

            // 2️⃣ Recuperar id_consumo recién creado
            String idConsumo;
            try (PreparedStatement psSel = conn.prepareStatement(sqlSelectIdConsumo)) {
                psSel.setString(1, idCliente);
                try (ResultSet rs = psSel.executeQuery()) {
                    if (rs.next()) {
                        idConsumo = rs.getString("id_consumo");
                    } else {
                        throw new SQLException("No se pudo obtener el id_consumo recién insertado.");
                    }
                }
            }

            // 3️⃣ Insertar los detalles del consumo (y activar trigger para restar stock)
            try (PreparedStatement psDetalle = conn.prepareStatement(sqlInsertDetalle)) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String idProducto = model.getValueAt(i, 0).toString();
                    double valorUnitario = Double.parseDouble(model.getValueAt(i, 2).toString());
                    int cantidad = Integer.parseInt(model.getValueAt(i, 3).toString());
                    double subtotal = cantidad * valorUnitario;

                    psDetalle.setString(1, idConsumo);
                    psDetalle.setString(2, idProducto);
                    psDetalle.setInt(3, cantidad);
                    psDetalle.setDouble(4, valorUnitario);
                   
                    psDetalle.addBatch();
                }
                psDetalle.executeBatch();
            }

            // 4️⃣ Actualizar total en la tabla principal
            try (PreparedStatement psUpd = conn.prepareStatement(sqlUpdateTotal)) {
                psUpd.setDouble(1, totalCuenta);
                psUpd.setString(2, idConsumo);
                psUpd.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(null, "✅ Consumo registrado correctamente. ID: " + idConsumo);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "❌ Error al registrar consumo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
