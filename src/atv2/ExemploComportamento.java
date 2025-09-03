package atv2;

import jade.core.Agent;
import java.util.Arrays;

public class ExemploComportamento extends Agent{

    private int[][] matriz;

    protected void setupMatriz(){
        for( int i = 0; i < matriz.length; i++) {
            for( int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = 0;
                System.out.print(matriz[i][j] + " ");
            }
            System.out.print("\n");
        }
    }

    @Override
    protected void setup() {
        System.out.println("Iniciando comportamento..." + getAID().getName());

        this.matriz = new int[4][4];

        setupMatriz();

        addBehaviour(new MeuComportamento(this.matriz));
    }
}
