package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientWorker implements Runnable {
    Socket socket;
    String nombreCliente;
    private static final String PALABRA_FINALIZAR_COMUNICACION = "bye";
    ArrayList<ClientWorker> clientWorkers;
    PrintWriter outToClient;

    public ClientWorker(Socket socket, String nombreCliente, ArrayList<ClientWorker> clientWorkers) {
        this.socket = socket;
        this.nombreCliente = nombreCliente;
        this.clientWorkers = clientWorkers;
    }

    @Override
    public void run() {
        String frase = "";
        while (!frase.equalsIgnoreCase(PALABRA_FINALIZAR_COMUNICACION)) {
            try {
                Scanner inFromClient = new Scanner(socket.getInputStream());
                outToClient = new PrintWriter(socket.getOutputStream(), true);

                // Recibe el mensaje del cliente
                String clientSentence = inFromClient.nextLine();
                frase = clientSentence;
                // Procesa el mensaje y responde
                String capitalizedSentence = nombreCliente + ": " + clientSentence + System.lineSeparator();

                for (ClientWorker cliente : clientWorkers) {
                    if (!cliente.getNombreCliente().equalsIgnoreCase(this.nombreCliente)) {
                        cliente.outToClient.println(capitalizedSentence);
                    }
                }
                outToClient.println(capitalizedSentence);

            } catch (IOException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        mostrarClientesAbandonados();
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void mostrarClientesAbandonados() {
        for (ClientWorker cliente : clientWorkers) {
            if (!cliente.getNombreCliente().equalsIgnoreCase(this.nombreCliente)) {
                cliente.outToClient.println(this.nombreCliente + " a abandonado el chat");
            }
        }
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
}

