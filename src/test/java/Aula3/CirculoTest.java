package Aula3;

import atividades.Aula3.Circulo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CirculoTest {
    @Test
    public void calcularPerimetroDeveCalcularPerimetro() {
        Circulo circulo = new Circulo(5);

        double perimetro = circulo.calcularPerimetro();

        assertEquals(2 * Math.PI * 5, perimetro);
    }

    @Test
    public void raio5_DeveTerRaioCorreto() {
        Circulo circulo = new Circulo(1);

        assertTrue(circulo.getRaio() != 0);
    }

    @Test
    public void raioNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Circulo(-1));
    }

    @Test
    public void raio5_DeveCalcularArea() {
        Circulo circulo = new Circulo(5);

        assertAll(
                () -> assertEquals(5, circulo.getRaio()),
                () -> assertEquals(Math.PI * 25, circulo.calcularArea())
        );
    }
}
