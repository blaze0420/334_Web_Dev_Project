package client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.Random;

import javax.imageio.ImageIO;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class Photo {
    private int owner, pid = -1, aid = -1;
    private String title, caption, ext;
    
    /*  Photo(int pid)
     *  gets a photo by pid and initializes an instance of it.
     *  Written by: J
     * */
    Photo(DBC dbc, int pid){
        //get photo PID from the database
        String q = "SELECT pid, usrid, aid, title, caption, ext FROM photos WHERE pid="+ pid;
        try{
            ResultSet res = dbc.executeQuery(q);    // query the DB
            res.next();                             
            
            // set attributes
            this.pid = res.getInt("pid");           
            this.aid = res.getInt("aid");
            this.owner = res.getInt("usrid");
            this.title = res.getString("title");
            this.caption = res.getString("caption");
            this.ext = res.getString("ext");
            
            System.out.println("Loading photo: "+pid);
        }
        catch(SQLException e){
            System.out.println("Error loading photo: " + e);
            System.out.println("With query: " + q);
        }
    }
    
    /*  Photo(int owner, String title, String caption, String filename )
     *  creates an instance of a photo with given parameters and adds them to the database
     *  assumes sanitized fields, ie. no bad quotes and such.
     *  Written by: J
     * */
    Photo(DBC dbc, int owner, String title, String caption, String serverRootPath, String fileName){
        if(setExtension(fileName)){ //only if we have a valid filename
         
            //set attributes
            this.owner=owner;
            this.title=title;
            this.caption=caption;
            
            String q = "INSERT INTO photos " +
                    "(usrid, title, caption, ext)" + 
                    "VALUES ("+ owner +",'"+ title +"','"+ caption +"','"+ ext +"' )";
            try{
                
                String key = dbc.executeUpdate(q);
                q = "SELECT pid FROM photos WHERE rowid='"+key+"'";
                ResultSet res = dbc.executeQuery(q);
                if (res.next())
                    pid = res.getInt(1);
                System.out.println("Adding new photo: "+pid);
            }
            catch(SQLException e){
                System.out.println("Error adding new photo: " + e);
                System.out.println("With query: " + q);
            }
            
            if(resizeImages(serverRootPath, fileName))
                System.out.println("Resize successful!");
            else
                System.out.println("Resize failed!");
        }
        else
            System.out.println("Invalid filetype");

    }

    /*  String getThumbFilename()
     *  gets the path to this images thumbmail (to help keep naming convention)
     *  Written by: J
     * */
    public String getThumbFilename(){      
        return "img_" + pid + "_thumb." + ext;
    }
    
    /*  String getFullFilename()
     *  gets the path to this images full image (to help keep naming convention)
     *  Written by: J
     * */
    public String getFullFilename(){
        return "img_" + pid + "_full." + ext;
    }
    
    
    public String debugOut(){
        return "<b><u>PHOTO</u></b>" 
            + "<br><b>PID: </b>" + pid  
            + "<br><b>UID: </b>" + owner  
            + "<br><b>AID: </b>" + aid     
            + "<br><b>Title: </b>" + title 
            + "<br><b>Caption: </b>" + caption
            + "<br><b>Extension: </b>" + ext
            + "<br><b>Thumb: </b>" + getThumbFilename()
            + "<br><b>Full: </b>" + getFullFilename();
    }
       
    /*  boolean setExtension(String filename )
     *  retrieves the extension from an original filename 
     *  returns true if successful, false otherwise.
     *  Written by: J
     * */  
    private boolean setExtension(String filename ){
        if(filename.toLowerCase().endsWith(".jpg")){
            ext = "jpg";
            return true;
        }
        if(filename.toLowerCase().endsWith(".png")){
            ext = "png";
            return true;
        }
        if(filename.toLowerCase().endsWith(".bmp")){
            ext = "jpg";
            return true;
        }
        return false;
    }
    
    private boolean resizeImages(String serverRootPath, String inFile){
        /* Here is where the file will be resized to the display size and thumbnail size
         * the inFile will be the source file (stored in temp) passed will only be the filename
         * the extension should be stored in this.ext already 
         * you can use this.getThumbFilename() and getFullFilename() for your target filenames
         * save the resized images in the images directory.
         * 
         * */
        
        String sep = File.separator;
        
        System.out.println(serverRootPath);
        try{
                BufferedImage originalImage = ImageIO.read(new File(serverRootPath+sep+"temp"+sep+ inFile));
                int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
    
            
                // write fullsized jpg to images folder
                BufferedImage resizeImageFullJpg = resizeImage_full(originalImage, type);
                ImageIO.write(resizeImageFullJpg, "jpg", new File(serverRootPath+sep+"images" +sep+ this.getFullFilename())); 
                
                // write fullsized png to images folder
                BufferedImage resizeImageFullPng = resizeImage_full(originalImage, type);
                ImageIO.write(resizeImageFullPng, "png", new File(serverRootPath+sep+"images" +sep+ this.getFullFilename())); 
            
                // write thumbsized jpg to images folder
                BufferedImage resizeImageThumbJpg = resizeImage_thumb(originalImage, type);
                ImageIO.write(resizeImageThumbJpg, "jpg", new File(serverRootPath+sep+"images" +sep+ this.getThumbFilename())); 
            
                // write thumbsize png to images folder
                BufferedImage resizeImageThumbPng = resizeImage_thumb(originalImage, type);
                ImageIO.write(resizeImageThumbPng, "png", new File(serverRootPath+sep+"images" +sep+ this.getThumbFilename())); 
    
            } catch(IOException e) {
                    System.out.println(e.getMessage());
                    return false;
            }

        return true;
    }
    private static BufferedImage resizeImage_full(BufferedImage originalImage, int type){
        int OIMG_WIDTH = originalImage.getWidth();
        int OIMG_HEIGHT = originalImage.getHeight();
        
        int IMG_WIDTH ;
        int IMG_HEIGHT ;
        
        if(OIMG_WIDTH > OIMG_HEIGHT){
            IMG_WIDTH = 800;
            IMG_HEIGHT = (int)(800.0/((float)OIMG_WIDTH/(float)OIMG_HEIGHT));
        }
        else{
            IMG_HEIGHT = 600;
            IMG_WIDTH = (int)(600.0/((float)OIMG_HEIGHT/(float)OIMG_WIDTH));
        }
        System.out.println("Resized to : "+IMG_WIDTH+"x"+IMG_HEIGHT);
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
    
        return resizedImage;
    }
    
    // method to resize the image
    private static BufferedImage resizeImage_thumb(BufferedImage originalImage, int type){
        int OIMG_WIDTH = originalImage.getWidth();
        int OIMG_HEIGHT = originalImage.getHeight();
        
        int IMG_WIDTH = 200;
        int IMG_HEIGHT = 200;
        
        int SCALED_WIDTH = 200;
        int SCALED_HEIGHT = 200;
        
        if(OIMG_WIDTH < OIMG_HEIGHT){
            SCALED_WIDTH = 200;
            SCALED_HEIGHT = (int)(200.0/(OIMG_WIDTH/(float)OIMG_HEIGHT));
        }
        else{
            SCALED_HEIGHT = 200;
            SCALED_WIDTH = (int)(200.0/(OIMG_HEIGHT/(float)OIMG_WIDTH));
        }
        
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, SCALED_WIDTH, SCALED_HEIGHT, null);
        g.dispose();
 
        return resizedImage;
    }


