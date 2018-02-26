package org.suxin.jwb;

import java.util.Arrays;

public class PaperContent {
	
	String title;	//标题
	String author;	//作者
	
	String[] content;	//内容
	String dateStr;	//时间
	boolean isMatch;
	String url;
	
	public PaperContent(){}
	
	public String createKey(){
		return this.title + this.author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String[] getContent() {
		return content;
	}

	public void setContent(String content[]) {
		this.content = content;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "PaperContent [title=" + title + ", author=" + author 
				 + ", content=" + Arrays.toString(content) + ", dateStr=" + dateStr + ", isMatch=" + isMatch
				+ "]";
	}

	
	
}
