package simpledb.query;

import simpledb.record.RID;

import java.util.*;

public class GroupJoinScan implements Scan {
    private HashTable hashTable;
    private Iterator<Map.Entry<Constant, List<HashTable.Pair<RID, HashTable.AggregateVal>>>> iter;
    private Map.Entry<Constant, List<HashTable.Pair<RID, HashTable.AggregateVal>>> currentEntry;
    private TableScan lhsScan, rhsScan;
    private String lhsField, rhsField;

    public GroupJoinScan(TableScan lhsScan, TableScan rhsScan,
                         String lhsField, String rhsField) {
        this.lhsScan = lhsScan;
        this.rhsScan = rhsScan;
        this.lhsField = lhsField;
        this.rhsField = rhsField;
        hashTable = new HashTable();
        beforeFirst();
    }

    public void beforeFirst() {
        // Initialize the hash table with counts for each SId
        System.out.println("Initializing hashTable...");
        lhsScan.beforeFirst();

        if (!lhsScan.next()) {
            System.out.println("No records found in STUDENT table.");
            return;
        }

        do {
            IntConstant sid = (IntConstant) lhsScan.getVal(lhsField);
            hashTable.add(sid, lhsScan.getRid(), new HashTable.AggregateVal());
            System.out.println("Added SId to hashTable: " + sid);
        } while (lhsScan.next());

        System.out.println("Updating counts based on enroll records...");

        // Update counts based on enroll records
        rhsScan.beforeFirst();
        while (rhsScan.next()) {
            Constant studentId = (IntConstant) rhsScan.getVal(rhsField);
            System.out.println("Processing StudentId: " + studentId);
            for (Map.Entry<Constant, List<HashTable.Pair<RID, HashTable.AggregateVal>>> entry : hashTable.entrySet()) {
                if (entry.getKey().equals(studentId)) {
                    System.out.println("Found matching StudentId: " + studentId);
                    for (HashTable.Pair<RID, HashTable.AggregateVal> pair : entry.getValue()) {
                        pair.second.increment();
                        System.out.println("Incremented count for StudentId: " + studentId);
                    }
                }
            }
        }
        System.out.println("Finished updating counts. Preparing for iteration...");
        iter = hashTable.entrySet().iterator();
    }

    public boolean next() {
        if (iter == null || !iter.hasNext()) {
            return false;
        }
        currentEntry = iter.next();
        return true;
    }


    @Override
    public void close() {
        lhsScan.close();
        rhsScan.close();
    }

    @Override
    public Constant getVal(String fldname) {
        if (fldname.equals(lhsField)) {
            return currentEntry.getKey();
        } else if (fldname.equals("cnt")) {
            int count = 0;
            for (HashTable.Pair<RID, HashTable.AggregateVal> pair : currentEntry.getValue()) {
                count += pair.second.getCount();
            }
            return new IntConstant(count);
        }
        throw new IllegalStateException("Field '" + fldname + "' not found");
    }


    public int getInt(String fldname) {
        Constant val = getVal(fldname);
        if (val instanceof IntConstant) {
            return (Integer) val.asJavaVal();
        }
        throw new IllegalStateException("Value for field '" + fldname + "' is not an integer");
    }

    public String getString(String fldname) {
        return getVal(fldname).toString();
    }


    public boolean hasField(String fldname) {
        return fldname.equals(lhsField) || fldname.equals("cnt");
    }

}
