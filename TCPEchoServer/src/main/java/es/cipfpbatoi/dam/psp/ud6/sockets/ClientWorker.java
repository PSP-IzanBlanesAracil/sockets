package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWorker implements Runnable {
    Socket socket;
    String nombreCliente;
    private static final String PALABRA_FINALIZAR_COMUNICACION = "bye";
    ArrayList<ClientWorker> clientWorkers;
    PrintWriter outToClient;
    static ConcurrentHashMap<String, String> listaMensajes= new ConcurrentHashMap<>();
    boolean tieneMensajesPendientes;

    int numeroMensaje =0;

    public ClientWorker(Socket socket, String nombreCliente, ArrayList<ClientWorker> clientWorkers, boolean tieneMensajesPendientes) {
        this.socket = socket;
        this.nombreCliente = nombreCliente;
        this.clientWorkers = clientWorkers;
        this.tieneMensajesPendientes=tieneMensajesPendientes;
    }

    @Override
    public void run() {
        String frase = "";
        try {
            outToClient = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outToClient.println("Bienvenido "+nombreCliente.toUpperCase()+"  al chat, hay "+clientWorkers.size() +" usuarios conectados");
        if(tieneMensajesPendientes){
            for (String keyMensaje: listaMensajes.keySet()){
//                keyMensaje.concat("\n");
                if(!keyMensaje.equalsIgnoreCase(this.nombreCliente)){
                    outToClient.println(listaMensajes.get(keyMensaje));
                }
            }
        }

        while (!frase.equalsIgnoreCase(PALABRA_FINALIZAR_COMUNICACION)) {
            try {
                Scanner inFromClient = new Scanner(socket.getInputStream());

                // Recibe el mensaje del cliente
                String clientSentence = inFromClient.nextLine();
                frase = clientSentence;
                // Procesa el mensaje y responde
                String fraseFinal = nombreCliente + ": " + clientSentence + System.lineSeparator();
                numeroMensaje++;
                for (ClientWorker cliente : clientWorkers) {
                    if (!cliente.getNombreCliente().equalsIgnoreCase(this.nombreCliente)) {
                        listaMensajes.put(this.nombreCliente, fraseFinal);
//                        listaMensajes.put(this.nombreCliente+"|"+numeroMensaje, fraseFinal);
                        cliente.outToClient.println(fraseFinal);
                    }
                }
//                outToClient.println(fraseFinal);

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

