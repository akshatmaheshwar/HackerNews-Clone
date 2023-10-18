package com.example.newest;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class CassandraDBConnect {
	Cluster cluster;
	Session session;
	public void connectdb(String seeds, int port) {
		this.cluster = Cluster.builder().addContactPoint(seeds).withPort(port).build();
		this.session = cluster.connect("hacker_news");
	}
	
	public Session getSession() {
		return this.session;
	}
	
	public void close() {
		cluster.close();
	}
}
