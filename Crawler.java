import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.sql.*;
import java.util.*;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler
{
	Connection connection;
	static int  urlID,wordid;
    int nextURLIDScanned, nextURLID,myURLID;
	public Properties props;
    //static HashMap<String, Integer> map;
    static HashSet set2;
	Crawler() {
		urlID = 0;
        wordid=0;
	}

	public void readProperties() throws IOException {
      		props = new Properties();
      		FileInputStream in = new FileInputStream("database.properties");
      		props.load(in);
      		in.close();
	}

	public void openConnection() throws SQLException, IOException
	{
		String drivers = props.getProperty("jdbc.drivers");
      		if (drivers != null) System.setProperty("jdbc.drivers", drivers);

      		String url = props.getProperty("jdbc.url");
      		String username = props.getProperty("jdbc.username");
      		String password = props.getProperty("jdbc.password");

		connection = DriverManager.getConnection( url, username, password);
   	}

	public void createDB() throws SQLException, IOException {
		openConnection();

         	Statement stat = connection.createStatement();
		
		// Delete the table first if any
		try {
			stat.executeUpdate("DROP TABLE URLS");
            stat.executeUpdate(" DROP TABLE WORDS");
		}
		catch (Exception e) {
		}
			
		// Create the table
        	stat.executeUpdate("CREATE TABLE URLS (urlid INT, url VARCHAR(512), description VARCHAR(200), image VARCHAR(200))");
            stat.executeUpdate("CREATE TABLE WORDS (word VARCHAR(200), urlid INT)");
	}

	public boolean urlInDB(String urlFound) throws SQLException, IOException {
         	Statement stat = connection.createStatement();
		ResultSet result = stat.executeQuery( "SELECT * FROM urls WHERE url LIKE '"+urlFound+"'");

		if (result.next()) {
	        	System.out.println("URL "+urlFound+" already in DB");
			return true;
		}
	       // System.out.println("URL "+urlFound+" not yet in DB");
		return false;
	}

	public void insertURLInDB( String url) throws SQLException, IOException {
        Document doc= Jsoup.connect(url).get();
        String urlText=doc.text();
        String imgLink=null;
	    String description=urlText.substring(0,100);
	    Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
        for (Element image : images) {
            if(!image.attr("abs:src").equals("https://www.cs.purdue.edu/images/brand.svg")
               && !image.attr("abs:src").equals("https://www.cs.purdue.edu/images/logo.svg"))
             {
                 imgLink=image.attr("abs:src");
                 break;
             }    
        }
        if(imgLink==null)
        {
            imgLink="https://www.cs.purdue.edu/images/brand.svg";
        }
        Statement stat = connection.createStatement();
		String query = "INSERT INTO urls VALUES ('"+urlID+"','"+url+"','"+description+"','"+imgLink+"')";
        System.out.println(urlID+ "Rows has been inserted");
		stat.executeUpdate( query );
		urlID++;
	}
    
    public void insertWordInDB( String urlText, int wordid) throws SQLException, IOException {
        Statement stat = connection.createStatement();
        String token[]=urlText.split(" ");
        HashSet set=new HashSet<String>();
        int k=0;
        for(int i=0;i<token.length;i++)
        {
            k=0;
            String word=token[i];
            char tem[] = word.toLowerCase().toCharArray();
            if(tem.length>=1 && !Character.isLetterOrDigit(tem[tem.length-1])){
                for(int j=tem.length-1;j>=0;j--)
                {
                    if(!Character.isLetterOrDigit(tem[j]))
                    {
                        k++;
                    }
                }
                word=word.substring(0,word.length()-k);
            }
            if(set.add(word.toLowerCase()))
            {
                //System.out.println(token[i].toLowerCase());
                try{
                    String query = "INSERT INTO words VALUES ('"+word.toLowerCase()+"',"+wordid+")";
                    stat.executeUpdate( query );
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
   	public void fetchURL(String urlScanned) {
		try {
			URL url = new URL(urlScanned);
			System.out.println("urlscanned="+urlScanned+" url.path="+url.getPath());
    		// open reader for URL
    		InputStreamReader in = new InputStreamReader(url.openStream());
    		// read contents into string builder
    		StringBuilder input = new StringBuilder();
    		int ch;
			while ((ch = in.read()) != -1) {
         			input.append((char) ch);
			}
     		// search for all occurrences of pattern
    		String patternString =  "<a\\s+href\\s*=\\s*(\"[^\"]*\"|[^\\s>]*)\\s*>";
    		Pattern pattern = 			
	           Pattern.compile(patternString, 
	           Pattern.CASE_INSENSITIVE);
    	       Matcher matcher = pattern.matcher(input);
		
			while (matcher.find()) {
    			int start = matcher.start();
    			int end = matcher.end();
    			String match = input.substring(start, end);
				String urlFound = matcher.group(1);
				System.out.println(urlFound);
				// Check if it is already in the database
				if (!urlInDB(urlFound)) {
					insertURLInDB(urlFound);
				}				
    				//System.out.println(match);
 			}

		}
      		catch (Exception e)
      		{
       			e.printStackTrace();
      		}
	}

    public void startCrawl(String root) throws SQLException, IOException{
        nextURLID=0;
        nextURLIDScanned=0;
        myURLID=0;
        
        while(urlID<10000)
        { 
            //System.out.println("get links for"+ root);
            getUrlList(root);
            //System.out.println("im so good");
            try {
                    Statement stat = connection.createStatement();
                    ResultSet result = stat.executeQuery( "SELECT * FROM urls WHERE urlid = " + myURLID + ";");
                    if(result.next())
                    {
                        String theURL=result.getString("url");
                        root=theURL;
                        myURLID++;
                        //System.out.println("the root is now: " + theURL);
                    }
            }catch(Exception e){} 
        }
    }
    
    public void getUrlList(String url) throws SQLException, IOException
    {
        Statement stat = connection.createStatement();
       
        try{
            Document doc= Jsoup.connect(url).get();
            Elements links=doc.select("a[href]");
            //String description=doc.select("meta[name=desciption]").get(0).attr("content");
            int count=0;
            for(Element link : links )
            {
                String a=link.attr("abs:href");
                //System.out.println(link.text());
                //if(a.matches("^(http|https)://.*$"))
                //if(a.startsWith("https://www.cs.purdue.edu")|| a.startsWith("http://www.cs.purdue.edu"))
                if(a.contains("cs.purdue.edu"))
                {
                    if(set2.add(a)){
                        insertURLInDB(a);
                    }
               }
            }
           

        }catch (Exception e){
            //e.printStackTrace();
        }
    }
    
    public void insertWordTable() throws SQLException, IOException
    {
        String url=null;
        int i=0;
        try {
            while(i<10000){
                Statement stat = connection.createStatement();
                ResultSet result = stat.executeQuery( "SELECT * FROM urls WHERE urlid = " + i + ";");
                System.out.println("Quesry runned "+ i+ " times");
                if(result.next())
                {
                    String theURL=result.getString("url");
                    url=theURL;
                    System.out.println(url);
                }
                Document doc= Jsoup.connect(url).get();
                String urlText=doc.text();
                insertWordInDB(urlText,i);
                i++;
            }
            
        }catch(Exception e){
            //e.printStackTrace();

        }

    
    }
    
    
    
    
    public void crawl(){
        while(nextURLIDScanned< nextURLID){
            int urlIndex=nextURLIDScanned;  
        }    
    }
    
   	public static void main(String[] args)
   	{
		Crawler crawler = new Crawler();
        set2=new HashSet<String>();
        long start = System.currentTimeMillis();
		try {
			crawler.readProperties();
			String root = crawler.props.getProperty("crawler.root");
			crawler.createDB();
			crawler.startCrawl(root);
            crawler.insertWordTable();
		}
		catch( Exception e) {
         		e.printStackTrace();
		}
        
        long end = System.currentTimeMillis();
        System.out.println(" Total time runned:" + (end-start)/1000 + "seconds");
    }
}

