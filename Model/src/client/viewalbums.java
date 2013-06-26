package client;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import javax.servlet.*;
import javax.servlet.http.*;

public class viewalbums extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        
        DBC dbc = new DBC();
        User user = new User(dbc,request);
        int page=1, pageSize=16;
        boolean own = false;
        ArrayList <Album> albumlist;
        
        String func = request.getParameter("func");
        
        if(func != null && func.equals("own") && user.isLoggedIn())
            albumlist=Album.getUserAlbums(dbc,user.getUid(),16,(page-1)*16);
        else
           albumlist=Album.getRecentAlbums(dbc, 16, (page-1)*16);
        
        int pages, albumCount;
        
        if(own){
            own=true;
            albumCount = Album.countAlbumsByUser(dbc, user.getUid());
        }
        else
            albumCount = Album.countAlbums(dbc);
        
        pages = albumCount / pageSize;

        if(pages*pageSize != albumCount)
            pages++;

        int start = Math.max(1, page-5);
        String pager="";
        for(int i=1;i<start+10 && i <= pages;i++){
            
            pager += "<a class='pager' href='/Client/viewalbums?page="+i;
            if(own)
                pager +="&func=own";
            pager+="'>";
            if(page==i)
                pager += "<strong>"+i+"</strong>";
            else
                pager += i;
            pager+="</a> ";
        }
        
        request.setAttribute("albums", albumlist);
        RequestDispatcher dispatcher = request.getRequestDispatcher("viewalbums.jsp");
        if (dispatcher != null)
            dispatcher.forward(request, response);
        else System.err.println("Error: File not found");
        out.close();
    }
}
