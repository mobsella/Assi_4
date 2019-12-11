package com.company;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class Client {
    private static InetAddress hostname;
    private static Socket socket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static boolean connectedToLandingZone = false;

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {
        hostname = InetAddress.getLocalHost();
        if (!connectedToLandingZone) {
            connectToLandingZone();
        }
        echoTenToServer();
        echoDateToServer();

    }

    private static void echoDateToServer() throws IOException, ClassNotFoundException, InterruptedException {

        socket = new Socket(hostname.getHostName(), 3000);
        System.out.println("Sending request to LandingZone...");
        oos = new ObjectOutputStream(socket.getOutputStream());
        Date date = new Date();
        oos.writeObject(date);
        ois = new ObjectInputStream(socket.getInputStream());
        Object message = ois.readObject();
        System.out.println("LandingZoneReply: " + message);
        //close resources
        ois.close();
        oos.close();
        Thread.sleep(100);

    }

    private static void connectToLandingZone() throws IOException, ClassNotFoundException {
        while (true) {
            socket = new Socket(hostname.getHostName(), 3000);
            System.out.println("Connecting to LandingZone Server...");
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("client");
            ois = new ObjectInputStream(socket.getInputStream());
            String reply = (String) ois.readObject();
            System.out.println("Reply From LandingZone: " + reply);
            //close resources
            ois.close();
            oos.close();
            if (reply.equalsIgnoreCase("OK")) {
                connectedToLandingZone = true;
                break;
            }
        }
    }

    private static void echoTenToServer() throws IOException, ClassNotFoundException, InterruptedException {
        int i = 0;
        while (true) {
            if (i == 11) {
                break;
            }
            socket = new Socket(hostname.getHostName(), 3000);
            System.out.println("Sending request to LandingZone...");
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject("" + i);
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("LandingZoneReply: " + message);
            //close resources
            ois.close();
            oos.close();
            if (!message.equalsIgnoreCase("server not online")) {
                i++;
            }
            Thread.sleep(100);
        }
    }

}
