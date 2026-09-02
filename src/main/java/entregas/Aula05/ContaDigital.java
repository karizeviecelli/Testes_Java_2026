package entregas.Aula05;

public class ContaDigital {
    private String titular;
    private double saldo;

    public ContaDigital(String titular) {
        this.titular = titular;
        this.saldo = 0;
    }

    public void sacar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O saque deve ser maior que zero.");
        }

        if (valor > this.saldo) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        this.saldo -= valor;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O depósito deve ser maior que zero.");
        }

        this.saldo += valor;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }
}
