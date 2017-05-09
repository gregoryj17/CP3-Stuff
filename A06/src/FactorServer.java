import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;


public class FactorServer extends Thread {

    public static final int port = 8901;

    public ArrayList<Client> clients = new ArrayList<Client>();
    public static int maxClients = 8;
    public static int nextClient = 0;
    public static int clientID = 0;
    public static PrintWriter logger;

    public FactorServer() throws Exception {
        logger = new PrintWriter("log.txt");
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Factor Server is Running");
        log("Server started.");
        Scanner scan = new Scanner(System.in);
        try {
            while (true) {
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).dead) {
                        clients.remove(i);
                        i--;
                    }
                }
                if (clients.size() < maxClients) {
                    Client client = new Client(listener.accept(), this, clientID++);
                    if (client != null) {
                        clients.add(client);
                        client.start();
                        System.out.println("Client connected.");
                        log("Client " + (clientID - 1) + " connected.");
                    }
                }
                if (scan.hasNext()) {
                    String cmdargs = scan.nextLine();
                    if (cmdargs.equals("CLIENTS")) {
                        for (int i = 0; i < clients.size(); i++) {
                            System.out.println(clients.get(i));
                        }
                    } else {
                        sendNext(cmdargs);
                        //clients.get(0).output.println(cmdargs);
                    }
                }
            }
        } finally {
            listener.close();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Max clients?");
        try {
            maxClients = Integer.parseInt((new Scanner(System.in)).nextLine());
        } catch (Exception e) {
            maxClients = 8;
        }
        FactorServer server = new FactorServer();
    }

    public void sendNext(String input) {
        for (int i = 0; i < clients.size(); i++) {
            if (!clients.get(nextClient).dead) clients.get(nextClient).output.println(input);
            log("Sent \"" + input + "\" to client " + i);
            nextClient = (nextClient + 1) % clients.size();
        }
    }

    public void log(String toLog) {
        Date d = new Date();
        logger.append(d + " " + toLog + "\n");
        logger.flush();
    }

}

class Client extends Thread {
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    FactorServer parent;
    boolean dead = false;
    int ID;

    public Client(Socket socket, FactorServer parent, int clientID) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            this.parent = parent;
            ID = clientID;
        } catch (IOException e) {
            System.out.println("Client died: " + e);
            dead = true;
            parent.clients.remove(this);
            parent.log("Client " + ID + " disconnected.");
        }
    }

    public void run() {
        while (!dead) {
            try {
                String in = input.readLine();
                parent.log("Received \"" + in + "\" from client " + ID);
                if (in != null) System.out.println(in);
                if (in.equals("QUIT")) {
                    dead = true;
                    parent.clients.remove(this);
                }
            } catch (Exception e) {
                dead = true;
                System.out.println("Client died!");
                parent.clients.remove(this);
            }
        }
    }
}
