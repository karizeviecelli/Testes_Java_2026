package entregas.Aula5;

import entregas.Aula05.Lampada;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class LampadaTest {
    @Test
    void lampadaDeveEstarDesligadaComIntensidadeZero() {
        Lampada lampada = new Lampada("Quarto");

        assertAll(
                () -> assertFalse(lampada.isLigada()),
                () -> assertEquals(0, lampada.getIntensidade())
        );
    }

    @Test
    void ligarDeveAlterarEstadoDaLampada() {
        // Arrange: crie uma lâmpada.
        Lampada lampada = new Lampada("Quarto");

        // Act: ligue a lâmpada.
        lampada.ligar();

        // Assert: verifique o estado e a intensidade.
        assertAll(
                () -> assertTrue(lampada.isLigada()),
                () -> assertEquals(100, lampada.getIntensidade())
        );
    }

    @Test
    void desligarDeveRestaurarIntensidadeParaZero() {
        Lampada lampada = new Lampada("Quarto");
        lampada.ligar();

        lampada.desligar();

        assertAll(
                () -> assertFalse(lampada.isLigada()),
                () -> assertEquals(0, lampada.getIntensidade())
        );
    }
}