/*  ==============================
 * STATIC METHODS BELOW THIS POINT
 *  ============================== */
                
    /*  boolean validExtension(String filename )
     *  checks that the extension of a file is validis valid 
     *  Written by: J
     * */  
    private static  boolean validExtension(String filename ){
        if(filename.toLowerCase().endsWith(".jpg"))
            return true;
        if(filename.toLowerCase().endsWith(".png"))
            return true;
        if(filename.toLowerCase().endsWith(".bmp"))
            return true;
        return false;
    }

    /* photos not in an album */
    public static ArrayList<Photo> getUserPhotosSingle(DBC dbc,int uid){
        ArrayList<Photo> photos = new ArrayList<Photo>();
        String q = "SELECT pid FROM photos WHERE usrid="+uid+" and aid IS NULL ORDER BY pid";
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting user unalbumed photos: "+uid);
            while(res.next())
                photos.add(new Photo(dbc,res.getInt("pid")));
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }
        return photos;
    }

    /*  static ArrayList<Photo> getUserPhotos(int uid,int count, int offset){
     *  returns an ArrayList of count photos by uid most recent first
     *  an offset is availablefor paging. May return an empty list
     *  Written by: J
     * */
    public static ArrayList<Photo> getUserPhotos(DBC dbc,int uid,int count, int offset){
        ArrayList<Photo> photos = new ArrayList<Photo>();
        String q = "SELECT * FROM " +
            "( SELECT rownum rnum, a.* FROM" +
            "( SELECT pid FROM photos" +
            " WHERE usrid="+ uid +
            " ORDER BY pid DESC) a " +
            "WHERE rownum <= "+offset+" + "+count+") WHERE rnum >"+offset;
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting user photos: "+uid);
            while(res.next())
                photos.add(new Photo(dbc,res.getInt("pid")));
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }
        return photos;
    }
    
    /*  static ArrayList<Photo> getRecentPhotos(int count, int offset){
     *  returns an ArrayList of count photos by all users most recent first
     *  an offset is availablefor paging. 
     *  Written by: J
     * */        
    public static ArrayList<Photo> getRecentPhotos(DBC dbc, int count, int offset){
        ArrayList<Photo> photos = new ArrayList<Photo>();
        String q = "SELECT * FROM " +
            "( SELECT rownum rnum, a.* FROM" +
            "( SELECT pid FROM photos" +
            " ORDER BY pid DESC) a " +
            "WHERE rownum <= "+offset+" + "+count+") WHERE rnum >"+offset;
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting recent photos: ");
            while(res.next())
                photos.add(new Photo(dbc,res.getInt("pid")));
        }
        catch(SQLException e){
            System.out.println("Error getting recent photos: " + e);
            System.out.println("With query: " + q);
        }
        return photos;
    }
    
    public static int countPhotos(DBC dbc){
        String q = "SELECT count(pid) FROM photos";
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting photo count");
            res.next();
            System.out.println("counted "+res.getInt("count(pid)"));
            return res.getInt("count(pid)");
            
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }   
        return -1;
    }
    
    public static int countPhotosByUser(DBC dbc, int uid){
        String q = "SELECT count(pid) FROM photos WHERE usrid="+uid;
        try{
            ResultSet res = dbc.executeQuery(q);                 //query the database and get new PID
            System.out.println("Getting photo count");
            res.next();
            return res.getInt("count(pid)");
        }
        catch(SQLException e){
            System.out.println("Error getting user photos: " + e);
            System.out.println("With query: " + q);
        }   
        return -1;
    }
    
    
    
    
