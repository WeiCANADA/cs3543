/**
 * CS3543 - Database Systems and Administration
   Hands-on 4
   Fall, 2023
 */
 

import java.util.Arrays;
import java.util.Collection;

import simpledb.metadata.MetadataMgr;
import simpledb.query.Constant;
import simpledb.query.ConstantExpression;
import simpledb.query.Expression;
import simpledb.query.FieldNameExpression;
import simpledb.query.IntConstant;
import simpledb.query.Predicate;
import simpledb.query.ProductScan;
import simpledb.query.ProjectScan;
import simpledb.query.Scan;
import simpledb.query.SelectScan;
import simpledb.query.TableScan;
import simpledb.query.Term;
import simpledb.record.TableInfo;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class handson4 {

	public static void main(String args[]) {
		
		/*
		 * Implement the query as R.A. scan. 
		 * select dname, title, prof from dept, course, section where did=deptid and cid=courseid
		 * 
		 */
		
		SimpleDB.init("studentdb");
		Transaction tx = new Transaction();
		MetadataMgr mdMgr = SimpleDB.mdMgr();

		// scans for each of the relations dept, course, section
		
		// create scans for relation dept
		TableInfo dti = mdMgr.getTableInfo("dept", tx);
		Scan s1 = new TableScan(dti, tx);
		
		TableInfo cti = mdMgr.getTableInfo("course", tx);
		Scan s2 = new TableScan(cti, tx);
		
		TableInfo sti = mdMgr.getTableInfo("section", tx);
		Scan s3 = new TableScan(sti, tx);
			
	
		
		// selection predicates
		
		//Instruction 1: create the predicate  did=deptid
		Expression lhs = new FieldNameExpression("did");
		Expression rhs = new FieldNameExpression("deptid");
		Term t1 = new Term(lhs, rhs);
		Predicate pred1 = new Predicate(t1);



		//Instruction 2: create the predicate cid=courseid
		
		Expression lhs2 = new FieldNameExpression("cid");
		Expression rhs2 = new FieldNameExpression("coursid");
		Term t2 = new Term(lhs2, rhs2);
		Predicate pred2 = new Predicate(t2);
		
		
		
	
		//Instruction 3: create the product and select scans for the query tree
		
		Scan p1 = new ProductScan(s1, s2);
		Scan p2 = new ProductScan(p1, s3);
		
		Scan s4 = new SelectScan(p2, pred1);
		Scan s5 = new SelectScan(s4, pred2);
		

		// creating the project scan
		Collection<String> fieldlist = Arrays.asList("dname", "title", "prof");
		
		Scan sTop = null;
		
		//Instruction 3: create a project scan with the topmost scan so far in the query tree,
		// for eg. if s7 is the topmost scan so far,
		// sTop = new ProjectScan(s7, fieldlist);
		sTop = new ProjectScan(s5, fieldlist);
		
		
		// executing the final scan
		while (sTop.next())
			System.out.println(sTop.getString("dname")
						+ "," + sTop.getString("title")
						+ "," + sTop.getString("prof"));
		sTop.close();

	}
}