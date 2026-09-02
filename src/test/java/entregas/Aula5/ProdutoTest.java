package entregas.Aula5;

import entregas.Aula05.Produto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {
    @Test
    void calcularValorEmEstoqueDeveMultiplicarPrecoPelaQuantidade() {
        // Arrange
        Produto produto = new Produto("Produto", 10.0, 5);

        // Act
        double valor = produto.calcularValorEmEstoque();

        // Assert
        assertEquals(50, valor, 0.001);
    }

    @Test
    void estoqueMaiorQueZeroDeveRetornarTrue() {
        Produto produto = new Produto("Produto", 10.0, 5);

        boolean temEstoque = produto.temEstoque();

        assertTrue(temEstoque);
    }

    @Test
    void estoqueIgualAZeroDeveRetornarFalse() {
        Produto produto = new Produto("Produto", 10.0, 0);

        boolean temEstoque = produto.temEstoque();

        assertFalse(temEstoque);
    }

    @Test
    void precoNegativoDeveLancarExcecao() {
        Exception excecao = assertThrows(IllegalArgumentException.class,
                () -> new Produto("Produto", -10.0, 5));

        assertEquals("O preço deve ser maior que zero.", excecao.getMessage());
    }

    @Test
    void estoqueNegativoDeveLancarExcecao() {
        Exception excecao = assertThrows(IllegalArgumentException.class,
                () -> new Produto("Produto", 10.0, -5));

        assertEquals("O estoque não pode ser negativo.", excecao.getMessage());
    }

}
