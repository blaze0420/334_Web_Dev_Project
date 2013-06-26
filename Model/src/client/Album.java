package client;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class Album {
    private ArrayList<Photo> photos;
    private String title, description;
    private int owner, aid = -1;
    
    Album(DBC dbc, int aid){
        // get a specific album from the database
        photos = new ArrayList<Photo> ();
        // add to database
        String q1 = "SELECT pid FROM photos WHERE aid="+aid;
        String q2 = "SELECT * FROM albums WHERE aid="+aid;
        try{
            ResultSet res = dbc.executeQuery(q1);
            System.out.println("Getting album: "+aid);
            while(res.next())
                photos.add(new Photo(dbc,res.getInt("pid")));
            ResultSet res2 = dbc.executeQuery(q2);
            res2.next();
            this.aid = aid;
            owner = res2.getInt("user_id");
            title = res2.getString("title");
            description = res2.getString("description");
        }
        catch(SQLException e){
            System.out.println("Error getting album: " + e);
            System.out.println("With query: " + q1);
            System.out.println("or: " + q2);
        }
        
    }
    
    Album(DBC dbc, int owner, String title, String description){
        // set values
        this.owner = owner;
        this.title = title;
        this.description = description;
        photos = new ArrayList<Photo> ();
        
        // add to database
        String q = "INSERT INTO albums " +
                "(user_id, title, description)" + 
                "VALUES ("+owner +",'"+ title +"','"+ description +"')";
        try{
            String key = dbc.executeUpdate(q);
            q = "SELECT aid FROM albums WHERE rowid='"+key+"'";
            ResultSet res = dbc.executeQuery(q);
            
            if (res.next())
                aid=res.getInt(1);
            System.out.println("Adding new album: "+aid);
        }
        catch(SQLException e){
            System.out.println("Error adding new album: " + e);
            System.out.println("With query: " + q);
        }
    }
    
    public void addPhoto(DBC dbc, Photo p){
        if (aid == -1 || p.getPid() == -1){
            System.out.println("Error: trying to add a photo to an album where one is unset.");
            return;
        }
        if(owner != p.getOwner()){
            System.out.println("Error: Album owner does not own image.");
            return;
        }
        photos.add(p);
        p.setAid(this);
    }
    
    public void removePhoto(DBC dbc, Photo p){
        if (aid == -1 || p.getPid() == -1){
            System.out.println("Error: trying to add a photo to an album where one is unset.");
            return;
        }
        if(owner != p.getOwner()){
            System.out.println("Error: Album owner does not own image.");
            return;
        }
        System.out.println("Unsetting: "+p.getPid());
        p.unsetAid();
        for(int i=0;i<photos.size();++i){
            if(photos.get(i).getPid() == p.getPid()){
                    System.out.println("Removing from album: "+p.getPid());
                    photos.remove(i);
                    break;
            }
        }
    }
    
    public String debugOut(){
        String o = "<b><u>ALBUM</u></b>" 
            + "<br><b>AID: </b>" + aid 
            + "<br><b>UID: </b>" + owner
            + "<br><b>Title: </b>" + title 
            + "<br><b>Description: </b>" + description
            + "<br><b>Photos: </b><ul>";
                 
        for(int i=0;i<photos.size();++i){
                o += "<li>"+photos.get(i).debugOut()+"</li>";
        }
        o += "</ul>";
        return o;
    }
    
/*  ==============================
 * STATIC ONLY BELOW THIS POINT 
 *  ============================== */
    
    /*  static ArrayList<Album> getUserAlbums(int uid,int count, int offset){
     *  returns an ArrayList of count Albums by uid most recent first
     *  an offset is available for paging. May return an empty list.
     *  Written by: J
     * */
    public static ArrayList<Album> getUserAlbums(DBC dbc,int uid,int count, int offset){
        ArrayList<Album> albums = new ArrayList<Album>();
        //String q = "SELECT aid FROM albums WHERE uid="+uid+" ORDER BY aid DESC LIMIT "+ count +" OFFSET "+offset;
        String q = "SELECT * FROM " +
            "( SELECT rownum rnum, a.* FROM" +
            "( SELECT aid FROM albums " +
            " WHERE user_id="+ uid +
            " ORDER BY aid DESC) a " +
            "WHERE rownum <= "+offset+" + "+count+") WHERE rnum >"+offset;
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting user albums: "+uid);
            while(res.next())
                albums.add(new Album(dbc,res.getInt("aid")));
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }
        return albums;
    }
    
    /*  static ArrayList<Album> getRecentAlbumss(int count, int offset){
     *  returns an ArrayList of count Albums by all users most recent first
     *  an offset is available for paging. 
     *  Written by: J
     * */        
    public static ArrayList<Album> getRecentAlbums(DBC dbc, int count, int offset){
        ArrayList<Album> albums = new ArrayList<Album>();
        //String q = "SELECT aid FROM albums ORDER BY aid DESC LIMIT "+ count +" OFFSET "+offset;
        String q = "SELECT * FROM " +
            "( SELECT rownum rnum, a.* FROM" +
            "( SELECT aid FROM albums" +
            " ORDER BY aid DESC) a " +
            "WHERE rownum <= "+offset+" + "+count+") WHERE rnum >"+offset;
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting recent albums");
            while(res.next())
                albums.add(new Album(dbc,res.getInt("aid")));
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }
        return albums;
    }
    
    public static int countAlbums(DBC dbc){
        String q = "SELECT count(aid) FROM albums";
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting album count");
            res.next();
            return res.getInt("count(aid)");
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }
            
        return -1;
    }
    
    public static int countAlbumsByUser(DBC dbc, int uid){
        String q = "SELECT count(aid) FROM albums WHERE user_id="+uid;
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting album count");
            res.next();
            return res.getInt("count(aid)");
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }
            
        return -1;
    }
    
    public ArrayList<Integer> getPids(){
        ArrayList<Integer> pids = new ArrayList<Integer>();
        for(int i=0;i<photos.size();++i)
            pids.add(photos.get(i).getPid());
        return pids;
    }
    
/*  ==============================
 * ACCESSORS ONLY BELOW THIS POINT 
 *  ============================== */
    
    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setTitle(DBC dbc, String title) {
        this.title = title;
        String q = "UPDATE albums " +
                "SET title='" +title+"' "+ 
                "WHERE aid="+this.getAid();
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
        }
        catch(SQLException e){
            System.out.println("Error updating album: " + e);
            System.out.println("With query: " + q);
        }   
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(DBC dbc, String description) {
        this.description = description;
        String q = "UPDATE albums " +
                "SET description='" +description+"' "+ 
                "WHERE aid="+this.getAid();
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
        }
        catch(SQLException e){
            System.out.println("Error updating album: " + e);
            System.out.println("With query: " + q);
        } 
    }

    public String getDescription() {
        return description;
    }

    public int getOwner() {
        return owner;
    }

    public int getAid() {
        return aid;
    }
}
