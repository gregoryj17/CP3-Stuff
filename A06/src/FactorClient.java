import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FactorClient {

    private static int PORT = 8901;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<Core> cores = new ArrayList<Core>();
    public static int maxCores = 8;

    public FactorClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        for (int i = 0; i < maxCores; i++) {
            Core core = new Core(in, out);
            cores.add(core);
        }
    }

    public void factor() throws Exception {
        try {
            Scanner scan = new Scanner(System.in);
            while (true) {
                for (int i = 0; i < cores.size(); i++) {
                    if (cores.get(i).getState() == Thread.State.TERMINATED) {
                        cores.remove(cores.get(i));
                        i--;
                    }
                }
                int size = cores.size();
                //out.println("CORES "+size);
                String input = in.readLine();
                if (input.startsWith("GIVE")) {
                    for (int i = 0; i < size; i++) {
                        if (!cores.get(i).inUse) {
                            cores.get(i).factor(input);
                            break;
                        }
                    }
                } else if (input.equals("QUIT")) {
                    out.println("QUIT");
                    System.out.println("Quitting.");
                    break;
                }

            }

        } finally {
            socket.close();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Please enter the server's IP address: ");
            String serverAddress = (new Scanner(System.in)).nextLine();
            System.out.println("How many cores?: ");
            try {
                maxCores = (new Scanner(System.in).nextInt());
            } catch (Exception e) {
                maxCores = 8;
            }
            FactorClient client = new FactorClient(serverAddress);
            client.factor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Core extends Thread {

    BufferedReader in;
    PrintWriter out;
    public boolean inUse;

    public Core(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void factor(String input) {
        try {
            inUse = true;
            System.out.println("RECEIVED "+input);
            String[] args = input.split(" ");
            //System.out.println(Arrays.toString(args));
            int id = Integer.parseInt(args[1]);
            BigInteger num = new BigInteger(args[2]);
            BigInteger start = new BigInteger(args[3]);
            BigInteger end = new BigInteger(args[4]);
            BigInteger result = check(num, start, end);
            System.out.println("FOUND "+result);
            out.println("FINISH " + input.substring(5) + " " + result);
        } catch (Exception e) {
            out.println("FAILED " + input.substring(5));
        } finally {
            inUse = false;
        }
    }

    public BigInteger check(BigInteger num, BigInteger start, BigInteger end) {
        if (start.compareTo(BigInteger.ZERO) == 0) start = start.add(BigInteger.ONE);
        if (start.compareTo(BigInteger.ONE) == 0) start = start.add(BigInteger.ONE);
        if (num.mod(new BigInteger("2")).compareTo(BigInteger.ZERO) == 0) return new BigInteger("2");
        if (start.mod(new BigInteger("2")).compareTo(BigInteger.ZERO) == 0) start = start.add(BigInteger.ONE);
        for (BigInteger i = new BigInteger(start + ""); i.compareTo(end) <= 0; i = i.add(new BigInteger("2"))) {
            if (num.mod(i).compareTo(BigInteger.ZERO) == 0) return i;
        }
        return new BigInteger("-1");
    }

}