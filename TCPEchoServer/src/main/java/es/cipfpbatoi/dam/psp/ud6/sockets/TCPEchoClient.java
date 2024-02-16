package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TCPEchoClient {

    public static void main(String[] args) throws IOException {
        String serverHostname = "localhost"; // Asegúrate de cambiar esto por la dirección IP del servidor si no es local
        int port = 6789; // El puerto debe coincidir con el del servidor

        try (Socket clientSocket = new Socket(serverHostname, port);
             // Crea flujos de entrada y salida para comunicarse con el servidor
             PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
             Scanner inFromServer = new Scanner(clientSocket.getInputStream());) {

            // Enviar mensaje al servidor
            String sentence = "Hola, servidor!";
            outToServer.println(sentence);

            // Recibir respuesta del servidor
            String modifiedSentence = inFromServer.nextLine();
            System.out.println("DEL SERVIDOR: " + modifiedSentence);

            // Cierra la conexión con el servidor
            clientSocket.close();
        } catch (UnknownHostException e) {
            System.err.println("No se puede encontrar el host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("No se puede obtener I/O para la conexión con: " + serverHostname);
            System.exit(1);
        }
    }

}
