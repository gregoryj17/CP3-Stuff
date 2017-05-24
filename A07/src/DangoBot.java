import java.util.*;
import java.io.*;
import java.net.*;

public class DangoBot {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket s = new Socket("localhost", 9090);
        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        int num = (int) (Math.random() + 3000);
        out.println("DangoBot" + num);


        int sinceBomb = 4;
        boolean repeat = false;
        while (true) {
            String field = in.readLine();
            int location = findLocation(field); //find column where the s is
            int otherLocation = findOtherLocation(field); //find column where the o is
            if ((sinceBomb > 3 || repeat) && otherLocation > location && location > 4) //possibly bomb behind if the s hasn't passed the o
            {
                repeat = !repeat;
                sinceBomb = 0;
                int size = (int) (Math.random() * 1 + 1);
                out.println("bomb " + (location - 1 - size) + " " + size);
                System.out.println("bomb " + (location - 1 - size) + " " + size);
            } else if(Math.abs(getElevation(field,location)-getElevation(field,location+1))>1){
                if(getElevation(field,location)>getElevation(field,location+1)){
                    out.println("dirt right self");
                    System.out.println("dirt right self");
                }else{
                    out.println("dirt self right");
                    System.out.println("dirt self right");
                }
            } else //run like crazy if the s has passed the o
            {
                sinceBomb++;
                repeat = false;
                out.println("move right");
                System.out.println("move right");

            }
        }
    }

    public static int findLocation(String field) {
        String[] lines = field.split(";");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].indexOf("s") > -1) return lines[i].indexOf("s");
        }
        return 0;
    }

    public static int findOtherLocation(String field) {
        String[] lines = field.split(";");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].indexOf("o") > -1) return lines[i].indexOf("o");
        }
        return 0;
    }

    public static int getElevation(String field, int column) {
        String[] lines = field.split(";");
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].charAt(column) =='*') return i;
        }
        return lines.length;
    }

}
