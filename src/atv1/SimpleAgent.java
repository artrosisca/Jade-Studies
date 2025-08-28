// src/SimpleAgent.java - ESTÁ CORRETO ✅
package atv1;

import jade.core.Agent;

public class SimpleAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Olá! O agente " + getAID().getName() + " está pronto. ✅");
    }
}