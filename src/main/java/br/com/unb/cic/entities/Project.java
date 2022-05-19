package br.com.unb.cic.entities;

import java.time.LocalDate;

public class Project {

	private String organization;
	private String name;
	private String url;
	private Double stars;
	private Integer commits;
	private Double percentCppCode;
	private String language;
	private LocalDate pushed;
	// githubApi
	private Boolean fork;
	private LocalDate created;
	private Integer contributors;

	public Project(String organization, String name, String url, Double stars, Integer commits, Integer contributors,
			Boolean fork, LocalDate created, LocalDate pushed, Double percentCppCode, String language) {
		this.organization = organization;
		this.name = name;
		this.url = url;
		this.stars = stars;
		this.commits = commits;
		this.fork = fork;
		this.created = created;
		this.pushed = pushed;
		this.percentCppCode = percentCppCode;
		this.language = language;
		this.contributors = contributors;
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

	public Double getStars() {
		return stars;
	}

	public void setStars(Double stars) {
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

	public Integer getContributors() {
		return contributors;
	}

	public void setContributors(Integer contributors) {
		this.contributors = contributors;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		return organization + ", " + name + ", " + url + ", " + stars + ", " + commits + ", " + contributors + ", "
				+ language + ", " + percentCppCode + ", " + fork + ", " + created + ", " + pushed;
	}
}
