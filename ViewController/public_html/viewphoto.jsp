<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=windows-1252"%>
<jsp:include page="includes/header.html" />
<h1 id="page-title">${requestScope.title}</h1>
<div id="contextual-links">${requestScope.links}</div>
<img class="large_img" src="${requestScope.src}" align="middle" />
<p class="caption">${requestScope.caption}</p>
<jsp:include page="includes/footer.html" />