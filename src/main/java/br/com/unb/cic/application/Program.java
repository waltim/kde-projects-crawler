package br.com.unb.cic.application;

import java.util.ArrayList;

import br.com.unb.cic.entities.Project;
import br.com.unb.cic.scrapy.ProjectCrawler;
import br.com.unb.cic.scrapy.ProjectScrapper;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		ArrayList<String> repositories = ProjectCrawler.crawler("https://github.com/orgs/KDE/repositories","KDE");

//		ArrayList<String> repositories = new ArrayList<String>();
//		repositories.add("https://github.com/KDE/incidenceeditor");
//		repositories.add("https://github.com/KDE/grantleetheme");
//		repositories.add("https://github.com/KDE/calendarsupport");
//		repositories.add("https://github.com/KDE/kdepim-addons");
//		repositories.add("https://github.com/KDE/kdepim-runtime");
//		repositories.add("https://github.com/KDE/snapcraft-kde-applications");

		ArrayList<Project> projects = ProjectScrapper.scrapper(repositories,"KDE");

		System.out.println("Number of Repositories: " + repositories.size());

		for (Project project : projects) {
			System.out.println(project);
		}
	}
}
