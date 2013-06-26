<!--This is the login page.  It is accessible from the Log In/out in the header.  It contains the link
    to new user registration.
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<jsp:include page="includes/header.html" />
<h1 id="page-title">Member Log In</h1>
    ${requestScope.msg}
<!-- A simple form, the user inputs their username and password and the data is sent to usr.java.
     When the information is verified, the user is sent back to the index.jsp.
-->
<form action="/Client/usr" method="POST" name="Register a free account">
    <input type="hidden" name="function" value="login"/>
    <table cellspacing="5" id="register-form">
        <tr>
            <td>Username: </td>
            <td><input type="text" name="username" /></td>
        </tr>
        <tr>
            <td>Password: </td>
            <td><input type="password" name="password" /></td>  
        </tr>
    </table>
    <input class="big-submit form-align" type="submit" name="submit" value="Submit">
    <!--The user registration link -->
    <p>Not a member? <a href="/Client/usr?func=reg" name="register">Register</a> now, it's free and only takes a minute.</p>
</form>
<jsp:include page="includes/footer.html" />