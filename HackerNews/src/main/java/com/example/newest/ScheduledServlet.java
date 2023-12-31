package com.example.newest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Servlet implementation class ScheduledServlet
 */
@WebServlet("/scheduledservlet")
public class ScheduledServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public ScheduledServlet() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
            int intervalMinutes = Integer.parseInt(request.getParameter("intervalMinutes"));
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail jobDetail = JobBuilder.newJob(UpdateDB.class).withIdentity("updateDB").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("updateDBTrigger").startNow()
            		.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(intervalMinutes).repeatForever()).build();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException | NumberFormatException e) {
            e.printStackTrace();
        }
		response.sendRedirect("dbnewest");
	}

}
