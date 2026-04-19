# HackerNews Clone

A full-stack HackerNews clone built during the Zoho Incubation program 
to learn enterprise Java web development.

## What it does
- Fetches and displays the latest 30 stories from the real HackerNews API
- Persists stories to both MySQL and Apache Cassandra databases
- Scheduled automatic DB updates (configurable interval in minutes)
- User-specific feeds stored in Cassandra
- Time-based filtering — last hour, 24 hours, this week, by day
- Comment threads via HackerNews API
- User login and sign-up

## Tech Stack
Java · Jakarta Servlets · JSP · Apache Tomcat · MySQL · Apache Cassandra · 
Jackson · Quartz Scheduler · C3P0 Connection Pooling

## Setup
1. Install Java 17+, Apache Tomcat 10.1, MySQL, and Cassandra
2. Create a MySQL database and Cassandra keyspace named `hacker_news`
3. Import the project into Eclipse as a Dynamic Web Project
4. Deploy to Tomcat and navigate to `/newest`

## Notes
Built during Zoho Incubation as a learning project to understand 
Java Servlets, dual-database architectures, and scheduled background jobs.
