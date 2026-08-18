package entregas.Aula3;

import entregas.Aula3.Triangulo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TrianguloTest {
    @Test
    void calcularPerimetroDeveSomarOsTresLados() {
        // Arrange
        Triangulo triangulo = new Triangulo(3, 4, 5);

        // Act
        double perimetro = triangulo.calcularPerimetro();

        // Assert
        assertEquals(12.0, perimetro);
    }

    @Test
    void ladoNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> new Triangulo(-1, 4, 5));
    }

    @Test
    void triangulo3_4_5DeveTerLadosCorretos() {
        Triangulo t = new Triangulo(3, 4, 5);
        assertAll(
                () -> assertEquals(3.0, t.getLadoA()),
                () -> assertEquals(4.0, t.getLadoB()),
                () -> assertEquals(5.0, t.getLadoC())
        );
    }


}
