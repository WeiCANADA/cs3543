/**
 
   Handson 3
   Fall 2023
 */
package simpledb;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import simpledb.metadata.MetadataMgr;
import simpledb.record.Schema;
import simpledb.remote.SimpleDriver;
import simpledb.server.SimpleDB;
import simpledb.tx.Transaction;

public class CS3543f23_handson3 {
	  
		public static void main(String args[]) {
			CS3543f23_handson3 hon3 = new CS3543f23_handson3();
			
			//hon3.createTable();// First, run this class (as a Java application) to create the customer table directly.
			hon3.PrintTableInfo();//Then, comment out the previous line,  uncomment this line and save. Start simpledb server and then run this class.
			
		}
		
		void createTable() {
			SimpleDB.init("studentdb"); 
			MetadataMgr mdMgr = SimpleDB.mdMgr(); 
			Transaction tx1 = new Transaction(); 
			
			//Instruction1: write your code below
			//Using the SimpleDB's MetadataMgr create a table customer (customerid, name, address)
			//**Hint: refer to lecture notes on simpledb metadata manager
		    
			// Create a schema for the table
		    Schema sch = new Schema();
		    sch.addIntField("customerid");
		    sch.addStringField("name", 20);
		    sch.addStringField("address", 50);
		    
		    // Create the table
		    mdMgr.createTable("customer", sch, tx1);
			
			
			tx1.commit(); 
		}
		
		//IMPORTANT: you would need to start simpledb server separately for this to work
		void PrintTableInfo() {
			//Map<String,List<String>> tbls =
	        //        new HashMap<String,List<String>>();

			Connection conn = null;
			try {
				// Step 1: connect to database server
				Driver d = new SimpleDriver();
				conn = d.connect("jdbc:simpledb://localhost", null);

				//Instruction2: write your code below
				// By querying the table fldcat(tblname, fldname, type, length, offset) 
				// directly using JDBC APIs
				// print the names and fields of ALL tables in the database 
				//(e.g., table T having fields A and B should print as  
				// Table T: Field A
				// Table T: Field B
				//)
				//**Hint: Using JDBC APIs create a statement, and execute the query with the statement and iterate over the resultset
				
				Statement stmt = conn.createStatement();
		        ResultSet rs = stmt.executeQuery("SELECT tblname, fldname FROM fldcat");
		        
		        while (rs.next()) {
		            String tblname = rs.getString("tblname");
		            String fldname = rs.getString("fldname");
		            System.out.println("Table " + tblname + ": Field " + fldname);
		        }	
				
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
			finally {
				// Step 4: close the connection
				try {
					if (conn != null)
						conn.close();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}


}
