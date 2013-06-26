package client;

/*
 * Jordan was here!
 * Colin was here!
 * Mahmood
 * Shawn was here!
 *
 */


import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SQLTest extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>SQLTest</title></head>");
        out.println("<body><a href='/Client/index.jsp'><h1>HOME</h1></a>");
        
        // create as few DBCs as possible
        DBC dbc = new DBC();
        // test "User" functions
        Random generator = new Random();
        int randBit = generator.nextInt(1000);
        try{
            out.println("<hr>User added: " + User.addUser(dbc, "user"+randBit, "a98fsd0afsd89afs9", "info"+randBit+"@example.com"));
        }
        catch(addUserException e){
            out.println("<br>Invalid Form Data: "+ e);
        }
        User u1,u2; 
        u1 = new User(dbc,request);
        u2 = new User(dbc,response, "user"+randBit,"a98fsd0afsd89afs9");
        out.println("<br>Logged old user: "+u1.isLoggedIn());
        out.println("<br>Logged new user: "+u2.isLoggedIn());
        out.println("<hr>");
        //test Photos
        Photo p1,p2;
        p1 = new Photo(dbc, 1,"Test Image", "This is a test of the image system","","test.jpg");
        p2 = new Photo(dbc, 2);
        out.println(p1.debugOut());
        out.println("<hr>" + p2.debugOut());
        
        // Test Albums
        Album a1,a2;
        a1 = new Album(dbc, 1,"Test Collection","This is a test of the album type");
        a1.addPhoto(dbc, p1);
        a1.addPhoto(dbc, p2);
        out.println("<hr>" + a1.debugOut());
        
        a2 = new Album(dbc, 6);
        out.println("<hr>" + a2.debugOut());
        
        // Test recent photo list
        ArrayList<Photo> rp1,rp2;
        rp1 = Photo.getRecentPhotos(dbc, 5, 0);
        rp2 = Photo.getUserPhotos(dbc,1, 5, 0);
        
        Photo px;   //temp photo
        out.println("<hr>GET RECENT Images("+rp1.size()+")<ol>");
        for(int i=0; i<rp1.size();++i){
            out.println("<li>");
            px = rp1.get(i);
            out.println(px.debugOut());
            out.println("</li>");
        }
        out.println("</ol>");
        
        out.println("<hr>GET RECENT Images USER("+rp2.size()+")<ol>");
        for(int i=0; i<rp2.size();++i){
            out.println("<li>");
            px = rp2.get(i);
            out.println(px.debugOut());
            out.println("</li>");
        }
        out.println("</ol>");
        
        // Test recent album list
        ArrayList<Album> ra1,ra2;
        ra1 = Album.getRecentAlbums(dbc, 5, 0);
        ra2 = Album.getUserAlbums(dbc,1, 5, 0);
        
        Album ax;
        out.println("<hr>GET RECENT ALBUMS("+ra1.size()+")<ol>");
        for(int i=0; i<ra1.size();++i){
            out.println("<li>");
            ax = ra1.get(i);
            out.println(ax.debugOut());
            out.println("</li>");
        }
        out.println("</ol>");

        out.println("<hr>GET RECENT USER ALBUMS("+ra2.size()+")<ol>");
        for(int i=0; i<ra2.size();++i){
            out.println("<li>");
            ax = ra2.get(i);
            out.println(ax.debugOut());
            out.println("</li>");
        }
        out.println("</ol>");
        
        // test "count" functions
        out.println("<hr>Total Albums: " + Album.countAlbums(dbc));
        out.println("<br>Total Photos: " + Photo.countPhotos(dbc));
        
        out.println("</body></html>");
        out.close();
    }
}
