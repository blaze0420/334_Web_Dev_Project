package client;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.Random;


public class DBCTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        DBC dbc = new DBC();
        Random r = new Random(System.currentTimeMillis());
        String username="Colin", email="Colinchristie444@gmail.com", password="gfdhsjak1";
        String title="Sample Image", caption="Sample Caption", ext="jpg", session=" ", description="test description";
        String hash=" ", salt=" ";
        int count = 2, offset=2, uid=25;
        
        String q = "INSERT INTO users " +
                "(username,email,password,salt)" + 
                "VALUES ('"+ username +"','"+ email +"','"+ hash +"','"+ salt +"')";
        try{
            String key = dbc.executeUpdate(q);
            System.out.println("added new user " + key);

            q = "SELECT user_id FROM users WHERE rowid='"+key+"'";
            ResultSet res = dbc.executeQuery(q);
            
            if (res.next())
                uid=res.getInt(1);
            System.out.println("added new user " + uid);
        }
        catch(SQLException e){
            System.out.println("Error adding new user: " + e);
            System.out.println("With query: " + q);
        }
    }
                
}
