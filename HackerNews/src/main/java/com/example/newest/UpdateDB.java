package com.example.newest;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class UpdateDB implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException{
		try {
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
				CassandraDBConnect connector = new CassandraDBConnect();
				connector.connectdb("localhost", 9042);
				com.datastax.driver.core.PreparedStatement cpst = connector.getSession().prepare("INSERT INTO hacker_news_post_by_time (id,descendants,posted_by,score,text,time,date,title,type,url) VALUES (?,?,?,?,?,?,?,?,?,?);");
				for(Posts post : postList) {
					int id =  post.getId();
					int descendants = post.getDescendants();
					String posted_by = post.getBy();
					int score = post.getScore();
					String text = post.getText();
					long time = post.getTime();
			        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(time*1000));
					String title = post.getTitle();
					String type = post.getType();
					String url = post.getUrl();
					connector.getSession().execute(cpst.bind(id, descendants, posted_by, score, text, time,formattedDate, title, type, url));
				}
				System.out.println("Updated Cassandra DB successfully at "+ new Date());
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
						System.out.println("Updated MySql DB successfully at "+ new Date());
					}catch(Exception e) {e.printStackTrace();}
				}catch(Exception e) {e.printStackTrace();}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
