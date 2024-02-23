package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientWorker implements Runnable {

    Socket socket;

    public ClientWorker(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //Ejecutar varios sockets, hacer lo mismo que en la clase TCPEchoServer
        int port = 6789; // Puerto en el que el servidor escuchará

//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            System.out.println("Servidor iniciado y escuchando en el puerto " + port);
//
//            // El servidor se queda en un bucle infinito esperando por conexiones de clientes
//            while (true) {

        try (// Espera una conexión de un cliente

             // Crea flujos de entrada y salida para leer y escribir datos hacia/desde el cliente
             Scanner inFromClient = new Scanner(socket.getInputStream());
             PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true)) {


            // Recibe el mensaje del cliente
            String clientSentence = inFromClient.nextLine();

            // Procesa el mensaje y responde
            String capitalizedSentence = "El servidor ha recibido: " + clientSentence.toUpperCase() + System.lineSeparator();

            outToClient.println(capitalizedSentence);

//                } catch (IOException e){
//                    System.err.println(e.getLocalizedMessage());
//                }
//
//            }
//        } catch (IOException e) {
//            System.err.println(e.getLocalizedMessage());
//        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        socket.close();
    }
}

