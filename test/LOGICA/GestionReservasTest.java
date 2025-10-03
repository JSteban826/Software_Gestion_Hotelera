/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package LOGICA;

import javax.swing.JTextField;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Astrid Acosta
 */
public class GestionReservasTest {
    
    public GestionReservasTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of obtenerCorreoCliente method, of class GestionReservas.
     */
    @Test
    public void testObtenerCorreoCliente() {
        System.out.println("obtenerCorreoCliente");
        int idCliente = 0;
        GestionReservas instance = new GestionReservas();
        String expResult = "";
        String result = instance.obtenerCorreoCliente(idCliente);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of obtenerMontoReserva method, of class GestionReservas.
     */
    @Test
    public void testObtenerMontoReserva() {
        System.out.println("obtenerMontoReserva");
        int idReserva = 0;
        GestionReservas instance = new GestionReservas();
        double expResult = 0.0;
        double result = instance.obtenerMontoReserva(idReserva);
        assertEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of convertirPesosADolares method, of class GestionReservas.
     */
    @Test
    public void testConvertirPesosADolares() {
        System.out.println("convertirPesosADolares");
        double totalCOP = 0.0;
        GestionReservas instance = new GestionReservas();
        String expResult = "";
        String result = instance.convertirPesosADolares(totalCOP);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of procesarPago method, of class GestionReservas.
     */
    @Test
    public void testProcesarPago() throws Exception {
        System.out.println("procesarPago");
        int idCliente = 0;
        int idReserva = 0;
        GestionReservas instance = new GestionReservas();
        instance.procesarPago(idCliente, idReserva);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of capturarDatosYRealizarPago method, of class GestionReservas.
     */
    @Test
    public void testCapturarDatosYRealizarPago() throws Exception {
        System.out.println("capturarDatosYRealizarPago");
        JTextField txtIdCliente = null;
        JTextField txtIdReserva = null;
        GestionReservas instance = new GestionReservas();
        instance.capturarDatosYRealizarPago(txtIdCliente, txtIdReserva);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clienteExiste method, of class GestionReservas.
     */
    @Test
    public void testClienteExiste() throws Exception {
        System.out.println("clienteExiste");
        String cedula = "";
        GestionReservas instance = new GestionReservas();
        boolean expResult = false;
        boolean result = instance.clienteExiste(cedula);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
