package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TCPEchoServer {
    public static void main(String[] args) throws IOException {
        TCPEchoServer tcpEchoServer = new TCPEchoServer();
        tcpEchoServer.mostrarEjecucionServer();
    }

    private void mostrarEjecucionServer() {
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

//                    PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(), true);
                ClientWorker clientWorker;
                if(nombreClientes.contains(nombreCliente)){
                    clientWorker= new ClientWorker(connectionSocket, nombreCliente, clientes, true);
                }else {
                    clientWorker = new ClientWorker(connectionSocket, nombreCliente, clientes, false);
                    nombreClientes.add(nombreCliente);
                }


                Thread t = new Thread(clientWorker);
                t.start();
                clientes.add(clientWorker);
                poolExecutor.getQueue().add(clientWorker);

                for (ClientWorker cliente : clientes) {
                    if (!cliente.getNombreCliente().equalsIgnoreCase(nombreCliente)) {
                        cliente.outToClient.println(clientWorker.getNombreCliente() + " se ha unido al chat");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }
}
