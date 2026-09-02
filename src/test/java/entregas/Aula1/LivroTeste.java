package entregas.Aula1;

import entregas.Aula1.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LivroTest {

    private Livro livro;

    @BeforeEach
    void configurar() {
        livro = new Livro("Livro 1", "Autor 1", 1999, 2);
    }

    @Test
    void deveEmprestarLivro() {
        livro.emprestar();

        assertEquals(1, livro.getQuantidade());
    }

    @Test
    void deveDevolverLivro() {
        livro.emprestar();
        livro.devolver();

        assertEquals(2, livro.getQuantidade());
    }
}