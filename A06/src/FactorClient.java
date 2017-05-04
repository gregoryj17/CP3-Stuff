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

    public FactorClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        for (int i = 0; i < 8; i++) {
            Core core = new Core(in, out);
            cores.add(core);
        }
    }

    public void factor() throws Exception {
        String response;
        try {
            Scanner scan = new Scanner(System.in);
            while (true) {
                while(cores.contains(null)){
                    cores.remove(null);
                }
                int size = cores.size();
                out.println(size);
                for (int i = 0; i < size; i++) {
                    cores.get(i).factor(in.readLine());
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

    public Core(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void factor(String input) {
        System.out.println(input);
        String[] args = input.split(" ");
        System.out.println(Arrays.toString(args));
        int id = Integer.parseInt(args[0]);
        BigInteger num = new BigInteger(args[1]);
        BigInteger start = new BigInteger(args[2]);
        BigInteger end = new BigInteger(args[3]);
        BigInteger result = check(num, start, end);
        System.out.println(result);
        out.println(input+" "+result);
        out.flush();
    }

    public BigInteger check(BigInteger num, BigInteger start, BigInteger end) {
        if (num.mod(new BigInteger("2")).compareTo(new BigInteger("0")) == 0) return new BigInteger("2");
        if (start.mod(new BigInteger("2")).compareTo(new BigInteger("0")) == 0) start.add(new BigInteger("1"));
        for (BigInteger i = new BigInteger(start + ""); i.compareTo(end) <= 0; i=i.add(new BigInteger("1"))) {
            if (num.mod(i).compareTo(new BigInteger("0")) == 0) return i;
        }
        return new BigInteger("-1");
    }

}