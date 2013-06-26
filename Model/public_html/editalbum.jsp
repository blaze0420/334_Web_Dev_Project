<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*,client.Album,client.Photo" %>

<jsp:useBean class="client.Album" scope="request" id="album" />
<jsp:useBean type="java.util.ArrayList<Photo>" scope="request" id="unalbumed" />

<jsp:include page="includes/header.html" />
    <h1 id="page-title">Edit Album</h1>
    ${requestScope.msg}
<% ArrayList<Photo> aphotos = album.getPhotos(); %>

<p>Create an album then you can add several images to it.</p>
<form action="/Client/addalbum" method="POST" >
<table cellspacing="5" id="register-form">
    <tr>
        <td>Album Name: </td>
        <td><input type="text" size=40 name="name" value="<%=album.getTitle()%>"></td>
    </tr>
    <tr>
        <td>Description: </td>
        <td><input type="text" size=40 name="description" value="<%=album.getDescription()%>"/></td>  
    </tr>
    <tr>
        <td align="right" colspan=2>
        </td>
    </tr>
</table>

<table class="photo-grid" id="album-edit-form">

    <tr>
    <%
    int count=0;
    for(int i=0;i<aphotos.size();++i){
        if(i%4==0){
            out.println("</tr><tr>");
        }
        %>
        <td id="td<%=aphotos.get(i).getPid()%>" class="img-selected" onclick="javascript:check('<%=aphotos.get(i).getPid()%>')" >
            <h4><%=aphotos .get(i).getTitle()%></h4>
            <img src="/Client/images/<%=aphotos.get(i).getThumbFilename()%>" > 
            <input name="image" value="<%=aphotos.get(i).getPid()%>" class="album-edit-check" type="checkbox" id="checkbox<%=aphotos.get(i).getPid()%>" checked="checked"/>
        </td>
    <%} %>
    </tr>
    <tr>
    <%
    for(int i=0;i<unalbumed.size();++i){
        if(i%4==0){
            out.println("</tr><tr>");
        }
        %>
        <td id="td<%=unalbumed.get(i).getPid()%>" class="img-deselected" onclick="javascript:check('<%=unalbumed.get(i).getPid()%>')" >
            <h4><%=unalbumed.get(i).getTitle()%></h4>
            <img src="/Client/images/<%=unalbumed.get(i).getThumbFilename()%>" > 
            <input name="image" height=200 width=200 value="<%=unalbumed.get(i).getPid()%>" class="album-edit-check" type="checkbox" id="checkbox<%=unalbumed.get(i).getPid()%>" />
        </td>
    <%} %>
    </tr>
    <input type="hidden" name="aid" value="<%=album.getAid()%>"/>
    <input type="hidden" name="func" value="save"/>
<tr><td colspan=4 align="right"><input class="big-submit" type="submit" name="save" value="Save"></td></tr>
</table>
<input type="hidden" name="func" value="save"/>
</form>
<jsp:include page="includes/footer.html" />