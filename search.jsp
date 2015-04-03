<!DOCTYPE html>
<html>
<title>Setting HTTP Status Code</title>
<body>

<% String query=request.getParameter("customsearch");
    String token[]=query.split(" ");
String word1=token[0];
String word2=token[1];
    //out.println(word1 + " "+ word2);

%>


<%@ page import="java.io.*" %>
<%@ page import="java.net.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="javax.servlet.http.*,javax.servlet.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%
     Connection connection;
     Properties props;
    //Class.forName("com.mysql.jdbc.Driver");
    props = new Properties();
    try{
        
        FileInputStream in = new FileInputStream("/usr/local/apache-tomcat-8.0.18/webapps/ROOT/database.properties");
        props.load(in);
        in.close();
    }catch(Exception e) {
    }



        String drivers = props.getProperty("jdbc.drivers");
        if (drivers != null) System.setProperty("jdbc.drivers", drivers);
         //out.println(drivers);
        String url = props.getProperty("jdbc.url");

        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
    //try{
       // Class.forName("com.mysql.jdbc.Driver").newInstance();
       //Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection( "jdbc:mysql://localhost:3306/CRAWLER", "root", "newpwd");
        out.println(connection);
        Statement stat = connection.createStatement();
        HashSet<int> set=new HashSet<int>();
        HashSet<int> set2=new HashSet<int>();
        ResultSet result = stat.executeQuery( "SELECT * FROM words WHERE word LIKE '"+word1+"' union SELECT * FROM words WHERE word LIKE '"+word2+"'");
        while (result.next()) {
            int urlid=result.getInt("urlid");
            if(!set.add(urlid))
            {
                set2.add(urlid);
            }
            out.println(urlid);
        }


    //}catch(Exception e) {
      //  out.println(e);
   // }


%>
</body>

</html>