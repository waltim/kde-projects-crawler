package br.com.unb.cic.entities;

import java.util.Comparator;

public class Project {

	private String group;
	private String name;
	private String url;
	private Integer stars;
	private Integer commits;

	public Project(String group, String name, String url, Integer stars, Integer commits) {
		this.group = group;
		this.name = name;
		this.url = url;
		this.stars = stars;
		this.commits = commits;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
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
	
	public static Comparator<Project> ProjectComparator = new Comparator<Project>() {

		public int compare(Project p1, Project p2) {

			String ProjectGroup1 = p1.getGroup().toUpperCase();
			String ProjectGroup2 = p2.getGroup().toUpperCase();

			// ascending order
			return ProjectGroup1.compareTo(ProjectGroup2);

		}
	};

}
