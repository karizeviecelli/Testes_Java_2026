package entregas.Aula05;

public class Produto {
    private String nome;
    private double preco;
    private int quantidadeEstoque;

    public Produto(String nome, double preco, int quantidadeEstoque) {
        if (preco < 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        if (quantidadeEstoque < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }

        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public double calcularValorEmEstoque() {
        return preco * quantidadeEstoque;
    }

    public boolean temEstoque() {
        return quantidadeEstoque > 0;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
}
