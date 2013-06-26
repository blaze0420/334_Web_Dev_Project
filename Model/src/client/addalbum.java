package client;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

public class addalbum extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        RequestDispatcher dispatcher;
            
        DBC dbc = new DBC();
        User u = new User(dbc,request);
        
        boolean success=true;
        int aid = -1;
        String func="", title, caption;
        
        
        // Redirect if user isn't logged in
        if(!u.isLoggedIn()){
            request.setAttribute("msg", "<p>You must be logged in to do that</p>");
            dispatcher = request.getRequestDispatcher("login.jsp");
            
            if (dispatcher != null) dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
            out.close();
            return;
        }
        
        // get available parameters
        title = request.getParameter("name").replaceAll("'", "&#39;");
        caption = request.getParameter("description").replaceAll("'", "&#39;");
        func = request.getParameter("func");
        try{ aid = Integer.parseInt(request.getParameter("aid")); }
        catch(NumberFormatException e){;}


        // if we're saving an existing album
        if(func != null && func.equals("save")){
        
            // check for problems
            if(title == "" && aid > 0){ // bad title
                request.setAttribute("msg", "<p>A title is required.</p>");
                dispatcher = request.getRequestDispatcher("addalbum.jsp?aid="+aid);
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
                return;
            }
            else if(aid < 0 ){  // bad aid
                request.setAttribute("msg", "<p>An error has occurred.</p>");
                dispatcher = request.getRequestDispatcher("index.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
                return;
            }
                       
            // now that we know there is a 
            Album a = new Album(dbc,aid);
            
            // if the curent user doesn't own the album redirect
            if(u.getUid() != a.getOwner()){
                request.setAttribute("msg", "<p>You are not the owner of that album.</p>");
                dispatcher = request.getRequestDispatcher("login.jsp");
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
            }
            else{
                // at this point we're saving an existing album with a valid title and the correct user
                
                // if the title has changed, save it
                if(a.getTitle()!=title)
                    a.setTitle(dbc, title);    
                // if the description has changed, save it
                if(a.getDescription()!=caption)
                    a.setDescription(dbc, caption);   
                


                // get the images from the submitted form
                String[] images = request.getParameterValues("image");
                
                boolean found;
                int id =-1;                
                Photo p;
                ArrayList<Integer> currentPIDs = a.getPids();
                
                // if an image is in the album but not the images list remove it from the album
                for(int j=0;j< currentPIDs.size();++j){            
                    found=false;
                    for (int i = 0; images != null && i < images.length; i++){                    
                        try{ id = Integer.parseInt(images[i]); }
                        catch(NumberFormatException e){;}
                        
                        if(currentPIDs.get(j) == id){
                            found=true;
                            break;
                        }  
                    }
                    if(!found){  //image is not selected but is in the album
                        a.removePhoto(dbc, new Photo(dbc, currentPIDs.get(j)));
                    } 
                }
                
                // if an image is in the list but not in the album add it
                for (int i = 0; images != null && i < images.length ; i++) 
                {                    
                    try{ id = Integer.parseInt(images[i]); }
                    catch(NumberFormatException e){;}
                    found=false;
                 
                    for(int j=0;j<currentPIDs.size();++j){
                        if(currentPIDs.get(j) == id){
                            found=true;
                            break;
                        }
                    }    
                    if(!found)  //image is selected but not in the album
                        a.addPhoto(dbc, new Photo(dbc, id));
                }
            
                // set beans
                request.setAttribute("album",a);
                request.setAttribute("unalbumed", Photo.getUserPhotosSingle(dbc, u.getUid()));
                
                // load the edit page
                dispatcher = request.getRequestDispatcher("editalbum.jsp?aid="+aid);
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
                
            }   // close "valid case"
        }// close if save
        else{
            //here we're creating a new album
            
            //if invalid title
            if(title == ""){
                request.setAttribute("msg", "<p>A title is required.</p>");
                dispatcher = request.getRequestDispatcher("addalbum.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
                return;
            }
            
            //attempt to add the new album
            Album a = new Album(dbc,u.getUid(),title,caption);
            
            if(a.getAid() == -1){   // add failed
                request.setAttribute("msg", "<p>Unable to add album.</p>");
                dispatcher = request.getRequestDispatcher("addalbum.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
                return;
            }
            else{   // add succeded
                // set beans
                request.setAttribute("album",a);
                request.setAttribute("unalbumed", Photo.getUserPhotosSingle(dbc, u.getUid()));
                request.setAttribute("msg", "<p>Album Created.</p>");
                // redirect to album edit screen
                dispatcher = request.getRequestDispatcher("editalbum.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
                return;
            }
        }
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        DBC dbc = new DBC();
        RequestDispatcher dispatcher;
        User u = new User(dbc,request);
        
        // redirect if user isn't logged in
        if(!u.isLoggedIn()){
            request.setAttribute("msg", "<p>You must be logged in to do that</p>");
            dispatcher = request.getRequestDispatcher("login.jsp");
            
            if (dispatcher != null) dispatcher.forward(request, response);
            else System.err.println("Error: File not found");    
            return;
        }
        
        Album a;
        int aid = -1;
        
        // parse aid from GET parameter
        try{ aid = Integer.parseInt(request.getParameter("aid")); }
        catch(NumberFormatException e){;}
        
        // if it's a valid album id
        if(aid > 0 ){
            a = new Album(dbc,aid); // get the album
            if(u.getUid() != a.getOwner()){ // redirect if the current user isnt the owner
                request.setAttribute("msg", "<p>You are not the owner of that album.</p>");
                dispatcher = request.getRequestDispatcher("login.jsp");
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
            }
            else{   // is the current user
                // set beans
                request.setAttribute("album",a);
                request.setAttribute("unalbumed", Photo.getUserPhotosSingle(dbc, u.getUid()));
                
                // dispatch site
                dispatcher = request.getRequestDispatcher("editalbum.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
                out.close();
            }
        }
        else{   // invalid AID       
            dispatcher = request.getRequestDispatcher("addalbum.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
            out.close();
        }
        out.close();
    }
}
