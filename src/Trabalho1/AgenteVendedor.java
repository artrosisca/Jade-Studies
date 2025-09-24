package Trabalho1;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.Agent;
import java.util.HashMap;
import java.util.Map;

public class AgenteVendedor extends Agent {

    private Map<String, Integer> catalogo;
    private String tipoDeServico;

    @Override
    protected void setup() {
        Logger.log("Olá! O agente vendedor " + getAID().getName() + " está pronto.");

        catalogo = new HashMap<>();

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            tipoDeServico = (String) args[0];
            for (int i = 1; i < args.length; i++) {
                String[] produtoPreco = ((String) args[i]).split(",");
                if (produtoPreco.length == 2) {
                    catalogo.put(produtoPreco[0].trim(), Integer.parseInt(produtoPreco[1].trim()));
                }
            }
            Logger.log("Catálogo do " + getLocalName() + ": " + catalogo.toString());
        }

        // registro no DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoDeServico);
        sd.setName("Negociacao de Produtos");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // comportamento para receber solicitações de propostas (CFP)
        addBehaviour(new ReceberCFPComportamento());

        // comportamento para receber e processar contrapropostas de negociação
        addBehaviour(new ReceberContrapropostaComportamento());
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        Logger.log("Agente vendedor " + getAID().getName() + " finalizando.");
    }

    private class ReceberCFPComportamento extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                String produto = msg.getContent();
                ACLMessage resposta = msg.createReply();
                Integer preco = catalogo.get(produto);

                if (preco != null) {
                    resposta.setPerformative(ACLMessage.PROPOSE);
                    resposta.setContent(String.valueOf(preco.intValue()));
                } else {
                    resposta.setPerformative(ACLMessage.REFUSE);
                    resposta.setContent("produto-nao-disponivel");
                }
                myAgent.send(resposta);
            } else {
                block();
            }
        }
    }

    private class ReceberContrapropostaComportamento extends CyclicBehaviour {
        public void action() {
            // comportamento só se interessa por mensagens do tipo PROPOSE (contrapropostas)
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                // contraproposta recebida
                String produto = msg.getConversationId(); // Pega o nome do produto do ID da conversa
                int precoOfertado = Integer.parseInt(msg.getContent());

                Logger.log(myAgent.getLocalName() + ": Recebi uma contraproposta de R$" + precoOfertado + " para o produto '" + produto + "'.");

                ACLMessage resposta = msg.createReply();
                Integer precoOriginal = catalogo.get(produto);

                // aceita se a oferta for no máximo 15% menor que o preço original
                if (precoOriginal != null && precoOfertado >= (precoOriginal * 0.85)) {
                    // aceita a proposta
                    Logger.log(myAgent.getLocalName() + ": Bom preço para '" + produto + "'! ACEITO a proposta de R$" + precoOfertado);
                    resposta.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    resposta.setContent(produto);
                    // remove o item do catálogo para não vender de novo
                    catalogo.remove(produto);
                } else {
                    // recusa a proposta
                    Logger.log(myAgent.getLocalName() + ": O valor de R$" + precoOfertado + " para '" + produto + "' é muito baixo. RECUSO.");
                    resposta.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    resposta.setContent("proposta-recusada");
                }
                myAgent.send(resposta);
            } else {
                block();
            }
        }
    }
}