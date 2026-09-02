package entregas.Aula04;

public class CalculadoraFrete {
    private static final double TAXA_FIXA = 8.00;
    private static final double VALOR_POR_KG = 2.00;
    private static final double ACRESCIMO_EXPRESSO = 0.50;

    private CalculadoraFrete() {
    }

    public static double calcular(double pesoKg, boolean entregaExpressa) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("O peso deve ser maior que zero.");
        }

        double valorFrete = TAXA_FIXA + (VALOR_POR_KG * pesoKg);

        if (entregaExpressa) {
            valorFrete *= 1 + ACRESCIMO_EXPRESSO;
        }

        return valorFrete;
    }
}
