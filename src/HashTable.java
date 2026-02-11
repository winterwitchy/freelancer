public class HashTable<K, V> {
    public int capacity;
    public int size;
    public K[] keys;
    public V[] values;

    public static Object deleted = new Object();        // instead of null for deleted entries to help with searches

    public HashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.size = 0;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
    }

    public boolean put(K key, V value) {
        if (size >= capacity / 2) {
            resize(nextPrime(capacity * 2));            // instead of doubling the size, find a new prime
        }
        int originalHash = getHash(key);
        int hash = originalHash;
        int i = 1;
        int firstDeleted = -1;
        while (keys[hash] != null) {
            if (keys[hash].equals(key)) {
                return false;
            }
            if (keys[hash] == deleted && firstDeleted == -1) {      // reuse deleted slot
                firstDeleted = hash;
            }
            hash = (originalHash + i * i) % capacity;
            i++;
            if (i > (capacity-size)) {
                resize(nextPrime(capacity * 2));
                return put(key, value);
            }
        }
        if (firstDeleted != -1) {
            hash = firstDeleted;
        }
        keys[hash] = key;
        values[hash] = value;
        size++;
        return true;
    }

    public int getHash(K key) {                     // horner's method
        int hash = 0;
        String s = key.toString();
        for (int i = 0; i < s.length(); i++) {
            hash = (hash * 31 + s.charAt(i)) % capacity;
        }
        return hash;
    }

    public void resize(int newCapacity) {           // find a new prime twice the current size, rehash everything
        HashTable<K, V> newTable = new HashTable<>(newCapacity);
        for (int i = 0; i < capacity; i++) {
            if (keys[i] != null && keys[i] != deleted) {
                newTable.put(keys[i], values[i]);
            }
        }
        this.capacity = newTable.capacity;
        this.keys = newTable.keys;
        this.values = newTable.values;
    }

    public int nextPrime(int num) {
        num++;
        boolean isPrime = true;
        if (num % 2 == 0 || num % 3 == 0) {
            return nextPrime(num+1);
        }
        for (int i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0) {
                isPrime = false;
                break;
            }
        }
        if (!isPrime) {
            return nextPrime(num+1);
        }
        return num;
    }


    public V get(K key) {
        int hash = getHash(key);
        int originalHash = hash;
        int i = 1;
        while ((keys[hash] != null) && i<=capacity) {
            if (keys[hash].equals(key)) {
                return values[hash];
            }
            hash = (originalHash + i*i) % capacity;
            i++;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public boolean remove(K key) {
        int hash = getHash(key);
        int originalHash = hash;
        int i = 1;
        while (keys[hash] != null && i<=capacity) {
            if (keys[hash].equals(key)) {
                keys[hash] = (K) deleted;
                values[hash] = null;
                size--;
                return true;
            }
            hash = (originalHash + i*i) % capacity;
            i++;
        }
        return false;
    }



}
