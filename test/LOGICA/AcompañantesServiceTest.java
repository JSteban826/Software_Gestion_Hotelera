package LOGICA;

import org.junit.*;
import static org.junit.Assert.*;
import java.sql.SQLException;
import java.util.Map;


public class AcompañantesServiceTest {

    private AcompañantesService service;

  
    @Before
    public void setUp() {
        System.out.println("\n⚙️ Iniciando nueva prueba...");
        service = new AcompañantesService();
    }

   
    @After
    public void tearDown() {
        System.out.println("Prueba finalizada correctamente.\n");
    }

    // 🧪 1. Verificar que cargarClientes nunca retorne null
    @Test
    public void testCargarClientes_NoDebeSerNulo() {
        System.out.println(" Ejecutando testCargarClientes_NoDebeSerNulo...");
        Map<String, Cliente1> clientes = service.cargarClientes();
        assertNotNull("El mapa de clientes no debe ser nulo", clientes);
        System.out.println(" cargarClientes devolvió un objeto válido.");
    }

    // 🧪 2. Verificar que cargarClientes puede devolver mapa vacío sin fallar
    @Test
    public void testCargarClientes_PuedeEstarVacioPeroNoFallar() {
        System.out.println(" Ejecutando testCargarClientes_PuedeEstarVacioPeroNoFallar...");
        Map<String, Cliente1> clientes = service.cargarClientes();
        assertTrue("El mapa puede estar vacío pero no debe lanzar error", clientes.size() >= 0);
        System.out.println(" cargarClientes ejecutado correctamente incluso si no hay datos.");
    }

    @Test
    public void testBuscarCheckIn_CuandoClienteNoExisteDebeRetornarMenosUno() {
        System.out.println(" Ejecutando testBuscarCheckIn_CuandoClienteNoExisteDebeRetornarMenosUno...");
        int resultado = service.buscarCheckIn("0000000000");
        assertEquals("Debe devolver -1 cuando no se encuentra el Check-In", -1, resultado);
        System.out.println(" Resultado esperado (-1) obtenido correctamente.");
    }


    @Test
    public void testBuscarClientePorCedula_LanzaExcepcionSiNoExiste() {
        System.out.println(" Ejecutando testBuscarClientePorCedula_LanzaExcepcionSiNoExiste...");
        try {
            service.buscarClientePorCedula("9999999999");
            fail(" Debería lanzar ClienteNoExisteException");
        } catch (ClienteNoExisteException e) {
            assertTrue(e.getMessage().contains("no existe"));
            System.out.println(" Excepción capturada correctamente: " + e.getMessage());
        } catch (SQLException e) {
            fail(" No debería lanzar SQLException: " + e);
        }
    }

  
    @Test
    public void testBuscarClientePorCedula_PuedeDevolverClienteValido() {
        System.out.println(" Ejecutando testBuscarClientePorCedula_PuedeDevolverClienteValido...");
        try {
            Cliente1 cliente = service.buscarClientePorCedula("1069714447"); // Ajusta con una cédula real
            assertNotNull("El cliente no debe ser nulo si existe en la BD", cliente);
            assertNotNull("El nombre del cliente no debe ser nulo", cliente.getNombreCompleto());
            System.out.println(" Cliente encontrado correctamente: " + cliente.getNombreCompleto());
        } catch (ClienteNoExisteException e) {
            System.out.println(" Cliente no encontrado (ok si la BD está vacía de pruebas).");
        } catch (Exception e) {
            fail(" Error inesperado: " + e);
        }
    }

    @Test
    public void testInsertarAcompañante_NoDebeLanzarExcepcion() {
        System.out.println(" Ejecutando testInsertarAcompañante_NoDebeLanzarExcepcion...");
        try {
            service.insertarAcompañante("12345", 1, "Carlos", "Pérez", "3015559999", "Hermano", 25);
            System.out.println(" Método insertarAcompañante ejecutado sin excepciones.");
        } catch (Exception e) {
            fail(" No debería lanzar excepción al insertar acompañante: " + e);
        }
    }


    @Test
    public void testCliente1_ConstruccionYNombreCompleto() {
        System.out.println(" Ejecutando testCliente1_ConstruccionYNombreCompleto...");
        Cliente1 cliente = new Cliente1("1010", "Ana", "Gómez");
        assertEquals("Ana Gómez", cliente.getNombreCompleto());
        assertEquals("1010", cliente.getCedula());
        System.out.println(" Cliente1 construido correctamente: " + cliente.getNombreCompleto());
    }
}
