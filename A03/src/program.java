import sun.security.mscapi.PRNG;

import java.text.DecimalFormat;
import java.util.TreeSet;
import java.io.*;
public class program {
    static DecimalFormat df = new DecimalFormat("##.#########");
    public static void main(String[] args) {
        try {
            PrintWriter out = new PrintWriter("addtimes.txt");
            PrintWriter hout = new PrintWriter("heights.txt");
            PrintWriter rout = new PrintWriter("removaltimes.txt");
            for (int i = 0; i < 24; i++) {
                try {
                    System.out.print(Math.pow(2, i) + " items: ");
                    out.print((int)(Math.pow(2,i))+"\t");
                    Tree<Integer> myTree = new Tree<Integer>();
                    long start = System.nanoTime();
                    for (int j = 0; j < Math.pow(2, i); j++) {
                        myTree.add(j);
                    }
                    long finish = System.nanoTime();
                    out.print(df.format(((finish - start) * Math.pow(10, -9))) + "\t");
                    System.out.print("My Tree: " + df.format(((finish - start) * Math.pow(10, -9))) + " seconds. ");
                    hout.println((int)(Math.pow(2,i))+"\t"+myTree.height());
                    TreeSet<Integer> treeSet = new TreeSet<Integer>();
                    start = System.nanoTime();
                    for (int j = 0; j < Math.pow(2, i); j++) {
                        treeSet.add(j);
                    }
                    finish = System.nanoTime();
                    out.println(df.format(((finish - start) * Math.pow(10, -9))));
                    System.out.println("TreeSet: " + df.format(((finish - start) * Math.pow(10, -9))) + " seconds. ");
                    System.out.print(Math.pow(2, i) + " items: ");
                    rout.print((int)(Math.pow(2,i))+"\t");
                    start=System.nanoTime();
                    while(!myTree.isEmpty()){
                        myTree.removeMin();
                    }
                    finish=System.nanoTime();
                    rout.print(df.format(((finish - start) * Math.pow(10, -9))) + "\t");
                    System.out.print("My Tree: " + df.format(((finish - start) * Math.pow(10, -9))) + " seconds. ");
                    start=System.nanoTime();
                    while(!treeSet.isEmpty()){
                        treeSet.remove(treeSet.first());
                    }
                    finish=System.nanoTime();
                    rout.println(df.format(((finish - start) * Math.pow(10, -9))));
                    System.out.println("TreeSet: " + df.format(((finish - start) * Math.pow(10, -9))) + " seconds. ");
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            out.close();
            hout.close();
            rout.close();
        }catch(Exception f){
            f.printStackTrace();
        }
    }
}
