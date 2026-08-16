package atividades.Aula1;

public class ContaBancaria {
    private String titular;
    private String numeroConta;
    private double saldo;

    public ContaBancaria(String titular, String numeroConta) {
        this.titular = titular;
        this.numeroConta = numeroConta;
        this.saldo = 0.0;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        this.saldo += valor;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que zero.");
        }
        if (valor > this.saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para este saque.");
        }
        this.saldo -= valor;
    }

    public String getTitular() { return titular; }
    public String getNumeroConta() { return numeroConta; }
    public double getSaldo() { return saldo; }

}