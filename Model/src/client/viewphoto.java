package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

public class viewphoto extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        System.out.println("Loaded viewphoto.java");
        DBC dbc = new DBC();
        User user = new User(dbc,request);
        int pid;
        String links, title, caption, imagePath;
        
        // set defaults for invalid pages
        links="";
        title="Image Not Found";
        caption="";
        imagePath="/Client/images/oops.jpg";
        
        // get pid we're trying to load
        pid = Integer.parseInt(request.getParameter("pid"));
        
        // if it's a valid pid
        if(pid > 0 ){
            Photo p = new Photo(dbc, pid); // try to load it
            
            if(p.getPid()==pid){    // if it loaded fine (if not it's -1)
                // get the info needed for the display page
                imagePath = "/Client/images/"+p.getFullFilename();  
                System.out.println(imagePath);
                
                title = p.getTitle();
                caption = p.getCaption();
                
                // if the current user is the owner, add the edit link
                if(p.getOwner() == user.getUid())
                    links+="<a href='/Client/addphoto?pid="+pid+"'>Edit</a>";
                if(p.getAid() != -1 && links=="")
                    links+="<a href='/Client/viewalbum?aid="+p.getAid()+"'>View Album</a>";
                else if(p.getAid() != -1)
                    links+=" | <a href='/Client/viewalbum?aid="+p.getAid()+"'>View Album</a>";
            }
        }
        
        // set the attributes for the jsp page
        request.setAttribute("src", imagePath);
        request.setAttribute("title", title);
        request.setAttribute("caption", caption);
        request.setAttribute("links",links);
        
        // dispatch the view
        RequestDispatcher dispatcher = request.getRequestDispatcher("viewphoto.jsp");
        if (dispatcher != null)
            dispatcher.forward(request, response);
        else System.err.println("Error: File not found");
        out.close();
    }
}