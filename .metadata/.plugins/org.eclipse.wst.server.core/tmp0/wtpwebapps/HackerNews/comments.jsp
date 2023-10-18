<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.net.URI" %>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.Iterator" %>
    <%@ page import="com.example.newest.Comments" %>
    <%@ page import="com.example.newest.Posts" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Hacker News</title>
</head>
<body>
<%!
String timeDiff(Long timeStamp){
	long epoch = System.currentTimeMillis()/1000;
	long difference = epoch - timeStamp;
	if(difference/86400>0)return (difference/86400) + " days ago";    
	if(difference/3600>0) return (difference/3600) + " hours ago";
	if (difference/60>0) return (difference/60) + " minutes ago";
	return (difference) + " seconds ago";
}
String getDomainName(String url){
	URI uri;
	try{
		uri = new URI(url);
		String host = uri.getHost();
		String domainName = host.startsWith("www.") ? host.substring(4) : host;
		return "(" + domainName + ")";
	}catch(Exception e){
		System.out.println(e);
	}
	return "";
}
%>
<h4> <a href ="newest">new</a>&nbsp;|&nbsp;<a href ="dbnewest">new from DB</a>&nbsp;|&nbsp;<a href ="">past</a>&nbsp;|&nbsp;<a href ="">comments</a>&nbsp;|&nbsp;<a href ="">ask</a>&nbsp;|&nbsp;<a href ="">show</a>&nbsp;|&nbsp;<a href ="">jobs</a>&nbsp;|&nbsp;<a href ="">submit</a></h4>
<h2>Post</h2>
<% Posts pItem = (Posts) request.getAttribute("post"); %>
<span><a href ="<%=pItem.getUrl()%>"><%= pItem.getTitle() %></a> <%= getDomainName(pItem.getUrl())%> </span>
            <p><%=pItem.getScore()%> point &nbsp; Posted by <%=pItem.getBy() %> &nbsp; <%= timeDiff(pItem.getTime()) %></p>
            <%if(pItem.getText()!=null){%><p><%= pItem.getText()%></p><%}%>
<h2>Comments</h2>
<ul>
     <%
    ArrayList<Comments> commentList = (ArrayList<Comments>)request.getAttribute("commentList");

    if (commentList != null) {
        Iterator<Comments> iterator = commentList.iterator();
        while (iterator.hasNext()) {
            Comments cItem = iterator.next();
%>
            <li><p><%=cItem.getBy()%> &nbsp;<%= timeDiff(cItem.getTime()) %></p>
            <p><%=cItem.getText() %></p>
            <p><%if(cItem.getKids()!=null&&cItem.getKids().size()>0){%> &nbsp; <a href = ""> <%=cItem.getKids().size()%> replies</a><%}%></p></li>
<%
        }
    }
%>
</ul>
</body>
</html>