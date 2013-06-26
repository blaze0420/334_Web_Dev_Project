<!-- 
    This JSP page is the log out.  It is a simple page, and appears when a logged in user clicks
    log in/out in the banner.  The purpose is to inform the user they are currently logged in
    and give them a chance to go back if accidental.  If they are logging out, the user clicks the
    link which runs the logout function in usr.java.  The user is then returned to the login page.
-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<jsp:include page="includes/header.html" />
<h1 id="page-title">Log Out</h1>
    ${requestScope.msg}
    <p>You are currently logged in, <a href="/Client/usr?func=logout" name="register">click here </a> to log out.</p>
</form>
<jsp:include page="includes/footer.html" />