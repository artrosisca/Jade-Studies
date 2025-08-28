package atv2;

import jade.core.behaviours.Behaviour;
import java.util.Arrays;

public class MeuComportamento extends Behaviour {
    private int acao = 0;

    public MeuComportamento(int[][] matriz) {

    }

    @Override
    public void action() {
        switch (acao) {
            case 1:
                System.out.println("Iniciando 1"); break;
            case 2:
                System.out.println("Iniciando 2"); break;
            case 3:
                System.out.println("Iniciando 3"); break;
            case 4:
                System.out.println("Iniciando 4"); break;
        }
        this.acao++;
    }

    @Override
    public boolean done() {
        return acao == 4;
    }
}
