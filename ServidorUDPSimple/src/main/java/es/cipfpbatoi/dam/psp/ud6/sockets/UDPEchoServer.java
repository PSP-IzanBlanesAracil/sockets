package es.cipfpbatoi.dam.psp.ud6.sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPEchoServer {

    public static void main(String[] args) {
        int port = 9876; // Puerto en el que el servidor escuchará
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

            while(true) {
                // Recibe el paquete del cliente
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("RECEIVED: " + sentence);

                // Enviar respuesta al cliente
                InetAddress IPAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                String capitalizedSentence = "El servidor ha recibido: " + sentence;
                sendData = capitalizedSentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
