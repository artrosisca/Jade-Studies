package Trabalho1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Logger {

    private static final String NOME_ARQUIVO = "log_mercado_agentes.txt";
    private static PrintWriter writer;
    private static final Object lock = new Object(); // Objeto para sincronização

    static {
        try {
            // FileWriter com 'false' no segundo argumento garante que o arquivo seja sobrescrito a cada nova execução.
            FileWriter fw = new FileWriter(NOME_ARQUIVO, false);
            BufferedWriter bw = new BufferedWriter(fw);
            writer = new PrintWriter(bw);

            // Adiciona um cabeçalho ao log para indicar o início de uma nova simulação
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.println("--- Log da Simulação Iniciada em: " + timestamp + " ---");
            writer.println(); // adiciona uma linha em branco para legibilidade
            writer.flush(); // garante que o cabeçalho seja escrito imediatamente

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String mensagem) {
        synchronized (lock) {
            if (writer != null) {
                String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()); // adiciona milissegundos para ver a ordem exata
                writer.println("[" + timestamp + "] " + mensagem);
                writer.flush(); // garante que a mensagem seja escrita imediatamente no arquivo, sem esperar o buffer encher.
            }
        }
    }

    public static void close() {
        synchronized (lock) {
            if (writer != null) {
                writer.close();
            }
        }
    }
}