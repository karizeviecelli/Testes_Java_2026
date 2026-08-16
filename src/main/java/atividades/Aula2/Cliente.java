package atividades.Aula2;

public class Cliente {
    private String nome;
    private String email;
    private boolean ativo;

    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.ativo = true;
    }

    public void desativar() {
        if (this.ativo) {
            this.ativo = false;
        } else {
            throw new IllegalStateException("O cliente já está desativado.");
        }
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAtivo() {
        return this.ativo;
    }
}
