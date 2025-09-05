package LOGICA;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Tablas {
    public static void CentrarEncabezados(JTable tabla) {
        // Configurar el renderizador para centrar los encabezados sin cambiar el renderizador predeterminado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Roboto Light", Font.ITALIC, 14)); // Cambiar fuente
        header.setForeground(Color.BLACK); // Color del texto
        header.setBackground(Color.LIGHT_GRAY); // Color de fondo

        // Modificar la alineación de los encabezados sin cambiar su renderizador original
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }
    
   public static void aplicarEstilosTabla(JTable tabla, Font fuente, Color colorTexto, Color colorFondo) {
    DefaultTableCellRenderer renderCentradoConEstilos = new DefaultTableCellRenderer() {
        private final DecimalFormat formatoPrecio = new DecimalFormat("###,###,###");
        private int indiceTotalPrecio = -1; // índice de la columna "total_precio"

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setFont(fuente);
            setHorizontalAlignment(SwingConstants.CENTER);
            c.setForeground(colorTexto);
            c.setBackground(colorFondo);

            // Inicializar el índice solo una vez
            if (indiceTotalPrecio == -1) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if ("total_precio".equalsIgnoreCase(table.getColumnName(i))) {
                        indiceTotalPrecio = i;
                        break;
                    }
                }
            }

            // Aplicar formato si es la columna "total_precio" y el valor es numérico
            if (column == indiceTotalPrecio && value instanceof Number) {
                setText(formatoPrecio.format(value));
            }

            return c;
        }
    };

    // Aplicar el renderizador a todas las columnas de la tabla
    for (int i = 0; i < tabla.getColumnCount(); i++) {
        tabla.getColumnModel().getColumn(i).setCellRenderer(renderCentradoConEstilos);
    }
}

}
