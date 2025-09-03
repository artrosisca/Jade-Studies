// src/atv2/MeuComportamento.java
package atv2;

import jade.core.behaviours.Behaviour;
import java.util.Arrays;

public class MeuComportamento extends Behaviour {
    private int acao = 0;
    private int[][] matriz;

    public MeuComportamento(int[][] matriz) {
        this.matriz = matriz;
        System.out.println("Comportamento recebeu a matriz.");
    }

    @Override
    public void action() {
        System.out.println("\nExecutando ação " + (acao + 1));

        switch (acao) {
            case 0:
                System.out.println("Lendo a matriz no primeiro passo:");
                System.out.println(Arrays.deepToString(this.matriz));
                break;
            case 1:
                System.out.println("Modificando a posição [0][0] para 99");
                break;
            case 2:
                System.out.println("Lendo a matriz novamente para ver a mudança:");
                System.out.println(Arrays.deepToString(this.matriz));
                break;
            case 3:
                System.out.println("Finalizando...");
                break;
        }
        this.acao++;
    }

    @Override
    public boolean done() {
        if (acao == 4) {
            System.out.println("Comportamento concluído.");
            return true;
        }
        return false;
    }
}