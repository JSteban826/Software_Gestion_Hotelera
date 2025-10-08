/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package LOGICA;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ClienteServiceTest {

    private ClienteService clienteService;

    @Before
    public void setUp() {
        clienteService = new ClienteService();
    }

@Test
public void testInsertarClienteCorrecto() throws Exception {
    boolean resultado = clienteService.insertarCliente("199", "Pau", "Acosta", "pau@test.com", "3001234567");
    assertTrue("Cliente debería ser ingresado correctamente", resultado);
    System.out.println(" Cliente ingresado correctamente en la BD");
}

@Test(expected = Exception.class)
public void testInsertarClienteDuplicado() throws Exception {
    clienteService.insertarCliente("2031", "Juan", "Perez", "juan@test.com", "3112233445");
    clienteService.insertarCliente("2031", "Juan", "Perez", "juan@test.com", "3112233445");
    fail(" Debería lanzar excepción por cliente duplicado");
    System.out.println("⚠️ Se detectó intento de cliente duplicado y se lanzó la excepción esperada");
}

@Test(expected = NullPointerException.class)
public void testCedulaVacia() throws Exception {
    clienteService.insertarCliente("", "Maria", "Lopez", "maria@test.com", "3009876543");
    fail(" Debe lanzar excepción porque la cédula está vacía");
    System.out.println(" Validación correcta: cédula vacía no permitida");
}

@Test(expected = NullPointerException.class)
public void testNombreVacio() throws Exception {
    clienteService.insertarCliente("3001", "", "Martinez", "martinez@test.com", "3125556677");
    fail(" Debe lanzar excepción porque el nombre está vacío");
    System.out.println("️ Validación correcta: nombre vacío no permitido");
}



}
