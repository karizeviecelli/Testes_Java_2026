package entregas.Aula05;

public class Usuario {
    private String nome;
    private String email;
    private String telefone;
    private boolean ativo;

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.telefone = null;
        this.ativo = true;
    }

    public void definirTelefone(String telefone) {
        if (telefone == null || telefone.isEmpty()) {
            throw new IllegalArgumentException("O telefone é obrigatório.");
        }

        this.telefone = telefone;
    }

    public void desativar() {
        this.ativo = false;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
