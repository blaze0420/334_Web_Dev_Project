<!-- 
    The Registration page of the site, It is accessible from the registration link on the login page.
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<jsp:include page="includes/header.html" />
<h1 id="page-title">Register</h1>
    ${requestScope.msg}
<!-- The page contains a simple form, which takes the user's username, Email, and password.
     The information is sent to usr.java when the user submits where it is stored in the database
     and the new account is created.  The user is then sent to the login screen to login.
-->
<form action="/Client/usr" method="POST" name="Register a free account">
    <input type="hidden" name="function" value="register"/>
    <table cellspacing="5" id="register-form">
        <tr>
            <td>Username: </td>
            <td><input type="text" name="username" /></td>
        </tr>
        <tr>
            <td>Email: </td>
            <td><input type="text" name="email" /></td>
        </tr>
        <tr>
            <td>Password: </td>
            <td><input type="password" name="password" /></td>  
        </tr>
        <tr>
            <td>Confirm: </td>
            <td><input type="password" name="confirm" /></td>
        </tr>
    </table>
    <input class="big-submit form-align" type="submit" name="submit" value="Submit">
</form>
<jsp:include page="includes/footer.html" />