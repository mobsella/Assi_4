package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {

    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;
    private static InetAddress hostname;
    private static Socket clientSocket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static boolean connectedToLandingZone = false;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while (true) {
            if(!connectedToLandingZone) {
                System.out.println("Establishing connection with landingZone...");
                connectToLandingZone();
            }
            System.out.println("Waiting for the client request...");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            Object message = ois.readObject();
            System.out.println("ack " + message);
            //create ObjectOutputStream object
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket
            oos.writeObject("Hi Client " + message);
            //close resources
            ois.close();
            oos.close();
            socket.close();
        }
    }

    private static void connectToLandingZone() throws IOException, ClassNotFoundException {
        hostname = InetAddress.getLocalHost();
       while(true) {
           clientSocket =  new Socket(hostname.getHostName(), 3000);
           System.out.println("Connecting to LandingZone Server...");
           oos = new ObjectOutputStream(clientSocket.getOutputStream());
           oos.writeObject("server");
           ois = new ObjectInputStream(clientSocket.getInputStream());
           String reply = (String) ois.readObject();
           System.out.println("Reply From LandingZone: " + reply);
           //close resources
           ois.close();
           oos.close();
           if(reply.equalsIgnoreCase("OK")) {
               connectedToLandingZone = true;
               break;
           }
       }
    }

}
