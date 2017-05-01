import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class RPSServer {
    public static final int port = 8901;

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Rock Paper Scissors Server is Running");
        try {
            while (true) {
                Player player1 = new Player(listener.accept());
                //System.out.println("Player 1 connected!");
                Player player2 = new Player(listener.accept());
                //System.out.println("Player 2 connected!");
                Game game = new Game(player1, player2);
                //System.out.println("A game is starting!");
                game.start();
                //System.out.println("A game has ended.");
            }
        } finally {
            listener.close();
        }
    }
}


class Game extends Thread {
    public Player player1, player2;
    int p1m, p2m;

    public Game(Player p1, Player p2) {
        player1 = p1;
        player2 = p2;
        sendp1("READY!");
        sendp2("READY!");
    }

    public void run() {
        while (true) {
            try {
                player1.makeThrow();
                player2.makeThrow();
                p1m = player1.getMove();
                p2m = player2.getMove();
                if (p1m == -1 || p2m == -1) {
                    if (p1m != -1) sendp1("QUIT");
                    if (p2m != -1) sendp2("QUIT");
                    break;
                } else result(p1m, p2m);
            } catch (Exception e) {
                System.out.println("Ouch!");
                e.printStackTrace();
            }
        }
    }

    public void sendp1(String message) {
        player1.output.println(message);
    }

    public void sendp2(String message) {
        player2.output.println(message);
    }

    public void result(int p1m, int p2m) {
        if (p1m == p2m) {
            sendp1("TIE");
            sendp2("TIE");
        } else if ((p1m + 1) % 3 == p2m % 3) {
            sendp1("LOSE");
            sendp2("WIN");
        } else {
            sendp1("WIN");
            sendp2("LOSE");
        }
    }
}

class Player extends Thread {
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    int move;

    public Player(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Player died: " + e);
        }
    }

    public int getMove() {
        return move;
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
