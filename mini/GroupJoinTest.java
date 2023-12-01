
// CS3543 - Database Systems and Administration
//
// Implement the following SQL query with Relational Algebra operators in SimpleDB:
//
//SELECT s.SId, COUNT(e.StudentId) AS cnt
//FROM Student s
//LEFT JOIN enroll e ON s.SId = e.StudentId
//GROUP BY s.SId;

import simpledb.metadata.MetadataMgr;
import simpledb.query.*;
import simpledb.record.TableInfo;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.logging.Level;


public class GroupJoinTest {
    private static final Logger logger = Logger.getLogger(GroupJoinTest.class.getName());

    public static void main(String[] args) {

//Initialize Scans for Each Table: Create scans for the Student and enroll tables.

        SimpleDB.init("studentdb");
        Transaction tx = new Transaction();

        try {MetadataMgr mdMgr = SimpleDB.mdMgr();


        /*  scans for each of the relations student, enroll */
        TableInfo studentTblInfo = mdMgr.getTableInfo("student", tx);
        TableScan studentsScan = new TableScan(studentTblInfo, tx);

        TableInfo enrollTblInfo = mdMgr.getTableInfo("enroll", tx);
        TableScan enrollScan = new TableScan(enrollTblInfo, tx);



//Initialize GroupJoinScan with the scans and the hash table.
        GroupJoinScan groupJoinScan = new GroupJoinScan(studentsScan, enrollScan, "sid", "studentid");

        while (groupJoinScan.next()) {
            int sid = groupJoinScan.getInt("sid");
            int count = groupJoinScan.getInt("cnt");
            System.out.println("SId: " + sid + ", Count: " + count);    
        }

        
        groupJoinScan.close();
        tx.commit();
    } catch (Exception e) {

            logger.log(Level.SEVERE, "Exception occurred", e);
            tx.rollback();
    }
}
}



