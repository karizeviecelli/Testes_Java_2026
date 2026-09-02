package entregas.Aula5;

import entregas.Aula05.ReservaHotel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaHotelTest {
    @Test
    void verificarEstadoInicialReserva() {
        ReservaHotel reserva = new ReservaHotel("João", 3, 200.0);

        assertAll(
            () -> assertEquals("João", reserva.getHospode()),
            () -> assertEquals(3, reserva.getQuantidadeDiarias()),
            () -> assertEquals(200.0, reserva.getValorDiaria()),
            () -> assertFalse(reserva.isConfirmada()),
            () -> assertEquals("", reserva.getCodigoConfirmacao())
        );
    }

    @Test
    void calcularTotalReserva() {
        ReservaHotel reserva = new ReservaHotel("João", 5, 150.0);
        double total = 5 * 150.0;

        assertEquals(total, reserva.calcularTotal());
    }

    @Test
    void confirmarReservaComCodigoValido() {
        ReservaHotel reserva = new ReservaHotel("João", 2, 300.0);
        reserva.confirmar("ABC123");

        assertAll(
            () -> assertEquals("ABC123", reserva.getCodigoConfirmacao()),
            () -> assertNotNull(reserva.getCodigoConfirmacao()),
            () -> assertTrue(reserva.isConfirmada())
        );
    }

    @Test
    void hospedeNuloDeveLancarExcecao() {
        IllegalArgumentException excecaonNulo = assertThrows(IllegalArgumentException.class, () -> new ReservaHotel(null, 2, 100.0));

        assertEquals("O hóspede é obrigatório.", excecaonNulo.getMessage());
    }

    @Test
    void hospedeEmBrancoDeveLancarExcecao() {
        IllegalArgumentException excecaoEmBranco = assertThrows(IllegalArgumentException.class, () -> new ReservaHotel("", 2, 100.0));

        assertEquals("O hóspede é obrigatório.", excecaoEmBranco.getMessage());
    }

    @Test
    void quantidadeDiariasInvalidaDeveLancarExcecao() {
        IllegalArgumentException excecaoZero = assertThrows(IllegalArgumentException.class, () -> new ReservaHotel("Ana", 0, 100.0));
        assertEquals("A quantidade de diárias deve ser maior que zero.", excecaoZero.getMessage());
    }

    @Test
    void codigoNuloDeveLancarExcecao() {
        ReservaHotel reserva = new ReservaHotel("João", 3, 200.0);

        IllegalArgumentException excecaoNulo = assertThrows(IllegalArgumentException.class, () -> reserva.confirmar(null));

        assertEquals("O código não pode ser nulo ou em branco.", excecaoNulo.getMessage());
    }

    @Test
    void codigoEmBrancoDeveLancarExcecao() {
        ReservaHotel reserva = new ReservaHotel("João", 3, 200.0);

        IllegalArgumentException excecaoEmBranco = assertThrows(IllegalArgumentException.class, () -> reserva.confirmar(""));

        assertEquals("O código não pode ser nulo ou em branco.", excecaoEmBranco.getMessage());
    }

    @Test
    void confirmacaoDuplicadaDeveLancarExcecao() {
        ReservaHotel reserva = new ReservaHotel("João", 4, 250.0);
        reserva.confirmar("ABC123");

        IllegalStateException excecao = assertThrows(IllegalStateException.class, () -> reserva.confirmar("ABC123"));
        assertEquals("A reserva já está confirmada.", excecao.getMessage());
    }
}
