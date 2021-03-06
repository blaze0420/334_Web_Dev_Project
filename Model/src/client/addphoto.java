package client;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

// to add this need to add the Struts Runtime library
import org.apache.commons.fileupload.*;

import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

public class addphoto extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
    private static String liveRoot;
        
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        ServletContext context = config.getServletContext();        
        //liveRoot = context.getRealPath("");
        
        liveRoot="/home/colin/jdeveloper/mywork/334_Web_Dev_Project/Model/public_html";
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        RequestDispatcher dispatcher;
        DBC dbc = new DBC();
        User u = new User(dbc,request);
        int pid=0;
        
        if(!u.isLoggedIn()){
            request.setAttribute("msg", "<p>You must be logged in to do that</p>");
            dispatcher = request.getRequestDispatcher("login.jsp");
            
            if (dispatcher != null) dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
            
            return;
        }
        
        try{ pid = Integer.parseInt(request.getParameter("pid")); }
        catch(NumberFormatException e){;}
        
        if(pid > 0){
            Photo p = new Photo(dbc,pid);
            if(pid == p.getPid() && p.getOwner() == u.getUid()){
                request.setAttribute("photo",p);
                request.setAttribute("msg", "<p>Photo Updated</p>");
                dispatcher = request.getRequestDispatcher("editphoto.jsp?pid="+pid);
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
            }
            else{
                request.setAttribute("msg", "<p>An error has occured</p>");
                dispatcher = request.getRequestDispatcher("viewphoto.jsp?pid="+pid);
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
            }
        }
        else{
            dispatcher = request.getRequestDispatcher("addphoto.jsp");
            if (dispatcher != null) 
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
        }
    }
       
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        
        String title="", caption="";
        String fileName="";
        int pid=0;
        PrintWriter out = response.getWriter();
        RequestDispatcher dispatcher;
        String sep = File.separator;
        
        DBC dbc = new DBC();
        User u = new User(dbc,request);
        
        // if the user isn't logged in
        if(!u.isLoggedIn()){
            request.setAttribute("msg", "<p>You must be logged in to do that</p>");
            dispatcher = request.getRequestDispatcher("login.jsp");
            if (dispatcher != null) dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
            
            return;
        }
        
        // get available parameters
        String func = request.getParameter("func");
        //  NEED TO CHECK THAT PID IS BEING RETRIEVED PROPERLY
        try{ pid = Integer.parseInt(request.getParameter("pid")); System.out.println(pid);}
        
        catch(NumberFormatException e){;}
        System.out.println(func);
        //saving image
        if(func != null && func.equals("save") && pid > 0){
            System.out.println("Saving");
            title = request.getParameter("title").replaceAll("'", "&#39;");
            caption = request.getParameter("caption").replaceAll("'", "&#39;");
            Photo p = new Photo(dbc,pid);
            // correct use
            if(pid == p.getPid() && p.getOwner() == u.getUid()){
                if(!p.getTitle().equals(title))
                    p.setTitle(dbc, title);
                if(!p.getCaption().equals(caption))
                    p.setCaption(dbc, caption);
                request.setAttribute("photo",p);
                request.setAttribute("msg", "<p>Photo Updated</p>");
                dispatcher = request.getRequestDispatcher("editphoto.jsp?pid="+pid);
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
            }
            else{
                request.setAttribute("msg", "<p>An error has occured</p>");
                dispatcher = request.getRequestDispatcher("viewphoto.jsp?pid="+pid);
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
            }
        }
        else{
            System.out.println("new");
            File file;
               int maxFileSize = 5000 * 1024;
               int maxMemSize = 5000 * 1024;    
    
               // TODO: this has to be changed to be the correct path on the local machine running the web logic server
               String filePath = liveRoot + sep+"images"+sep;
                System.out.println("LiveRoot="+liveRoot + "\n" + "filepath=" +filePath);
               // Verify the content type
               String contentType = request.getContentType();
               if ((contentType.indexOf("multipart/form-data") >= 0)) {
                    
                    DefaultFileItemFactory factory = new DefaultFileItemFactory();
                  
                  // maximum size that will be stored in memory
                  factory.setSizeThreshold(maxMemSize);
                  
                  // Location to save data that is larger than maxMemSize.
                  factory.setRepository(new File(liveRoot +sep+"temp"));
    
                  // Create a new file upload handler
                  FileUpload upload = new FileUpload(factory);
                  
                  // maximum file size to be uploaded.
                  upload.setSizeMax( maxFileSize );
                 
                  try{ 
                     // Parse the request to get file items.
                     List fileItems = upload.parseRequest(request);
    
                     // Process the uploaded file items
                     Iterator i = fileItems.iterator();
                      
                     while ( i.hasNext () ) 
                     {
                         FileItem fi = (FileItem)i.next();
                         
                        // the image to upload
                         if ( !fi.isFormField () ) { 
                             // get the name of the image
                             fileName = fi.getName();
                            
                            // Build the file path 
                            if( fileName.lastIndexOf(sep) >= 0 ){
                                file = new File( liveRoot + sep +"temp" +sep+ 
                                fileName.substring( fileName.lastIndexOf(sep))) ;
                                fileName = fileName.substring( fileName.lastIndexOf(sep));
                            }
                            else{
                                file = new File( liveRoot + sep+"temp" + sep+
                                fileName.substring(fileName.lastIndexOf(sep)+1)) ;
                                fileName = fileName.substring(fileName.lastIndexOf(sep)+1);
                            }
                             
                             fi.write(file);
                             
                        // the text input fields
                         } else {
                             String fieldname = fi.getFieldName();
                             // set the title of the picture
                             if (fieldname.equals("title"))
                                title = fi.getString().replaceAll("'", "&#39;"); 
                            // set the caption of the picture
                             else if (fieldname.equals("caption"))
                                 caption = fi.getString().replaceAll("'", "&#39;");
                         }
                         
                     }//end while
                         
                  }catch(Exception ex) {
                     System.out.println(ex);
                  }
    
                  // create photo constructor
                  new Photo(dbc,u.getUid(),title,caption,liveRoot,fileName);
                   
               }else{
                  out.println("<html>");
                  out.println("<head>");
                  out.println("<title>Servlet upload</title>");  
                  out.println("</head>");
                  out.println("<body>");
                  out.println("<p>No file uploaded</p>"); 
                  out.println("</body>");
                  out.println("</html>");
               }
               
            // go back to the upload page. TODO: include a msg informing file upload was successful           
            dispatcher = request.getRequestDispatcher("addphoto.jsp");
            if (dispatcher != null) 
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found"); 
        }
    }
}
