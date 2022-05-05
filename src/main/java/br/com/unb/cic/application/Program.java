package br.com.unb.cic.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import br.com.unb.cic.entities.Project;
import br.com.unb.cic.scrapy.GroupCrawler;
import br.com.unb.cic.scrapy.ProjectCrawler;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		ArrayList<String> groups = GroupCrawler.crawler("https://invent.kde.org/explore/groups");

		Map<String, String> projects = GroupCrawler.crawler(groups);

		ArrayList<Project> dataset = ProjectCrawler.crawler(projects);

		System.out.println("Number of Groups: " + groups.size());
		System.out.println("Number of links: " + projects.size());
		System.out.println("Number of Projects: " + dataset.size());

		Collections.sort(dataset, Project.ProjectComparator);

		for (Project project : dataset) {
			System.out.println(project.getGroup() + "," + project.getName() + "," + project.getUrl() + ","
					+ project.getCommits() + "," + project.getStars());
		}
	}
}
