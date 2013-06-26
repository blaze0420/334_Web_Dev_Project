package client;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// SEE JORDAN FOR ANY PROBLEMS WITH USAGE
public class User {
    
    private int uid=-1;
    private String username, hash, salt, session, email;
    private boolean loggedIn;
    
    
    
    /* checks for an existing session
     * logs in the user if possible
     */
    public User(DBC dbc, HttpServletRequest request){
        loggedIn=false;
        int uid=-1;
        String session="";
        
        // get cookies
        Cookie cookies [] = request.getCookies();
        if(cookies == null)
            return;
        
        // try to get the two necesary cookies
        for (int i = 0; i < cookies.length; i++){
            if (cookies[i].getName().equals ("uid")){
                try{
                    uid = Integer.parseInt(cookies[i].getValue());
                }
                catch(NumberFormatException e){
                    System.out.println(e);
                    uid=-1;    
                }
            }
            if (cookies[i].getName().equals("session"))
                session = cookies[i].getValue();
        }
        
        // if we don't get them both return
        if(uid < 1 || session.equals(""))
            return;
        
        // get the user information from the db
        System.out.println("Setting up the query");
        String q = "SELECT user_id,username,email,password,salt,sessionid " +
            "FROM users " +
            "WHERE user_id="+uid;
        
        try{
            System.out.println("execute query " + q);
            // do the query
            ResultSet res = dbc.executeQuery(q);    // query the DB
            res.next();
            
            // set the session first because it's all we need at this pont
            this.session = res.getString("sessionid");
            
            // compare session values (cookie vs stored)
            System.out.println("Session="+session + " " + "this.session="+this.session);
            if(session.equals(this.session)){
                
                // we're all good, set attributes
                this.uid = res.getInt("user_id");
                this.username = res.getString("username");
                this.email = res.getString("email");
                
                System.out.println("Good session match: "+username);
                
                // set this user to logged in
                this.loggedIn = true;
                
                System.out.println("Loading user: "+uid);
            }
            else    // the hashes didn't match, bad password
                System.out.println("Bad session match: "+this.username);
        
        }
        catch(Exception e){
            System.out.println("Error validating user: " + e);
            System.out.println("With query: " + q);
        }
    }
    
