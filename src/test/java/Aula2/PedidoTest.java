package Aula2;

import atividades.Aula2.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PedidoTest {
    private Pedido pedido;

    @BeforeEach
    void configurar() {
        pedido = new Pedido();
    }

    @Test
    void pedidoDeveNascerComValorZero() {
        assertEquals(0.0, pedido.getValorTotal());
    }

    @Test
    void adicionarItemDeveSomarAoTotal() {
        pedido.adicionarItem(50.0);
        pedido.adicionarItem(30.0);
        assertEquals(80.0, pedido.getValorTotal());
    }

}
