package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TCPEchoServer {
    public static void main(String[] args) {
        mostrarEjecucionServer();
    }

    private static void mostrarEjecucionServer() {
        ArrayList<String> nombreClientes = new ArrayList<>();
        int port = 6789; // Puerto en el que el servidor escuchará
        int totalNucleos = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(totalNucleos);
        ArrayList<ClientWorker> clientes = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado y escuchando en el puerto " + port);

            while (true) {
                Socket connectionSocket = serverSocket.accept();

                // Crea flujos de entrada y salida para leer y escribir datos hacia/desde el cliente
                Scanner inFromClient = new Scanner(connectionSocket.getInputStream());
                String nombreCliente = inFromClient.nextLine().trim();

                ClientWorker clientWorker = obtenerCliente(nombreClientes, nombreCliente, connectionSocket, clientes);
                ejecutarClienteWolker(clientWorker, clientes, poolExecutor);
                obtenerClienteConectado(clientes, nombreCliente, clientWorker);
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private static void obtenerClienteConectado(ArrayList<ClientWorker> clientes, String nombreCliente, ClientWorker clientWorker) {
        for (ClientWorker cliente : clientes) {
            if (!cliente.getNombreCliente().equalsIgnoreCase(nombreCliente)) {
                cliente.outToClient.println(clientWorker.getNombreCliente() + " se ha unido al chat");
            }
        }
    }

    private static void ejecutarClienteWolker(ClientWorker clientWorker, ArrayList<ClientWorker> clientes, ThreadPoolExecutor poolExecutor) {
        Thread t = new Thread(clientWorker);
        t.start();
        clientes.add(clientWorker);
        poolExecutor.getQueue().add(clientWorker);
    }

    private static ClientWorker obtenerCliente(ArrayList<String> nombreClientes, String nombreCliente, Socket connectionSocket, ArrayList<ClientWorker> clientes) {
        ClientWorker clientWorker;

        if (contieneElNombre(nombreClientes, nombreCliente)) {
            clientWorker = new ClientWorker(connectionSocket, nombreCliente, clientes, true);
        } else {
            clientWorker = new ClientWorker(connectionSocket, nombreCliente, clientes, false);
            nombreClientes.add(nombreCliente);
        }

        return clientWorker;
    }

    private static boolean contieneElNombre(ArrayList<String> nombreClientes, String nombreCliente) {
        boolean contieneEseNombre = false;

        if (nombreClientes.contains(nombreCliente)) {
            contieneEseNombre = true;
        }

        return contieneEseNombre;
    }
}
