package entregas.Aula2;

public class Pedido {
    private double valorTotal;

    public void adicionarItem(double valor) {
        if (valor <= 0)
            throw new IllegalArgumentException("Valor do item deve ser positivo.");
        this.valorTotal += valor;
    }

    public double getValorTotal() { return valorTotal; }
}
