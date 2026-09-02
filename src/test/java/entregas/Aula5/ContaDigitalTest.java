package entregas.Aula5;

import entregas.Aula05.ContaDigital;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContaDigitalTest {
    @Test
    void saldoInicialDeveSerZero() {
        ContaDigital conta = new ContaDigital("João");
        assertEquals(0, conta.getSaldo(), 0.001);
    }

    @Test
    void depositarDeveAumentarOSaldo() {
        ContaDigital conta = new ContaDigital("João");
        conta.depositar(100);

        assertEquals(100, conta.getSaldo(), 0.001);
    }

    @Test
    void sacarDeveDiminuirOSaldo() {
        ContaDigital conta = new ContaDigital("João");

        conta.depositar(100);
        conta.sacar(50);

        assertEquals(50, conta.getSaldo(), 0.001);
    }

    @Test
    void depositoZeroDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("João");

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.depositar(0));

        assertEquals("O depósito deve ser maior que zero.", excecao.getMessage());

    }

    @Test
    void depositoNegativoDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("João");

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.depositar(-50));

        assertEquals("O depósito deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    void saqueNegativoDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("João");

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.sacar(-50));

        assertEquals("O saque deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    void saqueZeroDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("João");

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.sacar(0));

        assertEquals("O saque deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    void saqueMaiorQueSaldoDeveLancarExcecao() {
        ContaDigital conta = new ContaDigital("João");
        conta.depositar(100);

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.sacar(150));

        assertAll(
            () -> assertEquals("Saldo insuficiente.", excecao.getMessage()),
            () -> assertEquals(100, conta.getSaldo(), 0.001)
        );
    }
}
