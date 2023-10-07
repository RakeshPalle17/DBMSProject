// usage:  1. compile: javac -cp /usr/lib/oracle/18.3/client64/lib/ojdbc8.jar RBMSJavaImpl.java
//         2. execute: java -cp /usr/lib/oracle/18.3/client64/lib/ojdbc8.jar RBMSJavaImpl.java
//
//Illustrate call stored procedure
import java.sql.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import oracle.jdbc.pool.OracleDataSource;
import java.util.Scanner;;

public class RBMSJavaImpl {

    static CallableStatement cs = null;
	static Connection conn = null;
	static ResultSetMetaData resultMetaData=null;

	// function for returing the query based on the user input
	public static String displayAllTables(String query){
	 try{
	  System.out.println("Select the table to display from the following: "+ "\n" + 
	                   "1. Employees." + "\n" + 
	                    "2. Customers." + "\n" + 
						"3. Products."+ "\n" +
						"4. ProdDiscnts."+ "\n" +
						"5. Purchases."+ "\n" +
						"6. Logs.");

	  Scanner s = new Scanner(System.in);
      int choice = s.nextInt();
	  // switch case to select the tables based on the user choice
	  switch(choice){
		case 1: query="begin ? := RBMSPackage.retrieveEmployees(); end;";
		         break;
		case 2: query="begin ? := RBMSPackage.retrieveCustomers(); end;";
		         break;
		case 3: query="begin ? := RBMSPackage.retrieveProducts(); end;";
		         break;
		case 4: query="begin ? := RBMSPackage.retrieveProdDiscnts(); end;";
		         break;
		case 5: query="begin ? := RBMSPackage.retrievePurchases(); end;";
		         break;
		case 6: query="begin ? := RBMSPackage.retrieveLogs(); end;";
		        break;
		default: System.out.println("Invalid: choice of Table");
	  }
	}

	catch(Exception ex){
		System.out.println ("\n** Exception caught **\n" + ex.getMessage());
	}
	return query;
	}


    /**
	 *  function to print the monthly sales activities of a given
	 *  employee which return the query for it
	 */

	public static String employeeMonthlySale(String query){

		query="begin ? := RBMSPackage.monthlySaleActvities(?); end;";
		return query;

	}

    // Function to add the tuple to employee table and excecuting the query
	public static void addTupleInEmployees(){
		try{
		    // Input eid, name, telephone, email from keyboard
			BufferedReader readKeyBoard;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Please enter EID: ");
			String eid = readKeyBoard.readLine();

			System.out.print("Please enter name: ");
			String name = readKeyBoard.readLine();

			System.out.print("Please enter telephone: ");
			String telephone = readKeyBoard.readLine();

			System.out.print("Please enter email: ");
			String email = readKeyBoard.readLine();
			
			String query = "{call RBMSPackage.add_employee(?, ?, ?, ?)}";
			cs = conn.prepareCall(query);
			cs.setString(1, eid);
			cs.setString(2, name);
			cs.setString(3, telephone);
			cs.setString(4, email);
			cs.executeUpdate();
            printGetLineComments();
	
		}
		 catch (Exception e) {
		 System.out.println (e + " Exception inside the addTupleInEmployees() function "+ e.getMessage());
	 }
	}

    // Function to take EID, Pid, Cid, pur_qty, pur_unit_price from user and execute query
	public static void addTupleInPurchases(){
		try{
		    // Input sid, Pid, Cid, pur_qty, pur_unit_price from keyboard
			BufferedReader readKeyBoard;
			readKeyBoard = new BufferedReader(new InputStreamReader(System.in));

			System.out.print("Please enter eid: ");
			String eid = readKeyBoard.readLine();

			System.out.print("Please enter pid: ");
			String pid = readKeyBoard.readLine();

			System.out.print("Please enter cid: ");
			String cid = readKeyBoard.readLine();

			System.out.print("Please enter pur_qty: ");
			String purQty = readKeyBoard.readLine();

			System.out.print("Please enter pur_unit_price: ");
			String purUnitPrice = readKeyBoard.readLine();
			
			String query = "{call RBMSPackage.add_purchase(?, ?, ?, ?, ?)}";
			cs = conn.prepareCall(query);
			cs.setString(1, eid);
			cs.setString(2, pid);
			cs.setString(3, cid);
			cs.setString(4, purQty);
			cs.setString(5, purUnitPrice);
			cs.executeUpdate();
			// calling printGetLineComments() function to print the put_line message
			printGetLineComments();
		}
		 catch (Exception e) {
		    System.out.println (e + " Exception inside addTupleInPurchases() function"+ e.getMessage());
	 }
	}


