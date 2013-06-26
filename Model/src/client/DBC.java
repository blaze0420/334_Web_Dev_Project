package client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Random;

import oracle.jdbc.pool.OracleDataSource;


public class DBC {
    String jdbcUrl = "jdbc:oracle:thin:@192.168.1.4:1521:XE";
    String userid = "colin";
    String password = "gfdhsjak1";
    
    Connection connection;
    Statement stmt;
    ResultSet rset;
    String query;
    String sqlString;
    
    public DBC(){
        // 3NQxrPMLwGEtrFsb
       // String host = "astathis.com";
        //String host = "localhost";
        //String dbName = "334";
        //int port = 3306;
        //String mySqlUrl = "jdbc:mysql://" + host + ":" + port
        //+ "/" + dbName;
        //Properties p = new Properties();
        //p.put("user", "334");
        //p.put("password","3NQxrPMLwGEtrFsb");
        
        try{
            //Class.forName("oracle.jdbc.OracleDriver");
            //Driver d = new Driver();
            //connection = (Connection)d.connect(mySqlUrl, p);
            getDBConnection();
            
        }
        catch(SQLException e){
            System.out.println("SQLException: " + e);
        }   
    }
    
    ResultSet executeQuery(String q) throws SQLException{
        Statement statement = (Statement)connection.createStatement();
        return statement.executeQuery(q);
    }
    
    /*
     * returns the key of the entry added.
     * */
    String executeUpdate(String q) throws SQLException{
        Statement statement;
        ResultSet res;
        String key="";
        statement = (Statement)connection.createStatement();
        statement.executeUpdate(q, statement.RETURN_GENERATED_KEYS);
        res = statement.getGeneratedKeys();
        
        if (res.next()) {
            key = res.getString(1);
            System.out.println("key is " + key);
        }

        return key;
    }
    
    boolean test(){
        try{
            if(!connection.isClosed()){
                Statement statement = (Statement)connection.createStatement();
                ResultSet res = statement.executeQuery("SELECT * FROM log");
                
                res.next();
                System.out.println("Get a log: " + res.getString("message"));
                return true;    
           }
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return false;
    }
    public void getDBConnection() throws SQLException{
            OracleDataSource ds;
            ds = new OracleDataSource();
            ds.setURL(jdbcUrl);
            connection=ds.getConnection(userid,password);
            
    }
}
