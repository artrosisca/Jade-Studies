package atv1;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.logging.Level;

public class Start {

    public static void main(String[] args) {

        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        ContainerController containerController = runtime.createMainContainer(profile);

        AgentController agentController;
        AgentController rma;

        try{
            rma = containerController.createNewAgent("rma", "jade.tools.rma.rma", null);

            agentController = containerController.createNewAgent("Agente_0", "src.SimpleAgent", null);
            agentController.start();

            agentController = containerController.acceptNewAgent("Agente_1", new SimpleAgent());
            agentController.start();

            rma.start();
        } catch (StaleProxyException ex) {
            System.out.println("Erro ao criar o RMA");
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
