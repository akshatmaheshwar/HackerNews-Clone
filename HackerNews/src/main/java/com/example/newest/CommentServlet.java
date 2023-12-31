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
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/comments")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public CommentServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ArrayList<Posts> postList = (ArrayList<Posts>)request.getAttribute("postList");
		ArrayList<Integer> commentIds = new ArrayList<Integer>();
		for(int i=0;i<postList.size();i++) {
			if(postList.get(i).getKids()!=null) {
				for(int j=0;j<postList.get(i).getKids().size();j++) {
					commentIds.add(postList.get(i).getKids().get(j));
				}
			}
		}
		ArrayList<Comments> commentList = new ArrayList<Comments>();
		for(int i=0;i<commentIds.size();i++) {
			URL urlObj1 = new URL("https://hacker-news.firebaseio.com/v0/item/"+commentIds.get(i)+".json?print=pretty");
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
					Comments comment = om1.readValue(sb1.toString(),Comments.class);
					commentList.add(comment);
				}catch(Exception e) {
					System.out.println(e);
				}
			}
		}
//		for(int i=0;i<commentList.size();i++)System.out.println(commentList.get(i).getText().toString());
		try {
		    Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		}
		try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/HackerNews","root","root")){
			try(PreparedStatement pst = con.prepareStatement("INSERT IGNORE INTO HackerNewsComments(id,posted_by,parent_id,text,time,type) values(?,?,?,?,?,?)")){
				for(Comments comment : commentList) {
					pst.setInt(1, comment.getId());
					pst.setString(2, comment.getBy());
					pst.setInt(3, comment.getParent());
					pst.setString(4, comment.getText());
					pst.setBigDecimal(5, new BigDecimal(comment.getTime()));
					pst.setString(6, comment.getType());
					pst.executeUpdate();
				}
				System.out.println("Updated DB successfully!");
			}catch(Exception e) {e.printStackTrace();}
		}catch(Exception e) {e.printStackTrace();}
		response.sendRedirect("dbnewest");
	}

}
