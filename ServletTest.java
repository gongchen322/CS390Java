import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;



public class ServletTest extends HttpServlet {

	
	@Override
	public void init(ServletConfig config) throws ServletException {	
	
		
		
	}
    
    private static final String JDBC_DRIVER="com.mysql.jdbc.Driver";
    static final String DB_URL="jdbc:mysql://localhost:3306/CRAWLER";
    
    //  Database credentials
    static final String USER = "root";
    static final String PASS = "newpwd";

	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Set the response message's MIME type.
		response.setContentType("text/html;charset=UTF-8");
		// Allocate a output writer to write the response message into the network socket.
		PrintWriter out = response.getWriter();
        
        String title = "Database Result";
        String docType =
        "<!doctype html public \"-//w3c//dtd html 4.0 " +
        "transitional//en\">\n";
        out.println(docType +
                    "<html>\n" +
                    "<head><title>" + title + "</title></head>\n" +
                    "<body bgcolor=\"#f0f0f0\">\n" +
                    "<h1 align=\"center\">" + title + "</h1>\n");
						
		
		
		String word1 = request.getParameter("firstWord");
		String word2 = request.getParameter("secondWord");
		
		
        try{
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            
            // Open a connection
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            
            // Execute SQL query
            stmt = conn.createStatement();
            String sql;
            sql1 = "SELECT * FROM words WHERE word LIKE '"+word1+"' UNION SELECT * FROM words WHERE word LIKE '"+word2+"'";
            ResultSet rs = stmt.executeQuery(sql);
            
            StringBuilder sb = new StringBuilder();
            sb.append("");
            // Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                int urlid  = rs.getInt("urlid");
                sb.append(urlid);
                sb.append(",");
              
            }
            String s=sb.toString();
            char ch[]=s.toCharArray();
            int intArray=new int[ch.length];
            int numbers=new int[ch.length];
            int j=0;
            for(int i=0;i<ch.length;i++)
            {
                intArray[i]=Integer.parseInt(ch[i]);
            }
            Arrays.sort(intArray);
            for(int i = 1; i < ch.length; i++) {
                if(intArray[i] == intArray[i - 1]) {
                    numbers[j++]=intArray[i];
                }
            }
            
            //////////// Now numbers contains the urlids
            
            out.println("Hello world");
            
            // Clean-up environment
            rs1.close();
            rs2.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        } //end try
			
		
		
		

	}
	
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doGet(request, response);
	}
	
	/*
	public static void main (String[] args) throws Exception {
		ServletTest st = new ServletTest();
		st.init(null);
		
		List<String> list = st.javaRDD.map(new Function<Object[], String>() {
			@Override
			public String call(final Object[] record) throws Exception {
				return "" + record[4];
			}
		}).collect();
		
		System.out.printf("\n\n%s\n\n", list.size());
	}
	*/
}
