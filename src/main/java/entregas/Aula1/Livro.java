package entregas.Aula1;

public class Livro {
    private String titulo;
    private String autor;
    private int anoPublicacao;
    private int quantidade;
    private boolean emprestado;

    public Livro(String titulo, String autor, int anoPublicacao, int quantidade) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.quantidade = quantidade;
        this.emprestado = false;
    }

    // teste de empréstimo disponível e indisponível
    public void emprestar() {
        if (this.quantidade > 0) {
            this.quantidade -= 1;
            this.emprestado = true;
        } else {
            throw new IllegalArgumentException("Sem livros disponíveis para empréstimo.");
        }
    }

    // teste de devolução disponível e indisponível
    public void devolver() {
        if (this.emprestado) {
            this.quantidade += 1;
            this.emprestado = false;
        } else {
            throw new IllegalArgumentException("O livro não está emprestado.");
        }
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }
}
