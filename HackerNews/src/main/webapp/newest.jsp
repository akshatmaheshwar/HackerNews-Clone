<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="java.util.ArrayList" %>
    <%@ page import="java.util.Iterator" %>
    <%@ page import="java.net.URI" %>
    <%@ page import="java.util.Calendar"%>
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
		
	}
	return "";
}
long getTimeStampLastHour(){
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(System.currentTimeMillis());
	c.add(Calendar.HOUR_OF_DAY,-1);
	return c.getTimeInMillis()/1000;
}
long getTimeStampLast24Hour(){
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(System.currentTimeMillis());
	c.add(Calendar.DAY_OF_MONTH,-1);
	return c.getTimeInMillis()/1000;
}
long getTimeStampThisWeek(){
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(System.currentTimeMillis());
    int daysUntilSunday = Calendar.SUNDAY - c.get(Calendar.DAY_OF_WEEK);
    daysUntilSunday = daysUntilSunday>0? daysUntilSunday - 7:daysUntilSunday;
    c.add(Calendar.DAY_OF_MONTH, daysUntilSunday);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
	return c.getTimeInMillis()/1000;
}
long getMidNight(int n){
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(System.currentTimeMillis());
	c.add(Calendar.DAY_OF_MONTH, n);
    c.set(Calendar.HOUR_OF_DAY, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
	return c.getTimeInMillis()/1000;
}
%>
<h4> <a href ="newest">new</a>&nbsp;|&nbsp;<a href ="dbnewest">new from MySql DB</a>&nbsp;|&nbsp;<a href ="casdbnewest">new from Cassandra DB</a>&nbsp;|&nbsp;<a href ="">past</a>&nbsp;|&nbsp;<a href ="">comments</a>&nbsp;|&nbsp;<a href ="">ask</a>&nbsp;|&nbsp;<a href ="">show</a>&nbsp;|&nbsp;<a href ="">jobs</a>&nbsp;|&nbsp;<a href ="">submit</a></h4>
<% int startNo = 1;
int dateNav = 0;
if(request.getAttribute("pagination")!=null)startNo = (int)request.getAttribute("pagination")-29;
if(request.getAttribute("datenav")!=null)dateNav = (int)request.getAttribute("datenav");%>
	<div style="display: flex;">
	<form action="dbnewest" method="post"><input type="submit" value="Update MySql DB manually"></form> &nbsp; &nbsp;
	<form action="casdbnewest" method="post"><input type="submit" value="Update Cassandra DB manually"></form> &nbsp; &nbsp;
	<form action="scheduledservlet" method="post">
        Scheduled DB update (in minutes): <input type="text" name="intervalMinutes" required>
        <input type="submit" value="Start">
    </form> &nbsp; &nbsp;
    <form action="stopscheduledservlet" method="post"><input type="submit" value="Stop Scheduled Task"></form></div>
    
    <br>
    <div>Show posts in MySql from <a href = "dbnewest?fromtime=<%=getTimeStampLastHour()%>">last hour</a> , <a href = "dbnewest?fromtime=<%=getTimeStampLast24Hour()%>">last 24 hours</a> , <a href = "dbnewest?fromtime=<%=getTimeStampThisWeek()%>">this week</a> , <a href = "dbnewest?fromtime=<%=getMidNight(dateNav-1)%>&totime=<%=getMidNight(dateNav)%>&datenav=<%=dateNav-1%>">previous day</a> and <a href = "dbnewest?fromtime=<%=getMidNight(dateNav+1)%>&totime=<%=getMidNight(dateNav+2)%>&datenav=<%=dateNav+1%>">next day</a></div>
    <div>Show posts in Cassandra from <a href = "casdbnewest?fromtime=<%=getTimeStampLastHour()%>">last hour</a> , <a href = "casdbnewest?fromtime=<%=getTimeStampLast24Hour()%>">last 24 hours</a> , <a href = "casdbnewest?fromtime=<%=getTimeStampThisWeek()%>">this week</a> , <a href = "casdbnewest?fromtime=<%=getMidNight(dateNav-1)%>&totime=<%=getMidNight(dateNav)%>&datenav=<%=dateNav-1%>">previous day</a> and <a href = "casdbnewest?fromtime=<%=getMidNight(dateNav+1)%>&totime=<%=getMidNight(dateNav+2)%>&datenav=<%=dateNav+1%>">next day</a></div>
<ol start = <%=startNo%>>
     <%
    ArrayList<Posts> postList = (ArrayList<Posts>)request.getAttribute("postList");

    if (postList != null&&!postList.isEmpty()) {
        Iterator<Posts> iterator = postList.iterator();
        while (iterator.hasNext()) {
            Posts item = iterator.next();
%>
            <li><span><a href ="<%=item.getUrl()%>"><%= item.getTitle() %></a> <%= getDomainName(item.getUrl())%> </span>
            <p><%=item.getScore()%> point &nbsp; Posted by <%=item.getBy() %> &nbsp; <%= timeDiff(item.getTime()) %><%if(item.getKids()!=null&&item.getKids().size()>0){%> &nbsp; <a href = "postcomments?postId=<%=item.getId()%>"> <%=item.getKids().size()%> comments</a><%}%></p></li>
<%
        }
    }else{%>
    <p>Nothing to display</p>
    <%
    }%>
</ol>
<%if(request.getAttribute("pagination")!=null&&postList!=null&&!postList.isEmpty()){%> <a href = "dbnewest?<%if(request.getAttribute("fromtime")!=null){%>fromtime=<%=request.getAttribute("fromtime")%>&<%}%><%if(request.getAttribute("totime")!=null){%>totime=<%=request.getAttribute("totime")%>&<%}%><%if(dateNav!=0){%>datenav=<%=dateNav%>&<%}%>pagination=${pagination}"> More</a><%} %>
</body>
</html>