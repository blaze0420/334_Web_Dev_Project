<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*,client.Photo" %>

<jsp:useBean type="java.util.ArrayList<Photo>" scope="request" id="photos"/>

<jsp:include page="includes/header.html" />
<h1 id="page-title">Photos</h1>
<div id="contextual-links"><a href="/Client/viewphotos?func=own">My Photos</a></div>

<table class="photo-grid">
<tr>
<%
    Photo p;
    String path;
    for(int i=0;i<photos.size();++i){
        path = "oops_thumb.jpg";
        if(photos.get(i) != null){
            p = photos.get(i);
            path = p.getThumbFilename();
        }
    if(i%4 == 0)
        out.println("</tr><tr>");
%>
            <td>
                <h4><%=photos.get(i).getTitle()%></h4>
                <a href="/Client/viewphoto?pid=<%=photos.get(i).getPid()%>">
                <img height="200" width="200" src="/Client/images/<%=path%>">
                </a>
            </td>
    
<%} %>
</tr>
</table>
<center><p>${requestScope.pager}</p></center>
<jsp:include page="includes/footer.html" />