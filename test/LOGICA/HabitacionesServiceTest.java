package LOGICA;

import org.junit.*;
import static org.junit.Assert.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HabitacionesServiceTest {

    private HabitacionesService service;
    private JTable tabla;

    @Before
    public void setUp() {
        System.out.println("🔧 Preparando entorno de prueba...");
        service = new HabitacionesService();

        // Crear tabla simulada con modelo de columnas
        tabla = new JTable(new DefaultTableModel(
                new Object[]{"id_habitacion", "tipo_habitacion", "nombre_habitacion", "precio_noche", "estado"}, 0
        ));
    }

    @Test
    public void testMostrarHabitacionesEnTabla() {
        System.out.println("️ Ejecutando test: mostrarHabitacionesEnTabla()");
        service.mostrarHabitacionesEnTabla(tabla);

        int filas = tabla.getRowCount();
        System.out.println(" Filas cargadas: " + filas);

        // Validar que haya al menos una habitación cargada
        assertTrue("La tabla debería contener al menos una habitación", filas > 0);
    }

    @Test
    public void testActualizarEstadoHabitacionLibre() {
        System.out.println("️ Ejecutando test: actualizarEstadoHabitacion(id, 'Libre')");
        int idHabitacionPrueba = 1; // Asegúrate de que exista una habitación con este ID
        boolean resultado = service.actualizarEstadoHabitacion(idHabitacionPrueba, "Libre");

        System.out.println("Resultado: " + resultado);
        assertTrue("La actualización del estado a 'Libre' debería ser exitosa", resultado);
    }

    @Test
    public void testActualizarEstadoHabitacionMantenimiento() {
        System.out.println("️ Ejecutando test: actualizarEstadoHabitacion(id, 'Mantenimiento')");
        int idHabitacionPrueba = 1;
        boolean resultado = service.actualizarEstadoHabitacion(idHabitacionPrueba, "Mantenimiento");

        System.out.println("Resultado: " + resultado);
        assertTrue("La actualización del estado a 'Mantenimiento' debería ser exitosa", resultado);
    }

    @Test
    public void testActualizarEstadoHabitacionInexistente() {
        System.out.println("️ Ejecutando test: actualizarEstadoHabitacion(id inexistente)");
        int idHabitacionInexistente = 9999;
        boolean resultado = service.actualizarEstadoHabitacion(idHabitacionInexistente, "Libre");

        System.out.println("Resultado esperado: false -> " + resultado);
        assertFalse("No debería actualizar una habitación inexistente", resultado);
    }

    @Test
    public void testAgregarMenuContextual() {
        System.out.println(" Ejecutando test: agregarMenuContextual()");
        service.agregarMenuContextual(tabla);

        // Verificar que la tabla tenga un MouseListener asignado
        int listenerCount = tabla.getMouseListeners().length;
        System.out.println("️ Listeners detectados: " + listenerCount);

        assertTrue("La tabla debería tener al menos un MouseListener asignado", listenerCount > 0);
    }

    @After
    public void tearDown() {
        System.out.println(" Limpieza posterior a las pruebas...");
        service = null;
        tabla = null;
    }
}
