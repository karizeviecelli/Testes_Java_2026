package entregas.Aula5;

import entregas.Aula05.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {
    @Test
    void usuarioDeveTerTelefoneNuloEEstarAtivo() {
        Usuario usuario = new Usuario("João", "joao@email.com");

        assertAll(
                () -> assertNull(usuario.getTelefone()),
                () -> assertTrue(usuario.isAtivo())
        );
    }

    @Test
    void definirTelefoneDeveAtualizarTelefone() {
        Usuario usuario = new Usuario("João", "joao@email.com");
        usuario.definirTelefone("123456789");

        assertAll(
                () -> assertNotNull(usuario.getTelefone()),
                () -> assertEquals("123456789", usuario.getTelefone())
        );
    }

    @Test
    void definirTelefoneComValorNuloDeveLancarExcecao() {
        Usuario usuario = new Usuario("João", "joao@email.com");

        IllegalArgumentException excececao = assertThrows(IllegalArgumentException.class, () -> usuario.definirTelefone(null));

        assertEquals("O telefone é obrigatório.", excececao.getMessage());
    }

    @Test
    void definirTelefoneComValorVazioDeveLancarExcecao() {
        Usuario usuario = new Usuario("João", "joao@email.com");

        IllegalArgumentException excececao = assertThrows(IllegalArgumentException.class, () -> usuario.definirTelefone(""));

        assertEquals("O telefone é obrigatório.", excececao.getMessage());
    }

    @Test
    void desativarDeveAlterarStatusParaInativo() {
        Usuario usuario = new Usuario("João", "joao@email.com");
        usuario.desativar();

        assertFalse(usuario.isAtivo());
    }

    @Test
    void verificarDadosIniciaisDoUsuario() {
        Usuario usuario = new Usuario("João", "joao@email.com");

        assertAll(
                () -> assertEquals("João", usuario.getNome()),
                () -> assertEquals("joao@email.com", usuario.getEmail()),
                () -> assertNull(usuario.getTelefone()),
                () -> assertTrue(usuario.isAtivo())
        );
    }
}