    // Function to print the table data in the terminal suing the resultset and resultMetaData
	public static void printTables(CallableStatement cs){
    try{
     //execute the stored procedure
	  cs.execute();
      ResultSet rs = (ResultSet) cs.getObject(1);
	  resultMetaData = rs.getMetaData();
	  while(rs.next()){
		for(int i = 1;i <= resultMetaData.getColumnCount(); i++)
            System.out.print(rs.getString(i) + "\t");
		System.out.println();
		}
		}
		catch (Exception e) {
		System.out.println (e + "Exception inside the PrintTables() function"+ e.getMessage());
	 }
	}

   // Function to get the put_line comments from pl/sql side and display them in the console
	public static void printGetLineComments(){
		try{
		
		    String query = "{ call dbms_output.get_lines(?, ?) }";

			cs = conn.prepareCall(query);
			cs.registerOutParameter(1, Types.ARRAY, "DBMSOUTPUT_LINESARRAY");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.execute();

			Array array = cs.getArray(1);
            if (cs.getArray(1) != null) {
                Object[] values = (Object[])array.getArray();
                for (int i = 0; i < values.length; i++) {
                    String line = (String)values[i];
                    if(null != line) {
                        System.out.println("\n  --> " + line + "   ");
                    }
                }
                System.out.println();
        }
		}
			catch (Exception e) {
		    System.out.println (e + "Exception inside the printGetLineComments() function"+ e.getMessage());
			}

	}


    public static void main (String args []) throws SQLException {
	ResultSetMetaData resultMetaData=null;
	Scanner sc = new Scanner(System.in);
    try
	{
	  OracleDataSource ds = new oracle.jdbc.pool.OracleDataSource();
	  ds.setURL("jdbc:oracle:thin:@castor.cc.binghamton.edu:1521:ACAD111");
	  System.out.print("entrer user name: ");
	  String userId = sc.nextLine();
	  Console console = System.console();
	  if (console == null) {
            System.err.println("No console available. Exiting...");
            System.exit(1);
        }
        // Prompt the user for password
		char[] passwordAr = console.readPassword("Enter your password: ");
        
    //Convert the password from char array to String
     String password = new String(passwordAr);
	conn = ds.getConnection(userId, password);
	//   close the result set, statement, and the connection
	   
	while(true){
		 String query="";
	     System.out.println("enter the choice: " + "\n" +
	                    "1. To show the tuples in each of the six tables." + "\n" + 
	                    "2. To report the monthly sale activities for any given employee." + "\n" + 
						"3. For adding tuples to the Employees table."+ "\n" +
						"4. For adding tuples to the Purchases table."+ "\n" +
						"5. Exit");

	    int q = sc.nextInt();
	    String query1 = "";

	switch(q){
	case 1: query = displayAllTables(query1);
	         cs = conn.prepareCall(query);
	         cs.registerOutParameter(1, OracleTypes.CURSOR);
			 printTables(cs);
	         break;

	case 2: query = employeeMonthlySale(query1);
	        System.out.print("Enter the Employee Id: ");
	        String empId = sc.next();
			cs = conn.prepareCall(query);
	        cs.registerOutParameter(1, OracleTypes.CURSOR);
	        cs.setString(2, empId);
			printTables(cs);
	        break;

    case 3: addTupleInEmployees();
			break;
	
	case 4: addTupleInPurchases();
			break;
	
	case 5:  sc.close();
	         cs.close();
	         conn.close();
	         System.exit(0);	

	default: System.out.println("Invalid: choice of question");
	
	}
	}
	}
	catch (Exception e) {
		System.out.println (e +" " + e.getMessage());
	 }
    }

}