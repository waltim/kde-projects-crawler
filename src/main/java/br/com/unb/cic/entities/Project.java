package br.com.unb.cic.entities;

import java.time.LocalDate;

public class Project {

	private String name;
	private String url;
	private Integer stars;
	private Integer commits;
	private Double percentCppCode;
	private String language;

	// githubApi
	private Boolean fork;
	private LocalDate created;
	private LocalDate pushed;

	public Project(String name, String url, Integer stars, Integer commits, Double percentCppCode, String language,
			LocalDate pushed) {
		this.name = name;
		this.url = url;
		this.stars = stars;
		this.commits = commits;
		this.percentCppCode = percentCppCode;
		this.fork = false;
		this.created = null;
		this.pushed = pushed;
		this.language = language;

	}

	public Project(String name, String url, Integer stars, Integer commits, Boolean fork, LocalDate created,
			LocalDate pushed, Double percentCppCode, String language) {
		this.name = name;
		this.url = url;
		this.stars = stars;
		this.commits = commits;
		this.fork = fork;
		this.created = created;
		this.pushed = pushed;
		this.percentCppCode = percentCppCode;
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStars() {
		return stars;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	public Integer getCommits() {
		return commits;
	}

	public void setCommits(Integer commits) {
		this.commits = commits;
	}

	public Double getPercentCppCode() {
		return percentCppCode;
	}

	public void setPercentCppCode(Double percentCppCode) {
		this.percentCppCode = percentCppCode;
	}

	public Boolean getFork() {
		return fork;
	}

	public void setFork(Boolean fork) {
		this.fork = fork;
	}

	public LocalDate getCreated() {
		return created;
	}

	public void setCreated(LocalDate created) {
		this.created = created;
	}

	public LocalDate getPushed() {
		return pushed;
	}

	public void setPushed(LocalDate pushed) {
		this.pushed = pushed;
	}

	@Override
	public String toString() {
		return "Project [name=" + name + ", url=" + url + ", stars=" + stars + ", commits=" + commits
				+ ", percentCppCode=" + percentCppCode + ", language=" + language + ", fork=" + fork + ", created="
				+ created + ", pushed=" + pushed + "]";
	}

}
