package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class LandingZone {
    //static ServerSocket variable
    private static ServerSocket serverSocket;
    //socket server port on which it will listen
    private static int port = 3000;
    private static InetAddress hostname;
    private static Socket clientSocket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static boolean serverOnline = false;
    private static boolean clientOnline = false;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        while (true) {
            System.out.println("Waiting for server/client to connect...");
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            System.out.println("Ack " + message);
            //create ObjectOutputStream object
            String reply;
            if (message.equalsIgnoreCase("server")) {
                reply = "OK";
                connectToServer();
            } else if(message.equalsIgnoreCase("client")) {
                reply = "OK";
                connectToClient();
            } else {
                if(clientOnline) {
                    reply = "server not online";
                } else if(serverOnline) {
                    reply = "client not online";
                } else {
                    reply = "server and client is not online";
                }
            }
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(reply);
            ois.close();
            oos.close();
            socket.close();
            if(clientOnline && serverOnline) break;
        }
        listenToClient();
    }

    private static void connectToServer() throws UnknownHostException {
        hostname = InetAddress.getLocalHost();
        serverOnline = true;
    }

    private static void connectToClient() {
        clientOnline = true;
    }

    private static void listenToClient() throws IOException, ClassNotFoundException {
        while (true) {
            System.out.println("Waiting for client to transmit...");
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            Object message = ois.readObject();
            System.out.println("Ack " + message);
            Object reply = proxyToServer(message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(reply);
            ois.close();
            oos.close();
            socket.close();
        }
    }

    private static Object proxyToServer(Object message) throws IOException, ClassNotFoundException {
        clientSocket =  new Socket(hostname.getHostName(), 9876);
        System.out.println("Proxying request to Server...");
        oos = new ObjectOutputStream(clientSocket.getOutputStream());
        oos.writeObject("" + message);
        ois = new ObjectInputStream(clientSocket.getInputStream());
        Object reply = ois.readObject();
        System.out.println("Reply From Server: " + reply);
        //close resources
        ois.close();
        oos.close();
        return reply;
    }
}
