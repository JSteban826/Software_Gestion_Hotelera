package LOGICA;

import LOGICA.LoginService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginServiceTest {

    private LoginService loginService;

    @Before
    public void setUp() {
        loginService = new LoginService();
    }

    @Test
    public void testLoginAdministradorValido() {
        // 👇 Este test supone que en tu BD existe usuario "admin" con pass "123"
        String rol = loginService.realizarLogin("adminHotel", "admin1234");
        assertEquals("Administrador", rol);
    }

    @Test
    public void testLoginRecepcionistaValido() {
        String rol = loginService.realizarLogin("dayis", "ebzjt");
        assertEquals("Recepcionista", rol);
    }

    @Test
    public void testLoginInvalido() {
        String rol = loginService.realizarLogin("pedro", "uehndnc");
        assertNull(rol);
    }

    @Test
    public void testManejoDeExcepciones() {
        String rol = loginService.realizarLogin(null, null);
        assertNull(rol);
    }
}
