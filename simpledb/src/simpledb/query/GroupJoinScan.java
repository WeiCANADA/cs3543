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
        lhsScan.beforeFirst();
        while (lhsScan.next()) {
            Constant sid = lhsScan.getVal(lhsField);
            hashTable.add(sid, lhsScan.getRid(), new HashTable.AggregateVal());
        }

        // Update counts based on enroll records
        rhsScan.beforeFirst();
        rhsScan.beforeFirst();
        while (rhsScan.next()) {
            Constant studentId = rhsScan.getVal(rhsField);
            for (Map.Entry<Constant, List<HashTable.Pair<RID, HashTable.AggregateVal>>> entry : hashTable.entrySet()) {
                if (entry.getKey().equals(studentId)) {
                    for (HashTable.Pair<RID, HashTable.AggregateVal> pair : entry.getValue()) {
                        pair.second.increment();
                    }
                }
            }
        }
        iter = hashTable.entrySet().iterator();
    }

    public boolean next() {
        if (!iter.hasNext()) {
            return false;
        }
        currentEntry =iter.next();
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
            return new IntConstant(currentEntry.getValue().size());
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
