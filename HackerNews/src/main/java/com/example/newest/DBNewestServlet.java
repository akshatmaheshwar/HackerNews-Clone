package com.example.newest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
@WebServlet("/dbnewest")
public class DBNewestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public DBNewestServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int pagination = 0;
		long fromTime = 0;
		long toTime = System.currentTimeMillis()/1000;
		int dateNav = 0;
		if(request.getParameter("pagination")!=null)pagination = Integer.parseInt(request.getParameter("pagination"));
		if(request.getParameter("fromtime")!=null) {
			fromTime = Long.parseLong(request.getParameter("fromtime"));
			request.setAttribute("fromtime", fromTime);		
		}
		if(request.getParameter("totime")!=null) {
			toTime = Long.parseLong(request.getParameter("totime"));
			request.setAttribute("totime", toTime);
		}
		if(request.getParameter("datenav")!=null)dateNav = Integer.parseInt(request.getParameter("datenav"));
	    try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/HackerNews","root","root")){
	    	Statement st = con.createStatement();
	    	ResultSet rs = st.executeQuery("SELECT * FROM HackerNewsPost WHERE time BETWEEN "+fromTime+" AND "+toTime+" ORDER BY time DESC LIMIT 30 OFFSET " + pagination );
	    	List<Posts> postList = new ArrayList<Posts>();
	    	while(rs.next()) {
	    		Posts post = new Posts();
	    		List<Integer> kidsId = new ArrayList<>();

	            try (Statement st1 = con.createStatement();
	                 ResultSet rs1 = st1.executeQuery("SELECT id FROM HackerNewsComments WHERE parent_id = " + rs.getInt("id"))) {

	                while (rs1.next()) {
	                    kidsId.add(rs1.getInt("id"));
	                }
	            }
	    		post.setKids(kidsId);
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
	    	request.setAttribute("postList", postList);
	    }catch(Exception e) {e.printStackTrace();}
	    request.setAttribute("pagination", pagination+30);
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
					
					ObjectMapper om1 = new ObjectMapper();
					try{
						Posts post = om1.readValue(sb1.toString(),Posts.class);
						postList.add(post);
					}catch(Exception e) {
						System.out.println(e);
					}
				}
			}
			try {
			    Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
			    e.printStackTrace();
			}
			try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/HackerNews","root","root")){
				try(PreparedStatement pst = con.prepareStatement("INSERT IGNORE INTO HackerNewsPost(id,posted_by,descendants,score,text,time,title,type,url) values(?,?,?,?,?,?,?,?,?)")){
					for(Posts post : postList) {
						pst.setInt(1, post.getId());
						pst.setString(2, post.getBy());
						pst.setInt(3, post.getDescendants());
						pst.setInt(4, post.getScore());
						pst.setString(5, post.getText());
						pst.setBigDecimal(6, new BigDecimal(post.getTime()));
						pst.setString(7,post.getTitle());
						pst.setString(8, post.getType());
						pst.setString(9,post.getUrl());
						pst.executeUpdate();
					}
					System.out.println("Updated DB successfully!");
				}catch(Exception e) {e.printStackTrace();}
			}catch(Exception e) {e.printStackTrace();}
			request.setAttribute("postList", postList);
			request.getRequestDispatcher("/comments").forward(request, response);
//			response.sendRedirect("dbnewest");
		}
	}

}
