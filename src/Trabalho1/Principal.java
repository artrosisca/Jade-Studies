package Trabalho1;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;

/**
 * Classe principal responsável por inicializar a plataforma JADE
 * e instanciar os agentes compradores e vendedores em seus respectivos containers.
 */
public class Principal {

    public static void main(String[] args) {
        // instância do Runtime do JADE
        Runtime rt = Runtime.instance();
        rt.setCloseVM(true); // Garante que a JVM será encerrada ao fechar a GUI

        // perfil para o container principal
        Profile perfilPrincipal = new ProfileImpl();
        perfilPrincipal.setParameter(Profile.GUI, "true"); // Inicia a GUI do JADE
        perfilPrincipal.setParameter(Profile.MAIN_HOST, "localhost");
        perfilPrincipal.setParameter(Profile.PLATFORM_ID, "PlataformaMercado");

        // container principal
        ContainerController containerPrincipal = rt.createMainContainer(perfilPrincipal);

        // perfil para o container dos agentes
        Profile perfilAgentes = new ProfileImpl();
        perfilAgentes.setParameter(Profile.MAIN_HOST, "localhost");
        perfilAgentes.setParameter(Profile.CONTAINER_NAME, "Mercado de Agentes");

        // cria o container secundário para nossos agentes
        ContainerController containerAgentes = rt.createAgentContainer(perfilAgentes);

        try {
            // criação dos agentes

            // --- VENDEDORES DE CARROS ---
            Object[] argsVendedorCarro1 = new Object[]{"venda-de-carros", "Ford Fiesta,15000", "Volkswagen Gol,25000"};
            AgentController vendedor1 = containerAgentes.createNewAgent("VendedorCarros_A", "Trabalho1.AgenteVendedor", argsVendedorCarro1);

            Object[] argsVendedorCarro2 = new Object[]{"venda-de-carros", "Ford Fiesta,14800", "Volkswagen Gol,26000"};
            AgentController vendedor2 = containerAgentes.createNewAgent("VendedorCarros_B", "Trabalho1.AgenteVendedor", argsVendedorCarro2);

            // --- VENDEDORES DE CASAS ---
            Object[] argsVendedorCasa1 = new Object[]{"venda-de-casas", "Apartamento 2 quartos,250000", "Casa 3 quartos,450000"};
            AgentController vendedor3 = containerAgentes.createNewAgent("VendedorCasas_A", "Trabalho1.AgenteVendedor", argsVendedorCasa1);

            Object[] argsVendedorCasa2 = new Object[]{"venda-de-casas", "Apartamento 2 quartos,265000", "Casa 3 quartos,445000"};
            AgentController vendedor4 = containerAgentes.createNewAgent("VendedorCasas_B", "Trabalho1.AgenteVendedor", argsVendedorCasa2);

            // --- VENDEDORES DE MÓVEIS ---
            Object[] argsVendedorMoveis1 = new Object[]{"venda-de-moveis", "Mesa de Jantar,800", "Sofá Retrátil,1500"};
            AgentController vendedor5 = containerAgentes.createNewAgent("VendedorMoveis_A", "Trabalho1.AgenteVendedor", argsVendedorMoveis1);

            Object[] argsVendedorMoveis2 = new Object[]{"venda-de-moveis", "Mesa de Jantar,750", "Sofá Retrátil,1600"};
            AgentController vendedor6 = containerAgentes.createNewAgent("VendedorMoveis_B", "Trabalho1.AgenteVendedor", argsVendedorMoveis2);

            // --- COMPRADORES ---
            Object[] argsComprador1 = new Object[]{"Ford Fiesta", 16000};
            AgentController comprador1 = containerAgentes.createNewAgent("Comprador1", "Trabalho1.AgenteComprador", argsComprador1);

            Object[] argsComprador2 = new Object[]{"Casa 3 quartos", 450000};
            AgentController comprador2 = containerAgentes.createNewAgent("Comprador2", "Trabalho1.AgenteComprador", argsComprador2);

            Object[] argsComprador3 = new Object[]{"Mesa de Jantar", 800};
            AgentController comprador3 = containerAgentes.createNewAgent("Comprador3", "Trabalho1.AgenteComprador", argsComprador3);

            // Iniciação dos agentes
            vendedor1.start();
            vendedor2.start();
            vendedor3.start();
            vendedor4.start();
            vendedor5.start();
            vendedor6.start();

            comprador1.start();
            comprador2.start();
            comprador3.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}