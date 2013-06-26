package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

public class viewalbum extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        
        DBC dbc = new DBC();
        User user = new User(dbc,request);
        int aid=-1;
        try{ aid = Integer.parseInt(request.getParameter("aid"));  }
        catch(NumberFormatException e){;}
        // if it's a valid aid
        if(aid > 0 ){
            Album a = new Album(dbc, aid); // try to load it
            request.setAttribute("album",a);
            if(a.getOwner() == user.getUid())
                request.setAttribute("msg","<a href=\"/Client/addalbum?aid="+aid+"\">Edit Album</a>");
            RequestDispatcher dispatcher = request.getRequestDispatcher("viewalbum.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
        }
        else{
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("viewalbums.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
            out.close();
        
        }
    }
}
