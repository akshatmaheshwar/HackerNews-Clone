package com.example.newest;

import java.util.ArrayList;

public class Comments {
	String by;
	int id;
	ArrayList<Integer> kids;
	int parent;
	String text;
	long time;
	String type;
	public Comments(){}
	public Comments(String by, int id, ArrayList<Integer> kids, int parent, String text, long time, String type) {
		super();
		this.by = by;
		this.id = id;
		this.kids = kids;
		this.parent = parent;
		this.text = text;
		this.time = time;
		this.type = type;
	}
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Integer> getKids() {
		return kids;
	}
	public void setKids(ArrayList<Integer> kids) {
		this.kids = kids;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
