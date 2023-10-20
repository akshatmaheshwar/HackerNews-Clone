package com.example.newest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class PostComments
 */
@WebServlet("/postcomments")
public class PostComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public PostComments() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int postId = Integer.parseInt(request.getParameter("postId"));
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/HackerNews","root","root")){
	    	Statement st = con.createStatement();
	    	ResultSet rs = st.executeQuery("SELECT * FROM HackerNewsPost WHERE id = " + postId );
	    	Posts post = new Posts();
	    	while(rs.next()) {
	    		post.setId(rs.getInt("id"));
	    		post.setBy(rs.getString("posted_by"));
	    		post.setDescendants(rs.getInt("descendants"));
	    		post.setScore(rs.getInt("score"));
	    		post.setText(rs.getString("text"));
	    		post.setTime(rs.getLong("time"));
	    		post.setTitle(rs.getString("title"));
	    		post.setType(rs.getString("type"));
	    		post.setUrl(rs.getString("url"));
	    		request.setAttribute("post", post);
	    	}
	    }catch(Exception e) {
	    	System.out.println(e);
	    }
	    
	    try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	List<Comments> commentList = new ArrayList<Comments>();
	    try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/HackerNews","root","root")){
	    	Statement st = con.createStatement();
	    	ResultSet rs = st.executeQuery("SELECT * FROM HackerNewsComments WHERE parent_id = " + postId +" ORDER BY time DESC");
	    	while(rs.next()) {
	    		Comments comment = new Comments();
	    		ArrayList<Integer> kidsId = new ArrayList<>();

	            try (Statement st1 = con.createStatement();
	                 ResultSet rs1 = st1.executeQuery("SELECT id FROM HackerNewsComments WHERE parent_id = " + rs.getInt("id"))) {

	                while (rs1.next()) {
	                    kidsId.add(rs1.getInt("id"));
	                }
	            }
	    		comment.setKids(kidsId);
	    		comment.setId(rs.getInt("id"));
	    		comment.setBy(rs.getString("posted_by"));
	    		comment.setParent(rs.getInt("parent_id"));
	    		comment.setText(rs.getString("text"));
	    		comment.setTime(rs.getLong("time"));
	    		comment.setPostTime(rs.getLong("post_time"));
	    		comment.setType(rs.getString("type"));
	    		commentList.add(comment);
	    	}
	    	request.setAttribute("commentList", commentList);
	    }catch(Exception e) {e.printStackTrace();}
	    if(request.getAttribute("post")==null) {
			CassandraDBConnect connector = new CassandraDBConnect();
			connector.connectdb("localhost", 9042);
			
			com.datastax.driver.core.ResultSet resultSet = connector.getSession().execute("SELECT * FROM hacker_news_post_by_time WHERE date = '"+ 
			new SimpleDateFormat("dd-MM-yyyy").format(new Date(commentList.get(0).getPostTime()*1000))+"' AND time = "+ commentList.get(0).getPostTime() + " AND id = "+commentList.get(0).getParent()+";");
			System.out.println("SELECT * FROM hacker_news_post_by_time WHERE date = '"+
			new SimpleDateFormat("dd-MM-yyyy").format(new Date(commentList.get(0).getPostTime()*1000))+"' AND time = "+ commentList.get(0).getPostTime() + " AND id = "+commentList.get(0).getParent()+";");
			Posts post = new Posts();
			for(Row rs : resultSet) {
				post.setId(rs.getInt("id"));
	    		post.setBy(rs.getString("posted_by"));
	    		post.setDescendants(rs.getInt("descendants"));
	    		post.setScore(rs.getInt("score"));
	    		post.setText(rs.getString("text"));
	    		post.setTime(rs.getLong("time"));
	    		post.setTitle(rs.getString("title"));
	    		post.setType(rs.getString("type"));
	    		post.setUrl(rs.getString("url"));
			}
			request.setAttribute("post", post);
	    }
		request.getRequestDispatcher("/comments.jsp").forward(request, response);
			
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
