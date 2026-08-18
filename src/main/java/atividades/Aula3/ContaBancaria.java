package atividades.Aula3;

public class ContaBancaria {
    private String titular;
    private String numeroConta;
    private double saldo;

    // Construtor para uma conta com saldo inicial.
    public ContaBancaria(String titular, String numeroConta, double saldoInicial) {
        this.titular = titular;
        this.numeroConta = numeroConta;
        this.saldo = saldoInicial;
    }

    // Sobrecarga: conta sem valor começa com saldo zero.
    public ContaBancaria(String titular, String numeroConta) {
        this(titular, numeroConta, 0.0);
    }

    public void depositar(double valor) {
        // Rejeita depósito igual ou menor que zero.
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    "O valor do depósito deve ser maior que zero.");
        }

        saldo += valor;
    }

    public void sacar(double valor) {
        // Sem esta validação, sacar -50 aumentaria o saldo.
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }

        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        saldo -= valor;
    }

    public String getTitular() {
        return titular;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public double getSaldo() {
        return saldo;
    }
}