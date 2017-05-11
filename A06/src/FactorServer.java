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
    public PrintWriter resultWriter;
    public PrintWriter timeLogger;
    public PrintWriter timelog;
    public ServerSocket listener;
    public PrintWriter csvlog;
    public long endtime;
    public long begintime;
    public static long lifetime;

    public FactorServer() throws Exception {
        logger = new PrintWriter("log.txt");
        numIn = new Scanner(new File("jackson.txt"));
        resultWriter = new PrintWriter("output.txt");
        timeLogger = new PrintWriter("timelog.txt");
        timelog = new PrintWriter("loglongtime.txt");
        csvlog = new PrintWriter("timelog.csv");
        listener = new ServerSocket(port);
        System.out.println("Factor Server is Running");
        log("Server started.");
        Scanner scan = new Scanner(System.in);
        try {
            while (clients.size() < maxClients) {
                Client client = new Client(listener.accept(), this, clientID++);
                if (client != null) {
                    clients.add(client);
                    client.start();
                    System.out.println("Client connected.");
                    log("Client " + (clientID - 1) + " connected.");
                }
            }
            begintime = System.nanoTime();
            endtime = (lifetime != -1) ? (begintime + lifetime) : -1;
            while (numIn.hasNext() && (endtime == -1 || System.nanoTime() < endtime)) {
                String nextline = numIn.nextLine();
                BigInteger num;
                if (nextline.equals("")) break;
                else {
                    num = new BigInteger(nextline.split(" ")[1]);
                }
                //System.out.println(nextline);
                queue = new LinkedList<>();
                BigInteger len = new BigInteger("30000000");
                BigInteger current = new BigInteger("0");
                result = new BigInteger("-1");
                boolean finished = false;
                while (!finished && (endtime == -1 || System.nanoTime() < endtime)) {
                    while (queue.size() < clients.size() * 8 && current.pow(2).compareTo(num) < 1) {
                        BigInteger next = current.add(len);
                        queue.add(num + " " + current + " " + next);
                        current = next;
                    }
                    while (result.compareTo(new BigInteger("-1")) == 0 && (endtime == -1 || System.nanoTime() < endtime)) {
                        if (!queue.isEmpty()) {
                            sendNext(queue.pollFirst());
                        }
                        for (int i = 0; i < clients.size(); i++) {
                            Client client = clients.get(i);
                            if (client.factor.compareTo(new BigInteger("-1")) != 0) {
                                result = clients.get(i).factor;
                                finished = true;
                            } else if (client.ready) {
                                BigInteger next = current.add(len);
                                sendNext(num + " " + current + " " + next);
                                current = next;
                            }
                        }
                    }
                    if (result.compareTo(new BigInteger("-1")) != 0) {
                        finished = true;
                    }
                }
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).dead) {
                        clients.remove(i);
                        i--;
                    }
                }
            }
        } finally {
            listener.close();
        }
        if (System.nanoTime() >= endtime) {
            log("The allotted runtime has elapsed. The server has stopped operations.");
            System.out.println("The allotted time has ended. Server is shutting down.");
            for (Client client : clients) {
                client.output.println("QUIT");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Max clients?");
        Scanner scan = new Scanner(System.in);
        try {
            maxClients = Integer.parseInt(scan.nextLine());
        } catch (Exception e) {
            maxClients = 8;
        }
        System.out.println("How many hours should this run? (-1 for infinite time): ");
        try {
            lifetime = (long) (scan.nextDouble() * 60 * 60 * Math.pow(10, 9));
        } catch (Exception e) {
            lifetime = -1;
        }
        FactorServer server = new FactorServer();
        System.out.println("Thank you for using FactorServer.");
    }

    public void sendNext(String input) {
        while (true) {
            if (clients.size() <= 0) {
                System.out.println("No clients found!");
            }
            while (clients.size() <= 0) {
                try {
                    Client client = new Client(listener.accept(), this, clientID++);
                    if (client != null) {
                        clients.add(client);
                        client.start();
                        System.out.println("Client connected.");
                        log("Client " + (clientID - 1) + " connected.");
                    }
                } catch (Exception e) {

                }
            }
            if (nextClient >= clients.size()) nextClient = 0;
            Client client = clients.get(nextClient);
            String job = "GIVE " + client.ID + " " + input;
            if (!client.dead) {
                client.newNum(job.split(" ")[2]);
                client.send(job);
                log("Sent \"" + job + "\" to client " + client.ID);
                timeLog(job);
                nextClient = (nextClient + 1) % clients.size();
                break;
            } else {
                nextClient = (nextClient + 1) % clients.size();
            }
        }
    }

    public void log(String toLog) {
        Date d = new Date();
        logger.append(d + " " + toLog + "\r\n");
        logger.flush();
    }

    public void timeLog(String toLog) {
        timelog.append(System.nanoTime() + " " + toLog + "\r\n");
        timelog.flush();
        String commalog = toLog.replaceAll(" ", ", ");
        csvlog.append(System.nanoTime() + ", " + commalog + "\r\n");
        csvlog.flush();
    }

    public void printResult(String result) {
        resultWriter.append(result + "\r\n");
        resultWriter.flush();
        this.result = new BigInteger(result.split(" ")[1]);
    }

    public void printTime(String toPrint) {
        timeLogger.append(toPrint + "\r\n");
        timeLogger.flush();
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
    public boolean ready;

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

    public void newNum(String num){
        BigInteger n=new BigInteger(num);
        for(int i=0;i<jobs.size();i++){
            if(new BigInteger(jobs.get(i).split(" ")[2]).compareTo(n)<0){
                jobs.remove(i);
                i--;
            }
        }
    }

    public void send(String toSend) {
        output.println(toSend);
        jobs.add(toSend.substring(7));
        parent.printTime("START " + toSend.substring(7) + " " + System.nanoTime());
        factor = new BigInteger("-1");
        ready = false;
    }

    public void run() {
        while (!dead) {
            try {
                String in = input.readLine();
                parent.log("Received \"" + in + "\" from client " + ID);
                parent.timeLog(in);
                //if (in != null) System.out.println(in);
                if (in.equals("QUIT")) {
                    dead = true;
                    parent.clients.remove(this);
                } else if (in.startsWith("FINISH")) {
                    String[] result = in.split(" ");
                    jobs.remove(result[2] + " " + result[3] + " " + result[4]);
                    if (!result[5].equals("-1")) {
                        factor = new BigInteger(result[5]);
                        if (factor.pow(2).compareTo(new BigInteger(result[2])) <= 0) {
                            parent.printResult(result[2] + " " + factor + " " + (new BigInteger(result[2]).divide(factor)));
                            System.out.println("Factored " + result[2] + ". Found factor " + factor + ".");
                        }
                    }
                    parent.printTime(result[0] + " " + result[2] + " " + result[3] + " " + result[4] + " " + System.nanoTime());
                }
                ready = true;
            } catch (Exception e) {
                dead = true;
                System.out.println("Client died!");
                parent.remove(this);
                parent.log("Client " + ID + " disconnected.");
            }
        }
    }
}
