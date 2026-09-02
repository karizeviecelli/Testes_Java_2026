package entregas.Aula4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import entregas.Aula04.CalculadoraFrete;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class CalculadoraFreteTest {
    private static final double DELTA = 0.0001;

    @ParameterizedTest(name = "peso={0} kg, expressa={1} => frete esperado R$ {2}")
    @CsvSource({
            "1.0,  false, 10.00",
            "1.0,  true,  15.00",
            "2.5,  false, 13.00",
            "5.0,  true,  27.00",
            "10.0, false, 28.00",
            "0.01, false, 8.02",
            "0.01, true,  12.03"
    })
    void deveCalcularFrete(double pesoKg, boolean entregaExpressa, double valorEsperado) {
        double resultado = CalculadoraFrete.calcular(pesoKg, entregaExpressa);

        assertEquals(valorEsperado, resultado, DELTA);
    }

    @ParameterizedTest(name = "peso inválido: {0} kg")
    @ValueSource(doubles = {0.0, -0.01, -1.0, -10.0})
    void deveRejeitarPesoZeroOuNegativo(double pesoKg) {
        IllegalArgumentException excecao = assertThrows(
                IllegalArgumentException.class,() -> CalculadoraFrete.calcular(pesoKg, false));

        assertEquals("O peso deve ser maior que zero.", excecao.getMessage());
    }
}
