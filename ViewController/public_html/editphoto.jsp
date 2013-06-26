<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<jsp:include page="includes/header.html" />
<jsp:useBean type="client.Photo" scope="request" id="photo" />

<h1 id="page-title">Edit Photo</h1>

<p align="center"> <img src=/Client/images/${photo.fullFilename} /></p>

<form action="addphoto" method="POST" >
    <!--FORM STUFF HERE-->
    <p>Edit your photo</p>
        
    <table cellspacing="5" >
        <tr>
            <td>Add a title</td>
            <td align=right><input type="text" size=35 name="title" value="<%=photo.getTitle()%>" /></td>
        </tr>
        <tr>
            <td>Add a caption</td>
            <td align=right><input type="text" size=35 name="caption" value="<%=photo.getCaption()%>" /></td>
        </tr>
    </table>
    <input type="hidden" name="pid" value="<%=photo.getPid()%>"/>
    <input class="big-submit form-align" type="submit" value="Update" />
    <input type="hidden" name="func" value="save"/>
</form>

<jsp:include page="includes/footer.html" />