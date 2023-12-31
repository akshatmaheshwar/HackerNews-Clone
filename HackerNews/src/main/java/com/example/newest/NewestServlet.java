package com.example.newest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.jdt.internal.compiler.ast.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
@WebServlet("/newest")
public class NewestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public NewestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
					try{Posts post = om1.readValue(sb1.toString(),Posts.class);
					postList.add(post);
					}catch(Exception e) {System.out.println(e);}
				}
			}
			request.setAttribute("postList", postList);
		}
        request.getRequestDispatcher("/newest.jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
