import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

    }

    public void factor() throws Exception {
        String response;
        try {
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.println("Would you like to throw rock, paper, or scissors?");
                String selection = scan.nextLine().toLowerCase();

            }

        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            String serverAddress = (args.length == 0) ? "172.16.2.13" : args[0];
            Client client = new Client(serverAddress);
            client.factor();
        }catch(Exception e){
            String serverAddress = (args.length == 0) ? "localhost" : args[0];
            Client client = new Client(serverAddress);
            client.factor();
        }
    }
}