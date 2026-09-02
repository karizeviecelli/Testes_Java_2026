package entregas.Aula05;

public class Lampada {
    private String comodo;
    private boolean ligada;
    private int intensidade;

    public Lampada(String comodo) {
        this.comodo = comodo;
        this.ligada = false;
        this.intensidade = 0;
    }

    public void ligar() {
        this.ligada = true;
        this.intensidade = 100;
    }

    public void desligar() {
        this.ligada = false;
        this.intensidade = 0;
    }

    public String getComodo() {
        return comodo;
    }

    public boolean isLigada() {
        return ligada;
    }

    public int getIntensidade() {
        return intensidade;
    }
}
