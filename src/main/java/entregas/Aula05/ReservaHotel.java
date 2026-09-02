package entregas.Aula05;

public class ReservaHotel {
    private String hospode;
    private int quantidadeDiarias;
    private double valorDiaria;
    private boolean confirmada;
    private String codigoConfirmacao;

    public ReservaHotel(String hospode, int quantidadeDiarias, double valorDiaria) {
        if (hospode == null || hospode.isBlank()) {
            throw new IllegalArgumentException("O hóspede é obrigatório.");
        }

        if (quantidadeDiarias <= 0) {
            throw new IllegalArgumentException("A quantidade de diárias deve ser maior que zero.");
        }

        if (valorDiaria <= 0) {
            throw new IllegalArgumentException("O valor da diária deve ser maior que zero.");
        }

        this.hospode = hospode;
        this.quantidadeDiarias = quantidadeDiarias;
        this.valorDiaria = valorDiaria;
        this.confirmada = false;
        this.codigoConfirmacao = null;
    }

    public double calcularTotal() {
        return quantidadeDiarias * valorDiaria;
    }

    public void confirmar(String codigo) {
        if (confirmada) {
            throw new IllegalStateException("A reserva já está confirmada.");
        }

        if (codigo == null || codigo.isEmpty()) {
            throw new IllegalArgumentException("O código não pode ser nulo ou em branco.");
        }

        this.confirmada = true;
        this.codigoConfirmacao = codigo;
    }

    public String getHospode() {
        return hospode;
    }

    public int getQuantidadeDiarias() {
        return quantidadeDiarias;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public boolean isConfirmada() {
        return confirmada;
    }

    public String getCodigoConfirmacao() {
        return codigoConfirmacao;
    }
}
