package entregas.Aula3;

import entregas.Aula3.Circulo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CirculoTest {
    @Test
    public void deveCalcularPerimetro() {
        Circulo circulo = new Circulo(5);

        double perimetro = circulo.calcularPerimetro();

        assertEquals(2 * Math.PI * 5, perimetro);
    }

    @Test
    public void raioNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Circulo(-1));
    }

    @Test
    public void deveValidarRaio() {
        Circulo circulo = new Circulo(5);

        assertAll(
                () -> assertTrue(circulo.getRaio() > 0),
                () -> assertFalse(circulo.getRaio() == 0)
        );
    }
}
