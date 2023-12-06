package simpledb.query;

import simpledb.record.RID;

import java.util.*;

/**
 * A hash table class that maps a field value to pairs of record IDs and aggregate values.
 */
public class HashTable {
    private Map<Constant, List<Pair<RID, AggregateVal>>> hashTable;

    public HashTable() {
        hashTable = new HashMap<>();
    }

    /**
     * Adds a record and its initial aggregate value to the hash table.
     *
     * @param fieldValue    the field value to hash on.
     * @param rid           the record ID associated with the field value.
     * @param initialAggVal the initial aggregate value associated with the record.
     */
    public void add(Constant fieldValue, RID rid, AggregateVal initialAggVal) {
        hashTable.computeIfAbsent(fieldValue, k -> new ArrayList<>())
                .add(new Pair<>(rid, initialAggVal));
    }

    /**
     * Retrieves a list of pairs of RIDs and aggregate values associated with the given field value.
     * //     * @param fieldValue the field value to look up.
     *
     * @return a list of pairs of RIDs and aggregate values, or null if no matching pairs are found.
     */
//    public List<Pair<RID, AggregateVal>> getEntries(Constant fieldValue) {
//        return hashTable.get(fieldValue);
//    }
    public Collection<List<Pair<RID, AggregateVal>>> getEntries() {
        return hashTable.values();
    }


 

    /**
     * A pair of a record ID and an aggregate value.
     */
    public static class Pair<T, U> {
        public T first;
        public U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }

    /**
     * A class representing an aggregate value.
     */
    public static class AggregateVal {
        private int count; // Holds the count

        public AggregateVal() {
            this.count = 0; // Initialize count to 0
        }

        public void increment() {
            count++; // Increment count
        }

        public int getCount() {
            return count; // Get current count
        }



    }
    public Set<Map.Entry<Constant, List<Pair<RID, AggregateVal>>>> entrySet() {
        return hashTable.entrySet();
    }



}
