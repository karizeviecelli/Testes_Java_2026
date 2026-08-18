package Aula3;

// Importa as asserções estáticas do JUnit 5.
import atividades.Aula3.ContaBancaria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContaBancariaTest {
    @Test
    void depositarValorValidoDeveAumentarSaldo() {
        // ARRANGE: conta com saldo inicial zero.
        ContaBancaria conta = new ContaBancaria("Maria", "123");

        // ACT: executa a ação testada.
        conta.depositar(100.0);

        // ASSERT: esperado, obtido e tolerância para double.
        assertEquals(100.0, conta.getSaldo(), 0.001);
    }

    @Test
    void sacarValorMaiorQueSaldoDeveLancarExcecao() {
        // ARRANGE: a nova conta começa com saldo 0.0.
        ContaBancaria conta = new ContaBancaria("Maria", "123");

        // ACT + ASSERT: executa sacar() e captura a exceção.
        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.sacar(50.0));

        // A exceção ocorreu e foi capturada, então esta linha roda.
        System.out.println("Mensagem capturada: " + excecao.getMessage());

        // A mensagem deve coincidir exatamente com a classe.
        assertEquals("Saldo insuficiente", excecao.getMessage());
    }

    @Test
    void sacarComSaldoDisponivelDeveReduzirSaldo() {
        // ARRANGE: prepara saldo suficiente.
        ContaBancaria conta = new ContaBancaria("Maria", "123");

        conta.depositar(100.0);

        // ACT + ASSERT: deve terminar normalmente.
        assertDoesNotThrow(() -> conta.sacar(40.0));

        // ASSERT: 100 - 40 = 60.
        assertEquals(60.0, conta.getSaldo(), 0.001);
    }

    @Test
    void depositarZeroDeveLancarExcecao() {
        ContaBancaria conta = new ContaBancaria("Maria", "123");

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class, () -> conta.depositar(0.0));

        assertEquals("O valor do depósito deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    void sacarValorNegativoDeveLancarExcecao() {
        ContaBancaria conta = new ContaBancaria("Maria", "123", 100.0);

        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-20.0));

    }

    @Test
    void construtorDeveInicializarTodosOsAtributos() {
        // ARRANGE + ACT
        ContaBancaria conta = new ContaBancaria("João", "99999-9");

        // ASSERT: todas as verificações serão executadas.
        assertAll(
                "Dados iniciais da conta",
                () -> assertEquals("João", conta.getTitular()),
                () -> assertEquals("99999-9", conta.getNumeroConta()),
                () -> assertEquals(0.0, conta.getSaldo(), 0.001)
        );
    }

}
