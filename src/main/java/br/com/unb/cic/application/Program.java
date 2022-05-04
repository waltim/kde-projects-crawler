package br.com.unb.cic.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.unb.cic.scrapy.GroupCrawler;
import br.com.unb.cic.scrapy.ProjectCrawler;

public class Program {

	public static void main(String[] args) throws InterruptedException {

		ArrayList<String> groups = GroupCrawler.crawler("https://invent.kde.org/explore/groups");
		System.out.println(groups.size());
//		for (String string : groups) {
//			System.out.println(string);
//		}
		Map<String, String> projects = GroupCrawler.crawler(groups);
		System.out.println(projects.size());
//		for (var entry : projects.entrySet()) {
//			System.out.println(entry.getKey() + " --> " + entry.getValue());
//		}
		
//		Map<String, String> projects = new HashMap<String, String>();
//		projects.put("Akonadi Calendar", "https://invent.kde.org/pim/akonadi-calendar");

		Map<String, Map<String, String>> dataset = ProjectCrawler.crawler(projects);
		System.out.println(dataset.size());
		for (var entry : dataset.entrySet()) {
			for (var project : entry.getValue().entrySet()) {
				System.out.println(
						"Group: " + entry.getKey() + ", Project: " + project.getKey() + ", Url: " + project.getValue());
			}
		}
	}
}
