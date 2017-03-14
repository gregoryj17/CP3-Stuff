import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class program {

    static BufferedImage image;
    static int[][] locations = new int[][]{new int[]{2512, 2257}, new int[]{4480, 8440}, new int[]{4600, 7630}, new int[]{3050, 1850}, new int[]{1561, 5692}, new int[]{5857, 3638}, new int[]{5448, 1745}, new int[]{7611, 308}, new int[]{5835, 1084}, new int[]{6395, 2618}, new int[]{7209, 8437}, new int[]{5005, 8305}};
    static String[] names = new String[]{"OldSquirrelTown", "Louisville", "LazyTown", "PlainsOfAshford", "Brockville", "AceHill", "CarrotCake", "Jaranohr", "MartianHub", "Williamsberg", "SquirrelTown", "GiveMeASecond"};
    static short[][] costmap;
    static short[][] tilecosts;
    static ArrayList<Node> fringe = new ArrayList<Node>();
    static short[] costlist;
    static short wcost;
    static Node[] hitpoints;
    static Node hitpoint;
    static final int RED = 0;
    static final int GREEN = 1;
    static final int BLUE = 2;
    static final int YELLOW = 3;
    static int s = 0;
    static short[][] dirs = new short[][]{new short[]{0, -1}, new short[]{1, 0}, new short[]{0, 1}, new short[]{-1, 0}};
    static boolean[][] end;

    public static void loadImage(String filename) throws Exception {
        image = ImageIO.read(new File(filename));
    }

    public static void saveImage(String filename) {
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (Exception e) {
        }
    }

    public static void setColor(int x, int y, int type) {
        int val = getValue(x, y);
        if (type == RED) {
            image.setRGB(x, y, val << 16);
        } else if (type == GREEN) {
            image.setRGB(x, y, val << 8);
        } else if (type == BLUE) {
            image.setRGB(x, y, val);
        } else if (type == YELLOW) {
            image.setRGB(x, y, new Color(255, 255, 0).getRGB());
        } else {
            image.setRGB(x, y, (((val << 8) + val) << 8) + val);
        }
    }

    public static boolean touching(int city, int cx, int cy) {
        int cityx = locations[city][0];
        int cityy = locations[city][1];
        if ((cx >= cityx && cx < cityx + 500) && (cy == cityy - 1 || cy == cityy + 500)) return true;
        if ((cy >= cityy && cy < cityy + 500) && (cx == cityx - 1 || cx == cityx + 500)) return true;
        return false;
    }

    public static boolean touchingme(int x, int y) {
        int cityx = 1300;
        int cityy = 1500;
        return (x >= cityx && x < cityx + 500 && y >= cityy && y < cityy + 500);
    }

    public static boolean touched(int city) {
        int cityx = locations[city][0];
        int cityy = locations[city][1];
        for (int i = cityx; i < cityx + 500; i++) {
            if ((costmap[i][cityy - 1] != Short.MAX_VALUE && costmap[i][cityy - 1] != -1) || (costmap[i][cityy + 500] != Short.MAX_VALUE && costmap[i][cityy + 500] != -1))
                return true;
        }
        for (int j = cityy; j < cityy + 500; j++) {
            if ((costmap[cityx - 1][j] != Short.MAX_VALUE && costmap[cityx - 1][j] != -1) || (costmap[cityx + 500][j] != Short.MAX_VALUE && costmap[cityx + 500][j] != -1))
                return true;
        }
        return false;
    }

    public static short dirTouching(int city, int cx, int cy) {
        int cityx = locations[city][0];
        int cityy = locations[city][1];
        if ((cx >= cityx && cx < cityx + 500) && (cy == cityy - 1)) return 0;
        if ((cx >= cityx && cx < cityx + 500) && (cy == cityy + 500)) return 2;
        if ((cy >= cityy && cy < cityy + 500) && (cx == cityx + 500)) return 1;
        if ((cy >= cityy && cy < cityy + 500) && (cx == cityx - 1)) return 3;
        return -1;
    }

    public static boolean allTouched() {
        for (int i = 0; i < locations.length; i++) {
            if (!touched(i)) return false;
        }
        return true;
    }

    public static void paths() {
        for (short i = 0; i < locations.length; i++) {
            Node current = hitpoints[i];
            setColor(current.x, current.y, RED);
            while (current.cost > 0) {
                Node min = current;
                for (short j = 0; j < dirs.length; j++) {
                    if (costmap[current.x + dirs[j][0]][current.y + dirs[j][1]] != -1 && costmap[current.x + dirs[j][0]][current.y + dirs[j][1]] < min.cost) {
                        min = new Node((short) (current.x + dirs[j][0]), (short) (current.y + dirs[j][1]), costmap[current.x + dirs[j][0]][current.y + dirs[j][1]]);
                    }
                }
                current = min;
                setColor(current.x, current.y, RED);
            }
        }
    }

    public static void path() {
        Node current = hitpoint;
        setColor(current.x, current.y, YELLOW);
        while (current.cost > 0) {
            Node min = current;
            for (short j = 0; j < dirs.length; j++) {
                if (costmap[current.x + dirs[j][0]][current.y + dirs[j][1]] != -1 && costmap[current.x + dirs[j][0]][current.y + dirs[j][1]] < min.cost) {
                    min = new Node((short) (current.x + dirs[j][0]), (short) (current.y + dirs[j][1]), costmap[current.x + dirs[j][0]][current.y + dirs[j][1]]);
                }
            }
            current = min;
            setColor(current.x, current.y, YELLOW);
        }
    }

    public static int getValue(int x, int y) {
        int val = image.getRGB(x, y) & 0xFF;
        if (val == 0) {
            val = (image.getRGB(x, y) >> 8) & 0xFF;
            if (val == 0) {
                val = (image.getRGB(x, y) >> 16) & 0xFF;
            }
        }
        return val;
    }

    public static short getCost(short x, short y, short dx, short dy) {
        return (short) (costmap[x][y] + 1 + Math.pow((getValue(x, y) - getValue(x + dx, y + dy)), 2));
    }

    public static short getdCost(short x, short y, short dx, short dy) {
        return (short) (1 + Math.pow((getValue(x, y) - getValue(x + dx, y + dy)), 2));
    }

    public static short getWCost(short x, short y, short dx, short dy) {
        return (short) (costmap[x][y] + tilecosts[x + dx][y + dy]);
    }

    public static void solveCost() {
        for (int i = 1300; i < 1800; i++) {
            fringe.add(new Node((short) i, (short) 1500, (short) 0));
            fringe.add(new Node((short) i, (short) 1999, (short) 0));
            costmap[i][1500] = 0;
            costmap[i][1999] = 0;
        }
        for (int j = 1501; j < 1999; j++) {
            fringe.add(new Node((short) 1300, (short) j, (short) 0));
            fringe.add(new Node((short) 1799, (short) j, (short) 0));
            costmap[1300][j] = 0;
            costmap[1799][j] = 0;
        }
        while (!fringe.isEmpty() && !allTouched()) {
            Node current = fringe.remove(0);
            for (short i = 0; i < dirs.length; i++) {
                short cx = (short) (current.x + dirs[i][0]);
                short cy = (short) (current.y + dirs[i][1]);
                if (cx < 0 || cy < 0 || cx > costmap.length - 1 || cy > costmap[0].length - 1) continue;
                short cost = getCost(current.x, current.y, dirs[i][0], dirs[i][1]);
                if ((cost < costmap[cx][cy] || costmap[cx][cy] == -1) && costmap[cx][cy] != Short.MAX_VALUE) {
                    costmap[cx][cy] = cost;
                    int index = fringe.size();
                    for (int k = 0; k < fringe.size(); k++) {
                        if (cost > fringe.get(k).cost) index = k;
                        else {
                            index = k;
                            break;
                        }
                    }
                    fringe.add(index, new Node(cx, cy, cost));
                }
                for (short k = 0; k < locations.length; k++) {
                    short q = dirTouching(k, cx, cy);
                    short r = (short) ((q + 2) % 4);
                    if (q != -1) cost = (short) (cost + getdCost(cx, cy, dirs[r][0], dirs[r][1]));
                    if (q != -1 && (cost < costlist[k] || costlist[k] == -1)) {
                        //System.out.println(names[k] + " " + cost);
                        costlist[k] = cost;
                        hitpoints[k] = new Node((short) (cx + dirs[r][0]), (short) (cy + dirs[r][1]), cost);
                    }
                }
            }
        }
    }

    public static void solveWCost() {
        int height = image.getHeight();
        costmap = new short[height][height];
        tilecosts = new short[height][height];
        fringe = new ArrayList<Node>();
        end = new boolean[height][height];
        for (short i = 0; i < height; i++) {
            for (short j = 0; j < height; j++) {
                int color = image.getRGB(i, j);
                costmap[i][j] = -1;
                if (color == (new Color(0, 255, 0)).getRGB()) {
                    fringe.add(new Node(i, j, (short) 0));
                    tilecosts[i][j] = 0;
                    costmap[i][j] = 0;
                } else if (color == (new Color(255, 0, 0)).getRGB()) {
                    end[i][j] = true;
                    tilecosts[i][j] = 1;
                } else if (color == (new Color(0, 0, 255)).getRGB()) {
                    tilecosts[i][j] = 2;
                } else if (color == (new Color(0, 0, 0)).getRGB()) {
                    tilecosts[i][j] = Short.MAX_VALUE;
                } else {
                    tilecosts[i][j] = 1;
                }
            }
        }
        boolean solved = false;
        while (!fringe.isEmpty() && !solved) {
            Node current = fringe.remove(0);
            for (short i = 0; i < dirs.length; i++) {
                short cx = (short) (current.x + dirs[i][0]);
                short cy = (short) (current.y + dirs[i][1]);
                if (cx < 0 || cy < 0 || cx > costmap.length - 1 || cy > costmap[0].length - 1) continue;
                short cost = getWCost(current.x, current.y, dirs[i][0], dirs[i][1]);
                if ((cost < costmap[cx][cy] || costmap[cx][cy] == -1) && tilecosts[cx][cy] != Short.MAX_VALUE) {
                    costmap[cx][cy] = cost;
                    int index = fringe.size();
                    for (int k = 0; k < fringe.size(); k++) {
                        if (cost > fringe.get(k).cost) index = k;
                        else {
                            index = k;
                            break;
                        }
                    }
                    fringe.add(index, new Node(cx, cy, cost));
                    if (end[cx][cy]) {
                        wcost = cost;
                        hitpoint = new Node(cx, cy, cost);
                        solved = true;
                        //System.out.println(hitpoint);
                        break;
                    }
                }
            }
        }
    }

    public static void doTerrain() throws Exception {
        loadImage("terrain.png");
        costmap = new short[10012][10012];
        for (int i = 0; i < costmap.length; i++) {
            for (int j = 0; j < costmap[0].length; j++) {
                costmap[i][j] = -1;
            }
        }
        for (int i = 1300; i < 1800; i++) {
            for (int j = 1500; j < 2000; j++) {
                costmap[i][j] = Short.MAX_VALUE;
            }
        }
        for (int i = 0; i < locations.length; i++) {
            for (int j = locations[i][0]; j < locations[i][0] + 500; j++) {
                for (int k = locations[i][1]; k < locations[i][1] + 500; k++) {
                    costmap[j][k] = Short.MAX_VALUE;
                }
            }
        }
        costlist = new short[locations.length];
        for (int i = 0; i < costlist.length; i++) {
            costlist[i] = -1;
        }
        hitpoints = new Node[costlist.length];
        solveCost();
        PrintWriter pw = new PrintWriter("paths.txt");
        for (int i = 0; i < hitpoints.length; i++) {
            //System.out.println("Jacksonville " + names[i] + " " + hitpoints[i]);
            pw.println("Jacksonville " + names[i] + " " + hitpoints[i]);
        }
        pw.close();
        paths();
        saveImage("mars_path.png");
        //System.out.println("Final save complete.");
    }

    public static void doWarmup()throws Exception{
        loadImage("dij.png");
        solveWCost();
        path();
        saveImage("warmup_paths.png");
    }

    public static void main(String[] args) throws Exception {
        if(args.length>0&&(args[0].charAt(0)=='w'||args[0].charAt(0)=='W')){
            doWarmup();
        }else{
            doTerrain();
        }
    }

}

class Node {
    public short x;
    public short y;
    public short cost;

    public Node(short x, short y, short cost) {
        this.x = x;
        this.y = y;
        this.cost = cost;
    }

    public String toString() {
        return x + " " + y + " " + cost;
    }
}