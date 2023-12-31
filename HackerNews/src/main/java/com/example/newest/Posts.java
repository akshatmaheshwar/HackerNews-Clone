package com.example.newest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Posts {
	
	String by;
	int descendants;
	int id;
	List<Integer> kids;
	int score;
	String text;
	long time;
	String title;
	String type;
	String url;
	public Posts() {}
	public Posts(String by, int descendants, int id, List<Integer> kids, int score, String text, long time,
			String title, String type, String url) {
		this.by = by;
		this.descendants = descendants;
		this.id = id;
		this.kids = kids;
		this.score = score;
		this.text = text;
		this.time = time;
		this.title = title;
		this.type = type;
		this.url = url;
	}
	
	public String getBy() {
		return by;
	}
	public void setBy(String by) {
		this.by = by;
	}
	public int getDescendants() {
		return descendants;
	}
	public void setDescendants(int descendants) {
		this.descendants = descendants;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Integer> getKids() {
		return kids;
	}
	public void setKids(List<Integer> kids) {
		this.kids = kids;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
