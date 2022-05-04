package br.com.unb.cic.scrapy;

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

public class ProjectCrawler {

	public static Map<String, Map<String, String>> crawler(Map<String, String> projects) throws InterruptedException {

		Map<String, Map<String, String>> dataset = new HashMap<String, Map<String, String>>();

		for (var entry : projects.entrySet()) {
			System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
			WebDriver driver = new ChromeDriver();
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
					if (maxKey.equals(new String("C++"))) {
						Map<String, String> project = new HashMap<String, String>();
						String kdeGroup = page.select("a.breadcrumb-item-text").text();
						String projectName = page.select("h1.home-panel-title").text();
						String gitUrl = page.select("input.qa-http-clone-url").attr("value");
						project.put(projectName, gitUrl);
						dataset.put(kdeGroup, project);
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
