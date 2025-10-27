package LOGICA;

import PERSISTENCIA.ConexionBD;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.toedter.calendar.JCalendar;
import java.awt.Font;

// Librerías básicas de JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.SimpleTimePeriod;

public class Panel_graficos {

    public static void obtenerEstadosHabitaciones(JPanel jPanel_grafica_hab) {
        Map<String, Integer> datos = new HashMap<>();
        String sql = "SELECT estado, COUNT(*) AS cantidad FROM habitaciones GROUP BY estado";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                datos.put(rs.getString("estado"), rs.getInt("cantidad"));
            }

            // Crear dataset
            DefaultPieDataset dataset = new DefaultPieDataset();
            for (Map.Entry<String, Integer> entry : datos.entrySet()) {
                dataset.setValue(entry.getKey(), entry.getValue());
            }

            // Crear el gráfico
            JFreeChart chart = ChartFactory.createPieChart(
                    "Estado de Habitaciones",
                    dataset,
                    true, // Mostrar leyenda
                    true, // Tooltips
                    false // URLs
            );

            // Personalizar el gráfico
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.WHITE);

            // Mostrar nombre, cantidad y porcentaje
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
            // {0} = nombre, {1} = valor, {2} = porcentaje

            // Colores personalizados por estado (ajústalos a tu DB)
            plot.setSectionPaint("Libre", new Color(100, 149, 237));        // Cornflower Blue (azul medio)
            plot.setSectionPaint("ocupado", new Color(70, 130, 180));       // Steel Blue (azul acero)
            plot.setSectionPaint("Mantenimiento", new Color(25, 25, 112));  // Midnight Blue (azul oscuro elegante)

            // Crear y configurar panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(350, 210));

            // Agregar al JPanel del formulario
            jPanel_grafica_hab.removeAll();
            jPanel_grafica_hab.setLayout(new BorderLayout());
            jPanel_grafica_hab.add(chartPanel, BorderLayout.CENTER);
            jPanel_grafica_hab.validate();

        } catch (Exception e) {
            System.out.println("Error al obtener datos o generar gráfica: " + e.getMessage());
        }
    }

    public static void mostrarReservasActivas(JPanel jPanel_grafico_reservas) {

        TaskSeriesCollection dataset = new TaskSeriesCollection();
        TaskSeries series = new TaskSeries("Reservas");

        String sql = "SELECT id_habitacion, fecha_entrada, fecha_salida FROM reservas ORDER BY id_habitacion";

        try (Connection conn = ConexionBD.conectar(); PreparedStatement statement = conn.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String habitacion = "Hab. " + rs.getInt("id_habitacion");

                java.sql.Date sqlInicio = rs.getDate("fecha_entrada");
                java.sql.Date sqlFin = rs.getDate("fecha_salida");

                if (sqlInicio != null && sqlFin != null) {
                    Date inicio = new Date(sqlInicio.getTime());
                    Date fin = new Date(sqlFin.getTime());
                    Task tarea = new Task(habitacion, new SimpleTimePeriod(inicio, fin));
                    series.add(tarea);
                }
            }

            dataset.add(series);

            // Crear gráfico Gantt
            JFreeChart chart = ChartFactory.createGanttChart(
                    "Cronograma de Reservas",
                    "Habitaciones",
                    "Fechas",
                    dataset,
                    true,
                    true,
                    false
            );

            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);

            // Eje de fechas (horizontal)
            DateAxis axis = (DateAxis) plot.getRangeAxis();
            axis.setDateFormatOverride(new SimpleDateFormat("dd/MM"));

            // 🔹 Establecer el rango del mes actual automáticamente
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            java.util.Date inicioMes = cal.getTime();

            cal.add(Calendar.MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            java.util.Date finMes = cal.getTime();

            //axis.setAutoRange(false);
            axis.setRange(inicioMes, finMes);

            // Asignar color a la serie
            plot.getRenderer().setSeriesPaint(0, new Color(70, 130, 180)); // SteelBlue

            // Panel del gráfico
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(350, 210));

            jPanel_grafico_reservas.removeAll();
            jPanel_grafico_reservas.setLayout(new BorderLayout());
            jPanel_grafico_reservas.add(chartPanel, BorderLayout.CENTER);
            jPanel_grafico_reservas.validate();

        } catch (Exception e) {
            System.out.println("Error al generar gráfico Gantt: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void MostrarClientes(JLabel lbl_cl, JLabel lbl_cl_rv) throws SQLException {
        String sqlTotal = "SELECT COUNT(*) AS total FROM clientes";
        String sqlConReserva = "SELECT COUNT(DISTINCT id_cliente) AS con_reserva FROM reservas";

        int total = 0;
        int conReserva = 0;

        try (Connection conn = ConexionBD.conectar()) {

            // Total de clientes
            try (PreparedStatement st = conn.prepareStatement(sqlTotal); ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
                lbl_cl.setText(String.valueOf(total));
            }

            // Clientes con reserva
            try (PreparedStatement st = conn.prepareStatement(sqlConReserva); ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    conReserva = rs.getInt("con_reserva");
                }
                lbl_cl_rv.setText(String.valueOf(conReserva));
            }

        }
    }

    public static void mostrarCalendario(JPanel jPanel_calendario) {
        jPanel_calendario.removeAll();
        jPanel_calendario.setLayout(new BorderLayout());

        JCalendar calendario = new JCalendar();

        // Personalización (opcional)
        calendario.setTodayButtonVisible(true);
        calendario.setWeekOfYearVisible(false);
        calendario.setSundayForeground(Color.BLUE);
        calendario.setDecorationBackgroundColor(new Color(240, 240, 240));
        calendario.setWeekdayForeground(new Color(0, 51, 102));
        calendario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        calendario.setPreferredSize(new Dimension(350, 284));
        calendario.setBackground(new Color(230, 240, 255)); // Azul claro

        
        jPanel_calendario.add(calendario, BorderLayout.CENTER);
        jPanel_calendario.revalidate();
        jPanel_calendario.repaint();
    }

}

/*plot.setSectionPaint("Azul Claro",       new Color(173, 216, 230)); // LightBlue
plot.setSectionPaint("Celeste",          new Color(135, 206, 250)); // SkyBlue
plot.setSectionPaint("Azul Acero",       new Color(70, 130, 180));  // SteelBlue
plot.setSectionPaint("Azul Real",        new Color(65, 105, 225));  // RoyalBlue
plot.setSectionPaint("Azul Medio",       new Color(0, 0, 205));     // MediumBlue
plot.setSectionPaint("Azul Profundo",    new Color(0, 0, 139));     // DarkBlue
plot.setSectionPaint("Azul Petróleo",    new Color(0, 128, 128));   // Teal
plot.setSectionPaint("Azul Cadete",      new Color(95, 158, 160));  // CadetBlue
plot.setSectionPaint("Azul Niebla",      new Color(100, 149, 237)); // CornflowerBlue
plot.setSectionPaint("Azul Cobalto",     new Color(0, 71, 171));    // Cobalt*/
