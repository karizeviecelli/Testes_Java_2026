package Aula2;

import atividades.Aula2.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {
    private Cliente cliente;

    @BeforeEach
    void configurar() {
        cliente = new Cliente("João", "joao@email.com");
    }

    @Test
    void deveEstarAtivo() {
        assertTrue(cliente.isAtivo());
    }

    @Test
    void deveEstarDesativado() {
        cliente.desativar();

        assertFalse(cliente.isAtivo());
    }

    @Test
    public void testarCliente(){
        Cliente cliente = new Cliente("Paulo","paulo@email.com");

        assertEquals("Paulo",cliente.getNome());
        assertEquals("paulo@email.com",cliente.getEmail());
    }
}
