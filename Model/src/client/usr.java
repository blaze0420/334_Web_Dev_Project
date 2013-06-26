package client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;

public class usr extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=windows-1252";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        String func = request.getParameter("func");
        
        DBC dbc = new DBC();
        User user = new User(dbc,request);
        
        if( func != null && func.equals("logout") && user.isLoggedIn()){
            user.logOut(dbc, response);
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
        }
        else if(user.isLoggedIn()){
            RequestDispatcher dispatcher = request.getRequestDispatcher("logout.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
        }
        else if( func != null && func.equals("reg")){        
            RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
        }
        else{   
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            if (dispatcher != null)
                dispatcher.forward(request, response);
            else System.err.println("Error: File not found");
        }
        out.close();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        
        DBC dbc = new DBC();
        boolean success=true;
        String func, username, password, confirm, email, error="";
        
        username = request.getParameter("username");
        password = request.getParameter("password");
        func = request.getParameter("function");
        if(func.equals("register")){
        
            confirm = request.getParameter("confirm");
            email = request.getParameter("email");
            
            if(password != confirm){
                error +="Passwords do not match. ";
                success = false;
            }
            
            try{
                success = User.addUser(dbc, username, password, email);
            }
            catch(addUserException e){
                success = false;
                error += e.getError();
            }   
            
            if(success){
                request.setAttribute("msg", "<p><strong>Registration successful</b>, please login below.</p>");
                RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");  
            }
            else{
                System.out.println("Registration Failed: "+error);
                request.setAttribute("msg", "<p><strong>Registration Failed:</strong> "+error+"</p>");
                RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp?func=reg");
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
            }
        }
        else if(func.equals("login")){
            User u = new User(dbc, response, username, password);
            
            if(u.isLoggedIn()){
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                if (dispatcher != null)
                    dispatcher.forward(request, response);
                else System.err.println("Error: File not found");  
            }
            else{
                request.setAttribute("msg", "<p>Invalid username or password, please try again.</p>");
                RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
                if (dispatcher != null) dispatcher.forward(request, response);
                else System.err.println("Error: File not found");
            }
        }
        
        out.close();
    }
}
