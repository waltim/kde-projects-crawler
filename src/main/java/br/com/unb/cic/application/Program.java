package br.com.unb.cic.application;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import br.com.unb.cic.entities.Project;
import br.com.unb.cic.enums.IOEnum;
import br.com.unb.cic.scrapy.ProjectScrapper;

public class Program {

	public static void main(String[] args) throws InterruptedException {

//		ArrayList<String> repositories = ProjectCrawler.crawler("https://github.com/orgs/KDE/repositories", "KDE");

		ArrayList<String> repositories = new ArrayList<String>();
		repositories.add("https://github.com/KDE/websites-docs-krita-org");
		repositories.add("https://github.com/KDE/incidenceeditor");
		repositories.add("https://github.com/KDE/grantleetheme");
		repositories.add("https://github.com/KDE/calendarsupport");
		repositories.add("https://github.com/KDE/kdepim-addons");
		repositories.add("https://github.com/KDE/krita");
		repositories.add("https://github.com/KDE/snapcraft-kde-applications");

		ArrayList<Project> projects = ProjectScrapper.scrapper(repositories, "KDE");

		System.out.println("Number of Repositories: " + repositories.size());
		System.out.println("Number of Projects extracted: " + projects.size());

		try {
			Files.deleteIfExists(Paths.get(IOEnum.PATH_REPOSITORIES.getProperty()));
			PrintStream fileStream = new PrintStream(new File(IOEnum.PATH_REPOSITORIES.getProperty()));
			for (Project project : projects) {
				fileStream.println(project.toString());
			}
			fileStream.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}
}
