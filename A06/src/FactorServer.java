import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class FactorServer {

    public static final int port = 8901;

    public static ArrayList<Client> clients = new ArrayList<Client>();

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Factor Server is Running");
        Scanner scan = new Scanner(System.in);
        try {
            while (true) {
                for(int i = 0;i<clients.size();i++){
                    if(clients.get(i).dead){
                        clients.remove(i);
                        i--;
                    }
                }
                Client client = new Client(listener.accept());
                if(client!=null){
                    clients.add(client);
                    client.start();
                    System.out.println("Client connected.");
                }
                if(scan.hasNext())clients.get(0).output.println(scan.nextLine());
            }
        } finally {
            listener.close();
        }
    }
}

class Client extends Thread {
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    int move;
    boolean dead=false;

    public Client(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Client died: " + e);
        }
    }

    public void run() {
        while(!dead){
            try {
                String in = input.readLine();
                if(in!=null)System.out.println(in);
            }catch(Exception e){
                dead=true;
                System.out.println("Server died!");
            }
        }
    }

    public void makeThrow() {
        try {
            String command = input.readLine();
            if (command.equals("QUIT")) {
                move = -1;
            } else if (command.equals("ROCK")) {
                move = 0;
            } else if (command.equals("PAPER")) {
                move = 1;
            } else if (command.equals("SCISSORS")) {
                move = 2;
            }

        } catch (IOException e) {
            System.out.println("Player died: " + e);
            move = -1;
        }
    }
}
