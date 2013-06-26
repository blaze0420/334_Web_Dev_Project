package client;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class viewphotos extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
    @SuppressWarnings("compatibility:7098415101167919225")
    private static final long serialVersionUID = 2326624863776990499L;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        
        DBC dbc = new DBC();
        User user = new User(dbc,request);
        ArrayList<Photo> photoList;
        int page = 1, pageSize=16;
        boolean own = false;
        
        try{ page = Integer.parseInt(request.getParameter("page")); }
        catch(NumberFormatException e){;}
        
        String func = request.getParameter("func");
        
        if(func != null && func.equals("own") && user.isLoggedIn()){
            photoList=Photo.getUserPhotos(dbc,user.getUid(),pageSize,(page-1)*pageSize);
            own = true;
        }
        else
           photoList=Photo.getRecentPhotos(dbc, pageSize, (page-1)*pageSize);
          
        int pages, photoCount;
        
        if(own)
            photoCount = Photo.countPhotosByUser(dbc, user.getUid());
        else
            photoCount = Photo.countPhotos(dbc);
        
        pages = photoCount / pageSize;

        if(pages*pageSize != photoCount)
            pages++;

        int start = Math.max(1, page-5);
        String pager="";
        for(int i=1;i<start+10 && i <= pages;i++){
            pager += "<a class='pager' href='/Client/viewphotos?page="+i;
            if(own)
                pager +="&func=own";
            pager+="'>";
            if(page==i)
                pager += "<strong>"+i+"</strong>";
            else
                pager += i;
            pager+="</a> ";
        }
        
        // set the attributes for the jsp page
        request.setAttribute("pager",pager);
        request.setAttribute("photos", photoList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("viewphotos.jsp");
        if (dispatcher != null)
            dispatcher.forward(request, response);
        else System.err.println("Error: File not found");
        out.close();
    }
}
