import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class program {

    public static byte[][] maze;
    public static int n = 0;
    public static int h = 1;
    public static int w = 1;
    public static int mh;
    public static int mw;
    public static int[] fib = new int[]{1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946, 17711, 28657, 46368, 75025, 121393, 196418, 317811, 514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465, 14930352, 24157817, 39088169, 63245986, 102334155, 165580141, 267914296, 433494437, 701408733, 1134903170, 1836311903};
    public static int lastDir=3;
    public static final byte DEAD_END=101;
    public static final byte VISITED=100;
    public static final byte PATH=127;
    public static final byte WALL=0;
    public static boolean solved=false;
    public static char action;
    public static String name;

    public static void drawMaze(int row, int col) {
        maze[row][col] = PATH;
        ArrayList<Integer> stack = new ArrayList<Integer>();
        stack.add(row*mw+col);
        while (!stack.isEmpty()) {
            int coords = stack.get(stack.size() - 1);
            ArrayList<Integer> possibles = getPossibles(coords/mw, coords%mw);
            if (!possibles.isEmpty()) {
                int ncoords = possibles.get((int) (Math.random() * possibles.size()));
                maze[(ncoords/mw + coords/mw) / 2][(ncoords%mw + coords%mw) / 2] = PATH;
                maze[ncoords/mw][ncoords%mw] = PATH;
                stack.add(ncoords);
            } else {
                stack.remove(stack.size() - 1);
            }
        }
        maze[1][0] = PATH;
        maze[2 * h - 1][2 * w] = PATH;
    }

    public static void drawSteps(int row, int col) {
        maze[row][col] = PATH;
        ArrayList<Integer> stack = new ArrayList<Integer>();
        stack.add(row*mw+col);
        long step=0;
        while (!stack.isEmpty()) {
            int coords = stack.get(stack.size() - 1);
            ArrayList<Integer> possibles = getPossibles(coords/mw, coords%mw);
            if (!possibles.isEmpty()) {
                int ncoords = possibles.get((int) (Math.random() * possibles.size()));
                maze[(ncoords/mw + coords/mw) / 2][(ncoords%mw + coords%mw) / 2] = PATH;
                maze[ncoords/mw][ncoords%mw] = PATH;
                stack.add(ncoords);
                saveStep(false, step);
                step++;
            } else {
                stack.remove(stack.size() - 1);
            }
        }
        maze[1][0] = PATH;
        maze[2 * h - 1][2 * w] = PATH;
        saveStep(false,step);
    }

    public static ArrayList<Integer> getPossibles(int row, int col) {
        ArrayList<Integer> dirs = new ArrayList<Integer>();
        int[][] directions = new int[][]{new int[]{-2, 0}, new int[]{0, -2}, new int[]{2, 0}, new int[]{0, 2}};
        for (int i = 0; i < directions.length; i++) {
            try {
                if (maze[row + directions[i][0]][col + directions[i][1]] == 0) {
                    dirs.add(((row + directions[i][0]) * mw) + (col + directions[i][1]));
                }
            } catch (Exception e) {}
        }
        return dirs;
    }

    public static void saveMaze() {
        BufferedImage img;
        if(solved)img = new BufferedImage(mw, mh, BufferedImage.TYPE_INT_RGB);
        else img=new BufferedImage(mw,mh,BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < mh; i++) {
            for (int j = 0; j < mw; j++) {
                short v = maze[i][j];
                if(v==DEAD_END)img.setRGB(j, i, (new Color(255, 0, 0)).getRGB());
                else if(v==VISITED)img.setRGB(j, i, (new Color(0, 0, 255)).getRGB());
                else if(v==PATH)img.setRGB(j, i, (new Color(255,255,255)).getRGB());
                else img.setRGB(j, i, (new Color(v, v, v)).getRGB());
            }
        }
        try {
            ImageIO.write(img, "png", new File("maze" + n + (solved?"solved":"") + ".png"));
        } catch (Exception e) {}
    }

    public static void saveMaze(String filename) {
        BufferedImage img;
        if(solved)img = new BufferedImage(mw, mh, BufferedImage.TYPE_INT_RGB);
        else img=new BufferedImage(mw,mh,BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < mh; i++) {
            for (int j = 0; j < mw; j++) {
                short v = maze[i][j];
                if(v==DEAD_END)img.setRGB(j, i, (new Color(255, 0, 0)).getRGB());
                else if(v==VISITED)img.setRGB(j, i, (new Color(0, 0, 255)).getRGB());
                else if(v==PATH)img.setRGB(j, i, (new Color(255,255,255)).getRGB());
                else img.setRGB(j, i, (new Color(v, v, v)).getRGB());
            }
        }
        try {
            ImageIO.write(img, "png", new File(filename));
        } catch (Exception e) {}
    }

    public static void saveStep(boolean solve, long step) {
        BufferedImage img = new BufferedImage(mw, mh, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < mh; i++) {
            for (int j = 0; j < mw; j++) {
                short v = maze[i][j];
                if(v==DEAD_END)img.setRGB(j, i, (new Color(255, 0, 0)).getRGB());
                else if(v==VISITED)img.setRGB(j, i, (new Color(0, 0, 255)).getRGB());
                else if(v==PATH)img.setRGB(j, i, (new Color(255,255,255)).getRGB());
                else img.setRGB(j, i, (new Color(v, v, v)).getRGB());
            }
        }
        try {
            ImageIO.write(img, "png", new File( (solve?"solve/":"gen/") + "maze" + n + "step"+ step + ".png"));
        } catch (Exception e) {}
    }

    public static void cmdSave() {
        BufferedImage img;
        if(solved)img = new BufferedImage(mw, mh, BufferedImage.TYPE_INT_RGB);
        else img=new BufferedImage(mw,mh,BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < mh; i++) {
            for (int j = 0; j < mw; j++) {
                short v = maze[i][j];
                if(v==DEAD_END)img.setRGB(j, i, (new Color(255, 0, 0)).getRGB());
                else if(v==VISITED)img.setRGB(j, i, (new Color(0, 0, 255)).getRGB());
                else if(v==PATH)img.setRGB(j, i, (new Color(255,255,255)).getRGB());
                else img.setRGB(j, i, (new Color(v, v, v)).getRGB());
            }
        }
        if(solved)img.setRGB(mw-1,mh-2,(new Color(0,0,255)).getRGB());
        try {
            String sname=name.substring(0,name.lastIndexOf('.'));
            ImageIO.write(img, "png", new File(sname + (solved?"solved":"") + ".png"));
        } catch (Exception e) {}
    }

    public static void cmdSaveStep(boolean solve, long step){
        BufferedImage img;
        if(solve)img = new BufferedImage(mw, mh, BufferedImage.TYPE_INT_RGB);
        else img=new BufferedImage(mw,mh,BufferedImage.TYPE_BYTE_BINARY);
        for (int i = 0; i < mh; i++) {
            for (int j = 0; j < mw; j++) {
                short v = maze[i][j];
                if(v==DEAD_END)img.setRGB(j, i, (new Color(255, 0, 0)).getRGB());
                else if(v==VISITED)img.setRGB(j, i, (new Color(0, 0, 255)).getRGB());
                else if(v==PATH)img.setRGB(j, i, (new Color(255,255,255)).getRGB());
                else img.setRGB(j, i, (new Color(v, v, v)).getRGB());
            }
        }
        try {
            String sname=name.substring(0,name.lastIndexOf('.'));
            ImageIO.write(img, "png", new File( (solve?"solve/":"gen/") + sname + "step" + step + ".png"));
        } catch (Exception e) {}
    }

    public static void makeMaze(int N){
        n=N;
        h = fib[n];
        w = fib[n + 1];
        mh = 2 * h + 1;
        mw = 2 * w + 1;
        maze = new byte[mh][mw];
        drawMaze(2 * ((int) (Math.random() * h)) + 1, 2 * ((int) (Math.random() * w)) + 1);
        saveMaze();
    }

    public static void makeMaze(){
        h = fib[n];
        w = fib[n + 1];
        mh = 2 * h + 1;
        mw = 2 * w + 1;
        maze = new byte[mh][mw];
        drawMaze(2 * ((int) (Math.random() * h)) + 1, 2 * ((int) (Math.random() * w)) + 1);
        saveMaze();
    }

    public static void makeStepMaze(int N){
        n=N;
        h = fib[n];
        w = fib[n + 1];
        mh = 2 * h + 1;
        mw = 2 * w + 1;
        maze = new byte[mh][mw];
        drawSteps(2 * ((int) (Math.random() * h)) + 1, 2 * ((int) (Math.random() * w)) + 1);
    }

    public static void makeStepMaze(){
        h = fib[n];
        w = fib[n + 1];
        mh = 2 * h + 1;
        mw = 2 * w + 1;
        maze = new byte[mh][mw];
        drawSteps(2 * ((int) (Math.random() * h)) + 1, 2 * ((int) (Math.random() * w)) + 1);
    }

    public static void loadMaze(String filename){
        try
        {
            BufferedImage image = ImageIO.read(new File(filename));
            mh=image.getHeight();
            mw=image.getWidth();
            byte[][] output = new byte[mh][mw];
            short sh=0;
            for(int i=0;i<mh;i++) {
                for (int j = 0; j < mw; j++) {
                    sh = (short) (image.getRGB(j, i) & 0xFF);
                    if(sh==0)output[i][j] = WALL;
                    else output[i][j] = PATH;
                }
            }
            maze=output;
            maze[1][0] = PATH;
            maze[0][1] = WALL;
            maze[mh-2][mw-1] = PATH;
            maze[mh-1][mw-2] = WALL;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Failed to import "+filename+". Please run the program again.");
        }
    }

    public static void loadMaze(String filename, int height, int width){
        mh=height;
        mw=width;
        loadMaze(filename);
    }

    public static boolean isFinished(){
        return (maze[mh-2][mw-2] != PATH);
    }

    public static void solveMaze(){
        int row=1;
        int col=0;
        int dir=getDir(row, col);
        boolean dead=false;
        while(!isFinished()){
            dead=isDead(row, col);
            mark(row, col, dead);
            row+=dir==0?-1:dir==2?1:0;
            col+=dir==3?-1:dir==1?1:0;
            lastDir=(dir+2)%4;
            dir=getDir(row,col);
        }
        solved=true;
    }

    public static void solveSteps(){
        int row=1;
        int col=0;
        int dir=getDir(row, col);
        boolean dead=false;
        long step=0;
        while(!isFinished()){
            dead=isDead(row, col);
            mark(row, col, dead);
            row+=dir==0?-1:dir==2?1:0;
            col+=dir==3?-1:dir==1?1:0;
            lastDir=(dir+2)%4;
            dir=getDir(row,col);
            saveStep(true,step);
            step++;
        }
        solved=true;
        saveStep(true,step);
    }

    public static void cmdSolveSteps(){
        int row=1;
        int col=0;
        int dir=getDir(row, col);
        boolean dead=false;
        long step=0;
        while(!isFinished()){
            dead=isDead(row, col);
            mark(row, col, dead);
            row+=dir==0?-1:dir==2?1:0;
            col+=dir==3?-1:dir==1?1:0;
            lastDir=(dir+2)%4;
            dir=getDir(row,col);
            cmdSaveStep(true,step);
            step++;
        }
        solved=true;
        cmdSaveStep(true,step);
        saveMaze("solve/"+(name.substring(0,name.lastIndexOf('.')))+"step"+(step+1)+".png");
    }

    public static boolean isDead(int row, int col){
        for(int i=0;i<4;i++) {
            int cr=row;
            int cc=col;
            cr+=i==0?-1:i==2?1:0;
            cc+=i==3?-1:i==1?1:0;
            try{
                if (maze[cr][cc] == PATH) {
                    return false;
                }
            }catch(Exception e){}
        }
        return true;
    }

    public static void mark(int row, int col, boolean dead){
        if(row==mh-2&&col==mw-1)maze[row][col]=VISITED;
        else if(dead)maze[row][col]=DEAD_END;
        else maze[row][col]=VISITED;
    }

    public static int getDir(int row, int col){
        if(row==mh-2&&col==mw-2)return 1;
        for(int i=1;i<5;i++) {
            int check=(lastDir+i)%4;
            int cr=row;
            int cc=col;
            cr+=check==0?-1:check==2?1:0;
            cc+=check==3?-1:check==1?1:0;
            try{
                if (maze[cr][cc] == PATH) {
                    return check;
                }
            }catch(Exception e){}
        }
        for(int i=1;i<5;i++) {
            int check=(lastDir+i)%4;
            int cr=row;
            int cc=col;
            cr+=check==0?-1:check==2?1:0;
            cc+=check==3?-1:check==1?1:0;
            try{
                if (maze[cr][cc] == VISITED) {
                    return check;
                }
            }catch(Exception e){}
        }
        return lastDir;
    }

    public static void doMaze(String filename){
        try {
            loadMaze(filename);
            solveMaze();
            saveMaze();
        }catch(Exception e){}
    }

    public static void doMaze(int i){
        try {
            n = i;
            loadMaze("maze" + i + ".png");
            solveMaze();
            saveMaze();
        }catch(Exception e){}
    }

    public static void doMaze(){
        try{
            solveMaze();
            saveMaze();
        }catch(Exception e){}
    }

    public static void cmdDo(String filename){
        try {
            //if(maze==null)loadMaze(filename);
            solveMaze();
            cmdSave();
        }catch(Exception e){}
    }

    public static void cmdSaveDo(String filename){
        try {
            cmdSolveSteps();
        }catch(Exception e){}
    }

    public static void doStepMaze(int i){
        n=i;
        loadMaze("maze"+i+".png");
        solveSteps();
    }

    public static void promptType(){
        char choice=' ';
        Scanner scan = new Scanner(System.in);
        do{
            System.out.println("Please select an option: \nType \"g\", \"gen\", or \"generate\" to generate a maze.\nType \"s\" or \"solve\" to solve a maze.");
            try{
                choice=scan.nextLine().toLowerCase().charAt(0);
            }catch(Exception e){}
        }while(choice!='g'&&choice!='s'&&choice!='o');
        action=choice;
    }

    public static void setType(String s){
        char choice=s.toLowerCase().charAt(0);
        if(choice=='g'||choice=='s')action=choice;
    }

    public static void promptGen(){
        int i=-1;
        Scanner scan = new Scanner(System.in);
        do{
            System.out.println("Please enter the index of the size of the maze you would like to generate:");
            try{
                i=scan.nextInt();
            }catch(Exception e){}
        }while(i<0||i>fib.length);
        n=i;
    }

    public static void promptSolve(){
        String s="";
        Scanner scan = new Scanner(System.in);
        boolean valid=false;
        do{
            System.out.println("Please enter the filename of the maze you would like to solve:");
            try{
                s=scan.nextLine();
            }catch(Exception e){}
            try{
                loadMaze(s);
                valid=true;
            }catch(Exception e){
                valid=false;
            }
        }while(!valid);
        name=s;
    }

    public static void ultraSolve(String filename){
        loadMaze(filename);
        solveMaze();
    }

    public static String getID(String s){
        return s.substring(0,4);
    }

    public static int getCells(){
        int r=0;
        for(int i=1;i<mw;i+=2){
            for(int j=1;j<mh;j+=2){
                if(maze[j][i]==VISITED){
                    r++;
                }
            }
        }
        return r;
    }

    public static void main(String[] args) {
        if(args.length==0){
            promptType();
            if(action=='g'){
                promptGen();
                makeMaze();
            }
            else if(action=='s'){
                promptSolve();
                cmdDo(name);
            }
            else if(action=='o'){
                promptType();
                if(action=='g'){
                    promptGen();
                    makeStepMaze();
                }
                else if(action=='s'){
                    promptSolve();
                    cmdSolveSteps();
                }
                else{
                    System.out.println("You selected an invalid option. Please run the program again.");
                }
            }
            else{
                System.out.println("You selected an invalid option. Please run the program again.");
            }
        }
        else if(args.length==1){
            setType(args[0]);
            if(action=='g'){
                promptGen();
                makeMaze();
            }
            else if(action=='s'){
                promptSolve();
                cmdDo(name);
            }
            else{
                System.out.println("You selected an invalid option. Please run the program again.");
            }
        }
        else{
            setType(args[0]);
            if(action=='g'){
                try{
                    makeMaze(Integer.parseInt(args[1]));
                }catch(Exception e){
                    System.out.println("Invalid size index. Please run the program again.");
                }
            }
            else if(action=='s'){
                try {
                    name = args[1];
                    cmdDo(name);
                }catch(Exception e){
                    System.out.println("Invalid maze name. Please run the program again.");
                }
            }
            else{
                System.out.println("You selected an invalid option. Please run the program again.");
            }
        }
        /*try{
            File[] files = new File("mazes/").listFiles();
            PrintWriter pw = new PrintWriter("solve.txt");
            for (File file : files) {
                System.out.println(file.getName());
                long start = System.nanoTime();
                ultraSolve("mazes/" + file.getName());
                long end = System.nanoTime();
                pw.println(getID(file.getName())+" "+(end-start)+" "+getCells());
                System.out.println(getID(file.getName())+" "+(end-start)+" "+getCells());
            }
            pw.close();
        }catch(Exception e){}*/
    }
}
