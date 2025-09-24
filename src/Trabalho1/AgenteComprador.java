package Trabalho1;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.AID;
import jade.core.Agent;

public class AgenteComprador extends Agent {

    private String produtoDesejado;
    private int orcamento;
    private AID[] vendedores;
    private AID melhorVendedor;
    private int melhorPreco = Integer.MAX_VALUE;

    @Override
    protected void setup() {
        Logger.log("Olá! O agente comprador " + getLocalName() + " está pronto.");

        Object[] args = getArguments();
        if (args != null && args.length > 1) {
            produtoDesejado = (String) args[0];
            orcamento = (int) args[1];
            Logger.log(getLocalName() + ": tentando comprar '" + produtoDesejado + "' com um orçamento de " + orcamento);

            addBehaviour(new jade.core.behaviours.WakerBehaviour(this, 15000) {
                protected void onWake() {
                    Logger.log(myAgent.getLocalName() + ": Iniciando processo de compra...");
                    myAgent.addBehaviour(new ProcessoDeCompraComportamento());
                }
            });
        } else {
            Logger.log("Argumentos de produto e orçamento não foram informados para o comprador " + getLocalName());
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        Logger.log("Agente comprador " + getLocalName() + " finalizando.");
    }

    private class ProcessoDeCompraComportamento extends Behaviour {
        private int passo = 0;
        private MessageTemplate mt;
        private int respostasRecebidas = 0;

        public void action() {
            switch (passo) {
                case 0:
                    buscarVendedores();
                    enviarCFP();
                    passo = 1;
                    break;
                case 1:
                    receberPropostas();
                    break;
                case 2:
                    if (melhorVendedor != null) {
                        Logger.log(myAgent.getLocalName() + ": A melhor proposta inicial foi de R$" + melhorPreco + " do vendedor " + melhorVendedor.getLocalName());
                        if (melhorPreco > orcamento) {
                            Logger.log(myAgent.getLocalName() + ": O preço de " + melhorPreco + " está acima do meu orçamento de " + orcamento + ". Não vou negociar.");
                            passo = 4;
                        } else {
                            enviarContraProposta();
                            passo = 3;
                        }
                    } else {
                        Logger.log(myAgent.getLocalName() + ": Nenhuma oferta recebida para o produto '" + produtoDesejado + "'.");
                        passo = 4;
                    }
                    break;
                case 3:
                    ACLMessage respostaFinal = myAgent.receive(mt);
                    if (respostaFinal != null) {
                        if (respostaFinal.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                            Logger.log(myAgent.getLocalName() + ": Compra do produto '" + produtoDesejado + "' efetuada com SUCESSO com o vendedor " + respostaFinal.getSender().getLocalName());
                        } else {
                            Logger.log(myAgent.getLocalName() + ": Negociação para o produto '" + produtoDesejado + "' FALHOU. O vendedor " + respostaFinal.getSender().getLocalName() + " recusou a contraproposta.");
                        }
                        passo = 4; // finaliza o processo
                    } else {
                        block();
                    }
                    break;
            }
        }

        private void buscarVendedores() {
            String tipoServico = inferirTipoServico(produtoDesejado);
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(tipoServico);
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                vendedores = (result.length > 0) ? new AID[result.length] : new AID[0];
                for (int i = 0; i < result.length; ++i) {
                    vendedores[i] = result[i].getName();
                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        private void enviarCFP() {
            if (vendedores.length > 0) {
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (AID vendedor : vendedores) {
                    cfp.addReceiver(vendedor);
                }
                cfp.setContent(produtoDesejado);
                cfp.setConversationId(produtoDesejado); // Usamos o produto como ID da conversa
                cfp.setReplyWith("cfp" + System.currentTimeMillis());
                myAgent.send(cfp);
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId(produtoDesejado), MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                Logger.log(myAgent.getLocalName() + ": Enviei um CFP para " + vendedores.length + " vendedores.");
            } else {
                Logger.log(myAgent.getLocalName() + ": Nenhum vendedor encontrado para este tipo de produto.");
            }
        }

        private void receberPropostas() {
            if (vendedores.length == 0) {
                passo = 2;
                return;
            }
            ACLMessage resposta = myAgent.receive(mt);
            if (resposta != null) {
                respostasRecebidas++;
                if (resposta.getPerformative() == ACLMessage.PROPOSE) {
                    int preco = Integer.parseInt(resposta.getContent());
                    Logger.log(myAgent.getLocalName() + ": Recebi proposta de R$" + preco + " do vendedor " + resposta.getSender().getLocalName());
                    if (preco < melhorPreco) {
                        melhorPreco = preco;
                        melhorVendedor = resposta.getSender();
                    }
                } else {
                    Logger.log(myAgent.getLocalName() + ": Recebi recusa do vendedor " + resposta.getSender().getLocalName());
                }
                if (respostasRecebidas >= vendedores.length) {
                    passo = 2;
                }
            } else {
                block();
            }
        }

        private void enviarContraProposta() {
            ACLMessage contraproposta = new ACLMessage(ACLMessage.PROPOSE);
            contraproposta.addReceiver(melhorVendedor);
            int valorContraproposta = (int) (melhorPreco * 0.9);
            Logger.log(myAgent.getLocalName() + ": A proposta de R$" + melhorPreco + " é boa, mas vou tentar negociar. Enviando contraproposta de R$" + valorContraproposta + " para " + melhorVendedor.getLocalName());
            contraproposta.setContent(String.valueOf(valorContraproposta));
            contraproposta.setConversationId(produtoDesejado);
            contraproposta.setReplyWith("proposta" + System.currentTimeMillis());
            myAgent.send(contraproposta);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId(produtoDesejado), MessageTemplate.MatchInReplyTo(contraproposta.getReplyWith()));
        }

        public boolean done() {
            return (passo == 4);
        }

        private String inferirTipoServico(String produto) {
            if (produto.toLowerCase().contains("ford") || produto.toLowerCase().contains("volkswagen")) {
                return "venda-de-carros";
            } else if (produto.toLowerCase().contains("apartamento") || produto.toLowerCase().contains("casa")) {
                return "venda-de-casas";
            } else if (produto.toLowerCase().contains("mesa") || produto.toLowerCase().contains("sofá")) {
                return "venda-de-moveis";
            }
            return "";
        }
    }
}