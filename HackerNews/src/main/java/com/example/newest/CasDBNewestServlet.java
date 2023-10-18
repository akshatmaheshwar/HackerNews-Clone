package com.example.newest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.fasterxml.jackson.databind.ObjectMapper;
@WebServlet("/casdbnewest")
public class CasDBNewestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CasDBNewestServlet() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long fromTime = 0;
		long toTime = System.currentTimeMillis()/1000;
		int dateNav = 0;
		if(request.getParameter("fromtime")!=null) {
			fromTime = Long.parseLong(request.getParameter("fromtime"));
			request.setAttribute("fromtime", fromTime);		
		}
		if(request.getParameter("totime")!=null) {
			toTime = Long.parseLong(request.getParameter("totime"));
			request.setAttribute("totime", toTime);
		}
		if(request.getParameter("datenav")!=null)dateNav = Integer.parseInt(request.getParameter("datenav"));
		CassandraDBConnect connector = new CassandraDBConnect();
		connector.connectdb("localhost", 9042);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String fromDate = sdf.format(new Date(fromTime*1000));
		String toDate = sdf.format(new Date(toTime*1000));
		Date date = new Date();
		try {
			date = sdf.parse(toDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
		List<Posts> postList = new ArrayList<Posts>();
		if(fromTime!=0) {
		do {
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			StringBuilder queryString = new StringBuilder("SELECT * FROM hacker_news_post_by_time WHERE date = '" + sdf.format(calendar.getTime())+ "'");
			if(sdf.format(calendar.getTime()).equals(fromDate)) queryString.append(" AND time >= " + fromTime);
			if(sdf.format(calendar.getTime()).equals(toDate)) queryString.append(" AND time <= " + toTime);
			queryString.append(";");
			System.out.println(queryString);
			ResultSet resultSet = connector.getSession().execute(queryString.toString());
	    	for(Row rs : resultSet) {
	    		Posts post = new Posts();
	    		post.setId(rs.getInt("id"));
	    		post.setBy(rs.getString("posted_by"));
	    		post.setDescendants(rs.getInt("descendants"));
	    		post.setScore(rs.getInt("score"));
	    		post.setText(rs.getString("text"));
	    		post.setTime(rs.getLong("time"));
	    		post.setTitle(rs.getString("title"));
	    		post.setType(rs.getString("type"));
	    		post.setUrl(rs.getString("url"));
	    		postList.add(post);
	    	}
		}while(!sdf.format(calendar.getTime()).equals(fromDate));
		}
		else {
			ResultSet resultSet = connector.getSession().execute("SELECT * FROM hacker_news_post_by_time;");
	    	for(Row rs : resultSet) {
	    		Posts post = new Posts();
	    		post.setId(rs.getInt("id"));
	    		post.setBy(rs.getString("posted_by"));
	    		post.setDescendants(rs.getInt("descendants"));
	    		post.setScore(rs.getInt("score"));
	    		post.setText(rs.getString("text"));
	    		post.setTime(rs.getLong("time"));
	    		post.setTitle(rs.getString("title"));
	    		post.setType(rs.getString("type"));
	    		post.setUrl(rs.getString("url"));
	    		postList.add(post);
	    	}
		}
    	request.setAttribute("postList", postList);
    	request.setAttribute("datenav", dateNav);
    	request.getRequestDispatcher("/newest.jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL urlObj = new URL("https://hacker-news.firebaseio.com/v0/newstories.json?print=pretty");
		HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		
		if(responseCode == HttpsURLConnection.HTTP_OK) {
			StringBuilder sb = new StringBuilder();
			Scanner sc = new Scanner(connection.getInputStream());
			
			while(sc.hasNext())sb.append(sc.nextLine());
			sc.close();
			
			ObjectMapper om = new ObjectMapper();
			@SuppressWarnings("unchecked")
			List<Integer> jsonArr = om.readValue(sb.toString(),List.class);
			List<Posts> postList = new ArrayList<Posts>();
			
			for(int i=0;i<30;i++) {
				URL urlObj1 = new URL("https://hacker-news.firebaseio.com/v0/item/"+jsonArr.get(i)+".json?print=pretty");
				HttpsURLConnection connection1 = (HttpsURLConnection) urlObj1.openConnection();
				connection1.setRequestMethod("GET");
				int responseCode1 = connection1.getResponseCode();
				
				if(responseCode1 == HttpsURLConnection.HTTP_OK) {
					StringBuilder sb1 = new StringBuilder();
					Scanner sc1 = new Scanner(connection1.getInputStream());
					
					while(sc1.hasNext())sb1.append(sc1.nextLine());
					sc1.close();
					
					try{
						Posts post = om.readValue(sb1.toString(),Posts.class);
						postList.add(post);
					}catch(Exception e) {
						System.out.println(e);
					}
				}
			}
			List<Posts> postsWithText = new ArrayList<Posts>();
			CassandraDBConnect connector = new CassandraDBConnect();
			connector.connectdb("localhost", 9042);
			PreparedStatement pst = connector.getSession().prepare("INSERT INTO hacker_news_post_by_time (id,descendants,first_comment,posted_by,score,text,time,date,title,type,url) VALUES (?,?,?,?,?,?,?,?,?,?,?);");
			for(Posts post : postList) {
				int id =  post.getId();
				int descendants = post.getDescendants();
				String posted_by = post.getBy();
				int score = post.getScore();
				String text = post.getText();
				long time = post.getTime();
				String first_comment = null;
				if(post.getKids()!=null&&post.getKids().size()!=0) {
					URL urlObj1 = new URL("https://hacker-news.firebaseio.com/v0/item/"+post.getKids().get(0)+".json?print=pretty");
					HttpsURLConnection connection1 = (HttpsURLConnection) urlObj1.openConnection();
					connection1.setRequestMethod("GET");
					int responseCode1 = connection1.getResponseCode();
					
					if(responseCode1 == HttpsURLConnection.HTTP_OK) {
						StringBuilder sb1 = new StringBuilder();
						Scanner sc1 = new Scanner(connection1.getInputStream());
						
						while(sc1.hasNext())sb1.append(sc1.nextLine());
						sc1.close();
						
						try{
							Comments comment = om.readValue(sb1.toString(),Comments.class);
							first_comment = comment.getText();
							if(first_comment.toLowerCase().contains("leetcode")) {
								try {
						            URL webHookUrlObj = new URL("https://webhook.site/552157ca-4009-4a8f-a35a-f09165e13052");
						            HttpURLConnection webHookConnection = (HttpURLConnection) webHookUrlObj.openConnection();
						            webHookConnection.setRequestMethod("POST");
						            webHookConnection.setDoOutput(true);
						            String payload = om.writeValueAsString(comment);
						            try (OutputStream os = webHookConnection.getOutputStream()) {
						                os.write(payload.getBytes());
						                os.flush();
						            }
						            int webHookResponseCode = webHookConnection.getResponseCode();
						            System.out.println("Webhook response code: " + webHookResponseCode);
						            webHookConnection.disconnect();
						        } catch (Exception e) {
						            e.printStackTrace();
						        }
							}
						}catch(Exception e) {
							System.out.println(e);
						}
					}
				}
		        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(time*1000));
				String title = post.getTitle();
				String type = post.getType();
				String url = post.getUrl();
				connector.getSession().execute(pst.bind(id, descendants,first_comment, posted_by, score, text, time,formattedDate, title, type, url));
				if(text!=null)postsWithText.add(post);
			}
			System.out.println("Updated Cassandra DB successfully!");
			response.sendRedirect("casdbnewest");
		}
	}

}
