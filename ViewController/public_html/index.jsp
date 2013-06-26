<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*,client.DBC,client.Album,client.Photo" %>
<% 
    DBC dbc = new DBC();
    ArrayList<Photo> p = Photo.getRecentPhotos(dbc, 1, 0);
%>
<jsp:include page="includes/header.html" />
<img src="/Client/images/<%=p.get(0).getThumbFilename()%>" id="front-img">
<h1 class="centertitle" id="page-title">Welcome to My Shots!</h1>
<p>My Shots has been developed to allow users to share and organize 
their photos. It's free and very simple to use so why not <a href="usr">get started</a> today!</p>

<jsp:include page="includes/footer.html" />