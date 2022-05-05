package br.com.unb.cic.scrapy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import br.com.unb.cic.entities.Project;
import br.com.unb.cic.enums.IOEnum;

public class ProjectCrawler {

	public static ArrayList<Project> crawler(Map<String, String> projects) throws InterruptedException {

		ArrayList<Project> dataset = new ArrayList<Project>();

		for (var entry : projects.entrySet()) {
			System.setProperty("webdriver.chrome.driver", IOEnum.PATH_CHROME_DRIVER.getProperty());
			ChromeOptions opt = new ChromeOptions();
			opt.addArguments("headless");
			WebDriver driver = new ChromeDriver(opt);
			driver.get(entry.getValue());
			Thread.sleep(3000);
			Document page = Jsoup.parse(driver.getPageSource());
			Elements body = page.select("div.progress");
			if (body != null) {
				Map<String, Double> languages = new HashMap<String, Double>();
				for (Element element : body.select("div.progress-bar")) {
					String html = element.attr("title");

					Document doc = Jsoup.parse(html);

					String language = doc.select("span.repository-language-bar-tooltip-language").text();
					String width = doc.select("span.repository-language-bar-tooltip-share").text().replaceAll("%", "");

					Double percent = Double.parseDouble(width);
					languages.put(language, percent);
				}
				if (languages.containsKey("C++")) {
					String maxKey = Collections
							.max(languages.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
					if (maxKey.equals(new String("C++")) && languages.get("C++") > 50.0) {
						String kdeGroup = page.select("a.breadcrumb-item-text").text();
						String projectName = page.select("h1.home-panel-title").text();
						String gitUrl = page.select("input.qa-http-clone-url").attr("value");
						Integer stars = Integer.parseInt(page.select("a.star-count").text());
						String cmts = page.select("strong.project-stat-value").first().text();
						Integer commits = Integer.parseInt(cmts.replaceAll(",", ""));
						Project pj = new Project(kdeGroup, projectName, gitUrl, stars, commits);
						dataset.add(pj);
						driver.close();
					} else {
						driver.close();
					}
				} else {
					driver.close();
				}
			} else {
				driver.close();
			}
		}
		return dataset;
	}
}
