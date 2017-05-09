import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class FactorServer extends Thread {

    public static final int port = 8901;

    public ArrayList<Client> clients = new ArrayList<Client>();
    public static int maxClients = 8;
    public static int nextClient = 0;
    public static int clientID = 0;
    public static PrintWriter logger;
    public static Scanner numIn;
    public BigInteger result = new BigInteger("-1");
    LinkedList<String> queue = new LinkedList<>();
    public static PrintWriter resultWriter;

    public FactorServer() throws Exception {
        logger = new PrintWriter("log.txt");
        numIn = new Scanner(new File("numbers.txt"));
        resultWriter = new PrintWriter("output.txt");
        ServerSocket listener = new ServerSocket(port);
        System.out.println("Factor Server is Running");
        log("Server started.");
        Scanner scan = new Scanner(System.in);
        try {
            while (numIn.hasNext()) {
                while (clients.size() < maxClients) {
                    Client client = new Client(listener.accept(), this, clientID++);
                    if (client != null) {
                        clients.add(client);
                        client.start();
                        System.out.println("Client connected.");
                        log("Client " + (clientID - 1) + " connected.");
                    }
                }
                String nextline = numIn.nextLine();
                if (nextline.equals("")) break;
                //System.out.println(nextline);
                BigInteger num = new BigInteger(nextline);
                queue = new LinkedList<>();
                BigInteger len = new BigInteger("30000000");
                BigInteger current = new BigInteger("0");
                result = new BigInteger("-1");
                while (current.pow(2).compareTo(num) < 1) {
                    BigInteger next = current.add(len);
                    queue.add(num + " " + current + " " + next);
                    current = next;
                }
                while (!queue.isEmpty() || result.compareTo(new BigInteger("-1")) == 0) {
                    if (!queue.isEmpty()) {
                        sendNext(queue.pollFirst());
                    }
                    for (int i = 0; i < clients.size(); i++) {
                        if (clients.get(i).factor.compareTo(new BigInteger("-1")) != 0) {
                            result = clients.get(i).factor;
                        }
                    }
                }
                if (result.compareTo(new BigInteger("-1")) != 0) {
                    resultWriter.println(num + " " + result + " " + num.divide(result));
                    resultWriter.flush();
                }
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).dead) {
                        clients.remove(i);
                        i--;
                    }
                }
                /*if (scan.hasNext()) {
                    String cmdargs = scan.nextLine();
                    if (cmdargs.equals("CLIENTS")) {
                        for (int i = 0; i < clients.size(); i++) {
                            System.out.println(clients.get(i));
                        }
                    } else {
                        sendNext(cmdargs);
                        //clients.get(0).output.println(cmdargs);
                    }
                }*/
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
            String job = "GIVE " + nextClient + " " + input;
            if (!clients.get(nextClient).dead) {
                clients.get(nextClient).send(job);
                log("Sent \"" + job + "\" to client " + nextClient);
                nextClient = (nextClient + 1) % clients.size();
                break;
            }else{
                nextClient=(nextClient+1)%clients.size();
            }
        }
    }

    public void log(String toLog) {
        Date d = new Date();
        logger.append(d + " " + toLog + "\n");
        logger.flush();
    }

    public void remove(Client client) {
        clients.remove(client);
        for (String job : client.jobs) {
            queue.add(job);
        }
    }

}

class Client extends Thread {
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    FactorServer parent;
    boolean dead = false;
    int ID;
    public ArrayList<String> jobs = new ArrayList<>();
    public BigInteger factor = new BigInteger("-1");

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
            parent.remove(this);
            parent.log("Client " + ID + " disconnected.");
        }
    }

    public void send(String toSend) {
        output.println(toSend);
        jobs.add(toSend);
        factor = new BigInteger("-1");
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
                } else if (in.startsWith("FINISH")) {
                    String[] result = in.split(" ");
                    if (!result[5].equals("-1")) {
                        factor = new BigInteger(result[5]);
                    }
                }
            } catch (Exception e) {
                dead = true;
                System.out.println("Client died!");
                parent.remove(this);
                parent.log("Client " + ID + " disconnected.");
            }
        }
    }
}
