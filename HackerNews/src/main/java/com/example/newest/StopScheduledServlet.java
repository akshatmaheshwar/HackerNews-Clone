package com.example.newest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Servlet implementation class StopScheduledServlet
 */
@WebServlet("/stopscheduledservlet")
public class StopScheduledServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public StopScheduledServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            if (scheduler != null && scheduler.isStarted()) {
                scheduler.shutdown(true);
                System.out.println("Scheduled Task Stopped successfully!");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
		response.sendRedirect("dbnewest");
	}

}
