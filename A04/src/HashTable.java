import java.io.*;
import java.util.*;

public class HashTable<K, V> {

    public K[] keys;
    public V[] values;
    public int load = 0;
    public int size;
    public int collisions=0;

    public HashTable() {
        size = 2;
        keys = (K[]) (new Object[size]);
        values = (V[]) (new Object[size]);
    }

    public HashTable(K key, V value) {
        size = 2;
        keys = (K[]) (new Object[size]);
        values = (V[]) (new Object[size]);
        add(key, value);
    }

    public HashTable(int size) {
        this.size = size;
        keys = (K[]) (new Object[size]);
        values = (V[]) (new Object[size]);
    }

    public HashTable(int size, K key, V value) {
        this.size = size;
        keys = (K[]) (new Object[size]);
        values = (V[]) (new Object[size]);
        add(key, value);
    }

    public void add(K key, V value) {
        if (key == null || value == null) return;
        int hash = Math.abs(key.hashCode()) % size;
        while (keys[hash] != null && values[hash] != null) {
            hash = (hash + 1) % size;
            collisions++;
        }
        keys[hash] = key;
        values[hash] = value;
        load++;
        if ((load * 1.0 / keys.length) >= .75) upsize();
    }

    public void upsize() {
        size *= 2;
        K[] oKeys = keys;
        V[] oVals = values;
        keys = (K[]) (new Object[size]);
        values = (V[]) (new Object[size]);
        load = 0;
        collisions=0;
        for (int i = 0; i < oKeys.length; i++) {
            if (oKeys[i] != null && oVals[i] != null) add(oKeys[i], oVals[i]);
        }
    }

    public boolean contains(K key) {
        int loc = find(key);
        return (loc > -1);
    }

    public V get(K key) {
        int loc = find(key);
        if (loc < 0) return null;
        else return values[loc];
    }

    public int find(K key) {
        int hash = Math.abs(key.hashCode()) % size;
        while (keys[hash] != null) {
            if (keys[hash].equals(key) && values[hash] != null) {
                return hash;
            }
            hash = (hash + 1) % size;
        }
        return -1;
    }

    public boolean delete(K key) {
        int loc = find(key);
        if (loc == -1) return false;
        else {
            values[loc] = null;
            load--;
            return true;
        }
    }

    public boolean set(K key, V value) {
        int loc = find(key);
        if (loc == -1) return false;
        else {
            values[loc] = value;
            return true;
        }
    }

    public K[] getKeys() {
        return (K[]) keys;
    }

    public V[] getValues() {
        return (V[]) values;
    }

    public double getLoadFactor() {
        return (load * 1.0) / size;
    }

    public int getSize() {
        return size;
    }

    public int getCollisions(){
        return collisions;
    }

    public String toString() {
        String str = "";
        str += load + ((load != 1) ? " elements\n" : " element\n");
        for (int i = 0; i < size; i++) {
            if (keys[i] != null && values[i] != null) str += keys[i] + ", " + values[i] + "\n";
        }
        return str;
    }

    public static void main(String[] args) throws FileNotFoundException {
        HashTable<String, Integer> hash = new HashTable<>();
        Scanner text = new Scanner(new File("book.txt"));
        String book = "";
        while (text.hasNextLine()) {
            book += text.nextLine() + " ";
        }
        text.close();
        System.out.println("Import complete.");
        String[] words = book.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        /*int num=0;
        PrintWriter statsout = new PrintWriter("stats.csv");
        statsout.println("Elements,Size,Load,Collisions");*/
        for (String word : words) {
            /*if(num%100==0){
                statsout.println(num+","+hash.getSize()+","+hash.getLoadFactor()+","+hash.getCollisions());
                System.out.println(num);
            }*/
            Integer n = hash.get(word);
            if (n == null) {
                hash.add(word, 1);
            } else {
                hash.set(word, (n + 1));
            }
            //num++;
        }
        //statsout.close();
        hash.delete("t");
        hash.delete("s");
        Object[] keys = hash.getKeys();
        Object[] values = hash.getValues();
        for (int i = 0; i < keys.length; i++) {
            int maxIndex = i;
            for (int j = i; j < keys.length; j++) {
                if (values[maxIndex] == null) maxIndex = j;
                if (values[j] != null && (Integer) (values[j]) > (Integer) (values[maxIndex])) maxIndex = j;
            }
            Object stemp = keys[i];
            keys[i] = keys[maxIndex];
            keys[maxIndex] = stemp;
            Object itemp = values[i];
            values[i] = values[maxIndex];
            values[maxIndex] = itemp;

        }
        PrintWriter out = new PrintWriter("output.csv");
        out.println("Word,Count");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null || values[i] == null) break;
            System.out.println((String) (keys[i]) + ", " + (Integer) (values[i]));
            out.println((String) (keys[i]) + "," + (Integer) (values[i]));
        }
        out.close();
        PrintWriter output = new PrintWriter("top100.csv");
        output.println("Word,Count");
        for (int i = 0; i < 100; i++) {
            if (keys[i] == null || values[i] == null) break;
            output.println((String) (keys[i]) + "," + (Integer) (values[i]));
        }
        output.close();
        /*
        HashMap<String, Integer> javahash = new HashMap<String, Integer>();
        for (String word : words) {
            Integer n = javahash.get(word);
            if (n == null) {
                javahash.put(word, 1);
            } else {
                javahash.put(word, n + 1);
            }
        }
        javahash.remove("t");
        javahash.remove("s");
        javahash.remove("");
        PrintWriter javaout = new PrintWriter("javaoutput.csv");
        String heck = "Word,Count\n";
        System.out.println(javahash.size());
        for (Map.Entry<String, Integer> entry : javahash.entrySet()) {
            heck += (entry.getKey() + ", " + entry.getValue().toString()) + "\n";
        }
        System.out.println(heck);
        javaout.println(heck);
        javaout.close();*/
    }
}
