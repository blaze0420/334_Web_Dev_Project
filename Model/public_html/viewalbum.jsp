<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*,client.Album,client.Photo" %>

<jsp:useBean class="client.Album" scope="request" id="album" />
<jsp:include page="includes/header.html" />
<h1 id="page-title"><%=album.getTitle()%></h1>
   <div id="contextual-links"> ${requestScope.msg}</div>
   <p class="caption"><%=album.getDescription()%></p>
<table class="photo-grid">
<tr>
<%
    ArrayList<Photo> photos= album.getPhotos();
for(int i=0;i<photos.size();++i){
    if(i%4 == 0)
        out.println("</tr><tr>");
%>
            <td>
                <h4><%=photos.get(i).getTitle()%></h4>
                <a href="/Client/viewphoto?pid=<%=photos.get(i).getPid()%>">
                <img src="/Client/images/<%=photos.get(i).getThumbFilename()%>">
                </a> 
            </td>
    
<%} %>
</tr>
</table>


<jsp:include page="includes/footer.html" />