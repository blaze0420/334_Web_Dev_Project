<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<jsp:include page="includes/header.html" />
    <h1 id="page-title">Add Album</h1>
    ${requestScope.msg}
    
<p>Create an album then you can add several images to it.</p>
<form action="/Client/addalbum" method="POST">
    <table cellspacing="5" id="register-form">
        <tr>
            <td>Album Name: </td>
            <td><input type="text" size=40 name="name" /></td>
        </tr>
        <tr>
            <td>Description: </td>
            <td><input type="text" size=40 name="description" /></td>  
        </tr>
    </table>
    <input class="big-submit form-align" type="submit" name="submit" value="Submit">
</form>
<jsp:include page="includes/footer.html" />