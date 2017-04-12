import java.util.*;

public class HashTable<K, V> {

    public K[] keys;
    public V[] values;
    public int load = 0;
    public int size;

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
        if (loc == -1) return null;
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

    public double getLoadFactor() {
        return (load * 1.0) / size;
    }

    public int getSize() {
        return size;
    }

    public String toString() {
        String str = "";
        str += load + ((load != 1) ? " elements\n" : " element\n");
        for (int i = 0; i < size; i++) {
            if (keys[i] != null && values[i] != null) str += keys[i] + ", " + values[i] + "\n";
        }
        return str;
    }

    public static void main(String[] args) {
        HashTable<String, Integer> hash = new HashTable<>("meme", 10);
        System.out.println(hash);
        System.out.println(hash.get("meme"));
        hash.set("meme", (hash.get("meme") + 1));
        System.out.println(hash.get("meme"));
        hash.delete("meme");
        System.out.println(hash.contains("meme"));
        System.out.println(hash);
        hash.add("meme", 1);
        hash.add("nice", 3);
        System.out.println(hash);
        System.out.println(hash.getLoadFactor());
    }
}