    /*  Log in a username with credentials
     *  does not need raw input
     *  If it is unsuccessful it isLoggedIn() will be false
     */
    public User(DBC dbc,HttpServletResponse response, String username, String password){
        loggedIn=false;
        
        // invalid username
        if(!validUsername(username))
            return;
        
        this.username = username;
        
        // create query
        String q = "SELECT user_id,username,email,password,salt " +
            "FROM users " +
            "WHERE username='"+username+"'";
        
        try{
            // query the DB
            ResultSet res = dbc.executeQuery(q);    
            res.next();
            
            // get credentials
            this.salt = res.getString("salt");
            this.hash = res.getString("password");
            
            if(matchPasswords(password,hash,salt)){
                System.out.println("Good password match: "+username);
                
                // Valid password, store the rest of the info
                this.uid = res.getInt("user_id");
                this.username = res.getString("username");
                this.email = res.getString("email");
                
                this.loggedIn = true;
                
                System.out.println("Loading user: "+uid);
            }
            else
                System.out.println("Bad password match: "+username);
        
        }
        catch(Exception e){
            System.out.println("Error validating user: " + e);
            System.out.println("With query: " + q);
        }
        
        // if it still hasn't logged in we're done
        if(!loggedIn)
            return;
                
        // otherwise we'll set the user's cookie
        Random generator = new Random();
        String ses = String.valueOf(generator.nextDouble()).replace('.', '9');
        
        // set values
        Cookie cuid = new Cookie("uid",String.valueOf(uid));
        Cookie cses = new Cookie("session", ses);
            
        // set expiry (1 yr)
        cuid.setMaxAge(365 * 24 * 60 * 60);
        cses.setMaxAge(365 * 24 * 60 * 60);
        
        // set domain
        cuid.setDomain("colinchristie.me");
        cses.setDomain("colinchristie.me");
        
        // add the cookies
        response.addCookie(cuid);
        response.addCookie(cses);
        
        // add to the database the session value to match to the cookie on following pages
        q = "UPDATE users " +
                "SET sessionid='" +ses+"' "+ 
                "WHERE user_id="+this.uid;
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
            System.out.println("Starting a session for: "+this.username);
        }
        catch(SQLException e){
            System.out.println("Error starting session: " + e);
            System.out.println("With query: " + q);
        }
    }
    
    public void logOut(DBC dbc, HttpServletResponse response){
        // unset values
        Cookie cuid = new Cookie("uid","");
        Cookie cses = new Cookie("session","");
            
        // set expiry (now)
        cuid.setMaxAge(0);
        cses.setMaxAge(0);
        
        // set domain
        cuid.setDomain("colinchristie.me");
        cses.setDomain("colinchristie.me");
        
        // add the cookies
        response.addCookie(cuid);
        response.addCookie(cses);
        
        // add to the database the session value to match to blank
        String q = "UPDATE users " +
                "SET sessionid=' ' "+ 
                "WHERE user_id="+this.uid;
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
            System.out.println("Destroying session for: "+this.username);
        }
        catch(SQLException e){
            System.out.println("Error destroying session: " + e);
            System.out.println("With query: " + q);
        }
        
    }
    
    
    /*  ==============================
     * STATIC METHODS BELOW THIS POINT
     *  ============================== */
    
    /* addUser
     * adds a new user to the database and 
     * loads that user's info
     */
    public static boolean addUser(DBC dbc, String username, String password, String email) throws addUserException{
        boolean success = true;
        String errors ="", hash="", salt;
        
        // validate username
        if( !validUsername(username)){
            errors += "Invalid Username. ";
            success = false;
        }
        else{
            //if username is valid check if exists
            if(userExistsUsername(dbc,username)){
                errors += "Username Exists. ";
                success = false;
            }
        }
        
        // validate email
        if( !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$")){
            errors += "Invalid Email. ";
            success = false;
        }
        else{
            // check if email exists
            if(userExistsEmail(dbc,email)){
                errors += "Email Exists. ";
                success = false;
            }
        }
        
        // check password.length
        if(password.length() < 7 ){
            errors += "Password must be at least 7 characters. ";
            success = false;
        }
        
        // generate a large random number to hash for a salt
        Random generator = new Random();
        salt = String.valueOf(generator.nextDouble()).replace('.', '9');
        
        //hash the password with the salt
        try{
            salt = doMD5(salt);
            password = doMD5(password);
            hash = doMD5(password + salt);
        }
        catch(NoSuchAlgorithmException e){
            success=false;
            errors += "Hashing algorithm failed. ";
        }

        // If we've encountered any exceptions at all throw them here
        if(!success)
            throw new addUserException(errors);
        else{   // otherwise add the user to the DB
            String q = "INSERT INTO users " +
                    "(username,email,password,salt)" + 
                    "VALUES ('"+ username +"','"+ email +"','"+ hash +"','"+ salt +"')";
            try{
                String key = dbc.executeUpdate(q);
                System.out.println("added new user " + key);

                q = "SELECT user_id FROM users WHERE rowid='"+key+"'";
                ResultSet res = dbc.executeQuery(q);
                int uid=-1;
                if (res.next())
                    uid=res.getInt(1);
                System.out.println("added new user " + uid);
            }
            catch(SQLException e){
                System.out.println("Error adding new user: " + e);
                System.out.println("With query: " + q);
            }
        }
            
        return success;       
    }
    
    // valid usernames can only contain letters and numbers and have to be length >0
    public static boolean validUsername(String username){
        return username.matches("^[a-zA-Z0-9]+$");
    }
    
    /*  Attempts to re-hash a password attempt with a salt to arrive
     *  at the same hash given (usually the one stored with the user
     *  record in the database
     */
    public static boolean matchPasswords(String attempt, String hash, String salt){
        String password;
       
        try{
            password = doMD5(attempt);
            password = doMD5(password + salt);
        }
        catch(NoSuchAlgorithmException e){
            System.out.println("Hashing error: "+e);
            return false;
        }
       
       return password.equals(hash);       
    }
    
    
    /* userExistsUsername
     * checks for a username in the database
     * assumes valid username
     * Written by: J
     */
    
    public static boolean userExistsUsername(DBC dbc, String username){
        String q = "SELECT count(user_id) FROM users WHERE username='"+username+"'";
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Checking user: "+username);
            res.next();
            return res.getInt("count(user_id)")>0;
        }
        catch(SQLException e){
            System.out.println("Error checking user by email: " + e);
            System.out.println("With query: " + q);
        }
        return true;    
    }
    
    /* userExistsEmail
     * checks for an email in the database
     * assumes valid email
     * Written by: J
     */
    public static boolean userExistsEmail(DBC dbc, String email){
        String q = "SELECT count(user_id) FROM users WHERE email='"+email+"'";
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Checking user: "+email);
            res.next();
            return res.getInt("count(user_id)")>0;
        }
        catch(SQLException e){
            System.out.println("Error checking user by email: " + e);
            System.out.println("With query: " + q);
        }
        return true;    
    }
    
    /*
     *  computes a MD5 hash of a given string
     *  Source: http://snippets.dzone.com/posts/show/3686
     */
    public static String doMD5(String pass) throws NoSuchAlgorithmException {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] data = pass.getBytes(); 
            m.update(data,0,data.length);
            BigInteger i = new BigInteger(1,m.digest());
            return String.format("%1$032X", i);
    }
        
    /*  ==============================
     * ACCESSORS ONLY BELOW THIS POINT 
     *  ============================== */

    public int getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}