/*  ==============================
 * ACCESSORS ONLY BELOW THIS POINT 
 *  ============================== */
    
    public int getOwner() {
        return owner;
    }

    public int getPid() {
        return pid;
    }

    public void setTitle(DBC dbc,String title) {
        this.title = title;
        String q = "UPDATE photos " +
                "SET title='" +title+"' "+ 
                "WHERE pid="+this.getPid();
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

    public void setCaption(DBC dbc, String caption) {
        this.caption = caption;
        
        String q = "UPDATE photos " +
                "SET caption='" +caption+"' "+ 
                "WHERE pid="+this.getPid();
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
        }
        catch(SQLException e){
            System.out.println("Error updating album: " + e);
            System.out.println("With query: " + q);
        }
    }

    public String getCaption() {
        return caption;
    }
    public void setAid(Album album){
        DBC dbc = new DBC();
        String q = "UPDATE photos " +
                "SET aid='" +album.getAid()+"' "+ 
                "WHERE pid="+this.pid;
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
        }
        catch(SQLException e){
            System.out.println("Error updating Aid: " + e);
            System.out.println("With query: " + q);
        }   
    }
    public void unsetAid(){
        DBC dbc = new DBC();
        String q = "UPDATE photos " +
                "SET aid=NULL "+ 
                "WHERE pid="+this.pid;
        try{
            dbc.executeUpdate(q);                 //query the database and get new PID
            aid=-1;
        }
        catch(SQLException e){
            System.out.println("Error unsetting aid: " + e);
            System.out.println("With query: " + q);
        } 
    }
    public int getAid(){
        return aid;    
    }
}
