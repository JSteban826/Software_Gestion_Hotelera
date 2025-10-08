package LOGICA;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RegistroServiceTest {

    private RegistroService registroService;

    @Before
    public void setUp() {
        registroService = new RegistroService();
    }

    // ✅ Test generar contraseña
    @Test
    public void testGenerarContraseña() {
        String usuario = "java";
        String expected = "kbwb"; // cada letra +1 en ASCII
        String actual = registroService.generarContraseña(usuario);

        assertEquals("La contraseña generada no coincide. "
                   + "Se esperaba: " + expected 
                   + " pero se obtuvo: " + actual, 
                   expected, actual);
    }

    // ✅ Test validar campos
    @Test
    public void testValidarCampos() {
        assertTrue("Los campos válidos no fueron reconocidos correctamente",
                   registroService.validarCampos("usuario", "1234"));

        assertFalse("El método permitió un usuario vacío, debería fallar",
                    registroService.validarCampos("", "1234"));

        assertFalse("El método permitió una contraseña vacía, debería fallar",
                    registroService.validarCampos("usuario", ""));
    }

    // ✅ Test insertar usuario con nombre único
    @Test
    public void testInsertarUsuario() throws Exception {
        // Genera un usuario único para no chocar con la PK
        String username = "testUser_" + System.currentTimeMillis();

        boolean resultado = registroService.insertarUsuario(username, "12345", 2);

        assertTrue("El usuario no se insertó correctamente en la BD", resultado);
    }
}
