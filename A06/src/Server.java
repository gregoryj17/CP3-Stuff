import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    public static final int port = 8901;

    public static ArrayList<Primer> clients = new ArrayList<Primer>();

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Server is Running");
        try {
            while (true) {
                Primer client = new Primer(listener.accept());
                if(client!=null)clients.add(client);
            }
        } finally {
            listener.close();
        }
    }
}

class Primer extends Thread {
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    int move;

    public Primer(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Client died: " + e);
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
