<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*,client.Album,client.Photo" %>

<jsp:useBean type="java.util.ArrayList<Album>" scope="request" id="albums" />

<jsp:include page="includes/header.html" />
<h1 id="page-title">Albums</h1>
<div id="contextual-links"><a href="/Client/viewalbums?func=own">My Albums</a></div>
    ${requestScope.msg}

<table class="photo-grid">
<tr>
<%
    Photo p;
    String path;
for(int i=0;i<albums.size();++i){
    path = "oops_thumb.jpg";
    if(albums.get(i).getPhotos().size()>0){
        p = albums.get(i).getPhotos().get(0);
        path = p.getThumbFilename();
    }
    if(i%4 == 0)
        out.println("</tr><tr>");
%>
            <td>
                <h4><%=albums.get(i).getTitle()%></h4>
                <a href="/Client/viewalbum?aid=<%=albums.get(i).getAid()%>">
                <img src="/Client/images/<%=path%>">
                </a>
            </td>
    
<%} %>
</tr>
</table>
<jsp:include page="includes/footer.html" />